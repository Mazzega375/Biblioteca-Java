package br.edu.biblioteca.repository;

import br.edu.biblioteca.model.Emprestimo;
import br.edu.biblioteca.database.ConexaoDB;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Repositório de empréstimos com persistência em MySQL.
 */
public class EmprestimoRepository {

    private final TreeMap<Integer, Emprestimo> cache = new TreeMap<>();
    private final ConexaoDB conexaoDB;

    public EmprestimoRepository() {
        this.conexaoDB = ConexaoDB.getInstance();
        carregarDoBanco();
    }

    public void salvar(Emprestimo emprestimo) {
        Objects.requireNonNull(emprestimo, "Empréstimo não pode ser nulo");
        
        try (Connection conn = conexaoDB.obterConexao()) {
            if (emprestimo.getId() == 0) {
                String sql = "INSERT INTO emprestimos (usuario_id, isbn_livro, data_emprestimo, data_prevista_devolucao, data_devolucao, devolvido) " +
                             "VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    //pstmt é objeto que envia comandos
                    pstmt.setInt(1, emprestimo.getUsuarioId());
                    pstmt.setString(2, emprestimo.getIsbnLivro());
                    pstmt.setDate(3, java.sql.Date.valueOf(emprestimo.getDataEmprestimo()));
                    pstmt.setDate(4, java.sql.Date.valueOf(emprestimo.getDataPrevistaDevolucao()));
                    if (emprestimo.getDataDevolucao() != null) {
                        pstmt.setDate(5, java.sql.Date.valueOf(emprestimo.getDataDevolucao()));
                    } else {
                        pstmt.setNull(5, Types.DATE);
                    }
                    pstmt.setBoolean(6, emprestimo.isDevolvido());
                    pstmt.executeUpdate();
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            emprestimo.setId(generatedKeys.getInt(1));
                        }
                    }
                    System.out.println("✓ Empréstimo #" + emprestimo.getId() + " salvo no banco de dados");
                }
            } else {
                String sql = "UPDATE emprestimos SET usuario_id=?, isbn_livro=?, data_emprestimo=?, data_prevista_devolucao=?, data_devolucao=?, devolvido=? WHERE id=?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, emprestimo.getUsuarioId());
                    pstmt.setString(2, emprestimo.getIsbnLivro());
                    pstmt.setDate(3, java.sql.Date.valueOf(emprestimo.getDataEmprestimo()));
                    pstmt.setDate(4, java.sql.Date.valueOf(emprestimo.getDataPrevistaDevolucao()));
                    if (emprestimo.getDataDevolucao() != null) {
                        pstmt.setDate(5, java.sql.Date.valueOf(emprestimo.getDataDevolucao()));
                    } else {
                        pstmt.setNull(5, Types.DATE);
                    }
                    pstmt.setBoolean(6, emprestimo.isDevolvido());
                    pstmt.setInt(7, emprestimo.getId());
                    pstmt.executeUpdate();
                    System.out.println("✓ Empréstimo #" + emprestimo.getId() + " atualizado no banco de dados");
                }
            }
            
            cache.put(emprestimo.getId(), emprestimo);
            
        } catch (SQLException e) {
            System.err.println("✗ Erro ao salvar empréstimo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Emprestimo buscarPorId(int id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, usuario_id, isbn_livro, data_emprestimo, data_prevista_devolucao, data_devolucao, devolvido FROM emprestimos WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Emprestimo emprestimo = extrairEmprestimoDoResultSet(rs);
                        cache.put(id, emprestimo);
                        return emprestimo;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao buscar empréstimo por ID: " + e.getMessage());
        }
        return null;
    }

    public void remover(int id) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "DELETE FROM emprestimos WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int linhasAfetadas = pstmt.executeUpdate();
                if (linhasAfetadas > 0) {
                    System.out.println("✓ Empréstimo #" + id + " removido do banco de dados");
                    cache.remove(id);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao remover empréstimo: " + e.getMessage());
        }
    }

    public List<Emprestimo> listarTodos() {
        List<Emprestimo> emprestimos = new ArrayList<>();
        
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, usuario_id, isbn_livro, data_emprestimo, data_prevista_devolucao, data_devolucao, devolvido FROM emprestimos ORDER BY id";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Emprestimo emprestimo = extrairEmprestimoDoResultSet(rs);
                    emprestimos.add(emprestimo);
                    cache.put(emprestimo.getId(), emprestimo);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao listar empréstimos: " + e.getMessage());
        }
        
        return emprestimos;
    }

    public List<Emprestimo> buscarPorUsuario(int usuarioId) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, usuario_id, isbn_livro, data_emprestimo, data_prevista_devolucao, data_devolucao, devolvido FROM emprestimos WHERE usuario_id = ? ORDER BY data_emprestimo DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, usuarioId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Emprestimo> emprestimos = new ArrayList<>();
                    while (rs.next()) {
                        Emprestimo emprestimo = extrairEmprestimoDoResultSet(rs);
                        emprestimos.add(emprestimo);
                        cache.put(emprestimo.getId(), emprestimo);
                    }
                    return emprestimos;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao buscar empréstimos por usuário: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public List<Emprestimo> listarAtivos() {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, usuario_id, isbn_livro, data_emprestimo, data_prevista_devolucao, data_devolucao, devolvido FROM emprestimos WHERE devolvido = FALSE ORDER BY data_prevista_devolucao";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                List<Emprestimo> emprestimos = new ArrayList<>();
                while (rs.next()) {
                    Emprestimo emprestimo = extrairEmprestimoDoResultSet(rs);
                    emprestimos.add(emprestimo);
                    cache.put(emprestimo.getId(), emprestimo);
                }
                return emprestimos;
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao listar empréstimos ativos: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public List<Emprestimo> listarEmAtraso() {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, usuario_id, isbn_livro, data_emprestimo, data_prevista_devolucao, data_devolucao, devolvido FROM emprestimos WHERE devolvido = FALSE AND data_prevista_devolucao < CURDATE() ORDER BY data_prevista_devolucao";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                List<Emprestimo> emprestimos = new ArrayList<>();
                while (rs.next()) {
                    Emprestimo emprestimo = extrairEmprestimoDoResultSet(rs);
                    emprestimos.add(emprestimo);
                    cache.put(emprestimo.getId(), emprestimo);
                }
                return emprestimos;
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao listar empréstimos em atraso: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public int quantidade() {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT COUNT(*) FROM emprestimos";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao contar empréstimos: " + e.getMessage());
        }
        return 0;
    }

    private Emprestimo extrairEmprestimoDoResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int usuarioId = rs.getInt("usuario_id");
        String isbn = rs.getString("isbn_livro");
        LocalDate dataEmprestimo = rs.getDate("data_emprestimo").toLocalDate();
        LocalDate dataPrevista = rs.getDate("data_prevista_devolucao").toLocalDate();
        
        Emprestimo emp = new Emprestimo(id, usuarioId, isbn, dataEmprestimo, dataPrevista);
        
        java.sql.Date dataDevolucao = rs.getDate("data_devolucao");
        if (dataDevolucao != null) {
            emp.setDataDevolucao(dataDevolucao.toLocalDate());
        }
        
        emp.setDevolvido(rs.getBoolean("devolvido"));
        return emp;
    }

    private void carregarDoBanco() {
        try {
            List<Emprestimo> emprestimos = listarTodos();
            if (!emprestimos.isEmpty()) {
                System.out.println("✓ " + emprestimos.size() + " empréstimos carregados do banco de dados");
            }
        } catch (Exception e) {
            System.err.println("✗ Erro ao carregar empréstimos do banco de dados: " + e.getMessage());
        }
    }

    /** Retorna empréstimos ativos (não devolvidos) de um usuário. */
    public List<Emprestimo> buscarAtivosDoUsuario(int usuarioId) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, usuario_id, isbn_livro, data_emprestimo, data_prevista_devolucao, data_devolucao, devolvido " +
                         "FROM emprestimos WHERE usuario_id = ? AND devolvido = FALSE ORDER BY data_emprestimo DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, usuarioId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Emprestimo> lista = new ArrayList<>();
                    while (rs.next()) lista.add(extrairEmprestimoDoResultSet(rs));
                    return lista;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao buscar ativos do usuário: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    /** Retorna todos os empréstimos (ativos e devolvidos) de um usuário. */
    public List<Emprestimo> buscarTodosDoUsuario(int usuarioId) {
        return buscarPorUsuario(usuarioId);
    }

    /** Retorna um mapa ISBN → quantidade de empréstimos, para relatórios. */
    public Map<String, Long> contagemPorLivro() {
        Map<String, Long> mapa = new LinkedHashMap<>();
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT isbn_livro, COUNT(*) AS total FROM emprestimos GROUP BY isbn_livro ORDER BY total DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    mapa.put(rs.getString("isbn_livro"), rs.getLong("total"));
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao calcular contagem por livro: " + e.getMessage());
        }
        return mapa;
    }

    public boolean testarConexao() {
        return conexaoDB.testarConexao();
    }
}
