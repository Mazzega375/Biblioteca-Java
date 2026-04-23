package br.unisales.service;

import br.unisales.database.table.Autor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class AutorService {

    private final EntityManagerFactory entityManagerFactory;

    public AutorService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void inserir(Autor autor) {
        autor.setId(this.getNextId() + 1);

        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            entityManager.persist(autor);
            transaction.commit();
            System.out.println("Autor inserido com sucesso.");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            Throwable causa = e;
            while (causa.getCause() != null) {
                causa = causa.getCause();
            }

            System.out.println("Erro ao inserir autor: " + causa.getMessage());
        } finally {
            entityManager.close();
        }
    }

    public List<Autor> listarTodos() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            return entityManager
                    .createQuery("SELECT u FROM Autor u ORDER BY u.id", Autor.class)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Erro ao listar autores: " + e.getMessage());
            return List.of();
        } finally {
            entityManager.close();
        }
    }

    public Autor buscarPorNome(String nome) {
    EntityManager entityManager = this.entityManagerFactory.createEntityManager();
    try {
        return entityManager.createQuery(
                "SELECT a FROM Autor a WHERE a.nome = :nome", Autor.class)
                .setParameter("nome", nome)
                .getSingleResult();
    } catch (Exception e) {
        System.out.println("Erro ao buscar autor por nome: " + e.getMessage());
        return null;
    } finally {
        entityManager.close();
    }
}

    public void deletar(Integer id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            Autor autor = entityManager.find(Autor.class, id);
            if (autor == null) {
                System.out.println("Autor nao encontrado para exclusao.");
                return;
            }

            transaction.begin();
            entityManager.remove(autor);
            transaction.commit();
            System.out.println("Autor removido com sucesso.");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Erro ao remover autor: " + e.getMessage());
        } finally {
            entityManager.close();
        }
    }

    private Integer getNextId() {
        EntityManager em = this.entityManagerFactory.createEntityManager();
        try {
            Integer maxId = em.createQuery(
                    "SELECT MAX(u.id) FROM Autor u",
                    Integer.class
            ).getSingleResult();
            return maxId != null ? maxId : 0;
        } catch (Exception e) {
            System.out.println("Erro ao buscar maior ID: " + e.getMessage());
            return 1;
        } finally {
            em.close();
        }
    }
}
