package br.com.fiap.model.dao;

import br.com.fiap.model.vo.Usuario;


public interface UsuarioDao {
    void inserir(Usuario usuario);
    boolean validarId(int id);
    int getUserIdByName(String name);

}

