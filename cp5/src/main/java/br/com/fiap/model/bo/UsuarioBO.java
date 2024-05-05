package br.com.fiap.model.bo;

import br.com.fiap.api.BibleApiClient;
import br.com.fiap.model.dao.UsuarioDao;
import br.com.fiap.model.dao.VersiculoDao;
import br.com.fiap.model.dao.impl.UsuarioDaoImpl;
import br.com.fiap.model.dao.impl.VersiculoDaoImpl;
import br.com.fiap.model.vo.Usuario;
import br.com.fiap.service.BibleService;

import java.sql.SQLException;

public class UsuarioBO {
    private UsuarioDao usuarioDao;

    // Criando o cliente da API
    BibleApiClient apiClient = new BibleApiClient();
    VersiculoDao versiculoDao = new VersiculoDaoImpl();
    BibleService bibleService = new BibleService(apiClient, usuarioDao, versiculoDao);


    public UsuarioBO() {
        this.usuarioDao = new UsuarioDaoImpl();
    }

    public UsuarioBO(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public void inserir(Usuario usuario) throws SQLException {
        // Aqui você pode decidir registrar primeiro na API externa
        String token = bibleService.registrar(usuario);
        if (token != null) {
            usuario.setExternalId(token);
            usuarioDao.inserir(usuario);  // Persistindo no banco de dados local
        } else {
            throw new IllegalStateException("Falha ao registrar usuário na API.");
        }
    }

}
