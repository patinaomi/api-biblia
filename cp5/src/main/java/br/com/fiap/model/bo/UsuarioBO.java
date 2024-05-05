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

    public UsuarioBO(UsuarioDao usuarioDao, BibleService bibleService) {
        this.usuarioDao = new UsuarioDaoImpl();
        this.bibleService = bibleService;
    }

    public void registrarUsuario(Usuario usuario) throws SQLException {
        // Validações e lógica de negócios aqui
        String token = bibleService.registrar(usuario);
        if (token != null) {
            usuario.setExternalId(token);
            bibleService.inserirUsuarioNoBanco(usuario);
        } else {
            throw new IllegalStateException("Falha ao registrar usuário na API e no banco de dados.");
        }
    }

}
