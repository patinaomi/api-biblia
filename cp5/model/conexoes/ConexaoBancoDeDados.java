package br.com.fiap.model.conexoes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBancoDeDados {
    public static final String user = "rm552981";
    public static final String pwd = "240794";
    public static final String url = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(url, user, pwd);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver do Oracle não encontrado", e);
        }
    }
}