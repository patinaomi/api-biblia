package br.com.fiap.model.dao.impl;

import br.com.fiap.model.conexoes.ConexaoBancoDeDados;
import br.com.fiap.model.dao.UsuarioDao;
import br.com.fiap.model.vo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UsuarioDaoImpl implements UsuarioDao {
    private Connection conn;
    PreparedStatement ps = null;
    @Override
    public void insert(Usuario usuario) {
        String sql = "INSERT INTO Tb_Usuario (usuario, email) VALUES (?, ?)";

        try (Connection conn = ConexaoBancoDeDados.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuario.getIdUser());
            ps.setString(2, usuario.getUsuario());
            ps.setString(3, usuario.getEmail());

            int dadosAlterados = ps.executeUpdate();
            if (dadosAlterados > 0) {
                System.out.println("Novo Usuário Cadastrado!");
            } else {
                System.err.println("Erro: Nenhum Usuário foi criado.");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao salvar questionário");
            e.printStackTrace();
        }

    }
}
