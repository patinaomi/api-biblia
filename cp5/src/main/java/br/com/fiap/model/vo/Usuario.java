package br.com.fiap.model.vo;

// Classe VO para a tabela Tb_Usuario
public class Usuario {
    private Integer idUser;
    private String usuario;
    private String email;

    // Construtores
    public Usuario() {
    }

    public Usuario(Integer idUser, String usuario, String email) {
        this.idUser = idUser;
        this.usuario = usuario;
        this.email = email;
    }

    // Getters e setters
    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Método toString para facilitar a depuração e visualização dos dados
    @Override
    public String toString() {
        return "UsuarioVO{" +
                "idUser=" + idUser +
                ", usuario='" + usuario + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}