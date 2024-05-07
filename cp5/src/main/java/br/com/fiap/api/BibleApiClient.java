package br.com.fiap.api;

import br.com.fiap.model.vo.Usuario;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Client API para interagir com a API da Bíblia Digital.
 */
public class BibleApiClient {
    private HttpClient client;

    /**
     * Construtor que inicializa o cliente HTTP.
     */
    public BibleApiClient() {
        this.client = HttpClient.newHttpClient();
    }

    /**
     * Cria um novo usuário na plataforma da Bíblia Digital.
     *
     * @param usuario Objeto {@link Usuario} contendo os detalhes do usuário.
     * @return Uma string com a resposta do servidor, tipicamente um JSON com detalhes do usuário criado ou nulo em caso de falha.
     */
    public String createUser(Usuario usuario) {
        String json = String.format("""
            {
              "name": "%s",
              "email": "%s",
              "password": "%s",
              "notifications": %b
            }
            """, usuario.getNome(), usuario.getEmail(), usuario.getSenha(), usuario.isNotificacoes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.abibliadigital.com.br/api/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtém um versículo aleatório da Bíblia.
     *
     * @return Um String contendo o versículo aleatório em formato JSON ou nulo se houver falha.
     */
    public String getRandomVerse() {
        String url = "https://www.abibliadigital.com.br/api/verses/nvi/random";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtém um versículo aleatório de um livro específico da Bíblia, usando sua abreviação.
     *
     * @param abbrev A abreviação do livro da Bíblia.
     * @return Um String contendo o versículo em formato JSON ou nulo se houver falha.
     */
    public String getRandomVerseByAbbreviation(String abbrev) {
        String url = "https://www.abibliadigital.com.br/api/verses/nvi/" + abbrev + "/random";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Carrega a lista de livros da Bíblia com suas abreviações.
     *
     * @return Um mapa contendo os nomes dos livros como chaves e suas abreviações como valores, ou um mapa vazio em caso de falha.
     */
    public Map<String, String> loadBooks() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.abibliadigital.com.br/api/books"))
                .build();
        Map<String, String> books = new HashMap<>();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
            for (JsonElement elem : jsonArray) {
                JsonObject obj = elem.getAsJsonObject();
                String name = obj.get("name").getAsString();
                String abbrev = obj.getAsJsonObject("abbrev").get("pt").getAsString();
                books.put(name.toLowerCase(), abbrev);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }
}