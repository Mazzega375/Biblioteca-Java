package br.edu.biblioteca.ui;

import java.util.Scanner;

/**
 * Menu principal do Sistema de Biblioteca.
 *
 * Ponto de entrada da interface console. Instancia o contexto global
 * (repositórios + pilha de histórico) e navega entre as telas.
 *
 * Estruturas de dados em uso:
 *   Lista    → repositórios de livros, usuários, empréstimos, exemplares
 *   Fila     → fila FIFO de reservas (ReservaRepository)
 *   Pilha    → histórico de ações / undo (BibliotecaContext)
 *   Árvore   → TreeMap nos repositórios para busca O(log n)
 *   Ordenação→ Comparator em LivroRepository (título, autor, ano)
 */
public class MenuPrincipal {

    private final BibliotecaContext ctx = new BibliotecaContext();
    private final Scanner scanner = new Scanner(System.in);

    // Sub-telas
    private final TelaCatalogo    telaCatalogo;
    private final TelaUsuarios    telaUsuarios;
    private final TelaEmprestimos telaEmprestimos;
    private final TelaReservas    telaReservas;
    private final TelaRelatorios  telaRelatorios;

    public MenuPrincipal() {
        this.telaCatalogo    = new TelaCatalogo(ctx, scanner);
        this.telaUsuarios    = new TelaUsuarios(ctx, scanner);
        this.telaEmprestimos = new TelaEmprestimos(ctx, scanner);
        this.telaReservas    = new TelaReservas(ctx, scanner);
        this.telaRelatorios  = new TelaRelatorios(ctx, scanner);
    }

    /** Inicia o loop principal da aplicação. */
    public void iniciar() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     SISTEMA DE BIBLIOTECA  v2.0     ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.printf("  Livros: %d  |  Usuários: %d%n",
                ctx.livroRepo.quantidade(), ctx.usuarioRepo.quantidade());

        boolean sair = false;
        while (!sair) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║         MENU PRINCIPAL       ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║  1. Catálogo de Livros       ║");
            System.out.println("║  2. Usuários                 ║");
            System.out.println("║  3. Empréstimos e Devoluções ║");
            System.out.println("║  4. Reservas                 ║");
            System.out.println("║  5. Relatórios               ║");
            System.out.println("║  6. Desfazer última ação     ║");
            System.out.println("║  0. Sair                     ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("Opção: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> telaCatalogo.exibir();
                case "2" -> telaUsuarios.exibir();
                case "3" -> telaEmprestimos.exibir();
                case "4" -> telaReservas.exibir();
                case "5" -> telaRelatorios.exibir();
                case "6" -> desfazerUltimaAcao();
                case "0" -> sair = confirmarSaida();
                default  -> System.out.println("⚠  Opção inválida.");
            }
        }

        System.out.println("\nSistema encerrado. Até logo!");
        scanner.close();
    }

    private void desfazerUltimaAcao() {
        if (ctx.tamanhoHistorico() == 0) {
            System.out.println("⚠  Nenhuma ação para desfazer.");
            return;
        }
        System.out.println("Última ação: " + ctx.descricaoUltimaAcao());
        System.out.print("Confirmar desfazer? (s/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
            ctx.desfazerUltima();
        }
    }

    private boolean confirmarSaida() {
        System.out.print("Deseja sair do sistema? (s/n): ");
        return scanner.nextLine().trim().equalsIgnoreCase("s");
    }

    // -------------------------------------------------------------------------
    // Ponto de entrada standalone
    // -------------------------------------------------------------------------

    /**
     * Método main para executar o sistema de biblioteca diretamente
     * (sem depender do Main.java existente).
     */
    public static void main(String[] args) {
        new MenuPrincipal().iniciar();
    }
}
