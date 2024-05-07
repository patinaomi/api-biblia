package br.com.fiap.service;

import br.com.fiap.api.BibleApiClient;
import br.com.fiap.model.bo.Validacoes;
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
    private TelegramBot bot;
    private OpenAiService openAiService;
    private TextToSpeechService textToSpeechService = new TextToSpeechService();
    private Map<String, String> userStates = new HashMap<>();
    private Map<String, Versiculo> ultimoVersiculoGerado = new HashMap<>();
    private Map<String, Usuario> tempUsers = new HashMap<>();
    private UserService userService = new UserService(usuarioDao, apiClient);

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

    //Aqui onde vão ficar as opções do menu lá no telegram
    private void processUpdate(Update update) throws SQLException {
        Message message = update.message();
        if (message != null && message.text() != null && !message.text().isEmpty()) {
            String chatId = String.valueOf(message.chat().id());
            String userText = message.text().trim();

            switch (userText.toLowerCase()) {
                case "/start":
                    opcaoStart(chatId);
                    break;
                case "/cadastro":
                    opcaoCadastro(chatId);
                    break;
                case "/devocional":
                    opcaoDevocional(chatId);
                    break;
                case "/versiculo":
                    opcaoVersiculo(chatId);
                    break;
                case "/salvar":
                    opcaoSalvarVersiculo(chatId);
                    break;
                case "/meusversiculos":
                    opcaoMeusVersiculos(chatId);
                    break;
                case "/oracao":
                    opcaoOracao(chatId);
                    break;
                default:
                    opcaoAcoesUser(chatId, userText);
                    break;
            }
        }
    }

    /**
     * Mensagem de início do bot
     */
    private void opcaoStart(String chatId) {
        String welcomeMessage = "Bem-vindo ao VerseBot! Utilize o menu inferior a esquerda para acessar as opções.";
        bot.execute(new SendMessage(chatId, welcomeMessage));
        userStates.put(chatId, null);
    }

    /**
     * Da início ao cadastro do usuário
     */
    private void opcaoCadastro(String chatId) {
        bot.execute(new SendMessage(chatId, "Digite seu usuário:"));
        userStates.put(chatId, "awaiting_name");
    }

    private void fluxoCadastro(String chatId, String userText) throws SQLException {
        String state = userStates.get(chatId);
        Usuario usuario = tempUsers.getOrDefault(chatId, new Usuario());

        switch (state) {
            case "awaiting_name":
                if (!Validacoes.validarUsuario(userText)) {
                    bot.execute(new SendMessage(chatId, "Erro: O nome deve ter entre 2 e 30 caracteres e conter apenas letras."));
                    return;  // Sai do método se a validação falhar
                }
                if(!usuarioDao.isUserDisponivel(userText)){
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
                userService.registrarUsuario(usuario);
                tempUsers.remove(chatId);
                userStates.remove(chatId);
                bot.execute(new SendMessage(chatId, "Cadastro concluído com sucesso!"));
                break;
        }
    }

    private void opcaoDevocional(String chatId) {
        Versiculo versiculoDevocional = bibleService.getVersiculoAleatorio(0); // Está como 0 só pra poder gerar o versículo
        String devotional = openAiService.gerarDevocional(versiculoDevocional.toString());
        ultimoVersiculoGerado.put(chatId, versiculoDevocional);
        bot.execute(new SendMessage(chatId, versiculoDevocional.toString() + "\n" + devotional));
    }

    private void opcaoVersiculo(String chatId) {
        Versiculo versiculo = bibleService.getVersiculoAleatorio(0);
        ultimoVersiculoGerado.put(chatId, versiculo);
        bot.execute(new SendMessage(chatId, versiculo.toString()));
    }
    private void opcaoSalvarVersiculo(String chatId) {
        userStates.put(chatId, "awaiting_user_for_saving");
        bot.execute(new SendMessage(chatId, "Por favor, digite seu usuário para salvar o versículo."));
    }
    private void fluxoSalvarVersiculo(String chatId, String userName) {
        int userId = usuarioDao.getUserIdByName(userName); //aqui pega o id do usuário a partir do nome dele
        if (userId != -1) { //Pra ver se o id é válido
            Versiculo versiculoToSave = ultimoVersiculoGerado.get(chatId);
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

    private void opcaoMeusVersiculos(String chatId) {
        userStates.put(chatId, "awaiting_user_name_for_listing");
        bot.execute(new SendMessage(chatId, "Digite seu nome para listar seus versículos cadastrados:"));
    }
    private void fluxoMeusVersiculos(String chatId, String userName) {
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

    private void opcaoOracao(String chatId) {
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

    private void opcaoAcoesUser(String chatId, String userText) throws SQLException {
        String state = userStates.get(chatId);
        if (state != null) {
            switch (state) {
                case "awaiting_name":
                case "awaiting_email":
                case "awaiting_password":
                    fluxoCadastro(chatId, userText);
                    break;
                case "awaiting_user_for_saving":
                    fluxoSalvarVersiculo(chatId, userText);
                    break;
                case "awaiting_user_name_for_listing":
                    fluxoMeusVersiculos(chatId, userText);
                    break;
                default:
                    opcaoDefault(chatId);
                    break;
            }
        } else {
            opcaoDefault(chatId);
        }
    }

    private void opcaoDefault(String chatId) {
        bot.execute(new SendMessage(chatId, "Opção inválida, por favor selecione uma opção localizada no menu inferior a esquerda."));
    }
}