package br.com.fiap.controller;

import br.com.fiap.model.dao.UsuarioDao;
import br.com.fiap.model.vo.Usuario;

import java.sql.SQLException;

//Classe Controller, será implementada futuramente quando esse projeto for integrado com o front end
public class UsuarioController {
    private UsuarioDao usuarioDao;

    public UsuarioController(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public void inserir(Usuario usuario) throws SQLException {
        usuarioDao.insert(usuario);
    }
}