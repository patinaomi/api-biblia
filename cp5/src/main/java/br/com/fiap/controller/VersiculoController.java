package br.com.fiap.controller;

import br.com.fiap.model.dao.VersiculoDao;
import br.com.fiap.model.vo.Versiculo;

public class VersiculoController {
    private VersiculoDao versiculoDao;

    public VersiculoController(VersiculoDao versiculoDao) {
        this.versiculoDao = versiculoDao;
    }

    public void inserir (Versiculo versiculo) {
        versiculoDao.inserir(versiculo);
    }
    public void listarVersiculosPorId(int usuarioId) {
    }
}
