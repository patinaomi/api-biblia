package br.com.fiap.service;

import br.com.fiap.api.BibleApiClient;
import br.com.fiap.model.dao.UsuarioDao;
import br.com.fiap.model.vo.Usuario;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.SQLException;

public class UserService {
    private UsuarioDao usuarioDao;
    private BibleApiClient apiClient;

    public UserService(UsuarioDao usuarioDao, BibleApiClient apiClient) {
        this.usuarioDao = usuarioDao;
        this.apiClient = apiClient;
    }

    public boolean registrarUsuario(Usuario usuario) throws SQLException {
        String response = apiClient.createUser(usuario);
        if (response != null && !response.isEmpty()) {
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            String token = jsonResponse.has("token") ? jsonResponse.get("token").getAsString() : "";
            if (!token.isEmpty()) {
                usuario.setExternalId(token);
                usuarioDao.insert(usuario);
                return true;
            }
        }
        return false;
    }
}
