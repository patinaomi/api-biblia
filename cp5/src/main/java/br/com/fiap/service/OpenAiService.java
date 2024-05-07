package br.com.fiap.service;

import br.com.fiap.config.Config;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;

import java.util.Arrays;

/**
 * Serviço para interagir com a API OpenAI, especificamente para gerar conteúdo de texto com base em modelos de linguagem.
 * Esta classe fornece métodos para solicitar e receber textos gerados pela API OpenAI.
 */
public class OpenAiService {
    private com.theokanning.openai.service.OpenAiService service;

    /**
     * Constrói uma instância de OpenAiService configurando o cliente da API OpenAI.
     * A chave API é obtida de um arquivo de configuração.
     */
    public OpenAiService() {
        String token = Config.getProperty("openai.api.key");
        this.service = new com.theokanning.openai.service.OpenAiService(token);
    }

    /**
     * Gera um devocional baseado em um versículo bíblico fornecido.
     * O texto gerado é limitado a um devocional curto e conciso conforme as instruções dadas ao modelo da OpenAI.
     *
     * @param versiculo O versículo bíblico usado como base para gerar o devocional.
     * @return Um texto de devocional gerado ou null se ocorrer um erro na requisição.
     * @throws Exception Se ocorrer um erro ao fazer a requisição para a OpenAI.
     */
    public String gerarDevocional(String versiculo) {
        String systemText = """
                Você é um professor de escola bíblica dominical.
                Deve gerar um devocional com até 150 palavras baseado no versículo fornecido.
                Não precisa escrever os títulos. Não pode escrever emojis.
                """;

        try {
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(Arrays.asList(
                            new ChatMessage(ChatMessageRole.SYSTEM.value(), systemText),
                            new ChatMessage(ChatMessageRole.USER.value(), versiculo)
                    ))
                    .build();

            StringBuilder resultado = new StringBuilder();

            service.createChatCompletion(completionRequest)
                    .getChoices()
                    .forEach(c -> resultado.append(c.getMessage().getContent()).append("\n"));

            return resultado.toString();
        } catch (Exception e) {
            System.err.println("Erro ao enviar requisição para OpenAI: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
