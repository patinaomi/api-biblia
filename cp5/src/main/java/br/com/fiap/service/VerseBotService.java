package br.com.fiap.service;

import br.com.fiap.api.BibleApiClient;
import br.com.fiap.model.bo.Validacoes;
import br.com.fiap.model.bo.UsuarioBO;
import br.com.fiap.model.dao.UsuarioDao;
import br.com.fiap.model.dao.VersiculoDao;
import br.com.fiap.model.dao.impl.UsuarioDaoImpl;
import br.com.fiap.model.dao.impl.VersiculoDaoImpl;
import br.com.fiap.model.vo.Usuario;
import br.com.fiap.model.vo.Versiculo;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class VerseBotService {
    BibleApiClient apiClient = new BibleApiClient();
    VersiculoDao versiculoDao = new VersiculoDaoImpl();
    UsuarioDao usuarioDao = new UsuarioDaoImpl();
    BibleService bibleService = new BibleService(apiClient, usuarioDao, versiculoDao);
    UsuarioBO usuarioBO = new UsuarioBO(usuarioDao, bibleService);
    private TelegramBot bot;
    private OpenAiService openAiService;
    private Map<String, String> userStates = new HashMap<>();
    private Map<String, Versiculo> lastGeneratedVerses = new HashMap<>();

    public VerseBotService(String token, OpenAiService openAiService) {
        bot = new TelegramBot(token);
        this.openAiService = openAiService;
    }

    public void setListener() {
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                try {
                    processUpdate(update);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            if (e.response() != null) {
                System.err.println("Erro da API do Telegram: " + e.response().description());
            } else {
                System.err.println("Erro de rede ou de conexão: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void processUpdate(Update update) throws SQLException {
        Message message = update.message();
        if (message != null && message.text() != null && !message.text().isEmpty()) {
            String chatId = String.valueOf(message.chat().id());
            String userText = message.text().trim();

            switch (userText.toLowerCase()) {
                case "/start":
                    handleStart(chatId);
                    break;
                case "/devocional":
                    handleDevocional(chatId);
                    break;
                case "/versiculo":
                    handleVersiculo(chatId);
                    break;
                case "/salvar":
                    requestUserName(chatId);
                    break;
                default:
                    if ("awaiting_name".equals(userStates.get(chatId))) {
                        handleSaveVerse(chatId, userText);
                    } else {
                        handleDefault(chatId);
                    }
                    break;
            }
        }
    }

    private void handleStart(String chatId) {
        String welcomeMessage = "Bem-vindo ao VerseBot! Utilize o menu inferior a esquerda para acessar as opções.";
        bot.execute(new SendMessage(chatId, welcomeMessage));
        userStates.put(chatId, null);
    }

    private void handleDevocional(String chatId) throws SQLException {
        Versiculo versiculoDevocional = getRandomVerse(0); // Assuming 0 is a default or non-user specific ID
        String devotional = openAiService.gerarDevocional(versiculoDevocional.toString());
        lastGeneratedVerses.put(chatId, versiculoDevocional);
        bot.execute(new SendMessage(chatId, versiculoDevocional.toString() + "\n\n" + devotional));
    }

    private void handleVersiculo(String chatId) throws SQLException {
        Versiculo versiculo = getRandomVerse(0); // Assuming 0 is a default or non-user specific ID
        lastGeneratedVerses.put(chatId, versiculo);
        bot.execute(new SendMessage(chatId, versiculo.toString()));
    }

    private void requestUserId(String chatId) {
        userStates.put(chatId, "awaiting_id");
        bot.execute(new SendMessage(chatId, "Por favor, digite seu ID para salvar o versículo:"));
    }

    private void handleSaveVerse(String chatId, String userName) {
        int userId = usuarioDao.getUserIdByName(userName);
        if (userId != -1) {
            Versiculo versiculoToSave = lastGeneratedVerses.get(chatId);
            if (versiculoToSave != null) {
                versiculoToSave.setIdUsuario(userId); // Atualiza o ID do usuário no versículo
                versiculoDao.inserir(versiculoToSave);
                bot.execute(new SendMessage(chatId, "Versículo salvo com sucesso."));
            } else {
                bot.execute(new SendMessage(chatId, "Nenhum versículo foi gerado anteriormente."));
            }
        } else {
            bot.execute(new SendMessage(chatId, "Nome de usuário não encontrado, tente novamente."));
        }
        userStates.remove(chatId); // Remover o estado independentemente do resultado
    }


    private void requestUserName(String chatId) {
        userStates.put(chatId, "awaiting_name");
        bot.execute(new SendMessage(chatId, "Por favor, digite seu nome para salvar o versículo:"));
    }


    private void handleDefault(String chatId) {
        bot.execute(new SendMessage(chatId, "Opção inválida, por favor selecione uma opção localizada no menu a esquerda."));
    }

    private Versiculo getRandomVerse(int userId) {
        return bibleService.getVersiculoAleatorio(userId);
    }


}