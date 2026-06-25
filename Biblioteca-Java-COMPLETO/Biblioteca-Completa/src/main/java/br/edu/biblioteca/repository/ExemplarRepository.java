package br.edu.biblioteca.repository;

import br.edu.biblioteca.model.Exemplar;
import br.edu.biblioteca.database.ConexaoDB;

import java.sql.*;
import java.util.*;

/**
 * Repositório de exemplares com persistência em MySQL.
 */
public class ExemplarRepository {

    private final TreeMap<Integer, Exemplar> cache = new TreeMap<>();
    private final ConexaoDB conexaoDB;

    public ExemplarRepository() {
        this.conexaoDB = ConexaoDB.getInstance();
        carregarDoBanco();
    }

    public void salvar(Exemplar exemplar) {
        Objects.requireNonNull(exemplar, "Exemplar não pode ser nulo");
        
        try (Connection conn = conexaoDB.obterConexao()) {
            if (exemplar.getId() == 0) {
                String sql = "INSERT INTO exemplares (isbn_livro, status) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, exemplar.getIsbnLivro());
                    pstmt.setString(2, exemplar.getStatus().name());
                    pstmt.executeUpdate();
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            exemplar.setId(generatedKeys.getInt(1));
                        }
                    }
                    System.out.println("✓ Exemplar #" + exemplar.getId() + " salvo no banco de dados");
                }
            } else {
                String sql = "UPDATE exemplares SET isbn_livro=?, status=? WHERE id=?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, exemplar.getIsbnLivro());
                    pstmt.setString(2, exemplar.getStatus().name());
                    pstmt.setInt(3, exemplar.getId());
                    pstmt.executeUpdate();
                    System.out.println("✓ Exemplar #" + exemplar.getId() + " atualizado no banco de dados");
                }
            }
            
            cache.put(exemplar.getId(), exemplar);
            
        } catch (SQLException e) {
            System.err.println("✗ Erro ao salvar exemplar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Exemplar buscarPorId(int id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, isbn_livro, status FROM exemplares WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Exemplar exemplar = extrairExemplarDoResultSet(rs);
                        cache.put(id, exemplar);
                        return exemplar;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao buscar exemplar por ID: " + e.getMessage());
        }
        return null;
    }

    public void remover(int id) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "DELETE FROM exemplares WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int linhasAfetadas = pstmt.executeUpdate();
                if (linhasAfetadas > 0) {
                    System.out.println("✓ Exemplar #" + id + " removido do banco de dados");
                    cache.remove(id);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao remover exemplar: " + e.getMessage());
        }
    }

    public List<Exemplar> listarTodos() {
        List<Exemplar> exemplares = new ArrayList<>();
        
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, isbn_livro, status FROM exemplares ORDER BY id";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Exemplar exemplar = extrairExemplarDoResultSet(rs);
                    exemplares.add(exemplar);
                    cache.put(exemplar.getId(), exemplar);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao listar exemplares: " + e.getMessage());
        }
        
        return exemplares;
    }

    public List<Exemplar> buscarPorIsbn(String isbn) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, isbn_livro, status FROM exemplares WHERE isbn_livro = ? ORDER BY id";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, isbn);
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Exemplar> exemplares = new ArrayList<>();
                    while (rs.next()) {
                        Exemplar exemplar = extrairExemplarDoResultSet(rs);
                        exemplares.add(exemplar);
                        cache.put(exemplar.getId(), exemplar);
                    }
                    return exemplares;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao buscar exemplares por ISBN: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public List<Exemplar> buscarPorStatus(Exemplar.Status status) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, isbn_livro, status FROM exemplares WHERE status = ? ORDER BY id";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, status.name());
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Exemplar> exemplares = new ArrayList<>();
                    while (rs.next()) {
                        Exemplar exemplar = extrairExemplarDoResultSet(rs);
                        exemplares.add(exemplar);
                        cache.put(exemplar.getId(), exemplar);
                    }
                    return exemplares;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao buscar exemplares por status: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public List<Exemplar> buscarDisponiveisPorIsbn(String isbn) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, isbn_livro, status FROM exemplares WHERE isbn_livro = ? AND status = 'DISPONIVEL' ORDER BY id";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, isbn);
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Exemplar> exemplares = new ArrayList<>();
                    while (rs.next()) {
                        Exemplar exemplar = extrairExemplarDoResultSet(rs);
                        exemplares.add(exemplar);
                        cache.put(exemplar.getId(), exemplar);
                    }
                    return exemplares;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao buscar exemplares disponíveis: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public int quantidade() {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT COUNT(*) FROM exemplares";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao contar exemplares: " + e.getMessage());
        }
        return 0;
    }

    private Exemplar extrairExemplarDoResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String isbn = rs.getString("isbn_livro");
        Exemplar.Status status = Exemplar.Status.valueOf(rs.getString("status"));
        
        Exemplar exemplar = new Exemplar(id, isbn);
        exemplar.setStatus(status);
        return exemplar;
    }

    private void carregarDoBanco() {
        try {
            List<Exemplar> exemplares = listarTodos();
            if (!exemplares.isEmpty()) {
                System.out.println("✓ " + exemplares.size() + " exemplares carregados do banco de dados");
            }
        } catch (Exception e) {
            System.err.println("✗ Erro ao carregar exemplares do banco de dados: " + e.getMessage());
        }
    }

    /** Retorna o primeiro exemplar disponível para um ISBN (usado no empréstimo). */
    public Optional<Exemplar> buscarDisponivelPorIsbn(String isbn) {
        return buscarDisponiveisPorIsbn(isbn).stream().findFirst();
    }

    /** Conta quantos exemplares disponíveis existem para um ISBN. */
    public long contarDisponiveis(String isbn) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT COUNT(*) FROM exemplares WHERE isbn_livro = ? AND status = 'DISPONIVEL'";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, isbn);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao contar disponíveis: " + e.getMessage());
        }
        return 0;
    }

    /** Conta o total de exemplares (de qualquer status) para um ISBN. */
    public long contarTotal(String isbn) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT COUNT(*) FROM exemplares WHERE isbn_livro = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, isbn);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao contar total de exemplares: " + e.getMessage());
        }
        return 0;
    }

    public boolean testarConexao() {
        return conexaoDB.testarConexao();
    }
}
