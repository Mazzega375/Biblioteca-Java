package br.unisales.service;

import br.unisales.database.table.Multa;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class MultaService {

    private final EntityManagerFactory entityManagerFactory;

    public MultaService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void inserir(Multa multa) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(multa);
            transaction.commit();
            System.out.println("Multa registrada com sucesso.");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Erro ao registrar multa: " + e.getMessage());
        } finally {
            entityManager.close();
        }
    }

    public List<Multa> listarTodos() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            return entityManager
                    .createQuery("SELECT m FROM Multa m ORDER BY m.quitada, m.id", Multa.class)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Erro ao listar multas: " + e.getMessage());
            return List.of();
        } finally {
            entityManager.close();
        }
    }

    public List<Multa> listarPendentes() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            return entityManager
                    .createQuery("SELECT m FROM Multa m WHERE m.quitada = false ORDER BY m.id", Multa.class)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Erro ao listar multas pendentes: " + e.getMessage());
            return List.of();
        } finally {
            entityManager.close();
        }
    }

    public Multa buscarPorId(Integer id) {
        if (id == null) {
            return null;
        }

        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(Multa.class, id);
        } catch (Exception e) {
            System.out.println("Erro ao buscar multa por ID: " + e.getMessage());
            return null;
        } finally {
            entityManager.close();
        }
    }

    public Multa buscarPorEmprestimoId(Integer emprestimoId) {
        if (emprestimoId == null) {
            return null;
        }

        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            List<Multa> multas = entityManager
                    .createQuery("SELECT m FROM Multa m WHERE m.emprestimoId = :emprestimoId", Multa.class)
                    .setParameter("emprestimoId", emprestimoId)
                    .setMaxResults(1)
                    .getResultList();

            if (multas.isEmpty()) {
                return null;
            }
            return multas.get(0);
        } catch (Exception e) {
            System.out.println("Erro ao buscar multa por empréstimo: " + e.getMessage());
            return null;
        } finally {
            entityManager.close();
        }
    }

    public boolean quitarMulta(Integer multaId) {
        if (multaId == null) {
            return false;
        }

        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            Multa multa = entityManager.find(Multa.class, multaId);
            if (multa == null) {
                return false;
            }

            if (Boolean.TRUE.equals(multa.getQuitada())) {
                return true;
            }

            transaction.begin();
            multa.setQuitada(Boolean.TRUE);
            entityManager.merge(multa);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Erro ao quitar multa: " + e.getMessage());
            return false;
        } finally {
            entityManager.close();
        }
    }
}