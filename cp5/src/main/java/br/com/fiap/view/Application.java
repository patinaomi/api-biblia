package br.com.fiap.view;

import br.com.fiap.config.Config;
import br.com.fiap.service.OpenAiService;
import br.com.fiap.service.VerseBotService;

/**
 * Classe principal que inicia a aplicação.
 * Esta classe é responsável por configurar e iniciar o bot do Telegram, utilizando
 * as configurações de propriedades e os serviços necessários para o funcionamento do bot.
 *
 * Essa configuração inclui a inicialização do serviço de interação com a API do OpenAI
 * e a configuração do bot do Telegram com a chave de API específica definida nas propriedades de configuração.
 */
public class Application {
    public static void main(String[] args) {

        //Para funcionar o bot do Telegram
        OpenAiService openAiService = new OpenAiService(); // Inicializa OpenAiService
        VerseBotService botService = new VerseBotService(Config.getProperty("telegram.api.key"), openAiService);
        botService.setListener(); // Configura o listener
        System.out.println("Iniciando bot...");
    }
}
