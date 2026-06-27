package br.edu.biblioteca.repository;

import br.edu.biblioteca.model.Usuario;
import br.edu.biblioteca.database.ConexaoDB;

import java.sql.*;
import java.util.*;

/**
 * Repositório de usuários com persistência em MySQL.
 * 
 * Mantém cache em memória para otimizar operações frequentes.
 */
public class UsuarioRepository {

    private final TreeMap<Integer, Usuario> cache = new TreeMap<>();
    private final ConexaoDB conexaoDB;

    public UsuarioRepository() {
        this.conexaoDB = ConexaoDB.getInstance();
        carregarDoBanco();
    }

    // CRUD = Create, Read, Update, Delete

    public void salvar(Usuario usuario) {
        Objects.requireNonNull(usuario, "Usuário não pode ser nulo");
        
        try (Connection conn = conexaoDB.obterConexao()) {
            if (usuario.getId() == 0) {
                // Novo usuário
                String sql = "INSERT INTO usuarios (nome, email, tipo, bloqueado) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, usuario.getNome());
                    pstmt.setString(2, usuario.getEmail());
                    pstmt.setString(3, usuario.getTipo().name());
                    pstmt.setBoolean(4, usuario.isBloqueado());
                    pstmt.executeUpdate();
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            usuario.setId(generatedKeys.getInt(1));
                        }
                    }
                    System.out.println("✓ Usuário #" + usuario.getId() + " salvo no banco de dados");
                }
            } else {
                // Atualizar usuário existente
                String sql = "UPDATE usuarios SET nome=?, email=?, tipo=?, bloqueado=? WHERE id=?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, usuario.getNome());
                    pstmt.setString(2, usuario.getEmail());
                    pstmt.setString(3, usuario.getTipo().name());
                    pstmt.setBoolean(4, usuario.isBloqueado());
                    pstmt.setInt(5, usuario.getId());
                    pstmt.executeUpdate();
                    System.out.println("✓ Usuário #" + usuario.getId() + " atualizado no banco de dados");
                }
            }
            
            // Atualizar cache
            cache.put(usuario.getId(), usuario);
            
        } catch (SQLException e) {
            System.err.println("✗ Erro ao salvar usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Usuario buscarPorId(int id) {
        // Verificar cache primeiro
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, nome, email, tipo, bloqueado FROM usuarios WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Usuario usuario = extrairUsuarioDoResultSet(rs);
                        cache.put(id, usuario);
                        return usuario;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao buscar usuário por ID: " + e.getMessage());
        }
        return null;
    }

    public void remover(int id) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "DELETE FROM usuarios WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int linhasAfetadas = pstmt.executeUpdate();
                if (linhasAfetadas > 0) {
                    System.out.println("✓ Usuário #" + id + " removido do banco de dados");
                    cache.remove(id);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao remover usuário: " + e.getMessage());
        }
    }

    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, nome, email, tipo, bloqueado FROM usuarios ORDER BY id";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Usuario usuario = extrairUsuarioDoResultSet(rs);
                    usuarios.add(usuario);
                    cache.put(usuario.getId(), usuario);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao listar usuários: " + e.getMessage());
        }
        
        return usuarios;
    }

    // Busca 

    public List<Usuario> buscarPorNome(String nome) {
        String n = nome.toLowerCase();
        
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, nome, email, tipo, bloqueado FROM usuarios WHERE LOWER(nome) LIKE ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + n + "%");
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Usuario> usuarios = new ArrayList<>();
                    while (rs.next()) {
                        Usuario usuario = extrairUsuarioDoResultSet(rs);
                        usuarios.add(usuario);
                        cache.put(usuario.getId(), usuario);
                    }
                    return usuarios;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao buscar usuários por nome: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public List<Usuario> buscarPorTipo(Usuario.Tipo tipo) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, nome, email, tipo, bloqueado FROM usuarios WHERE tipo = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, tipo.name());
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Usuario> usuarios = new ArrayList<>();
                    while (rs.next()) {
                        Usuario usuario = extrairUsuarioDoResultSet(rs);
                        usuarios.add(usuario);
                        cache.put(usuario.getId(), usuario);
                    }
                    return usuarios;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao buscar usuários por tipo: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public List<Usuario> listarBloqueados() {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, nome, email, tipo, bloqueado FROM usuarios WHERE bloqueado = TRUE";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                List<Usuario> usuarios = new ArrayList<>();
                while (rs.next()) {
                    Usuario usuario = extrairUsuarioDoResultSet(rs);
                    usuarios.add(usuario);
                    cache.put(usuario.getId(), usuario);
                }
                return usuarios;
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao listar usuários bloqueados: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    // Utilitários

    public int quantidade() {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT COUNT(*) FROM usuarios";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao contar usuários: " + e.getMessage());
        }
        return 0;
    }

   
    // Métodos Auxiliares
    
    private Usuario extrairUsuarioDoResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String email = rs.getString("email");
        Usuario.Tipo tipo = Usuario.Tipo.valueOf(rs.getString("tipo"));
        boolean bloqueado = rs.getBoolean("bloqueado");
        
        Usuario usuario = new Usuario(id, nome, email, tipo);
        usuario.setBloqueado(bloqueado);
        return usuario;
    }

    private void carregarDoBanco() {
        try {
            List<Usuario> usuarios = listarTodos();
            if (!usuarios.isEmpty()) {
                System.out.println("✓ " + usuarios.size() + " usuários carregados do banco de dados");
            }
        } catch (Exception e) {
            System.err.println("✗ Erro ao carregar usuários do banco de dados: " + e.getMessage());
        }
    }

    public boolean testarConexao() {
        return conexaoDB.testarConexao();
    }
}
