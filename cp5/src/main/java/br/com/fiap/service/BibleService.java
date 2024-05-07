package br.com.fiap.service;

import br.com.fiap.api.BibleApiClient;
import br.com.fiap.model.bo.GestaoData;
import br.com.fiap.model.dao.UsuarioDao;
import br.com.fiap.model.dao.VersiculoDao;
import br.com.fiap.model.vo.Versiculo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BibleService {
    private BibleApiClient apiClient;
    private VersiculoDao versiculoDao;
    private UsuarioDao usuarioDao;

    public BibleService(BibleApiClient apiClient, UsuarioDao usuarioDao, VersiculoDao versiculoDao) {
        this.apiClient = apiClient;
        this.usuarioDao = usuarioDao;
        this.versiculoDao = versiculoDao;
    }

    /**
     * Obtém um versículo aleatório da API e associa-o a um usuário.
     * @param userId O identificador do usuário, que será associado ao versículo como chave estrangeira.
     * @return Um objeto Versiculo com todas as informações, incluindo o userId para referência no banco de dados.
     */
    public Versiculo getVersiculoAleatorio(int userId) {
        String jsonResponse = apiClient.getRandomVerse();
        if (jsonResponse != null && !jsonResponse.isEmpty()) {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            return parseVersiculo(jsonObject, userId);
        }
        return null;
    }

    public Versiculo getVersiculoAleatorioDeLivro(int userId, String abbrev) {
        String jsonResponse = apiClient.getRandomVerseByAbbreviation(abbrev);
        if (jsonResponse != null && !jsonResponse.isEmpty()) {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            return parseVersiculo(jsonObject, userId);
        }
        return null;
    }

    private Versiculo parseVersiculo(JsonObject jsonObject, int userId) {
        String livro = jsonObject.getAsJsonObject("book").get("name").getAsString();
        int capitulo = jsonObject.get("chapter").getAsInt();
        int numero = jsonObject.get("number").getAsInt();
        String texto = jsonObject.get("text").getAsString();

        Versiculo versiculo = new Versiculo();
        versiculo.setLivro(livro);
        versiculo.setCapitulo(capitulo);
        versiculo.setNumero(numero);
        versiculo.setTexto(texto);
        versiculo.setDataRegistro(GestaoData.obterDataHoraAtual());
        versiculo.setIdUsuario(userId);
        return versiculo;
    }
}
