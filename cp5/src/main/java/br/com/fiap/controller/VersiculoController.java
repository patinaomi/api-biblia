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

    //Salva o versiculo no banco de dados
    public void inserir (Versiculo versiculo) throws SQLException {
        versiculoDao.insert(versiculo);
    }

    //Mostra os versiculos cadastrados por usuário
    public void listarVersiculosPorUser(String usuario) throws SQLException {
        List<Versiculo> versiculos = versiculoDao.listarVersiculosPorUser(usuario);
        for (Versiculo v : versiculos) {
            System.out.println(v);
        }
    }
}
