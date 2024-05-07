package br.com.fiap.model.vo;

import java.sql.Timestamp;

/**
 * Representa um versículo bíblico, contendo informações sobre a localização do texto na Bíblia
 * e o texto em si, além de metadados para associação com um usuário e registro no sistema.
 */
public class Versiculo {
    //Atributos
    private Integer idVers;
    private String livro;
    private Integer capitulo;
    private Integer numero;
    private String texto;
    private Timestamp dataRegistro;
    private Integer idUsuario;

    // Construtores
    public Versiculo() {
    }

    // Getters e setters
    public Integer getIdVers() {
        return idVers;
    }

    public void setIdVers(Integer idVers) {
        this.idVers = idVers;
    }

    public String getLivro() {
        return livro;
    }

    public void setLivro(String livro) {
        this.livro = livro;
    }

    public Integer getCapitulo() {
        return capitulo;
    }

    public void setCapitulo(Integer capitulo) {
        this.capitulo = capitulo;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Timestamp getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Timestamp dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    // Método toString
    @Override
    public String toString() {
        return  getTexto() +
                "\n" + getLivro() +
                "  " + getCapitulo() + ":" + getNumero();
    }
}
