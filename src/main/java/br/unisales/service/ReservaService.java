package br.unisales.service;

import br.unisales.database.table.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.List;

public class ReservaService {

    private final EntityManagerFactory entityManagerFactory;

    public ReservaService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void inserir(Reserva reserva) {
        reserva.setDataReserva(LocalDate.now());
        reserva.setStatus("RESERVADO");

        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            entityManager.persist(reserva);
            transaction.commit();
            System.out.println("Reserva inserida com sucesso.");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            Throwable causa = e;
            while (causa.getCause() != null) {
                causa = causa.getCause();
            }

            System.out.println("Erro ao inserir reserva: " + causa.getMessage());
        } finally {
            entityManager.close();
        }
    }

    public List<Reserva> listarTodos() {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            return entityManager
                    .createQuery("SELECT r FROM Reserva r ORDER BY r.id", Reserva.class)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Erro ao listar reservas: " + e.getMessage());
            return List.of();
        } finally {
            entityManager.close();
        }
    }

    public void deletar(Integer id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            Reserva reserva = entityManager.find(Reserva.class, id);
            if (reserva == null) {
                System.out.println("Reserva nao encontrada para exclusao.");
                return;
            }

            transaction.begin();
            entityManager.remove(reserva);
            transaction.commit();
            System.out.println("Reserva removida com sucesso.");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Erro ao remover reserva: " + e.getMessage());
        } finally {
            entityManager.close();
        }
    }
}