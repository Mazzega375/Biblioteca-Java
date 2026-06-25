package br.edu.biblioteca.ui;

import br.edu.biblioteca.model.Usuario;

import java.util.List;
import java.util.Scanner;

/**
 * Tela de gerenciamento de usuários (alunos, professores, bibliotecários).
 */
public class TelaUsuarios {

    private final BibliotecaContext ctx;
    private final Scanner scanner;

    public TelaUsuarios(BibliotecaContext ctx, Scanner scanner) {
        this.ctx     = ctx;
        this.scanner = scanner;
    }

    public void exibir() {
        boolean sair = false;
        while (!sair) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║       GESTÃO DE USUÁRIOS     ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║  1. Cadastrar usuário        ║");
            System.out.println("║  2. Remover usuário          ║");
            System.out.println("║  3. Buscar por ID            ║");
            System.out.println("║  4. Buscar por nome          ║");
            System.out.println("║  5. Listar todos             ║");
            System.out.println("║  6. Listar bloqueados        ║");
            System.out.println("║  7. Bloquear / desbloquear   ║");
            System.out.println("║  0. Voltar                   ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("Opção: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> cadastrarUsuario();
                case "2" -> removerUsuario();
                case "3" -> buscarPorId();
                case "4" -> buscarPorNome();
                case "5" -> listar(ctx.usuarioRepo.listarTodos(), "Todos os Usuários");
                case "6" -> listar(ctx.usuarioRepo.listarBloqueados(), "Usuários Bloqueados");
                case "7" -> alternarBloqueio();
                case "0" -> sair = true;
                default  -> System.out.println("⚠  Opção inválida.");
            }
        }
    }

    // -------------------------------------------------------------------------

    private void cadastrarUsuario() {
        System.out.println("\n--- Cadastrar Usuário ---");
        System.out.print("Nome: ");  String nome  = scanner.nextLine().trim();
        System.out.print("Email: "); String email = scanner.nextLine().trim();
        System.out.println("Tipo: 1-ALUNO  2-PROFESSOR  3-BIBLIOTECARIO");
        System.out.print("Tipo: ");
        Usuario.Tipo tipo = switch (scanner.nextLine().trim()) {
            case "2" -> Usuario.Tipo.PROFESSOR;
            case "3" -> Usuario.Tipo.BIBLIOTECARIO;
            default  -> Usuario.Tipo.ALUNO;
        };

        Usuario u = new Usuario(0, nome, email, tipo);
        ctx.usuarioRepo.salvar(u);
        System.out.println("✔ Usuário cadastrado: " + u);
    }

    private void removerUsuario() {
        System.out.print("ID do usuário: ");
        int id = lerInt();
        Usuario u = ctx.usuarioRepo.buscarPorId(id);
        if (u == null) { System.out.println("⚠  Usuário não encontrado."); return; }
        System.out.println("Usuário: " + u);
        System.out.print("Confirmar remoção? (s/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
            ctx.usuarioRepo.remover(id);
            System.out.println("✔ Usuário removido.");
        }
    }

    private void buscarPorId() {
        System.out.print("ID: ");
        int id = lerInt();
        Usuario u = ctx.usuarioRepo.buscarPorId(id);
        if (u == null) System.out.println("⚠  Não encontrado.");
        else           exibirDetalhes(u);
    }

    private void buscarPorNome() {
        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();
        listar(ctx.usuarioRepo.buscarPorNome(nome), "Resultado da busca");
    }

    private void alternarBloqueio() {
        System.out.print("ID do usuário: ");
        int id = lerInt();
        Usuario u = ctx.usuarioRepo.buscarPorId(id);
        if (u == null) { System.out.println("⚠  Usuário não encontrado."); return; }
        u.setBloqueado(!u.isBloqueado());
        ctx.usuarioRepo.salvar(u);
        System.out.println(u.isBloqueado()
                ? "🔒 Usuário bloqueado: " + u.getNome()
                : "🔓 Usuário desbloqueado: " + u.getNome());
    }

    private void exibirDetalhes(Usuario u) {
        System.out.println("\n  " + u);
        var emprestimos = ctx.emprestimoRepo.buscarAtivosDoUsuario(u.getId());
        System.out.println("  Empréstimos ativos: " + emprestimos.size());
        double totalMulta = emprestimos.stream()
                .mapToDouble(e -> e.calcularMulta())
                .sum();
        if (totalMulta > 0) {
            System.out.printf("  ⚠  Multa pendente: R$ %.2f%n", totalMulta);
        }
    }

    private void listar(List<Usuario> usuarios, String titulo) {
        System.out.println("\n--- " + titulo + " (" + usuarios.size() + ") ---");
        if (usuarios.isEmpty()) System.out.println("  (nenhum usuário)");
        else usuarios.forEach(u -> System.out.println("  " + u));
    }

    private int lerInt() {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}
