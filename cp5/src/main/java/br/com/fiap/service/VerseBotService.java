package br.com.fiap.service;

import br.com.fiap.api.BibleApiClient;
import br.com.fiap.model.bo.UsuarioBO;
import br.com.fiap.model.bo.Validacoes;
import br.com.fiap.model.bo.VersiculoBO;
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

/**
 * Classe de serviço para o bot do Telegram que gerencia interações e processa comandos dos usuários.
 * Esta classe utiliza componentes como o {@link BibleApiClient} para interagir com uma API externa
 * de versículos bíblicos, e gerencia o estado do usuário e as ações do bot através de comandos recebidos.
 */
public class VerseBotService {
    private TelegramBot bot;
    private OpenAiService openAiService;
    private TextToSpeechService textToSpeechService;
    private BibleApiClient apiClient;
    private VersiculoDao versiculoDao;
    private UsuarioDao usuarioDao;
    private BibleService bibleService;
    private UsuarioBO usuarioBO;
    private VersiculoBO versiculoBO;
    private Map<String, Versiculo> ultimoVersiculoGerado  = new HashMap<>();;
    private Map<String, Usuario> tempUsers  = new HashMap<>();;
    private Map<String, String> books;
    private Map<String, String> userStates = new HashMap<>();


    /**
     * Construtor que inicializa o bot com a chave de API do Telegram e os serviços necessários.
     *
     * @param token Chave de API do Telegram para conectar ao bot.
     * @param openAiService Serviço para processamento de IA para gerar conteúdos como devocionais.
     */
    public VerseBotService(String token, OpenAiService openAiService) {
        this.bot = new TelegramBot(token);
        this.openAiService = openAiService;
        this.apiClient = new BibleApiClient();
        this.versiculoDao = new VersiculoDaoImpl();
         this.textToSpeechService = new TextToSpeechService();
        this.usuarioDao = new UsuarioDaoImpl();
        this.bibleService = new BibleService(apiClient, usuarioDao, versiculoDao);
        this.usuarioBO = new UsuarioBO(usuarioDao, apiClient);
        this.versiculoBO = new VersiculoBO(versiculoDao);
        this.books = apiClient.loadBooks();
    }

    /**
     * Configura o listener para receber atualizações do bot do Telegram e processá-las.
     */
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

