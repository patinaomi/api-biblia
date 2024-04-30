package br.com.fiap.view;


import br.com.fiap.api.BibleApiClient;
import br.com.fiap.controller.VersiculoController;
import br.com.fiap.model.conexoes.ConexaoBancoDeDados;
import br.com.fiap.model.dao.UsuarioDao;
import br.com.fiap.model.dao.VersiculoDao;
import br.com.fiap.model.dao.impl.UsuarioDaoImpl;
import br.com.fiap.model.dao.impl.VersiculoDaoImpl;
import br.com.fiap.service.BibleService;
import br.com.fiap.service.OpenAiService;
import br.com.fiap.service.VerseBotService;

import java.sql.Connection;
import java.sql.SQLException;


public class Main {
    public static void main(String[] args) throws SQLException {

        //Bot do Telegram
        OpenAiService openAiService = new OpenAiService(); // Inicializa OpenAiService
        VerseBotService botService = new VerseBotService("7139256025:AAG-ytt-WXW-wxWKiUzw1PGjJu3sA8DajVw", openAiService);
        botService.setListener(); // Configura o listener


        // Configurando a conexão com o banco de dados
        Connection conn = ConexaoBancoDeDados.getConnection();



        //versiculoController.listarVersiculosPorUser("testetoken");



        // Criando a implementação do DAO
        UsuarioDao usuarioDao = new UsuarioDaoImpl();

        // Criando o serviço com as dependências


        // Registrando o usuário via serviço
        //bibleService.registrar(novoUsuario);

        // Chamada ao método para obter um versículo aleatório
        //Versiculo versiculo = bibleService.getVersiculoAleatorio(3);

       //Versiculo versiculo = bibleService.getVersiculoAleatorioDeLivro(3, "gn");
       // if (versiculo != null) {
       //     System.out.println("Versículo: " + versiculo.getTexto());
       // } else {
        //    System.out.println("Falha ao obter o versículo.");
       // }

        //bibleService.salvarVersiculo(versiculo);


        // Verificar e imprimir o resultado

    }



    }
