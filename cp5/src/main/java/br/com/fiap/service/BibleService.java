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


    public void registrar(Usuario usuario) {
        String response = apiClient.createUser(usuario);
        if (response != null && !response.isEmpty()) {
            // Usando Gson para parsear a resposta JSON
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            String token = jsonResponse.has("token") ? jsonResponse.get("token").getAsString() : "";
            if (!token.isEmpty()) {
                usuario.setExternalId(token);  // Atualizando o objeto usuario com o token
                usuarioDao.inserir(usuario);
                System.out.println("User created in API and saved to database with token.");
            } else {
                System.out.println("User created in API but no token found.");
            }
        } else {
            System.out.println("Failed to create user in API.");
        }
    }

    public Versiculo getRandomVerse(int userId) {
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

    public void salvarVersiculo(Versiculo versiculo) {
            versiculoDao.inserir(versiculo);
    }
}