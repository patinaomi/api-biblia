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
    private Map<String, String> userStates = new HashMap<>();
    private Map<String, Usuario> tempUsers = new HashMap<>();
    private OpenAiService openAiService;

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
            String currentState = userStates.getOrDefault(chatId, "");

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
                case "/cadastro":
                    startCadastro(chatId);
                    break;
                default:
                    if (!currentState.isEmpty()) {
                        handleCadastro(chatId, userText, currentState);
                    } else {
                        handleDefault(chatId);
                    }
                    break;
            }
        }
    }

    private void handleStart(String chatId) {
        String welcomeMessage = "Bem-vindo ao VerseBot! Selecione alguma opção do menu no canto inferior esquerdo.";
        bot.execute(new SendMessage(chatId, welcomeMessage));
        userStates.put(chatId, null);
    }

    private void handleDevocional(String chatId) throws SQLException {
        Versiculo versiculoDevocional = getRandomVerse(3);
        String devotional = openAiService.gerarDevocional(versiculoDevocional.toString());
        bot.execute(new SendMessage(chatId, versiculoDevocional.toString() + "\n\n" + devotional));
    }

    private void handleVersiculo(String chatId) throws SQLException {
        Versiculo versiculo = getRandomVerse(3);
        bot.execute(new SendMessage(chatId, versiculo.toString()));
    }

    private void startCadastro(String chatId) {
        userStates.put(chatId, "awaiting_name");
        bot.execute(new SendMessage(chatId, "Digite seu nome:"));
    }

    private void handleCadastro(String chatId, String userText, String currentState) throws SQLException {
        switch (currentState) {
            case "awaiting_name":
                if (Validacoes.validarNome(userText)) {
                    tempUsers.put(chatId, new Usuario());
                    tempUsers.get(chatId).setNome(userText);
                    userStates.put(chatId, "awaiting_email");
                    bot.execute(new SendMessage(chatId, "Nome válido! Digite seu e-mail:"));
                } else {
                    bot.execute(new SendMessage(chatId, "Nome inválido, tente novamente."));
                }
                break;
            case "awaiting_email":
                if (Validacoes.validarEmail(userText)) {
                    tempUsers.get(chatId).setEmail(userText);
                    userStates.put(chatId, "awaiting_password");
                    bot.execute(new SendMessage(chatId, "E-mail válido! Digite sua senha:"));
                } else {
                    bot.execute(new SendMessage(chatId, "E-mail inválido, tente novamente."));
                }
                break;
            case "awaiting_password":
                tempUsers.get(chatId).setSenha(userText);
                usuarioBO.registrarUsuario(tempUsers.get(chatId));
                tempUsers.remove(chatId);
                userStates.remove(chatId);
                bot.execute(new SendMessage(chatId, "Usuário cadastrado com sucesso!"));
                break;
        }
    }

    private void handleDefault(String chatId) {
        bot.execute(new SendMessage(chatId, "Opção inválida, por favor selecione uma opção localizada no menu a esquerda."));
    }

    private Versiculo getRandomVerse(int userId) {
        return bibleService.getVersiculoAleatorio(userId);
    }
}