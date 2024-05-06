package br.com.fiap.model.dao.impl;

import br.com.fiap.model.conexoes.ConexaoBancoDeDados;
import br.com.fiap.model.dao.UsuarioDao;
import br.com.fiap.model.vo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDaoImpl implements UsuarioDao {
    @Override
    public void inserir(Usuario usuario) {
        String sql = "INSERT INTO Tb_Usuario (nome, email, senha, notificacoes, external_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBancoDeDados.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenha());
            ps.setBoolean(4, usuario.isNotificacoes());
            ps.setString(5, usuario.getExternalId()); // Adiciona o ID externo

            int dadosAlterados = ps.executeUpdate();
            if (dadosAlterados > 0) {
                System.out.println("Novo Usuário cadastrado!");
            } else {
                System.err.println("Erro: Nenhum Usuário foi criado.");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao salvar novo usuário");
            e.printStackTrace();
        }
    }

    public int getUserIdByName(String name) {
        String sql = "SELECT id_user FROM Tb_Usuario WHERE nome = ?";
        try (Connection conn = ConexaoBancoDeDados.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_user");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar nome do usuário");
            e.printStackTrace();
        }
        return -1; // ID inválido
    }






}
