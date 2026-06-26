package br.edu.biblioteca.ui;

import br.edu.biblioteca.model.Emprestimo;
import br.edu.biblioteca.model.Livro;
import br.edu.biblioteca.model.Reserva;
import br.edu.biblioteca.model.Usuario;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Scanner;

/**
 * Tela de relatórios da biblioteca.
 * Gera visões consolidadas sobre o acervo, empréstimos, multas e usuários.
 */
public class TelaRelatorios {

    private final BibliotecaContext ctx;
    private final Scanner scanner;

    public TelaRelatorios(BibliotecaContext ctx, Scanner scanner) {
        this.ctx     = ctx;
        this.scanner = scanner;
    }

    public void exibir() {
        boolean sair = false;
        while (!sair) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║          RELATÓRIOS          ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║  1. Livros mais emprestados  ║");
            System.out.println("║  2. Usuários inadimplentes   ║");
            System.out.println("║  3. Empréstimos em atraso    ║");
            System.out.println("║  4. Resumo geral do acervo   ║");
            System.out.println("║  5. Resumo de reservas       ║");
            System.out.println("║  6. Histórico de ações       ║");
            System.out.println("║  7. Livros sem exemplares    ║");
            System.out.println("║  0. Voltar                   ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("Opção: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> relatorioLivrosMaisEmprestados();
                case "2" -> relatorioUsuariosInadimplentes();
                case "3" -> relatorioEmprestimosEmAtraso();
                case "4" -> relatorioResumoAcervo();
                case "5" -> relatorioResumoReservas();
                case "6" -> relatorioHistoricoAcoes();
                case "7" -> relatorioLivrosSemExemplares();
                case "0" -> sair = true;
                default  -> System.out.println("⚠  Opção inválida.");
            }
        }
    }

    // -------------------------------------------------------------------------

    private void relatorioLivrosMaisEmprestados() {
        System.out.println("\n════════ LIVROS MAIS EMPRESTADOS ════════");
        Map<String, Long> contagem = ctx.emprestimoRepo.contagemPorLivro();
        if (contagem.isEmpty()) { System.out.println("  (sem empréstimos registrados)"); return; }

        // Ordena por quantidade decrescente
        contagem.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .forEach(e -> {
                    Livro l = ctx.livroRepo.buscarPorIsbn(e.getKey());
                    String titulo = l != null ? l.getTitulo() : "(ISBN desconhecido)";
                    System.out.printf("  %-40s  %3d empréstimo(s)%n", titulo, e.getValue());
                });
    }

    private void relatorioUsuariosInadimplentes() {
        System.out.println("\n════════ USUÁRIOS INADIMPLENTES ════════");
        boolean encontrou = false;
        for (Usuario u : ctx.usuarioRepo.listarTodos()) {
            List<Emprestimo> atraso = ctx.emprestimoRepo.buscarAtivosDoUsuario(u.getId())
                    .stream().filter(Emprestimo::estaEmAtraso).collect(Collectors.toList());
            if (!atraso.isEmpty()) {
                encontrou = true;
                double totalMulta = atraso.stream().mapToDouble(Emprestimo::calcularMulta).sum();
                System.out.printf("  %s | Qtd em atraso: %d | Multa total: R$ %.2f%n",
                        u, atraso.size(), totalMulta);
            }
        }
        if (!encontrou) System.out.println("  ✔ Nenhum usuário inadimplente.");
    }

    private void relatorioEmprestimosEmAtraso() {
        System.out.println("\n════════ EMPRÉSTIMOS EM ATRASO ════════");
        List<Emprestimo> lista = ctx.emprestimoRepo.listarEmAtraso();
        if (lista.isEmpty()) { System.out.println("  ✔ Nenhum empréstimo em atraso."); return; }

        double totalMultas = 0;
        for (Emprestimo e : lista) {
            Usuario u = ctx.usuarioRepo.buscarPorId(e.getUsuarioId());
            String nome = u != null ? u.getNome() : "ID " + e.getUsuarioId();
            Livro l = ctx.livroRepo.buscarPorIsbn(e.getIsbnLivro());
            String titulo = l != null ? l.getTitulo() : e.getIsbnLivro();
            double multa = e.calcularMulta();
            totalMultas += multa;
            System.out.printf("  Emp#%d | %s | %s | Atraso: %d dia(s) | Multa: R$ %.2f%n",
                    e.getId(), nome, titulo, e.diasAtraso(), multa);
        }
        System.out.printf("%n  Total de multas em aberto: R$ %.2f%n", totalMultas);
    }

    private void relatorioResumoAcervo() {
        System.out.println("\n════════ RESUMO DO ACERVO ════════");
        int totalLivros      = ctx.livroRepo.quantidade();
        int totalExemplares  = ctx.exemplarRepo.quantidade();
        int totalUsuarios    = ctx.usuarioRepo.quantidade();
        int totalEmprestimos = ctx.emprestimoRepo.quantidade();
        int ativos = (int) ctx.emprestimoRepo.listarTodos().stream()
                .filter(e -> !e.isDevolvido()).count();
        int emAtraso = ctx.emprestimoRepo.listarEmAtraso().size();

        System.out.printf("  Títulos no acervo:          %d%n", totalLivros);
        System.out.printf("  Total de exemplares:        %d%n", totalExemplares);
        System.out.printf("  Usuários cadastrados:       %d%n", totalUsuarios);
        System.out.printf("  Total de empréstimos:       %d%n", totalEmprestimos);
        System.out.printf("  Empréstimos ativos:         %d%n", ativos);
        System.out.printf("  Empréstimos em atraso:      %d%n", emAtraso);

        // Distribuição por categoria
        System.out.println("\n  -- Livros por Categoria --");
        ctx.livroRepo.listarTodos().stream()
                .collect(Collectors.groupingBy(Livro::getCategoria, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .forEach(e -> System.out.printf("    %-25s %d%n", e.getKey(), e.getValue()));
    }

    private void relatorioResumoReservas() {
        System.out.println("\n════════ RESUMO DE RESERVAS ════════");
        List<Reserva> todas = ctx.reservaRepo.listarTodas();
        long aguardando = todas.stream().filter(r -> r.getStatus() == Reserva.Status.AGUARDANDO).count();
        long atendidas  = todas.stream().filter(r -> r.getStatus() == Reserva.Status.ATENDIDA).count();
        long canceladas = todas.stream().filter(r -> r.getStatus() == Reserva.Status.CANCELADA).count();

        System.out.printf("  Total de reservas:   %d%n", todas.size());
        System.out.printf("  Aguardando:          %d%n", aguardando);
        System.out.printf("  Atendidas:           %d%n", atendidas);
        System.out.printf("  Canceladas:          %d%n", canceladas);

        if (aguardando > 0) {
            System.out.println("\n  -- ISBNs com maior fila de espera --");
            ctx.reservaRepo.filaDeEspera().stream()
                    .collect(Collectors.groupingBy(Reserva::getIsbnLivro, Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                    .limit(5)
                    .forEach(e -> {
                        Livro l = ctx.livroRepo.buscarPorIsbn(e.getKey());
                        String titulo = l != null ? l.getTitulo() : e.getKey();
                        System.out.printf("    %-35s %d na fila%n", titulo, e.getValue());
                    });
        }
    }

    private void relatorioHistoricoAcoes() {
        System.out.println("\n════════ HISTÓRICO DE AÇÕES ════════");
        System.out.printf("  Total de ações registradas: %d%n", ctx.tamanhoHistorico());
        System.out.println("  (mais recente primeiro)");
        ctx.imprimirHistorico();
    }

    private void relatorioLivrosSemExemplares() {
        System.out.println("\n════════ LIVROS SEM EXEMPLARES ════════");
        boolean encontrou = false;
        for (Livro l : ctx.livroRepo.listarTodos()) {
            if (ctx.exemplarRepo.contarTotal(l.getIsbn()) == 0) {
                System.out.println("  " + l);
                encontrou = true;
            }
        }
        if (!encontrou) System.out.println("  ✔ Todos os livros possuem ao menos um exemplar.");
    }
}
