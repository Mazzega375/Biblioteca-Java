package br.unisales.service;

import br.unisales.database.table.Livro;
import br.unisales.database.table.Autor;
import br.unisales.database.table.Exemplar;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class LivroService {

    private final EntityManagerFactory entityManagerFactory;

    public LivroService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void inserir(Livro livro) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            entityManager.persist(livro);
            transaction.commit();
            System.out.println("Livro inserido com sucesso.");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            Throwable causa = e;
            while (causa.getCause() != null) {
                causa = causa.getCause();
            }

            System.out.println("Erro ao inserir livro: " + causa.getMessage());
        } finally {
            entityManager.close();
        }
    }

    public List<Livro> listarTodos() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            List<Livro> livros = entityManager
                    .createQuery("SELECT l FROM Livro l ORDER BY l.titulo", Livro.class)
                    .getResultList();
            livros.forEach(this::inicializarColecoes);
            return livros;
        } catch (Exception e) {
            System.out.println("Erro ao listar livros: " + e.getMessage());
            return List.of();
        } finally {
            entityManager.close();
        }
    }

    public Livro buscarPorIsbn(String isbn) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            Livro livro = entityManager.find(Livro.class, isbn);
            if (livro != null) {
                inicializarColecoes(livro);
            }
            return livro;
        } catch (Exception e) {
            System.out.println("Erro ao buscar livro por ISBN: " + e.getMessage());
            return null;
        } finally {
            entityManager.close();
        }
    }

    public void atualizar(Livro livro) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(livro);
            transaction.commit();
            System.out.println("Livro atualizado com sucesso.");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Erro ao atualizar livro: " + e.getMessage());
        } finally {
            entityManager.close();
        }
    }

    public void deletar(String isbn) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            Livro livro = entityManager.find(Livro.class, isbn);
            if (livro == null) {
                System.out.println("Livro não encontrado para exclusão.");
                return;
            }

            transaction.begin();
            entityManager.remove(livro);
            transaction.commit();
            System.out.println("Livro removido com sucesso.");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Erro ao remover livro: " + e.getMessage());
        } finally {
            entityManager.close();
        }
    }

    public List<Livro> buscarPorTitulo(String titulo) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            List<Livro> livros = entityManager
                    .createQuery("SELECT l FROM Livro l WHERE l.titulo LIKE :titulo ORDER BY l.titulo", Livro.class)
                    .setParameter("titulo", "%" + titulo + "%")
                    .getResultList();
            livros.forEach(this::inicializarColecoes);
            return livros;
        } catch (Exception e) {
            System.out.println("Erro ao buscar livro por título: " + e.getMessage());
            return List.of();
        } finally {
            entityManager.close();
        }
    }

    public List<Livro> buscarPorAno(Integer ano) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            List<Livro> livros = entityManager
                    .createQuery("SELECT l FROM Livro l WHERE l.ano = :ano ORDER BY l.titulo", Livro.class)
                    .setParameter("ano", ano)
                    .getResultList();
            livros.forEach(this::inicializarColecoes);
            return livros;
        } catch (Exception e) {
            System.out.println("Erro ao buscar livro por ano: " + e.getMessage());
            return List.of();
        } finally {
            entityManager.close();
        }
    }

    public Autor buscarAutorPorId(Integer id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(Autor.class, id);
        } catch (Exception e) {
            System.out.println("Erro ao buscar autor: " + e.getMessage());
            return null;
        } finally {
            entityManager.close();
        }
    }

    public List<Autor> listarTodosAutores() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            return entityManager
                    .createQuery("SELECT a FROM Autor a ORDER BY a.nome", Autor.class)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Erro ao listar autores: " + e.getMessage());
            return List.of();
        } finally {
            entityManager.close();
        }
    }
    public List<Exemplar> listarExemplaresPorLivro(String isbn) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            return entityManager
                    .createQuery("SELECT e FROM Exemplar e WHERE e.isbnLivro = :isbn", Exemplar.class)
                    .setParameter("isbn", isbn)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Erro ao listar exemplares: " + e.getMessage());
            return List.of();
        } finally {
            entityManager.close();
        }
    }

    private void inicializarColecoes(Livro livro) {
        if (livro == null) {
            return;
        }
        livro.getPalavrasChave().size();
        livro.getLivroAutores().size();
        livro.getLivroCategorias().size();
    }
}
