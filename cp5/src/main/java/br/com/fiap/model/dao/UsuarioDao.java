package br.com.fiap.model.dao;

import br.com.fiap.model.vo.Usuario;


public interface UsuarioDao {
    void insert(Usuario usuario);
    int getUserIdByName(String name);
    boolean isUsernameAvailable(String username);
}

