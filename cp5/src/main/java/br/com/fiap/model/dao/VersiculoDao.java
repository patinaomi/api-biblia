package br.com.fiap.model.dao;

import br.com.fiap.model.vo.Versiculo;
import java.util.List;

public interface VersiculoDao {
    void inserir(Versiculo versiculo);

    boolean validarUsuario(int userId);
    List<Versiculo> listarVersiculosPorUser(String usuario);
}

