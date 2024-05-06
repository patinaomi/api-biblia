package br.com.fiap.service;

import br.com.fiap.api.BibleApiClient;
import br.com.fiap.model.bo.GestaoData;
import br.com.fiap.model.dao.UsuarioDao;
import br.com.fiap.model.dao.VersiculoDao;
import br.com.fiap.model.vo.Usuario;
import br.com.fiap.model.vo.Versiculo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.SQLException;


public class BibleService {
    private BibleApiClient apiClient;
    private UsuarioDao usuarioDao;
    private VersiculoDao versiculoDao;

    public BibleService(BibleApiClient apiClient, UsuarioDao usuarioDao, VersiculoDao versiculoDao) {
        this.apiClient = apiClient;
        this.usuarioDao = usuarioDao;
        this.versiculoDao = versiculoDao;
    }

    // Método para registrar usuário na API externa e retornar o token
    public String registrar(Usuario usuario) throws SQLException {
        String response = apiClient.createUser(usuario);
        if (response != null && !response.isEmpty()) {
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            String token = jsonResponse.has("token") ? jsonResponse.get("token").getAsString() : "";
            if (!token.isEmpty()) {
                usuario.setExternalId(token);
                return token;
            } else {
                System.out.println("Usuário criado na API porém o token não foi encontrado.");
                return null;
            }
        } else {
            System.out.println("Não foi possível criar o usuário na API.");
            return null;
        }
    }

    public void inserirUsuarioNoBanco(Usuario usuario) throws SQLException {
        usuarioDao.inserir(usuario);
        System.out.println("Usuário salvo no banco de dados com o token.");
    }

    public Versiculo getVersiculoAleatorio(int userId) {
        Gson gson = new Gson();
        String jsonResponse = apiClient.getRandomVerse();
        if (jsonResponse != null && !jsonResponse.isEmpty()) {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            String livro = jsonObject.getAsJsonObject("book").get("name").getAsString();
            int capitulo = jsonObject.get("chapter").getAsInt();
            int numero = jsonObject.get("number").getAsInt();
            String texto = jsonObject.get("text").getAsString();

            // Criação do objeto Versiculo com a data atual e o id do usuário fornecido
            Versiculo versiculo = new Versiculo();
            versiculo.setLivro(livro);
            versiculo.setCapitulo(capitulo);
            versiculo.setNumero(numero);
            versiculo.setTexto(texto);
            versiculo.setDataRegistro(GestaoData.obterDataHoraAtual());
            versiculo.setIdUsuario(userId);

            return versiculo;
        } else {
            return null; // Em caso de falha
        }
    }

    public Versiculo getVersiculoAleatorioDeLivro(int userId, String abbrev) {
        Gson gson = new Gson();
        String jsonResponse = apiClient.getRandomVerseByAbbreviation(abbrev);
        if (jsonResponse != null && !jsonResponse.isEmpty()) {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            String livro = jsonObject.getAsJsonObject("book").get("name").getAsString();
            int capitulo = jsonObject.get("chapter").getAsInt();
            int numero = jsonObject.get("number").getAsInt();
            String texto = jsonObject.get("text").getAsString();

            // Criação do objeto Versiculo com a data atual e o id do usuário fornecido
            Versiculo versiculo = new Versiculo();
            versiculo.setLivro(livro);
            versiculo.setCapitulo(capitulo);
            versiculo.setNumero(numero);
            versiculo.setTexto(texto);
            versiculo.setDataRegistro(GestaoData.obterDataHoraAtual());
            versiculo.setIdUsuario(userId);

            return versiculo;
        } else {
            return null;
        }
    }
}
