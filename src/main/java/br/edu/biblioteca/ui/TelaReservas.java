package br.edu.biblioteca.ui;

import br.edu.biblioteca.action.AcaoCancelarReserva;
import br.edu.biblioteca.action.AcaoReservar;
import br.edu.biblioteca.model.Livro;
import br.edu.biblioteca.model.Reserva;
import br.edu.biblioteca.model.Usuario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Tela de gerenciamento de reservas de livros.
 * A fila de reservas é FIFO: quem reservou primeiro tem prioridade.
 */
public class TelaReservas {

    private final BibliotecaContext ctx;
    private final Scanner scanner;

    public TelaReservas(BibliotecaContext ctx, Scanner scanner) {
        this.ctx     = ctx;
        this.scanner = scanner;
    }

    public void exibir() {
        boolean sair = false;
        while (!sair) {
            System.out.println("\n      - RESERVAS DE LIVROS -     ");
            System.out.println("  1. Fazer reserva            ");
            System.out.println("  2. Cancelar reserva         ");
            System.out.println("  3. Fila de espera por ISBN  ");
            System.out.println("  4. Minhas reservas          ");
            System.out.println("  5. Atender próxima reserva  ");
            System.out.println("  6. Listar todas as reservas ");
            System.out.println("  0. Voltar                   ");
            System.out.print("Opção: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> fazerReserva();
                case "2" -> cancelarReserva();
                case "3" -> filaPorIsbn();
                case "4" -> reservasDoUsuario();
                case "5" -> atenderProxima();
                case "6" -> listar(ctx.reservaRepo.listarTodas(), "Todas as Reservas");
                case "0" -> sair = true;
                default  -> System.out.println("⚠  Opção inválida.");
            }
        }
    }

    // -------------------------------------------------------------------------

    private void fazerReserva() {
        System.out.println("\n--- Nova Reserva ---");
        System.out.print("ID do usuário: ");
        int usuarioId = lerInt();
        Usuario u = ctx.usuarioRepo.buscarPorId(usuarioId);
        if (u == null)       { System.out.println("⚠  Usuário não encontrado.");  return; }
        if (u.isBloqueado()) { System.out.println("⚠  Usuário bloqueado.");        return; }

        System.out.print("ISBN do livro: ");
        String isbn = scanner.nextLine().trim();
        Livro livro = ctx.livroRepo.buscarPorIsbn(isbn);
        if (livro == null)   { System.out.println("⚠  Livro não encontrado."); return; }

        // Verifica se já tem reserva ativa para esse usuário + ISBN
        boolean jaReservou = ctx.reservaRepo.buscarPorUsuario(usuarioId).stream()
                .anyMatch(r -> r.getIsbnLivro().equals(isbn)
                            && r.getStatus() == Reserva.Status.AGUARDANDO);
        if (jaReservou) {
            System.out.println("⚠  Você já possui uma reserva ativa para este livro.");
            return;
        }

        Reserva reserva = new Reserva(0, usuarioId, isbn, LocalDate.now());
        ctx.executar(new AcaoReservar(ctx.reservaRepo, reserva));

        long posicao = ctx.reservaRepo.tamanhoFilaPorIsbn(isbn);
        System.out.printf("  Posição na fila de espera: %d°%n", posicao);
    }

    private void cancelarReserva() {
        System.out.print("ID da reserva: ");
        int id = lerInt();
        Reserva reserva = ctx.reservaRepo.buscarPorId(id);
        if (reserva == null)                              { System.out.println("⚠  Reserva não encontrada."); return; }
        if (reserva.getStatus() != Reserva.Status.AGUARDANDO) {
            System.out.println("⚠  Reserva já foi " + reserva.getStatus() + ".");
            return;
        }
        ctx.executar(new AcaoCancelarReserva(ctx.reservaRepo, reserva));
    }

    private void filaPorIsbn() {
        System.out.print("ISBN do livro: ");
        String isbn = scanner.nextLine().trim();
        List<Reserva> fila = ctx.reservaRepo.buscarAguardandoPorIsbn(isbn);
        System.out.println("\n--- Fila de Espera para ISBN " + isbn + " (" + fila.size() + ") ---");
        if (fila.isEmpty()) {
            System.out.println("  (sem reservas ativas)");
        } else {
            int pos = 1;
            for (Reserva r : fila) {
                Usuario u = ctx.usuarioRepo.buscarPorId(r.getUsuarioId());
                String nome = u != null ? u.getNome() : "ID " + r.getUsuarioId();
                System.out.printf("  %d° → %s (reservado em %s)%n", pos++, nome, r.getDataReserva());
            }
        }
    }

    private void reservasDoUsuario() {
        System.out.print("ID do usuário: ");
        int id = lerInt();
        listar(ctx.reservaRepo.buscarPorUsuario(id), "Reservas do usuário " + id);
    }

    private void atenderProxima() {
        System.out.print("ISBN do livro: ");
        String isbn = scanner.nextLine().trim();
        Optional<Reserva> opt = ctx.reservaRepo.atenderProximaReserva(isbn);
        if (opt.isEmpty()) {
            System.out.println("⚠  Sem reservas na fila para esse ISBN.");
        } else {
            Reserva r = opt.get();
            Usuario u = ctx.usuarioRepo.buscarPorId(r.getUsuarioId());
            String nome = u != null ? u.getNome() : "ID " + r.getUsuarioId();
            System.out.printf("Reserva atendida → Notificar usuário: %s (%s)%n", nome, u != null ? u.getEmail() : "");
        }
    }

    private void listar(List<Reserva> lista, String titulo) {
        System.out.println("\n--- " + titulo + " (" + lista.size() + ") ---");
        if (lista.isEmpty()) System.out.println("  (nenhuma reserva)");
        else lista.forEach(r -> System.out.println("  " + r));
    }

    private int lerInt() {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}
