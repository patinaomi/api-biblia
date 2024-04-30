package br.com.fiap.service;

import br.com.fiap.api.BibleApiClient;
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
    private Map<String, Usuario> tempUsers = new HashMap<>(); // Armazena temporariamente informações de cada usuário

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
            Chat chat = message.chat();
            String chatId = String.valueOf(chat.id());

            String userText = message.text();

            String currentState = userStates.get(chatId); // Obtém o estado atual

            if (userText.equalsIgnoreCase("/start")) {
                String welcomeMessage = "Bem-vindo ao VerseBot! Use /versiculo para um versículo aleatório ou /devocional para um devocional baseado em um versículo aleatório.";
                bot.execute(new SendMessage(chatId, welcomeMessage));
                userStates.put(chatId, null); // Redefine o estado

            } else if (userText.equalsIgnoreCase("/cadastro") || "awaiting_name".equals(currentState)) {
                if ("awaiting_name".equals(currentState)) {
                    tempUsers.get(chatId).setNome(userText);
                    userStates.put(chatId, "awaiting_email");
                    bot.execute(new SendMessage(chatId, "Digite seu e-mail:"));
                } else {
                    userStates.put(chatId, "awaiting_name");
                    tempUsers.put(chatId, new Usuario());
                    bot.execute(new SendMessage(chatId, "Digite seu nome:"));
                }
            } else if ("awaiting_email".equals(currentState)) {
                tempUsers.get(chatId).setEmail(userText);
                userStates.put(chatId, "awaiting_password");
                bot.execute(new SendMessage(chatId, "Digite sua senha:"));
            } else if ("awaiting_password".equals(currentState)) {
                Usuario newUser = tempUsers.get(chatId);
                newUser.setSenha(userText);
                usuarioBO.inserir(newUser);

                userStates.put(chatId, null); // Reseta o estado da conversa
                bot.execute(new SendMessage(chatId, "Usuário cadastrado com sucesso!"));
            } else if (userText.equalsIgnoreCase("/versiculo")) {
                Versiculo verse = getRandomVerse(3);
                bot.execute(new SendMessage(chatId, verse.toString()));
            } else if (userText.equalsIgnoreCase("/devocional")) {
                Versiculo verse = getRandomVerse(3);
                String devotional = openAiService.gerarDevocional(verse.toString());
                bot.execute(new SendMessage(chatId, verse.toString() + "\n\n" + devotional));
            } else {
                bot.execute(new SendMessage(chatId, "Por favor, use /versiculo para um versículo aleatório ou /devocional para um devocional baseado em um versículo."));
            }
        }
    }



    private Versiculo getRandomVerse(int userId) {
        return bibleService.getVersiculoAleatorio(userId);
    }

}
