package br.com.fiap.model.dao;

import br.com.fiap.model.vo.Usuario;

import java.sql.SQLException;

public interface UsuarioDao {
    void inserir(Usuario usuario) throws SQLException;
}

