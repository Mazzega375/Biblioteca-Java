package br.unisales.service;

import br.unisales.database.table.Emprestimo;
import br.unisales.database.table.Multa;
import br.unisales.database.table.Usuario;
import br.unisales.structures.Matriz;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RelatorioService {

    private final EntityManagerFactory entityManagerFactory;

    public RelatorioService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Retorna os livros mais emprestados em ordem decrescente
     */
    public List<Object[]> topMaisEmprestados(int limite) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            String jpql = "SELECT l.isbn, l.titulo, COUNT(e) as total_emprestimos " +
                    "FROM Emprestimo e JOIN e.livro l " +
                    "GROUP BY l.isbn, l.titulo " +
                    "ORDER BY total_emprestimos DESC";

            return entityManager.createQuery(jpql, Object[].class)
                    .setMaxResults(limite)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Erro ao buscar livros mais emprestados: " + e.getMessage());
            return List.of();
        } finally {
            entityManager.close();
        }
    }

    /**
     * Retorna empréstimos em atraso (data de devolução prevista passou)
     */
    public List<Emprestimo> emAtraso() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            LocalDate hoje = LocalDate.now();

            return entityManager.createQuery(
                    "SELECT e FROM Emprestimo e " +
                            "JOIN FETCH e.usuario " + // ← adicionado
                            "JOIN FETCH e.livro " + // ← adicionado
                            "WHERE e.dataPrevista < :hoje AND e.dataDevolucao IS NULL " +
                            "ORDER BY e.dataPrevista ASC",
                    Emprestimo.class)
                    .setParameter("hoje", hoje)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Erro ao buscar empréstimos em atraso: " + e.getMessage());
            return List.of();
        } finally {
            entityManager.close();
        }
    }

    /**
     * Retorna usuários com mais atrasos em empréstimos
     */
    public List<Object[]> usuariosComMaisAtrasos(int limite) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            LocalDate hoje = LocalDate.now();

            String jpql = "SELECT u.id, u.nome, COUNT(e) as total_atrasos " +
                    "FROM Emprestimo e JOIN e.usuario u " +
                    "WHERE e.dataPrevista < :hoje AND e.dataDevolucao IS NULL " +
                    "GROUP BY u.id, u.nome " +
                    "ORDER BY total_atrasos DESC";

            return entityManager.createQuery(jpql, Object[].class)
                    .setParameter("hoje", hoje)
                    .setMaxResults(limite)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Erro ao buscar usuários com mais atrasos: " + e.getMessage());
            return List.of();
        } finally {
            entityManager.close();
        }
    }

    /**
     * Retorna usuários com multas pendentes (não quitadas)
     */
    public List<Object[]> usuariosComMultasPendentes() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            String jpql = "SELECT u.id, u.nome, SUM(m.valor) as valor_total, COUNT(m) as quantidade_multas " +
                    "FROM Multa m JOIN m.emprestimo e JOIN e.usuario u " +
                    "WHERE m.quitada = false " +
                    "GROUP BY u.id, u.nome " +
                    "ORDER BY valor_total DESC";

            return entityManager.createQuery(jpql, Object[].class)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Erro ao buscar usuários com multas pendentes: " + e.getMessage());
            return List.of();
        } finally {
            entityManager.close();
        }
    }

    /**
     * Retorna estatísticas mensais de empréstimos
     * Retorna uma Matriz onde linhas = meses (0-11) e colunas = categorias
     */
    public Matriz<Integer> estatisticasMensais() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            // Quantidade de categorias (simplificado para 10)
            int numCategorias = 10;
            Matriz<Integer> matriz = new Matriz<>(12, numCategorias);

            // Inicializa com zeros
            for (int i = 0; i < 12; i++) {
                for (int j = 0; j < numCategorias; j++) {
                    matriz.set(i, j, 0);
                }
            }

            String jpql = "SELECT FUNCTION('MONTH', e.dataEmprestimo) as mes, lc.categoria.id, COUNT(e) " +
                    "FROM Emprestimo e JOIN e.livro l JOIN l.livroCategorias lc " +
                    "GROUP BY FUNCTION('MONTH', e.dataEmprestimo), lc.categoria.id";

            List<Object[]> resultados = entityManager.createQuery(jpql, Object[].class)
                    .getResultList();

            for (Object[] resultado : resultados) {
                Integer mes = ((Number) resultado[0]).intValue() - 1; // Converte para 0-11
                Integer categoriaId = ((Number) resultado[1]).intValue();
                Integer count = ((Number) resultado[2]).intValue();

                if (mes >= 0 && mes < 12 && categoriaId >= 0 && categoriaId < numCategorias) {
                    matriz.set(mes, categoriaId, count);
                }
            }

            return matriz;
        } catch (Exception e) {
            System.out.println("Erro ao calcular estatísticas: " + e.getMessage());
            return new Matriz<>(12, 10);
        } finally {
            entityManager.close();
        }
    }

    /**
     * Retorna o total de multas geradas em um período
     */
    public double totalMultasGeradas(LocalDate dataInicio, LocalDate dataFim) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            String jpql = "SELECT SUM(m.valor) FROM Multa m " +
                    "WHERE m.emprestimo.dataEmprestimo BETWEEN :inicio AND :fim";

            Object resultado = (Object) entityManager.createQuery(jpql)
                    .setParameter("inicio", dataInicio)
                    .setParameter("fim", dataFim)
                    .getSingleResult();

            return resultado != null ? ((Number) resultado).doubleValue() : 0.0;
        } catch (Exception e) {
            System.out.println("Erro ao calcular total de multas: " + e.getMessage());
            return 0.0;
        } finally {
            entityManager.close();
        }
    }

    /**
     * Retorna informações gerais da biblioteca
     */
    public Map<String, Object> estatisticasGerais() {
        Map<String, Object> stats = new HashMap<>();
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            // Total de livros
            Long totalLivros = (Long) entityManager
                    .createQuery("SELECT COUNT(l) FROM Livro l", Long.class)
                    .getSingleResult();

            // Total de usuários
            Long totalUsuarios = (Long) entityManager
                    .createQuery("SELECT COUNT(u) FROM Usuario u", Long.class)
                    .getSingleResult();

            // Empréstimos ativos
            Long emprestimosAtivos = (Long) entityManager
                    .createQuery("SELECT COUNT(e) FROM Emprestimo e WHERE e.dataDevolucao IS NULL", Long.class)
                    .getSingleResult();

            // Multas pendentes
            Long multasPendentes = (Long) entityManager
                    .createQuery("SELECT COUNT(m) FROM Multa m WHERE m.quitada = false", Long.class)
                    .getSingleResult();

            // Total valor multas pendentes
            Object totalMultas = entityManager
                    .createQuery("SELECT SUM(m.valor) FROM Multa m WHERE m.quitada = false", Object.class)
                    .getSingleResult();

            stats.put("totalLivros", totalLivros);
            stats.put("totalUsuarios", totalUsuarios);
            stats.put("emprestimosAtivos", emprestimosAtivos);
            stats.put("multasPendentes", multasPendentes);
            stats.put("totalValorMultasPendentes", totalMultas != null ? ((Number) totalMultas).doubleValue() : 0.0);

            return stats;
        } catch (Exception e) {
            System.out.println("Erro ao calcular estatísticas gerais: " + e.getMessage());
            return stats;
        } finally {
            entityManager.close();
        }
    }

    /**
     * Gera relatório detalhado de um usuário
     */
    public Map<String, Object> relatorioUsuario(Integer usuarioId) {
        Map<String, Object> relatorio = new HashMap<>();
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            Usuario usuario = entityManager.find(Usuario.class, usuarioId);
            if (usuario == null) {
                return relatorio;
            }

            relatorio.put("usuario", usuario);

            // Empréstimos ativos
            List<Emprestimo> emprestimosAtivos = entityManager
                    .createQuery(
                            "SELECT e FROM Emprestimo e WHERE e.usuario.id = :usuarioId AND e.dataDevolucao IS NULL",
                            Emprestimo.class)
                    .setParameter("usuarioId", usuarioId)
                    .getResultList();
            relatorio.put("emprestimosAtivos", emprestimosAtivos);

            // Multas pendentes
            List<Multa> multasPendentes = entityManager
                    .createQuery(
                            "SELECT m FROM Multa m WHERE m.emprestimo.usuario.id = :usuarioId AND m.quitada = false",
                            Multa.class)
                    .setParameter("usuarioId", usuarioId)
                    .getResultList();
            relatorio.put("multasPendentes", multasPendentes);

            // Total devido
            Object totalDevido = entityManager
                    .createQuery(
                            "SELECT SUM(m.valor) FROM Multa m WHERE m.emprestimo.usuario.id = :usuarioId AND m.quitada = false",
                            Object.class)
                    .setParameter("usuarioId", usuarioId)
                    .getSingleResult();
            relatorio.put("totalDevido", totalDevido != null ? ((Number) totalDevido).doubleValue() : 0.0);

            return relatorio;
        } catch (Exception e) {
            System.out.println("Erro ao gerar relatório do usuário: " + e.getMessage());
            return relatorio;
        } finally {
            entityManager.close();
        }
    }
}
