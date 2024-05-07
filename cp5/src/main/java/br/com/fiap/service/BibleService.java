package br.com.fiap.service;

import br.com.fiap.api.BibleApiClient;
import br.com.fiap.model.bo.GestaoData;
import br.com.fiap.model.bo.VersiculoBO;
import br.com.fiap.model.dao.UsuarioDao;
import br.com.fiap.model.dao.VersiculoDao;
import br.com.fiap.model.vo.Versiculo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Serviço para interação com a API de Bíblia, responsável por buscar versículos bíblicos aleatórios
 * e gerenciar suas associações com usuários no banco de dados.
 */
public class BibleService {
    private BibleApiClient apiClient;
    private VersiculoDao versiculoDao;
    private UsuarioDao usuarioDao;
    private VersiculoBO versiculoBO;

    /**
     * Construtor que inicializa o serviço com as dependências necessárias para operação.
     *
     * @param apiClient Cliente da API para acesso aos versículos.
     * @param usuarioDao DAO para operações com usuários.
     * @param versiculoDao DAO para operações com versículos.
     */
    public BibleService(BibleApiClient apiClient, UsuarioDao usuarioDao, VersiculoDao versiculoDao) {
        this.apiClient = apiClient;
        this.usuarioDao = usuarioDao;
        this.versiculoDao = versiculoDao;
    }

    /**
     * Busca um versículo aleatório da Bíblia e o associa a um usuário.
     *
     * @param userId Identificador do usuário para associação do versículo.
     * @return Um objeto {@link Versiculo} com as informações do versículo buscado.
     */
    public Versiculo getVersiculoAleatorio(int userId) {
        try {
            String jsonResponse = apiClient.getRandomVerse();
            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                return parseVersiculo(jsonObject, userId);
            }
        } catch (JsonSyntaxException e) {
            System.err.println("Erro ao analisar resposta JSON: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao obter versículo aleatório: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca um versículo aleatório de um livro específico da Bíblia.
     *
     * @param userId Identificador do usuário para associação do versículo.
     * @param abbrev Abreviação do nome do livro na Bíblia.
     * @return Um objeto {@link Versiculo} ou null caso não encontre um versículo.
     */
    public Versiculo getVersiculoAleatorioDeLivro(int userId, String abbrev) {
        try {
            String jsonResponse = apiClient.getRandomVerseByAbbreviation(abbrev);
            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                return parseVersiculo(jsonObject, userId);
            }
        } catch (JsonSyntaxException e) {
            System.err.println("Erro ao analisar resposta JSON para livro específico: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao obter versículo do livro: " + e.getMessage());
        }
        return null;
    }

    /**
     * Converte um objeto JSON em um objeto {@link Versiculo}.
     *
     * @param jsonObject Objeto JSON contendo dados do versículo.
     * @param userId Identificador do usuário associado.
     * @return Um objeto {@link Versiculo} populado com os dados do JSON.
     */
    private Versiculo parseVersiculo(JsonObject jsonObject, int userId) {
        try {
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
        } catch (NullPointerException e) {
            System.err.println("Dados incompletos no objeto JSON: " + e.getMessage());
            return null;
        }
    }
}
