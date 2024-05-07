package br.com.fiap.model.bo;

import br.com.fiap.model.dao.VersiculoDao;
import br.com.fiap.model.vo.Versiculo;

import java.util.List;

/**
 * Classe de negócio para operações relacionadas a versículos.
 */
public class VersiculoBO {
    private VersiculoDao versiculoDao;

    /**
     * Construtor que inicializa o VersiculoBO com uma instância de VersiculoDao.
     *
     * @param versiculoDao A instância de VersiculoDao usada para interação com o banco de dados.
     */
    public VersiculoBO(VersiculoDao versiculoDao) {
        this.versiculoDao = versiculoDao;
    }

    /**
     * Insere um versículo no banco de dados se ele passar na validação.
     *
     * @param versiculo O objeto Versiculo a ser inserido.
     * @return true se o versículo foi inserido com sucesso, false caso contrário.
     */
    public boolean inserirVersiculo(Versiculo versiculo) {
        if (validarVersiculo(versiculo)) {
            try {
                versiculoDao.insert(versiculo);
                return true;
            } catch (Exception e) {
                System.err.println("Erro ao inserir versículo: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Valida um versículo para garantir que todos os campos obrigatórios estão preenchidos.
     *
     * @param versiculo O versículo a ser validado.
     * @return true se o versículo é válido, false caso contrário.
     */
    private boolean validarVersiculo(Versiculo versiculo) {
        // Validações como por exemplo, verificar se campos obrigatórios estão preenchidos
        return versiculo.getLivro() != null && !versiculo.getLivro().isEmpty() &&
                versiculo.getTexto() != null && !versiculo.getTexto().isEmpty() &&
                versiculo.getCapitulo() != null && versiculo.getNumero() != null;
    }

    /**
     * Retorna uma lista de versículos associados a um nome de usuário.
     *
     * @param userName O nome do usuário cujos versículos são solicitados.
     * @return Uma lista de objetos Versiculo.
     */
    public List<Versiculo> listarVersiculosPorUsuario(String userName) {
        return versiculoDao.listarVersiculosPorUser(userName);
    }
}
