package br.edu.biblioteca.repository;

import br.edu.biblioteca.model.Livro;
import br.edu.biblioteca.database.ConexaoDB;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repositório de livros com persistência em MySQL.
 */
public class LivroRepository {

    private final TreeMap<String, Livro> cache = new TreeMap<>();
    private final ConexaoDB conexaoDB;

    public LivroRepository() {
        this.conexaoDB = ConexaoDB.getInstance();
        carregarDoBanco();
    }

    public void salvar(Livro livro) {
        Objects.requireNonNull(livro, "Livro não pode ser nulo");
        
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "INSERT INTO livros (isbn, titulo, autor, editora, ano, categoria) VALUES (?, ?, ?, ?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE titulo=?, autor=?, editora=?, ano=?, categoria=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, livro.getIsbn());
                pstmt.setString(2, livro.getTitulo());
                pstmt.setString(3, livro.getAutor());
                pstmt.setString(4, livro.getEditora());
                pstmt.setInt(5, livro.getAno());
                pstmt.setString(6, livro.getCategoria());
                pstmt.setString(7, livro.getTitulo());
                pstmt.setString(8, livro.getAutor());
                pstmt.setString(9, livro.getEditora());
                pstmt.setInt(10, livro.getAno());
                pstmt.setString(11, livro.getCategoria());
                pstmt.executeUpdate();
                System.out.println(" Livro [" + livro.getIsbn() + "] salvo no banco de dados");
            }
            
            cache.put(livro.getIsbn(), livro);
            
        } catch (SQLException e) {
            System.err.println(" Erro ao salvar livro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Livro buscarPorIsbn(String isbn) {
        if (cache.containsKey(isbn)) {
            return cache.get(isbn);
        }
        
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT isbn, titulo, autor, editora, ano, categoria FROM livros WHERE isbn = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, isbn);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Livro livro = extrairLivroDoResultSet(rs);
                        cache.put(isbn, livro);
                        return livro;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(" Erro ao buscar livro por ISBN: " + e.getMessage());
        }
        return null;
    }

    public void remover(String isbn) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "DELETE FROM livros WHERE isbn = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, isbn);
                int linhasAfetadas = pstmt.executeUpdate();
                if (linhasAfetadas > 0) {
                    System.out.println(" Livro [" + isbn + "] removido do banco de dados");
                    cache.remove(isbn);
                }
            }
        } catch (SQLException e) {
            System.err.println(" Erro ao remover livro: " + e.getMessage());
        }
    }

    public List<Livro> listarTodos() {
        List<Livro> livros = new ArrayList<>();
        
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT isbn, titulo, autor, editora, ano, categoria FROM livros ORDER BY titulo";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Livro livro = extrairLivroDoResultSet(rs);
                    livros.add(livro);
                    cache.put(livro.getIsbn(), livro);
                }
            }
        } catch (SQLException e) {
            System.err.println(" Erro ao listar livros: " + e.getMessage());
        }
        
        return livros;
    }

    public List<Livro> buscarPorTitulo(String titulo) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT isbn, titulo, autor, editora, ano, categoria FROM livros WHERE LOWER(titulo) LIKE ? ORDER BY titulo";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + titulo.toLowerCase() + "%");
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Livro> livros = new ArrayList<>();
                    while (rs.next()) {
                        Livro livro = extrairLivroDoResultSet(rs);
                        livros.add(livro);
                        cache.put(livro.getIsbn(), livro);
                    }
                    return livros;
                }
            }
        } catch (SQLException e) {
            System.err.println(" Erro ao buscar livros por título: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public List<Livro> buscarPorAutor(String autor) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT isbn, titulo, autor, editora, ano, categoria FROM livros WHERE LOWER(autor) LIKE ? ORDER BY titulo";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + autor.toLowerCase() + "%");
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Livro> livros = new ArrayList<>();
                    while (rs.next()) {
                        Livro livro = extrairLivroDoResultSet(rs);
                        livros.add(livro);
                        cache.put(livro.getIsbn(), livro);
                    }
                    return livros;
                }
            }
        } catch (SQLException e) {
            System.err.println(" Erro ao buscar livros por autor: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public List<Livro> buscarPorCategoria(String categoria) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT isbn, titulo, autor, editora, ano, categoria FROM livros WHERE LOWER(categoria) LIKE ? ORDER BY titulo";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + categoria.toLowerCase() + "%");
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Livro> livros = new ArrayList<>();
                    while (rs.next()) {
                        Livro livro = extrairLivroDoResultSet(rs);
                        livros.add(livro);
                        cache.put(livro.getIsbn(), livro);
                    }
                    return livros;
                }
            }
        } catch (SQLException e) {
            System.err.println(" Erro ao buscar livros por categoria: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public int quantidade() {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT COUNT(*) FROM livros";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println(" Erro ao contar livros: " + e.getMessage());
        }
        return 0;
    }

    private Livro extrairLivroDoResultSet(ResultSet rs) throws SQLException {
        return new Livro(
                rs.getString("isbn"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("editora"),
                rs.getInt("ano"),
                rs.getString("categoria")
        );
    }

    private void carregarDoBanco() {
        try {
            List<Livro> livros = listarTodos();
            System.out.println(" " + livros.size() + " livros carregados do banco de dados");
        } catch (Exception e) {
            System.err.println(" Erro ao carregar livros do banco de dados: " + e.getMessage());
        }
    }

    public List<Livro> ordenarPorTitulo() {
        return listarTodos().stream()
                .sorted(Comparator.comparing(Livro::getTitulo, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public List<Livro> ordenarPorAutor() {
        return listarTodos().stream()
                .sorted(Comparator.comparing(Livro::getAutor, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public List<Livro> ordenarPorAno() {
        return listarTodos().stream()
                .sorted(Comparator.comparingInt(Livro::getAno))
                .collect(Collectors.toList());
    }

    public boolean testarConexao() {
        return conexaoDB.testarConexao();
    }
}

