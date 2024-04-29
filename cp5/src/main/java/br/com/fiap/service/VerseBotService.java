package br.com.fiap.service;

import br.com.fiap.api.BibleApiClient;
import br.com.fiap.model.dao.UsuarioDao;
import br.com.fiap.model.dao.VersiculoDao;
import br.com.fiap.model.dao.impl.UsuarioDaoImpl;
import br.com.fiap.model.dao.impl.VersiculoDaoImpl;
import br.com.fiap.model.vo.Versiculo;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

public class VerseBotService {

    BibleApiClient apiClient = new BibleApiClient();
    VersiculoDao versiculoDao = new VersiculoDaoImpl();
    UsuarioDao usuarioDao = new UsuarioDaoImpl();
    BibleService bibleService = new BibleService(apiClient, usuarioDao, versiculoDao);
    private OpenAiService openAiService;
    private TelegramBot bot;

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

            String userText = message.text(); // Texto enviado pelo usuário

            if (userText.equalsIgnoreCase("/start")) {
                String welcomeMessage = "Bem-vindo ao VerseBot! Use /versiculo para um versículo aleatório ou /devocional para um devocional baseado em um versículo aleatório.";
                bot.execute(new SendMessage(chatId, welcomeMessage));

            } else if (userText.equalsIgnoreCase("/versiculo")) {
                // Responde com um versículo aleatório
                Versiculo verse = getRandomVerse(3); // Usa o ID de usuário ou outro identificador conforme necessário
                SendResponse response = bot.execute(new SendMessage(chatId, verse.toString()));
                if (!response.isOk()) {
                    System.err.println("Erro ao enviar mensagem: " + response.description());
                }

            } else if (userText.equalsIgnoreCase("/devocional")) {
                // Gera um devocional baseado em um versículo aleatório
                Versiculo verse = getRandomVerse(3);
                String devotional = openAiService.gerarDevocional(verse.toString());

                // Constrói a resposta incluindo o versículo e o devocional
                String responseText = verse.toString() + "\n\n" + (devotional != null ? devotional : "");

                SendResponse response = bot.execute(new SendMessage(chatId, responseText));
                if (!response.isOk()) {
                    System.err.println("Erro ao enviar mensagem: " + response.description());
                }
            } else {
                // Mensagem não reconhecida
                SendResponse response = bot.execute(new SendMessage(chatId, "Por favor, use /versiculo para um versículo aleatório ou /devocional para um devocional baseado em um versículo."));
                if (!response.isOk()) {
                    System.err.println("Erro ao enviar mensagem: " + response.description());
                }
            }
        }
    }



    private Versiculo getRandomVerse(int userId) {
        return bibleService.getVersiculoAleatorio(userId);
    }

}
