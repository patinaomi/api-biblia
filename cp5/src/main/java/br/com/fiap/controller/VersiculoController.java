package br.com.fiap.controller;

import br.com.fiap.model.dao.VersiculoDao;
import br.com.fiap.model.vo.Versiculo;

import java.sql.SQLException;
import java.util.List;

public class VersiculoController {
    private VersiculoDao versiculoDao;

    public VersiculoController(VersiculoDao versiculoDao) {
        this.versiculoDao = versiculoDao;
    }

    public void inserir (Versiculo versiculo) throws SQLException {
        versiculoDao.inserir(versiculo);
    }
    public void listarVersiculosPorUser(String usuario) throws SQLException {
        List<Versiculo> versiculos = versiculoDao.listarVersiculosPorUser(usuario);
        for (Versiculo v : versiculos) {
            System.out.println(v); // Ou qualquer outra forma de exibição adequada
        }
    }
}
