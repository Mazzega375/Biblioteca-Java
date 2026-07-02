package br.edu.biblioteca.ui;

import br.edu.biblioteca.action.AcaoDevolver;
import br.edu.biblioteca.action.AcaoEmpresta;
import br.edu.biblioteca.model.Emprestimo;
import br.edu.biblioteca.model.Exemplar;
import br.edu.biblioteca.model.Livro;
import br.edu.biblioteca.model.Usuario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Tela de controle de empréstimos e devoluções.
 */
public class TelaEmprestimos {

    /** Prazo padrão de empréstimo em dias. */
    private static final int PRAZO_PADRAO_DIAS = 14;

    private final BibliotecaContext ctx;
    private final Scanner scanner;

    public TelaEmprestimos(BibliotecaContext ctx, Scanner scanner) {
        this.ctx     = ctx;
        this.scanner = scanner;
    }

    public void exibir() {
        boolean sair = false;
        while (!sair) {

            System.out.println("\n    - EMPRÉSTIMOS E DEVOLUÇÕES - ");
            System.out.println("  1. Realizar empréstimo      ");
            System.out.println("  2. Registrar devolução      ");
            System.out.println("  3. Ver empréstimos ativos   ");
            System.out.println("  4. Buscar por usuário       ");
            System.out.println("  5. Listar em atraso         ");
            System.out.println("  6. Detalhar empréstimo      ");
            System.out.println("  0. Voltar                   ");
            System.out.print("Opção: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> realizarEmprestimo();
                case "2" -> registrarDevolucao();
                case "3" -> listar(ctx.emprestimoRepo.listarTodos().stream()
                                        .filter(e -> !e.isDevolvido()).toList(),
                                   "Empréstimos Ativos");
                case "4" -> buscarPorUsuario();
                case "5" -> listarEmAtraso();
                case "6" -> detalharEmprestimo();
                case "0" -> sair = true;
                default  -> System.out.println("⚠  Opção inválida.");
            }
        }
    }

    // -------------------------------------------------------------------------

    private void realizarEmprestimo() {
        System.out.println("\n--- Novo Empréstimo ---");
        System.out.print("ID do usuário: ");
        int usuarioId = lerInt();
        Usuario usuario = ctx.usuarioRepo.buscarPorId(usuarioId);
        if (usuario == null)          { System.out.println("⚠  Usuário não encontrado.");  return; }
        if (usuario.isBloqueado())    { System.out.println("⚠  Usuário bloqueado.");        return; }

        System.out.print("ISBN do livro: ");
        String isbn = scanner.nextLine().trim();
        Livro livro = ctx.livroRepo.buscarPorIsbn(isbn);
        if (livro == null) { System.out.println("⚠  Livro não encontrado."); return; }

        Optional<Exemplar> exemplarOpt = ctx.exemplarRepo.buscarDisponivelPorIsbn(isbn);
        if (exemplarOpt.isEmpty()) {
            System.out.println("⚠  Nenhum exemplar disponível para esse ISBN.");
            System.out.println("   Deseja verificar a fila de reservas? (s/n): ");
            scanner.nextLine();
            return;
        }

        System.out.printf("Prazo de devolução em dias [%d]: ", PRAZO_PADRAO_DIAS);
        String prazoStr = scanner.nextLine().trim();
        int prazo = prazoStr.isEmpty() ? PRAZO_PADRAO_DIAS : Integer.parseInt(prazoStr);

        LocalDate hoje      = LocalDate.now();
        LocalDate devolucao = hoje.plusDays(prazo);

        Emprestimo emp = new Emprestimo(0, usuarioId, isbn, hoje, devolucao);
        Exemplar exemplar = exemplarOpt.get();

        ctx.executar(new AcaoEmpresta(ctx.emprestimoRepo, ctx.exemplarRepo, emp, exemplar));
        System.out.printf("  Devolução prevista: %s%n", devolucao);
    }

    private void registrarDevolucao() {
        System.out.println("\n--- Registrar Devolução ---");
        System.out.print("ID do empréstimo: ");
        int id = lerInt();
        Emprestimo emp = ctx.emprestimoRepo.buscarPorId(id);
        if (emp == null)          { System.out.println("⚠  Empréstimo não encontrado."); return; }
        if (emp.isDevolvido())    { System.out.println("⚠  Livro já devolvido.");        return; }

        // Busca o exemplar emprestado
        List<Exemplar> exemplares = ctx.exemplarRepo.buscarPorIsbn(emp.getIsbnLivro());
        Optional<Exemplar> exemplarOpt = exemplares.stream()
                .filter(e -> e.getStatus() == Exemplar.Status.EMPRESTADO)
                .findFirst();
        if (exemplarOpt.isEmpty()) {
            System.out.println("⚠  Exemplar correspondente não encontrado.");
            return;
        }

        ctx.executar(new AcaoDevolver(ctx.emprestimoRepo, ctx.exemplarRepo,
                emp, exemplarOpt.get(), LocalDate.now()));
    }

    private void buscarPorUsuario() {
        System.out.print("ID do usuário: ");
        int id = lerInt();
        listar(ctx.emprestimoRepo.buscarTodosDoUsuario(id), "Empréstimos do usuário " + id);
    }

    private void listarEmAtraso() {
        List<Emprestimo> atrasados = ctx.emprestimoRepo.listarEmAtraso();
        System.out.println("\n--- Empréstimos em Atraso (" + atrasados.size() + ") ---");
        if (atrasados.isEmpty()) {
            System.out.println("Nenhum empréstimo em atraso.");
        } else {
            atrasados.forEach(e -> {
                Usuario u = ctx.usuarioRepo.buscarPorId(e.getUsuarioId());
                String nomeUsuario = u != null ? u.getNome() : "ID " + e.getUsuarioId();
                System.out.printf("  %s | %s | Atraso: %d dia(s) | Multa: R$ %.2f%n",
                        e, nomeUsuario, e.diasAtraso(), e.calcularMulta());
            });
        }
    }

    private void detalharEmprestimo() {
        System.out.print("ID do empréstimo: ");
        int id = lerInt();
        Emprestimo emp = ctx.emprestimoRepo.buscarPorId(id);
        if (emp == null) { System.out.println("⚠  Não encontrado."); return; }
        System.out.println("\n  " + emp);
        Usuario u = ctx.usuarioRepo.buscarPorId(emp.getUsuarioId());
        if (u != null) System.out.println("  Usuário: " + u);
        Livro l = ctx.livroRepo.buscarPorIsbn(emp.getIsbnLivro());
        if (l != null) System.out.println("  Livro:   " + l);
    }

    private void listar(List<Emprestimo> lista, String titulo) {
        System.out.println("\n--- " + titulo + " (" + lista.size() + ") ---");
        if (lista.isEmpty()) System.out.println("  (nenhum registro)");
        else lista.forEach(e -> System.out.println("  " + e));
    }

    private int lerInt() {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}
