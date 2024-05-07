package br.com.fiap.model.dao;

import br.com.fiap.model.vo.Usuario;

/**
 * Interface que define as operações de acesso a dados para objetos do tipo Usuario.
 */
public interface UsuarioDao {
    /**
     * Insere um novo usuário no banco de dados.
     *
     * @param usuario O objeto Usuario a ser inserido.
     */
    void insert(Usuario usuario);

    /**
     * Recupera o identificador (ID) de um usuário baseado em seu nome.
     *
     * @param name O nome do usuário cujo ID é solicitado.
     * @return O ID do usuário ou -1 se o usuário não for encontrado.
     */
    int getUserIdByName(String name);

    /**
     * Verifica se um nome de usuário está disponível para cadastro.
     *
     * @param username O nome de usuário a ser verificado.
     * @return true se o nome de usuário estiver disponível, false caso contrário.
     */
    boolean isUserDisponivel(String username);
}