    /**
     * Processa cada atualização recebida pelo bot do Telegram.
     * Distribui as ações baseadas nos comandos enviados pelos usuários.
     *
     * @param update A atualização recebida do Telegram.
     * @throws SQLException Se ocorrer um erro de SQL durante o processamento do comando.
     */
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
                case "/versiculolivro":
                    opcaoVersiculoPorLivro(chatId);
                    break;
                default:
                    opcaoAcoesUser(chatId, userText);
                    break;
            }
        }
    }

    /**
     * Envia uma mensagem de boas-vindas ao usuário quando o comando /start é recebido.
     * Este método é responsável por iniciar uma nova sessão para o usuário, exibindo uma mensagem
     * introdutória e configurando o estado inicial do usuário no bot.
     *
     * @param chatId O identificador do chat onde a mensagem deve ser enviada. Este é o ID do usuário.
     *
     */
    private void opcaoStart(String chatId) {
        String welcomeMessage = "Bem-vindo ao VerseBot! Utilize o menu inferior a esquerda para acessar as opções.";
        bot.execute(new SendMessage(chatId, welcomeMessage));
        userStates.put(chatId, null);
    }

    /**
     * Inicia o processo de cadastro de um novo usuário no bot.
     * Este método envia uma mensagem solicitando que o usuário insira seu nome de usuário.
     * Também atualiza o estado do usuário no bot para 'awaiting_name', indicando que o bot está
     * aguardando a entrada do nome do usuário como próximo passo do cadastro.
     *
     * @param chatId O identificador do chat onde a mensagem deve ser enviada. Este é o ID do usuário.
     */
    private void opcaoCadastro(String chatId) {
        bot.execute(new SendMessage(chatId, "Digite seu usuário:"));
        userStates.put(chatId, "awaiting_name");
    }

    /**
     * Gerencia o fluxo de cadastro de um novo usuário, processando as respostas em várias etapas.
     * Este método gerencia diferentes estados de cadastro, como recebimento do nome, email e senha.
     * Valida cada entrada e prossegue para a próxima etapa ou retorna uma mensagem de erro se a entrada for inválida.
     * Finaliza o cadastro se todas as entradas forem válidas e o nome de usuário não estiver em uso.
     *
     * @param chatId O identificador do chat no Telegram, usado para enviar mensagens de volta ao usuário.
     * @param userText O texto inserido pelo usuário, que pode ser o nome, email ou senha, dependendo do estado atual.
     */
    private void fluxoCadastro(String chatId, String userText) {
        String state = userStates.get(chatId);
        Usuario usuario = tempUsers.getOrDefault(chatId, new Usuario());

        switch (state) {
            case "awaiting_name":
                if (!Validacoes.validarUsuario(userText)) {
                    bot.execute(new SendMessage(chatId, "Erro: O nome deve ter entre 2 e 30 caracteres e conter apenas letras."));
                    return;  // Sai do método se a validação falhar
                }
                if(!usuarioBO.isUsuarioDisponivel(userText)){
                    bot.execute(new SendMessage(chatId, "Erro: Nome de usuário já está em uso. Por favor, escolha outro nome."));
                    return;
                }
                usuario.setNome(userText.toLowerCase());
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
                boolean sucesso = usuarioBO.registrarUsuario(usuario);

                if(sucesso) {
                    bot.execute(new SendMessage(chatId, "Cadastro concluído com sucesso!"));
                } else {
                    bot.execute(new SendMessage(chatId, "Erro: Tivemos um problema ao fazer seu cadastro!"));
                }
                tempUsers.remove(chatId);
                userStates.remove(chatId);
                break;
        }
    }

    /**
     * Responde a solicitação de um devocional gerando e enviando um versículo bíblico aleatório,
     * seguido de um devocional baseado nesse versículo. O devocional é gerado pelo serviço OpenAi.
     *
     * @param chatId O identificador do chat no Telegram, usado para enviar a mensagem de resposta ao usuário.
     * Este método busca um versículo aleatório usando o método {@code getVersiculoAleatorio} e, se bem-sucedido,
     * solicita ao OpenAi que gere um devocional com base nesse versículo. A resposta é formatada e enviada ao usuário.
     * Se um versículo não puder ser obtido, envia uma mensagem de erro.
     */
    private void opcaoDevocional(String chatId) {
        Versiculo versiculoDevocional = bibleService.getVersiculoAleatorio(0); // Está como 0 só pra poder gerar o versículo
        if (versiculoDevocional != null) {
            String devotional = openAiService.gerarDevocional(versiculoDevocional.toString());
            ultimoVersiculoGerado.put(chatId, versiculoDevocional);
            bot.execute(new SendMessage(chatId, versiculoDevocional.toString() + "\n" + devotional));
        } else {
            bot.execute(new SendMessage(chatId, "Desculpe, não consegui obter um versículo aleatório no momento."));
        }
    }

    /**
     * Responde ao comando do usuário para gerar um versículo aleatório.
     * Este método obtém um versículo aleatório usando o serviço de BibleService,
     * armazena este versículo no mapa de últimos versículos gerados para o usuário específico,
     * e envia o versículo como resposta no chat.
     *
     * @param chatId O identificador do chat no Telegram, usado para enviar a resposta ao usuário.
     * Este método busca um versículo aleatório, armazena esse versículo em um mapa que rastreia o último versículo
     * gerado por chatId e, em seguida, envia esse versículo ao usuário.
     */
    private void opcaoVersiculo(String chatId) {
        Versiculo versiculo = bibleService.getVersiculoAleatorio(0);
        ultimoVersiculoGerado.put(chatId, versiculo);
        bot.execute(new SendMessage(chatId, versiculo.toString()));
    }

    /**
     * Inicia o processo de salvar um versículo para um usuário.
     * Este método ajusta o estado do chat para 'awaiting_user_for_saving', solicitando ao usuário
     * que forneça seu nome de usuário para salvar o versículo atualmente exibido ou recentemente buscado.
     *
     * @param chatId O identificador do chat no Telegram, usado para solicitar informações adicionais ao usuário.
     * Este método apenas configura o estado para esperar o nome do usuário e solicita essa informação,
     * preparando o sistema para a próxima etapa de salvamento do versículo.
     */
    private void opcaoSalvarVersiculo(String chatId) {
        userStates.put(chatId, "awaiting_user_for_saving");
        bot.execute(new SendMessage(chatId, "Por favor, digite seu usuário para salvar o versículo."));
    }

    /**
     * Processa o pedido de um usuário para salvar um versículo.
     * Este método verifica a existência do usuário e se um versículo foi previamente gerado e está disponível para salvar.
     * Se positivo, o versículo é salvo no banco de dados associado ao usuário.
     *
     * @param chatId O identificador do chat no Telegram, usado para interagir com o usuário.
     * @param userName O nome do usuário fornecido, usado para buscar o ID correspondente no sistema.
     * Este método primeiramente converte o nome de usuário para minúsculas, busca seu ID,
     * verifica a existência do versículo a ser salvo e realiza a operação de inserção no banco de dados.
     */
    private void fluxoSalvarVersiculo(String chatId, String userName) {
        int userId = usuarioBO.obterUsuarioIdPorNome(userName.toLowerCase()); //aqui pega o id do usuário a partir do nome dele
        if (userId != -1) { //Pra ver se o id é válido
            Versiculo versiculoToSave = ultimoVersiculoGerado.get(chatId);
            if (versiculoToSave != null) {
                versiculoToSave.setIdUsuario(userId); // Atualiza o ID do usuário no versículo
                versiculoBO.inserirVersiculo(versiculoToSave);
                bot.execute(new SendMessage(chatId, "Versículo salvo com sucesso."));
            } else {
                bot.execute(new SendMessage(chatId, "Nenhum versículo foi gerado anteriormente."));
            }
        } else {
            bot.execute(new SendMessage(chatId, "Nome de usuário não encontrado, tente novamente."));
        }
        userStates.remove(chatId); // Remover o estado independentemente do resultado
    }

    /**
     * Inicia o processo de listar todos os versículos salvos por um usuário.
     * Este método ajusta o estado do chat para 'awaiting_user_name_for_listing',
     * solicitando ao usuário que forneça seu nome para buscar e listar todos os versículos associados a ele.
     *
     * @param chatId O identificador do chat no Telegram, usado para iniciar a interação com o usuário sobre a listagem de seus versículos.
     * Este método simplesmente solicita o nome do usuário para iniciar a busca dos versículos.
     */
    private void opcaoMeusVersiculos(String chatId) {
        userStates.put(chatId, "awaiting_user_name_for_listing");
        bot.execute(new SendMessage(chatId, "Digite seu nome para listar seus versículos cadastrados:"));
    }

    /**
     * Processa a solicitação de listagem dos versículos de um usuário.
     * Este método busca e lista todos os versículos associados ao nome do usuário fornecido.
     *
     * @param chatId O identificador do chat no Telegram, usado para enviar respostas ao usuário.
     * @param userName O nome do usuário fornecido, usado para buscar os versículos associados a ele.
     * Este método converte o nome do usuário para minúsculas, busca os versículos associados e,
     * se encontrados, envia uma lista desses versículos ao usuário.
     */
    private void fluxoMeusVersiculos(String chatId, String userName) {
        List<Versiculo> versiculos = versiculoBO.listarVersiculosPorUsuario(userName.toLowerCase());
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

    /**
     * Gera e envia uma oração diária como arquivo de áudio.
     * Este método usa o serviço OpenAi para gerar um texto de oração e então converte esse texto em áudio usando o serviço IBM Watson Text to Speech.
     * O áudio é enviado ao usuário através do chat.
     *
     * @param chatId O identificador do chat no Telegram, usado para enviar mensagens e áudios ao usuário.
     * Este método informa ao usuário que o áudio está sendo gerado, gera o áudio e tenta enviá-lo.
     * Se o áudio for gerado com sucesso, ele é enviado e depois excluído do sistema para evitar o armazenamento desnecessário.
     */
    private void opcaoOracao(String chatId) {
        String oracao = "Por favor, gere uma oração para hoje.";
        String textToPray = openAiService.gerarDevocional(oracao);
        File audioFile = textToSpeechService.synthesizeToFile(textToPray);

        bot.execute(new SendMessage(chatId, "Gerando Audio..."));

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

    /**
     * Processa as ações do usuário com base no estado atual do chat.
     * Este método direciona o fluxo de interação com base no estado guardado para cada chat.
     * Ele permite uma sequência de interações como cadastro, salvamento e listagem de versículos, entre outros.
     *
     * @param chatId O identificador do chat no Telegram, usado para administrar o estado e as respostas.
     * @param userText O texto fornecido pelo usuário, usado para determinar a próxima ação.
     * Este método verifica o estado do chat e chama o método apropriado para lidar com a ação do usuário.
     * Se o estado for desconhecido ou inexistente, ele chama o método para tratar como ação padrão.
     */
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
                case "awaiting_book_name":
                    fluxoVersiculoPorNomeLivro(chatId, userText);
                    break;
                default:
                    opcaoDefault(chatId);
                    break;
            }
        } else {
            opcaoDefault(chatId);
        }
    }

    /**
     * Inicia a solicitação de um versículo específico de um livro bíblico.
     * Este método pede ao usuário que digite o nome do livro do qual deseja receber um versículo aleatório.
     * O estado do chat é ajustado para 'awaiting_book_name' para preparar a próxima interação.
     *
     * @param chatId O identificador do chat no Telegram, usado para interagir com o usuário solicitando o nome do livro.
     * Este método configura o estado do chat para esperar o nome do livro e informa o usuário sobre a entrada necessária.
     */
    private void opcaoVersiculoPorLivro(String chatId) {
        bot.execute(new SendMessage(chatId, "Por favor, digite o nome do livro para o qual deseja um versículo:"));
        userStates.put(chatId, "awaiting_book_name");
    }

    /**
     * Processa a solicitação de um versículo aleatório de um livro específico.
     * Este método obtém um versículo aleatório do livro especificado pelo usuário.
     *
     * @param chatId O identificador do chat no Telegram, usado para enviar mensagens ao usuário.
     * @param abbrev A abreviatura do livro bíblico do qual o versículo será obtido.
     * Este método busca um versículo aleatório do livro especificado e envia ao usuário.
     * Se não for possível encontrar um versículo, informa ao usuário para tentar novamente.
     */
    private void fluxoVersiculoPorLivro(String chatId, String abbrev) {
        int userId = 0; // Aqui você pode ajustar para pegar o ID real do usuário se necessário
        Versiculo versiculo = bibleService.getVersiculoAleatorioDeLivro(userId, abbrev);
        if (versiculo != null) {
            ultimoVersiculoGerado.put(chatId, versiculo);
            bot.execute(new SendMessage(chatId, versiculo.toString()));
        } else {
            bot.execute(new SendMessage(chatId, "Não foi possível encontrar um versículo para o livro especificado. Tente novamente."));
        }
        userStates.remove(chatId);
    }

    /**
     * Determina a abreviatura de um livro bíblico a partir de seu nome e solicita um versículo aleatório.
     * Este método é chamado após um usuário fornecer o nome de um livro bíblico.
     *
     * @param chatId O identificador do chat no Telegram, usado para enviar mensagens ao usuário.
     * @param bookName O nome do livro bíblico fornecido pelo usuário.
     * Este método busca a abreviatura do livro a partir do nome fornecido.
     * Se a abreviatura não for encontrada, informa ao usuário que o livro não foi encontrado e pede para tentar novamente.
     */
    private void fluxoVersiculoPorNomeLivro(String chatId, String bookName) {
        String abbrev = books.get(bookName.toLowerCase()); // Assume que `books` é o mapa carregado anteriormente
        if (abbrev == null) {
            bot.execute(new SendMessage(chatId, "Livro não encontrado. Por favor, verifique o nome e tente novamente."));
            return;
        }
        fluxoVersiculoPorLivro(chatId, abbrev);
    }

    /**
     * Responde a comandos desconhecidos ou inválidos.
     * Este método é chamado quando um comando fornecido pelo usuário não corresponde a nenhum comando conhecido.
     *
     * @param chatId O identificador do chat no Telegram, usado para interagir com o usuário.
     * Este método envia uma mensagem ao usuário informando que a opção selecionada é inválida e solicita que uma opção válida seja selecionada do menu.
     */
    private void opcaoDefault(String chatId) {
        bot.execute(new SendMessage(chatId, "Opção inválida, por favor selecione uma opção localizada no menu inferior a esquerda."));
    }
}
