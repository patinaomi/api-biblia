package br.com.fiap.model.bo;

import br.com.fiap.api.BibleApiClient;
import br.com.fiap.model.dao.UsuarioDao;
import br.com.fiap.model.vo.Usuario;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Classe de negócios para operações relacionadas aos usuários.
 * Gerencia a lógica de registro e validação de usuários, interagindo com a API externa e o DAO.
 */
public class UsuarioBO {
    private UsuarioDao usuarioDao;
    private BibleApiClient apiClient;

    /**
     * Construtor para criar uma instância de UsuarioBO com dependências específicas.
     *
     * @param usuarioDao DAO para operações de banco de dados com usuários.
     * @param apiClient Cliente API para comunicação com serviços externos.
     */
    public UsuarioBO(UsuarioDao usuarioDao, BibleApiClient apiClient) {
        this.usuarioDao = usuarioDao;
        this.apiClient = apiClient;
    }

    /**
     * Registra um novo usuário no sistema e na plataforma externa.
     *
     * @param usuario Objeto Usuario contendo os dados do usuário a ser registrado.
     * @return true se o usuário foi registrado com sucesso, false caso contrário.
     */
    public boolean registrarUsuario(Usuario usuario) {
        if (!isUsuarioDisponivel(usuario.getNome())) {
            System.out.println("Nome de usuário já está em uso.");
            return false;
        }

        if (validarUsuario(usuario)) {
            String response = apiClient.createUser(usuario);
            if (response != null && !response.isEmpty()) {
                JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
                String token = jsonResponse.has("token") ? jsonResponse.get("token").getAsString() : "";
                if (!token.isEmpty()) {
                    usuario.setExternalId(token);
                    usuarioDao.insert(usuario);
                    return true;
                } else {
                    System.out.println("Não foi possível registrar o usuário na plataforma externa.");
                }
            }
        }
        return false;
    }

    /**
     * Valida os dados de um usuário.
     *
     * @param usuario Objeto Usuario a ser validado.
     * @return true se os dados do usuário são válidos, false caso contrário.
     */
    private boolean validarUsuario(Usuario usuario) {
        return Validacoes.validarUsuario(usuario.getNome()) &&
                Validacoes.validarEmail(usuario.getEmail()) &&
                Validacoes.validarSenha(usuario.getSenha());
    }

    /**
     * Verifica se um nome de usuário está disponível para registro.
     *
     * @param username O nome de usuário a ser verificado.
     * @return true se o nome de usuário está disponível, false caso contrário.
     */
    public boolean isUsuarioDisponivel(String username) {
        return usuarioDao.isUserDisponivel(username);
    }

    /**
     * Obtém o identificador de um usuário baseado em seu nome.
     *
     * @param userName O nome do usuário cujo identificador é necessário.
     * @return O identificador do usuário ou -1 se o usuário não for encontrado.
     */
    public int obterUsuarioIdPorNome(String userName) {
        return usuarioDao.getUserIdByName(userName);
    }

}
