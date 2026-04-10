package br.unisales.service;

import br.unisales.database.table.Emprestimo;
import br.unisales.database.table.Livro;
import br.unisales.database.table.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

import java.util.List;

public class EmprestimoService {

    private final EntityManagerFactory entityManagerFactory;

    public EmprestimoService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void inserir(Emprestimo emprestimo) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(emprestimo);
            transaction.commit();
            System.out.println("Empréstimo cadastrado com sucesso.");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Erro ao cadastrar empréstimo: " + e.getMessage());
        } finally {
            entityManager.close();
        }
    }

    public List<Emprestimo> listarTodos() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            return entityManager
                    .createQuery("SELECT e FROM Emprestimo e JOIN FETCH e.usuario JOIN FETCH e.livro ORDER BY e.id", Emprestimo.class)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Erro ao listar empréstimos: " + e.getMessage());
            return List.of();
        } finally {
            entityManager.close();
        }
    }

    public Emprestimo buscarPorId(Integer id) {
        if (id == null) {
            return null;
        }

        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            return entityManager
                    .createQuery("SELECT e FROM Emprestimo e JOIN FETCH e.usuario JOIN FETCH e.livro WHERE e.id = :id", Emprestimo.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            System.out.println("Erro ao buscar empréstimo por ID: " + e.getMessage());
            return null;
        } finally {
            entityManager.close();
        }
    }

    public boolean atualizar(Emprestimo emprestimo) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            Emprestimo existente = entityManager.find(Emprestimo.class, emprestimo.getId());
            if (existente == null) {
                return false;
            }
            transaction.begin();
            entityManager.merge(emprestimo);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Erro ao atualizar empréstimo: " + e.getMessage());
            return false;
        } finally {
            entityManager.close();
        }
    }

    public boolean deletar(Integer id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            Emprestimo emprestimo = entityManager.find(Emprestimo.class, id);
            if (emprestimo == null) {
                return false;
            }
            transaction.begin();
            entityManager.remove(emprestimo);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Erro ao excluir empréstimo: " + e.getMessage());
            return false;
        } finally {
            entityManager.close();
        }
    }

    public Usuario buscarUsuarioPorId(Integer id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(Usuario.class, id);
        } catch (Exception e) {
            System.out.println("Erro ao buscar usuário: " + e.getMessage());
            return null;
        } finally {
            entityManager.close();
        }
    }

    public Livro buscarLivroPorIsbn(String isbn) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(Livro.class, isbn);
        } catch (Exception e) {
            System.out.println("Erro ao buscar livro: " + e.getMessage());
            return null;
        } finally {
            entityManager.close();
        }
    }
}
