package br.unisales.service;

import br.unisales.database.table.Notificacao;
import br.unisales.database.table.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.List;

public class NotificacaoService {

    private final EntityManagerFactory entityManagerFactory;

    public NotificacaoService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void gerarNotificacoesDeReservasAtendidas() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            List<Reserva> reservasAtendidas = entityManager
                    .createQuery("SELECT r FROM Reserva r WHERE r.status = 'ATENDIDA'", Reserva.class)
                    .getResultList();

            transaction.begin();
            int geradas = 0;

            for (Reserva reserva : reservasAtendidas) {
                Long jaExiste = entityManager
                        .createQuery(
                                "SELECT COUNT(n) FROM Notificacao n " +
                                "WHERE n.usuarioId = :uid AND n.mensagem LIKE :msg",
                                Long.class)
                        .setParameter("uid", reserva.getUsuarioId())
                        .setParameter("msg", "%Reserva ID: " + reserva.getId() + "%")
                        .getSingleResult();

                if (jaExiste == 0) {
                    Notificacao notificacao = Notificacao.builder()
                            .usuarioId(reserva.getUsuarioId())
                            .mensagem("O livro que voce reservou esta disponivel para retirada! " +
                                      "ISBN: " + reserva.getIsbnLivro() +
                                      " | Reserva ID: " + reserva.getId())
                            .data(LocalDate.now())
                            .lida(false)
                            .build();

                    entityManager.persist(notificacao);
                    geradas++;
                }
            }

            transaction.commit();

            if (geradas > 0) {
                System.out.println(geradas + " nova(s) notificacao(es) gerada(s) automaticamente.");
            } else {
                System.out.println("Nenhuma nova notificacao para gerar.");
            }

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Erro ao gerar notificacoes automaticas: " + e.getMessage());
        } finally {
            entityManager.close();
        }
    }

    public void inserir(Notificacao notificacao) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            entityManager.persist(notificacao);
            transaction.commit();
            System.out.println("Notificacao inserida com sucesso.");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            Throwable causa = e;
            while (causa.getCause() != null) {
                causa = causa.getCause();
            }
            System.out.println("Erro ao inserir notificacao: " + causa.getMessage());
        } finally {
            entityManager.close();
        }
    }

    public List<Notificacao> listarTodos() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            return entityManager
                    .createQuery("SELECT n FROM Notificacao n ORDER BY n.data DESC, n.id DESC", Notificacao.class)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Erro ao listar notificacoes: " + e.getMessage());
            return List.of();
        } finally {
            entityManager.close();
        }
    }

    public List<Notificacao> listarNaoLidasPorUsuario(Integer usuarioId) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            return entityManager
                    .createQuery(
                            "SELECT n FROM Notificacao n WHERE n.usuarioId = :uid AND n.lida = false " +
                            "ORDER BY n.data DESC, n.id DESC",
                            Notificacao.class)
                    .setParameter("uid", usuarioId)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Erro ao listar notificacoes nao lidas: " + e.getMessage());
            return List.of();
        } finally {
            entityManager.close();
        }
    }

    public void marcarComoLida(Integer id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            Notificacao notificacao = entityManager.find(Notificacao.class, id);
            if (notificacao == null) {
                System.out.println("Notificacao nao encontrada.");
                return;
            }
            transaction.begin();
            notificacao.setLida(true);
            entityManager.merge(notificacao);
            transaction.commit();
            System.out.println("Notificacao marcada como lida.");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Erro ao marcar notificacao como lida: " + e.getMessage());
        } finally {
            entityManager.close();
        }
    }

    public void deletar(Integer id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            Notificacao notificacao = entityManager.find(Notificacao.class, id);
            if (notificacao == null) {
                System.out.println("Notificacao nao encontrada para exclusao.");
                return;
            }
            transaction.begin();
            entityManager.remove(notificacao);
            transaction.commit();
            System.out.println("Notificacao removida com sucesso.");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Erro ao remover notificacao: " + e.getMessage());
        } finally {
            entityManager.close();
        }
    }
}