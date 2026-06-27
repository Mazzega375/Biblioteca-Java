package br.edu.biblioteca.database;

import java.sql.*;

/**
 * Gerenciador de conexão com o banco de dados MySQL.
 * Implementa o padrão Singleton para garantir uma única conexão.
 */
public class ConexaoDB {

    private static ConexaoDB instancia;

    private static final String HOST   = "localhost";
    private static final String PORTA  = "3306";
    private static final String BANCO  = "biblioteca";
    private static final String USUARIO = "root";
    private static final String SENHA   = "2501"; 

    private static final String URL    = "jdbc:mysql://" + HOST + ":" + PORTA + "/" + BANCO
                                         + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private ConexaoDB() {
        try {
            Class.forName(DRIVER);
            System.out.println("✓ Driver MySQL carregado com sucesso");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Erro ao carregar driver MySQL: " + e.getMessage());
            throw new RuntimeException("Driver MySQL não encontrado", e);
        }
    }

    public static ConexaoDB getInstance() {
        if (instancia == null) {
            instancia = new ConexaoDB();
        }
        return instancia;
    }

    public Connection obterConexao() throws SQLException {
        try {
            Connection conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            return conexao;
        } catch (SQLException e) {
            System.err.println("✗ Erro ao conectar ao banco de dados: " + e.getMessage());
            System.err.println("  → Verifique: host=" + HOST + " porta=" + PORTA
                    + " banco=" + BANCO + " usuario=" + USUARIO);
            throw new SQLException("Falha na conexão com o banco de dados", e);
        }
    }

    public boolean testarConexao() {
        try (Connection conexao = obterConexao()) {
            return conexao.isValid(5);
        } catch (SQLException e) {
            System.err.println("✗ Teste de conexão falhou: " + e.getMessage());
            return false;
        }
    }

    public static void fecharConexao(Connection conexao) {
        if (conexao != null) {
            try { conexao.close(); } catch (SQLException ignored) {}
        }
    }

    public static void fecharStatement(Statement stmt) {
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException ignored) {}
        }
    }

    public static void fecharResultSet(ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException ignored) {}
        }
    }

    public static void fecharRecursos(ResultSet rs, Statement stmt, Connection conexao) {
        fecharResultSet(rs);
        fecharStatement(stmt);
        fecharConexao(conexao);
    }
}
