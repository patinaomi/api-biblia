package br.com.fiap.model.bo;

import br.com.fiap.api.BibleApiClient;
import br.com.fiap.model.dao.UsuarioDao;
import br.com.fiap.model.dao.VersiculoDao;
import br.com.fiap.model.dao.impl.UsuarioDaoImpl;
import br.com.fiap.model.dao.impl.VersiculoDaoImpl;
import br.com.fiap.service.BibleService;


public class UsuarioBO {
    private UsuarioDao usuarioDao;

    // Criando o cliente da API
    BibleApiClient apiClient = new BibleApiClient();
    VersiculoDao versiculoDao = new VersiculoDaoImpl();
    BibleService bibleService = new BibleService(apiClient, usuarioDao, versiculoDao);

    public UsuarioBO(UsuarioDao usuarioDao, BibleService bibleService) {
        this.usuarioDao = new UsuarioDaoImpl();
        this.bibleService = bibleService;
    }

}