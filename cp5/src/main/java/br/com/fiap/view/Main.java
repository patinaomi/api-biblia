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
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;


public class Main {
    public static void main(String[] args) throws SQLException {

        //Bot do Telegram
        OpenAiService openAiService = new OpenAiService(); // Inicializa OpenAiService
        VerseBotService botService = new VerseBotService("7139256025:AAG-ytt-WXW-wxWKiUzw1PGjJu3sA8DajVw", openAiService); // Passa para o construtor
        botService.setListener(); // Configura o listener



        // Criando o cliente da API
        BibleApiClient apiClient = new BibleApiClient();
        VersiculoDao versiculoDao = new VersiculoDaoImpl();

        VersiculoController versiculoController = new VersiculoController(versiculoDao);

        //versiculoController.listarVersiculosPorUser("testetoken");


        //openAiService.dispararRequisicao(user);

        // Configurando a conexão com o banco de dados
        Connection conn = ConexaoBancoDeDados.getConnection();

        // Criando a implementação do DAO
        UsuarioDao usuarioDao = new UsuarioDaoImpl();

        // Criando o serviço com as dependências
        BibleService bibleService = new BibleService(apiClient, usuarioDao, versiculoDao);

        // Criando um novo usuário
        //Usuario novoUsuario = new Usuario("testetoken", "testetoken@gmail.com", "senha123", true);

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
