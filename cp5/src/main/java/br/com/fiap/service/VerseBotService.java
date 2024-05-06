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
import com.pengrad.telegrambot.request.SendAudio;
import com.pengrad.telegrambot.request.SendMessage;
import java.io.File;

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
    private Map<String, Usuario> tempUsers = new HashMap<>();

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
                case "/cadastro":
                    startCadastro(chatId);
                    break;
                case "/devocional":
                    handleDevocional(chatId);
                    break;
                case "/versiculo":
                    handleVersiculo(chatId);
                    break;
                case "/salvar":
                    startSaveVersicle(chatId);
                    break;
                case "/meusversiculos":
                    requestUserNameForListing(chatId);
                    break;
                case "/oracao":
                    handlePrayer(chatId);
                    break;
                default:
                    handleStateBasedActions(chatId, userText);
                    break;
            }
        }
    }



    private void startSaveVersicle(String chatId) {
        userStates.put(chatId, "awaiting_user_for_saving");
        bot.execute(new SendMessage(chatId, "Por favor, digite seu nome para salvar o versículo."));
    }



    private void handleSaveVerse(String chatId, String userName) {
        int userId = usuarioDao.getUserIdByName(userName);
        if (userId != -1) {
            Versiculo versiculoToSave = lastGeneratedVerses.get(chatId);
            if (versiculoToSave != null) {
                versiculoToSave.setIdUsuario(userId); // Atualiza o ID do usuário no versículo
                versiculoDao.insert(versiculoToSave);
                bot.execute(new SendMessage(chatId, "Versículo salvo com sucesso."));
            } else {
                bot.execute(new SendMessage(chatId, "Nenhum versículo foi gerado anteriormente."));
            }
        } else {
            bot.execute(new SendMessage(chatId, "Nome de usuário não encontrado, tente novamente."));
        }
        userStates.remove(chatId); // Remover o estado independentemente do resultado
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


    private void handleStateBasedActions(String chatId, String userText) throws SQLException {
        String state = userStates.get(chatId);
        if (state != null) {
            switch (state) {
                case "awaiting_name":
                case "awaiting_email":
                case "awaiting_password":
                    handleCadastro(chatId, userText);
                    break;
                case "awaiting_user_for_saving":
                    handleSaveVerse(chatId, userText);
                    break;
                case "awaiting_user_name_for_listing":
                    handleListingVerses(chatId, userText);
                    break;
                default:
                    handleDefault(chatId);
                    break;
            }
        } else {
            handleDefault(chatId);
        }
    }


    private void handleResponsesBasedOnState(String chatId, String userText) throws SQLException {
        String state = userStates.get(chatId);
        if (state != null) {
            switch (state) {
                case "awaiting_name":
                    handleCadastro(chatId, userText);
                    break;
                case "awaiting_email":
                    handleCadastro(chatId, userText);
                    break;
                case "awaiting_password":
                    handleCadastro(chatId, userText);
                    break;
                case "awaiting_user_for_saving":
                    handleSaveVerse(chatId, userText);
                    break;
                default:
                    handleDefault(chatId);
                    break;
            }
        } else {
            handleDefault(chatId);
        }
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
        userStates.put(chatId, "awaiting_user_name_for_listing");
        bot.execute(new SendMessage(chatId, "Digite seu nome para listar seus versículos cadastrados:"));
    }

    private void handleListingVerses(String chatId, String userName) throws SQLException {
        List<Versiculo> versiculos = versiculoDao.listarVersiculosPorUser(userName);
        if (!versiculos.isEmpty()) {
            StringBuilder response = new StringBuilder("Seus versículos: \n");
            for (Versiculo v : versiculos) {
                response.append(String.format("\n%s  %d:%d - %s\n", v.getLivro(), v.getCapitulo(), v.getNumero(), v.getTexto()));
            }
            bot.execute(new SendMessage(chatId, response.toString()));
        } else {
            bot.execute(new SendMessage(chatId, "Nenhum versículo encontrado para o usuário: " + userName));
        }
        userStates.remove(chatId); // Remover o estado após a execução
    }





    private void handlePrayer(String chatId) {
        String oracao = "Por favor, gera uma oração para hoje.";
        String textToPray = openAiService.gerarDevocional(oracao);
        File audioFile = textToSpeechService.synthesizeToFile(textToPray);

        if (audioFile != null) {
            try {
                SendAudio sendAudio = new SendAudio(chatId, audioFile);
                bot.execute(sendAudio);
                audioFile.delete(); // Remove o arquivo temporário após o envio
            } catch (Exception e) {
                bot.execute(new SendMessage(chatId, "Erro ao enviar áudio: " + e.getMessage()));
            }
        } else {
            bot.execute(new SendMessage(chatId, "Erro ao gerar áudio da oração."));
        }
    }


    private void startCadastro(String chatId) {
        bot.execute(new SendMessage(chatId, "Digite seu nome completo:"));
        userStates.put(chatId, "awaiting_name");
    }

    private void handleCadastro(String chatId, String userText) throws SQLException {
        String state = userStates.get(chatId);
        Usuario usuario = tempUsers.getOrDefault(chatId, new Usuario());

        switch (state) {
            case "awaiting_name":
                if (!Validacoes.validarUsuario(userText)) {
                    bot.execute(new SendMessage(chatId, "Erro: O nome deve ter entre 2 e 30 caracteres e conter apenas letras."));
                    return;  // Sai do método se a validação falhar
                }
                if(!usuarioDao.isUsernameAvailable(userText)){
                    bot.execute(new SendMessage(chatId, "Erro: Nome de usuário já está em uso. Por favor, escolha outro nome."));
                    return;
                }
                usuario.setNome(userText);
                tempUsers.put(chatId, usuario);
                userStates.put(chatId, "awaiting_email");
                bot.execute(new SendMessage(chatId, "Agora, digite seu email:"));
                break;
            case "awaiting_email":
                if (!Validacoes.validarEmail(userText)) {
                    bot.execute(new SendMessage(chatId, "Erro: Email inválido. Por favor, insira um email válido."));
                    return;  // Sai do método se a validação falhar
                }
                usuario.setEmail(userText);
                tempUsers.put(chatId, usuario);
                userStates.put(chatId, "awaiting_password");
                bot.execute(new SendMessage(chatId, "Por fim, digite sua senha:"));
                break;
            case "awaiting_password":
                if (!Validacoes.validarSenha(userText)) {
                    bot.execute(new SendMessage(chatId, "Erro: A senha deve conter no mínimo 6 caracteres, incluindo pelo menos um número, uma letra maiúscula, uma letra minúscula e um caractere especial."));
                    return;
                }
                usuario.setSenha(userText);
                // Supondo que a senha não precise de validação específica
                usuarioBO.registrarUsuario(usuario);
                tempUsers.remove(chatId);
                userStates.remove(chatId);
                bot.execute(new SendMessage(chatId, "Cadastro concluído com sucesso!"));
                break;
        }
    }







}