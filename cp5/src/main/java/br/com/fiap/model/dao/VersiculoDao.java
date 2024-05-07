package br.com.fiap.model.dao;

import br.com.fiap.model.vo.Versiculo;
import java.util.List;

/**
 * Interface que define as operações de acesso a dados para objetos do tipo Versículo.
 */
public interface VersiculoDao {
    /**
     * Insere um novo versículo no banco de dados.
     *
     * @param versiculo O objeto Versiculo a ser inserido.
     */
    void insert(Versiculo versiculo);

    /**
     * Retorna uma lista de versículos associados a um usuário específico.
     *
     * @param usuario O nome do usuário para o qual os versículos devem ser listados.
     * @return Uma lista de objetos Versiculo pertencentes ao usuário especificado.
     */
    List<Versiculo> listarVersiculosPorUser(String usuario);
}

