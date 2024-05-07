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
    public void insert(Usuario usuario) {
        String sql = "INSERT INTO Tb_Usuario (nome, email, senha, notificacoes, external_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBancoDeDados.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenha());
            ps.setBoolean(4, usuario.isNotificacoes());
            ps.setString(5, usuario.getExternalId()); // Adiciona o ID externo da API da Biblia

            //Pra ver se teve dado alterado, assim consigo saber se um usuário foi cadastrado ou não
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

    //Vai pegar o id do usuário pelo nome
    public int getUserIdByName(String userName) {
        String sql = "SELECT id_user FROM Tb_Usuario WHERE nome = ?";
        try (Connection conn = ConexaoBancoDeDados.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_user");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar ID do usuário.");
            e.printStackTrace();
        }
        return -1;  // Retorna -1 se o usuário não for encontrado (-1 porque não vai ter um usuário com id "negativo")
    }

    //esse método vai ver se só tem um usuario com esse nome
    public boolean isUserDisponivel(String username) {
        String sql = "SELECT COUNT(*) FROM Tb_Usuario WHERE nome = ?";
        try (Connection conn = ConexaoBancoDeDados.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // retorna true se não houver registros com esse nome
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar a disponibilidade do usuário.");
            e.printStackTrace();
        }
        return false; // Em caso de erro vai considerar como não disponível
    }

}
