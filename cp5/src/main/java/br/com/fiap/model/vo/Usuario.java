package br.com.fiap.model.vo;

public class Usuario {
    //Atributos
    private Integer idUser;
    private String nome;
    private String email;
    private String senha;
    private boolean notificacoes;
    private String externalId;

    // Construtores
    public Usuario() {
    }

    public Usuario(Integer idUser, String nome, String email, String senha, boolean notificacoes, String externalId) {
        this.idUser = idUser;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.notificacoes = notificacoes;
        this.externalId = externalId;
    }

    public Usuario(String nome, String email, String senha, boolean notificacoes) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.notificacoes = notificacoes;
    }

    //Getters & Setters
    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isNotificacoes() {
        return notificacoes;
    }

    public void setNotificacoes(boolean notificacoes) {
        this.notificacoes = notificacoes;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

}