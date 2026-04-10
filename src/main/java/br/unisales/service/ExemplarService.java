package br.unisales.service;

import br.unisales.database.table.Exemplar;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class ExemplarService {

    private final EntityManagerFactory entityManagerFactory;

    public ExemplarService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void inserir(Exemplar exemplar) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            entityManager.persist(exemplar);
            transaction.commit();
            System.out.println("Exemplar inserido com sucesso. ID: " + exemplar.getId());
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            Throwable causa = e;
            while (causa.getCause() != null) {
                causa = causa.getCause();
            }

            System.out.println("Erro ao inserir exemplar: " + causa.getMessage());
        } finally {
            entityManager.close();
        }
    }

    public List<Exemplar> listarTodos() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            return entityManager
                    .createQuery("SELECT e FROM Exemplar e ORDER BY e.id", Exemplar.class)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Erro ao listar exemplares: " + e.getMessage());
            return List.of();
        } finally {
            entityManager.close();
        }
    }

    public Exemplar buscarPorId(Integer id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(Exemplar.class, id);
        } catch (Exception e) {
            System.out.println("Erro ao buscar exemplar: " + e.getMessage());
            return null;
        } finally {
            entityManager.close();
        }
    }

    public void atualizar(Exemplar exemplar) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(exemplar);
            transaction.commit();
            System.out.println("Exemplar atualizado com sucesso.");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Erro ao atualizar exemplar: " + e.getMessage());
        } finally {
            entityManager.close();
        }
    }

    public void deletar(Integer id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            Exemplar exemplar = entityManager.find(Exemplar.class, id);
            if (exemplar == null) {
                System.out.println("Exemplar não encontrado para exclusão.");
                return;
            }

            transaction.begin();
            entityManager.remove(exemplar);
            transaction.commit();
            System.out.println("Exemplar removido com sucesso.");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Erro ao remover exemplar: " + e.getMessage());
        } finally {
            entityManager.close();
        }
    }
}