package br.com.fiap.model.vo;

import br.com.fiap.model.bo.GestaoData;

import java.sql.Timestamp;

public class Versiculo {
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

    public Versiculo(Integer idVers, String livro, Integer capitulo, Integer numero, String texto, Timestamp dataRegistro, Integer idUsuario) {
        this.idVers = idVers;
        this.livro = livro;
        this.capitulo = capitulo;
        this.numero = numero;
        this.texto = texto;
        this.dataRegistro = dataRegistro;
        this.idUsuario = idUsuario;
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
        return "VersiculoVO{" +
                "idVers=" + idVers +
                ", livro='" + livro + '\'' +
                ", capitulo=" + capitulo +
                ", numero=" + numero +
                ", texto='" + texto + '\'' +
                ", dataRegistro=" + GestaoData.formatarTimestampParaString(getDataRegistro()) +
                ", idUsuario=" + idUsuario +
                '}';
    }
}
