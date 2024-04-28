package br.com.fiap.view;


import br.com.fiap.api.BibleApiClient;
import br.com.fiap.model.bo.GestaoData;
import br.com.fiap.model.conexoes.ConexaoBancoDeDados;
import br.com.fiap.model.dao.UsuarioDao;
import br.com.fiap.model.dao.VersiculoDao;
import br.com.fiap.model.dao.impl.UsuarioDaoImpl;
import br.com.fiap.model.dao.impl.VersiculoDaoImpl;
import br.com.fiap.model.vo.Usuario;
import br.com.fiap.model.vo.Versiculo;
import br.com.fiap.service.BibleService;
import br.com.fiap.service.OpenAiService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        // Criando o cliente da API
        BibleApiClient apiClient = new BibleApiClient();
        VersiculoDao versiculoDao = new VersiculoDaoImpl();

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
        //Versiculo versiculo = bibleService.getRandomVerse(3);

        versiculoDao.listarVersiculosPorUser("testetoken");

        //bibleService.salvarVersiculo(versiculo);


        // Verificar e imprimir o resultado

    }

        String livro = "João";
        int numCapitulo = 3;
        int numVersiculo = 16;

        String user = "Gere um devocional " + livro + " " + numCapitulo + ":"+ numVersiculo;
        String system = """
                Você é um professor de escola bíblica dominical e está ajudando na igreja .
                Deve gerar em até 200 palavras. Não precisa escrever os títulos. 
                Não pode escrever emojis. """;

        //OpenAiService.dispararRequisicao(user, system);

    }
