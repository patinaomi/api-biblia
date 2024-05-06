package br.com.fiap.view;

import br.com.fiap.model.conexoes.ConexaoBancoDeDados;
import br.com.fiap.service.OpenAiService;
import br.com.fiap.service.VerseBotService;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {

        //Para funcionar o bot do Telegram
        OpenAiService openAiService = new OpenAiService(); // Inicializa OpenAiService
        VerseBotService botService = new VerseBotService("7139256025:AAG-ytt-WXW-wxWKiUzw1PGjJu3sA8DajVw", openAiService);
        botService.setListener(); // Configura o listener


        // Configurando a conexão com o banco de dados
        Connection conn = ConexaoBancoDeDados.getConnection();
    }



    }
