package br.com.fiap.model.dao;

import br.com.fiap.model.vo.Versiculo;

import java.util.List;
public interface VersiculoDao {
    void insert(Versiculo versiculo);
    List<Versiculo> listVersiculosByUsuarioId(int usuarioId);
}

