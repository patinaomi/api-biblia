package br.com.fiap.model.bo;

import br.com.fiap.api.BibleApiClient;
import br.com.fiap.model.dao.UsuarioDao;
import br.com.fiap.model.dao.VersiculoDao;
import br.com.fiap.model.dao.impl.UsuarioDaoImpl;
import br.com.fiap.model.dao.impl.VersiculoDaoImpl;
import br.com.fiap.model.vo.Usuario;
import br.com.fiap.service.BibleService;

public class UsuarioBO {
    private UsuarioDao usuarioDao;

    // Criando o cliente da API
    BibleApiClient apiClient = new BibleApiClient();
    VersiculoDao versiculoDao = new VersiculoDaoImpl();
    BibleService bibleService = new BibleService(apiClient, usuarioDao, versiculoDao);


    public UsuarioBO() {
        this.usuarioDao = new UsuarioDaoImpl();
    }

    public UsuarioBO(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public void inserir(Usuario usuario) {
        // Aqui você pode adicionar lógica de negócios, se necessário, antes de inserir o usuário
        // Por exemplo, validar dados do usuário
        if (usuario.getNome() == null || usuario.getNome().isEmpty()) {
            throw new IllegalArgumentException("Nome do usuário não pode ser vazio.");
        }
        usuarioDao.inserir(usuario);
        //bibleService.registrar(usuario);
    }

}
