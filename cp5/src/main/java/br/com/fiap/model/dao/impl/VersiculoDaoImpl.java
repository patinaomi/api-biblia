package br.com.fiap.model.dao.impl;

import br.com.fiap.model.conexoes.ConexaoFactory;
import br.com.fiap.model.dao.VersiculoDao;
import br.com.fiap.model.vo.Versiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class VersiculoDaoImpl implements VersiculoDao {

    @Override
    public void insert(Versiculo versiculo) {
        String sql = "INSERT INTO Tb_Versiculo (livro, capitulo, numero, texto, data_registro, id_usuario) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, versiculo.getLivro());
            ps.setInt(2, versiculo.getCapitulo());
            ps.setInt(3, versiculo.getNumero());
            ps.setString(4, versiculo.getTexto());
            ps.setTimestamp(5, versiculo.getDataRegistro());
            ps.setInt(6, versiculo.getIdUsuario()); // Garantir que este é o ID correto (é o FK)

            int dadosAlterados = ps.executeUpdate();
            if (dadosAlterados > 0) {
                System.out.println("Novo Versículo cadastrado!");
            } else {
                System.err.println("Erro: Nenhum Versículo foi registrado.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar novo versículo");
            e.printStackTrace();
        }
    }

    @Override
    public List<Versiculo> listarVersiculosPorUser(String usuario) {
        List<Versiculo> versiculos = new ArrayList<>();

        String sql = """
        SELECT v.livro, v.capitulo, v.numero, v.texto, v.data_registro
        FROM Tb_Versiculo v JOIN Tb_Usuario u ON v.id_usuario = u.id_user
        WHERE u.nome = ?
        """;

        try (Connection conn = ConexaoFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Versiculo versiculo = new Versiculo();
                versiculo.setLivro(rs.getString("livro"));
                versiculo.setCapitulo(rs.getInt("capitulo"));
                versiculo.setNumero(rs.getInt("numero"));
                versiculo.setTexto(rs.getString("texto"));
                versiculo.setDataRegistro(rs.getTimestamp("data_registro"));
                versiculos.add(versiculo);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar versículos");
            e.printStackTrace();
        }
        return versiculos;
    }
}
