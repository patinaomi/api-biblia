package br.com.fiap.api;

import br.com.fiap.model.vo.Usuario;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class BibleApiClient {
    private HttpClient client;

    public BibleApiClient() {
        this.client = HttpClient.newHttpClient();
    }

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

    //Método pra gerar um versículo aleatório de um livro específico da Bíblia
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
}