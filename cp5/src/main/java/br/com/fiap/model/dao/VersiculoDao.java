package br.com.fiap.model.dao;

import br.com.fiap.model.vo.Versiculo;

import java.sql.SQLException;
import java.util.List;

public interface VersiculoDao {
    void inserir(Versiculo versiculo) throws SQLException;
    List<Versiculo> listarVersiculosPorUser(String usuario);
}

