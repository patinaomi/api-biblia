package br.com.fiap.model.bo;

import br.com.fiap.model.dao.UsuarioDao;
import br.com.fiap.model.dao.impl.UsuarioDaoImpl;
import br.com.fiap.model.vo.Usuario;

import java.sql.SQLException;
import java.util.List;

public class UsuarioBO {
    private UsuarioDao usuarioDao;

    public UsuarioBO() {
        // Inicializa o DAO
        this.usuarioDao = new UsuarioDaoImpl();
    }

    public UsuarioBO(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public void inserir(Usuario usuario) throws SQLException {
        // Aqui você pode adicionar lógica de negócios, se necessário, antes de inserir o usuário
        // Por exemplo, validar dados do usuário
        if (usuario.getNome() == null || usuario.getNome().isEmpty()) {
            throw new IllegalArgumentException("Nome do usuário não pode ser vazio.");
        }

        usuarioDao.inserir(usuario);
    }
}
