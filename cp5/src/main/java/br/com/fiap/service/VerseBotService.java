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
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import java.util.HashMap;
import java.util.Map;

public class VerseBotService {

    BibleApiClient apiClient = new BibleApiClient();
    VersiculoDao versiculoDao = new VersiculoDaoImpl();
    UsuarioDao usuarioDao = new UsuarioDaoImpl();
    BibleService bibleService = new BibleService(apiClient, usuarioDao, versiculoDao);
    UsuarioBO usuarioBO = new UsuarioBO();
    private OpenAiService openAiService;
    private TelegramBot bot;

    private Map<String, String> userStates = new HashMap<>(); // Armazena o estado da conversa de cada usuário
    private Map<String, Usuario> tempUsers = new HashMap<>(); // Pra armazenar temporariamente as informações de cada usuário

    public VerseBotService(String token, OpenAiService openAiService) {
        bot = new TelegramBot(token);
        this.openAiService = openAiService; // Inicializando corretamente o OpenAiService
    }

    public void setListener() {
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                processUpdate(update);
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

    private void processUpdate(Update update) {
        Message message = update.message();
        if (message != null && message.text() != null && !message.text().isEmpty()) {
            String chatId = String.valueOf(message.chat().id());
            String userText = message.text();
            String currentState = userStates.getOrDefault(chatId, "");

            switch (userText.toLowerCase()) {
                case "/start":
                    String welcomeMessage = "Bem-vindo ao VerseBot! Selecione alguma opção do menu no canto inferior esquerdo.";
                    bot.execute(new SendMessage(chatId, welcomeMessage));
                    userStates.put(chatId, null);
                    break;
                case "/cadastro":
                    userStates.put(chatId, "awaiting_name");
                    bot.execute(new SendMessage(chatId, "Digite seu nome:"));
                    break;
                default:
                    if ("awaiting_name".equals(currentState)) {
                        if (Validacoes.validarNome(userText)) {
                            tempUsers.put(chatId, new Usuario()); // Inicializa um novo usuário
                            tempUsers.get(chatId).setNome(userText);
                            userStates.put(chatId, "awaiting_email");
                            bot.execute(new SendMessage(chatId, "Nome válido! Digite seu e-mail:"));
                        } else {
                            bot.execute(new SendMessage(chatId, "Nome inválido, tente novamente."));
                        }
                    } else if ("awaiting_email".equals(currentState)) {
                        if (Validacoes.validarEmail(userText)) {
                            tempUsers.get(chatId).setEmail(userText);
                            userStates.put(chatId, "awaiting_password");
                            bot.execute(new SendMessage(chatId, "E-mail válido! Digite sua senha:"));
                        } else {
                            bot.execute(new SendMessage(chatId, "E-mail inválido, tente novamente."));
                        }
                    } else if ("awaiting_password".equals(currentState)) {
                        tempUsers.get(chatId).setSenha(userText);
                        usuarioBO.inserir(tempUsers.get(chatId));
                        tempUsers.remove(chatId);
                        userStates.remove(chatId);
                        bot.execute(new SendMessage(chatId, "Usuário cadastrado com sucesso!"));
                    } else {
                        bot.execute(new SendMessage(chatId, "Por favor, use /versiculo para um versículo aleatório ou /devocional para um devocional baseado em um versículo."));
                    }
                    break;
            }
        }
    }


    private Versiculo getRandomVerse(int userId) {
        return bibleService.getVersiculoAleatorio(userId);
    }

}
