package br.edu.biblioteca.repository;

import br.edu.biblioteca.model.Reserva;
import br.edu.biblioteca.database.ConexaoDB;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Repositório de reservas com persistência em MySQL.
 */
public class ReservaRepository {

    private final TreeMap<Integer, Reserva> cache = new TreeMap<>();
    private final ConexaoDB conexaoDB;

    public ReservaRepository() {
        this.conexaoDB = ConexaoDB.getInstance();
        carregarDoBanco();
    }

    public void salvar(Reserva reserva) {
        Objects.requireNonNull(reserva, "Reserva não pode ser nulo");
        
        try (Connection conn = conexaoDB.obterConexao()) {
            if (reserva.getId() == 0) {
                String sql = "INSERT INTO reservas (usuario_id, isbn_livro, data_reserva, status) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, reserva.getUsuarioId());
                    pstmt.setString(2, reserva.getIsbnLivro());
                    pstmt.setDate(3, java.sql.Date.valueOf(reserva.getDataReserva()));
                    pstmt.setString(4, reserva.getStatus().name());
                    pstmt.executeUpdate();
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            reserva.setId(generatedKeys.getInt(1));
                        }
                    }
                    System.out.println("✓ Reserva #" + reserva.getId() + " salva no banco de dados");
                }
            } else {
                String sql = "UPDATE reservas SET usuario_id=?, isbn_livro=?, data_reserva=?, status=? WHERE id=?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, reserva.getUsuarioId());
                    pstmt.setString(2, reserva.getIsbnLivro());
                    pstmt.setDate(3, java.sql.Date.valueOf(reserva.getDataReserva()));
                    pstmt.setString(4, reserva.getStatus().name());
                    pstmt.setInt(5, reserva.getId());
                    pstmt.executeUpdate();
                    System.out.println("✓ Reserva #" + reserva.getId() + " atualizada no banco de dados");
                }
            }
            
            cache.put(reserva.getId(), reserva);
            
        } catch (SQLException e) {
            System.err.println("✗ Erro ao salvar reserva: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Reserva buscarPorId(int id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, usuario_id, isbn_livro, data_reserva, status FROM reservas WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Reserva reserva = extrairReservaDoResultSet(rs);
                        cache.put(id, reserva);
                        return reserva;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao buscar reserva por ID: " + e.getMessage());
        }
        return null;
    }

    public void remover(int id) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "DELETE FROM reservas WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int linhasAfetadas = pstmt.executeUpdate();
                if (linhasAfetadas > 0) {
                    System.out.println("✓ Reserva #" + id + " removida do banco de dados");
                    cache.remove(id);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao remover reserva: " + e.getMessage());
        }
    }

    public List<Reserva> listarTodos() {
        List<Reserva> reservas = new ArrayList<>();
        
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, usuario_id, isbn_livro, data_reserva, status FROM reservas ORDER BY id";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Reserva reserva = extrairReservaDoResultSet(rs);
                    reservas.add(reserva);
                    cache.put(reserva.getId(), reserva);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao listar reservas: " + e.getMessage());
        }
        
        return reservas;
    }

    public List<Reserva> buscarPorUsuario(int usuarioId) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, usuario_id, isbn_livro, data_reserva, status FROM reservas WHERE usuario_id = ? ORDER BY data_reserva DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, usuarioId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Reserva> reservas = new ArrayList<>();
                    while (rs.next()) {
                        Reserva reserva = extrairReservaDoResultSet(rs);
                        reservas.add(reserva);
                        cache.put(reserva.getId(), reserva);
                    }
                    return reservas;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao buscar reservas por usuário: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public List<Reserva> buscarPorIsbn(String isbn) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, usuario_id, isbn_livro, data_reserva, status FROM reservas WHERE isbn_livro = ? ORDER BY data_reserva";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, isbn);
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Reserva> reservas = new ArrayList<>();
                    while (rs.next()) {
                        Reserva reserva = extrairReservaDoResultSet(rs);
                        reservas.add(reserva);
                        cache.put(reserva.getId(), reserva);
                    }
                    return reservas;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao buscar reservas por ISBN: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public List<Reserva> listarAguardando() {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, usuario_id, isbn_livro, data_reserva, status FROM reservas WHERE status = 'AGUARDANDO' ORDER BY data_reserva";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                List<Reserva> reservas = new ArrayList<>();
                while (rs.next()) {
                    Reserva reserva = extrairReservaDoResultSet(rs);
                    reservas.add(reserva);
                    cache.put(reserva.getId(), reserva);
                }
                return reservas;
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao listar reservas aguardando: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public List<Reserva> buscarPorStatus(Reserva.Status status) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, usuario_id, isbn_livro, data_reserva, status FROM reservas WHERE status = ? ORDER BY data_reserva";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, status.name());
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Reserva> reservas = new ArrayList<>();
                    while (rs.next()) {
                        Reserva reserva = extrairReservaDoResultSet(rs);
                        reservas.add(reserva);
                        cache.put(reserva.getId(), reserva);
                    }
                    return reservas;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao buscar reservas por status: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public int quantidade() {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT COUNT(*) FROM reservas";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao contar reservas: " + e.getMessage());
        }
        return 0;
    }

    private Reserva extrairReservaDoResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int usuarioId = rs.getInt("usuario_id");
        String isbn = rs.getString("isbn_livro");
        LocalDate dataReserva = rs.getDate("data_reserva").toLocalDate();
        
        Reserva reserva = new Reserva(id, usuarioId, isbn, dataReserva);
        reserva.setStatus(Reserva.Status.valueOf(rs.getString("status")));
        
        return reserva;
    }

    private void carregarDoBanco() {
        try {
            List<Reserva> reservas = listarTodos();
            if (!reservas.isEmpty()) {
                System.out.println("✓ " + reservas.size() + " reservas carregadas do banco de dados");
            }
        } catch (Exception e) {
            System.err.println("✗ Erro ao carregar reservas do banco de dados: " + e.getMessage());
        }
    }

    /** Alias para listarTodos() — usado pela UI. */
    public List<Reserva> listarTodas() {
        return listarTodos();
    }

    /** Retorna reservas com status AGUARDANDO para um determinado ISBN. */
    public List<Reserva> buscarAguardandoPorIsbn(String isbn) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT id, usuario_id, isbn_livro, data_reserva, status FROM reservas " +
                         "WHERE isbn_livro = ? AND status = 'AGUARDANDO' ORDER BY data_reserva ASC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, isbn);
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Reserva> lista = new ArrayList<>();
                    while (rs.next()) lista.add(extrairReservaDoResultSet(rs));
                    return lista;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao buscar reservas aguardando por ISBN: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    /** Quantidade de reservas aguardando para um ISBN (tamanho da fila). */
    public long tamanhoFilaPorIsbn(String isbn) {
        try (Connection conn = conexaoDB.obterConexao()) {
            String sql = "SELECT COUNT(*) FROM reservas WHERE isbn_livro = ? AND status = 'AGUARDANDO'";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, isbn);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao contar fila por ISBN: " + e.getMessage());
        }
        return 0;
    }

    /** Atende a próxima reserva aguardando para um ISBN (muda status para ATENDIDA). */
    public Optional<Reserva> atenderProximaReserva(String isbn) {
        List<Reserva> fila = buscarAguardandoPorIsbn(isbn);
        if (fila.isEmpty()) return Optional.empty();
        Reserva proxima = fila.get(0);
        proxima.setStatus(Reserva.Status.ATENDIDA);
        salvar(proxima);
        return Optional.of(proxima);
    }

    /** Retorna todas as reservas aguardando (fila de espera completa), ordenadas por data. */
    public List<Reserva> filaDeEspera() {
        return listarAguardando();
    }

    public boolean testarConexao() {
        return conexaoDB.testarConexao();
    }
}
