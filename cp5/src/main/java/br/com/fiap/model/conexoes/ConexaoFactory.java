package br.com.fiap.model.conexoes;

import br.com.fiap.config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Factory para criar conexões com o banco de dados.
 */
public class ConexaoFactory {
    private static final String USER = Config.getProperty("db.username");
    private static final String PASSWORD = Config.getProperty("db.password");
    private static final String URL = Config.getProperty("db.url");

    /**
     * Cria uma conexão com o banco de dados configurado.
     *
     * @return uma nova conexão com o banco de dados.
     * @throws SQLException se ocorrer um erro ao conectar com o banco de dados.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver do Oracle não encontrado", e);
        }
    }
}
