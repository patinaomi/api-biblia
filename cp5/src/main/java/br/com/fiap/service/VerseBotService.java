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
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InputFile;
import com.pengrad.telegrambot.request.SendAudio;
import com.pengrad.telegrambot.request.SendMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerseBotService {
    BibleApiClient apiClient = new BibleApiClient();
    VersiculoDao versiculoDao = new VersiculoDaoImpl();
    UsuarioDao usuarioDao = new UsuarioDaoImpl();

    BibleService bibleService = new BibleService(apiClient, usuarioDao, versiculoDao);
    UsuarioBO usuarioBO = new UsuarioBO(usuarioDao, bibleService);
    private TelegramBot bot;
    private OpenAiService openAiService;

    private TextToSpeechService textToSpeechService = new TextToSpeechService();
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
                case "/meusversiculos":
                    requestUserNameForListing(chatId);
                    break;
                case "/speak":
                    handleSpeak(chatId);
                    break;
                default:
                    if ("awaiting_name".equals(userStates.get(chatId))) {
                        handleListingVerses(chatId, userText);
                    } else {
                        handleDefault(chatId);
                    }
                    break;
            }
        }
    }

    private void handleSpeak(String chatId) {
        String textToSpeak = "Olá, isso é uma mensagem de teste";
        try {
            InputStream audioStream = textToSpeechService.synthesize(textToSpeak);
            if (audioStream != null) {
                // Convertendo InputStream para arquivo
                File audioFile = streamToFile(audioStream, "text-to-speech.mp3");
                if (audioFile != null) {
                    // Usando o arquivo diretamente no SendAudio
                    SendAudio sendAudio = new SendAudio(chatId, audioFile);
                    bot.execute(sendAudio);
                    // Deletar o arquivo após enviar
                    audioFile.delete();
                } else {
                    bot.execute(new SendMessage(chatId, "Erro ao salvar o áudio em arquivo."));
                }
            } else {
                bot.execute(new SendMessage(chatId, "Erro ao gerar áudio."));
            }
        } catch (IOException e) {
            bot.execute(new SendMessage(chatId, "Erro ao processar o áudio."));
        }
    }

    /**
     * Salva o InputStream em um arquivo temporário.
     */
    private File streamToFile(InputStream in, String fileName) throws IOException {
        File tempFile = File.createTempFile("tts", ".mp3");
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
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
        bot.execute(new SendMessage(chatId, versiculoDevocional.toString() + "\n" + devotional));
    }

    private void handleVersiculo(String chatId) throws SQLException {
        Versiculo versiculo = getRandomVerse(0); // Assuming 0 is a default or non-user specific ID
        lastGeneratedVerses.put(chatId, versiculo);
        bot.execute(new SendMessage(chatId, versiculo.toString()));
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
        bot.execute(new SendMessage(chatId, "Por favor, digite seu nome cadastrado para salvar o versículo: "));
    }


    private void handleDefault(String chatId) {
        bot.execute(new SendMessage(chatId, "Opção inválida, por favor selecione uma opção localizada no menu inferior a esquerda."));
    }

    private Versiculo getRandomVerse(int userId) {
        return bibleService.getVersiculoAleatorio(userId);
    }

    private void requestUserNameForListing(String chatId) {
        userStates.put(chatId, "awaiting_name");
        bot.execute(new SendMessage(chatId, "Digite seu nome para listar seus versículos cadastrados:"));
    }

    private void handleListingVerses(String chatId, String userName) throws SQLException {
        List<Versiculo> versiculos = versiculoDao.listarVersiculosPorUser(userName);
        if (!versiculos.isEmpty()) {
            StringBuilder response = new StringBuilder("Seus versículos:\n");
            for (Versiculo v : versiculos) {
                response.append(String.format("Livro: %s, Capítulo: %d, Versículo: %d - %s\n", v.getLivro(), v.getCapitulo(), v.getNumero(), v.getTexto()));
            }
            bot.execute(new SendMessage(chatId, response.toString()));
        } else {
            bot.execute(new SendMessage(chatId, "Nenhum versículo encontrado para o usuário: " + userName));
        }
        userStates.remove(chatId); // Remover o estado após a execução
    }



}