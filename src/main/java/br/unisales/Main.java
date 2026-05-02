package br.unisales;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.unisales.menu.CategoriaMenu;
import br.unisales.menu.LivroMenu;
import br.unisales.menu.MultaMenu;
import br.unisales.menu.RelatorioMenu;
import br.unisales.menu.ReservaMenu;
import br.unisales.menu.UsuarioMenu;
import br.unisales.menu.util.MenuUtil;

public class Main {

    private static Scanner scanner;

    public static void main(String[] args) {
        // MenuUtil.configurarConsoleUtf8();
        configurarLogs();
        scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        System.out.println("==========================================");
        System.out.println("       BIBLIOTECA       ");
        System.out.println("==========================================");
        int opcao;

        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opcao: ");

            switch (opcao) {
                case 1 -> menuUsuario();
                case 2 -> menuLivro();
                case 3 -> menuAutor();
                case 4 -> menuCategoria();
                case 5 -> menuEmprestimo();
                case 6 -> menuReserva();
                case 7 -> menuMulta();
                case 8 -> menuRelatorio();
                case 0 -> System.out.println("Encerrando o sistema...");
                default -> System.out.println("Opcao invalida. Tente novamente.");
            }
            System.out.println();
        } while (opcao != 0);

        scanner.close();
    }

    private static void configurarLogs() {
        Logger hibernateLogger = Logger.getLogger("org.hibernate");
        hibernateLogger.setLevel(Level.SEVERE);
        hibernateLogger.setUseParentHandlers(false);

        Logger jbossLogger = Logger.getLogger("org.jboss");
        jbossLogger.setLevel(Level.SEVERE);
        jbossLogger.setUseParentHandlers(false);
    }

    private static void exibirMenu() {
        System.out.println("--------------- MENU ----------------");
        System.out.println("1 - Usuário");
        System.out.println("2 - Livro");
        System.out.println("3 - Autor");
        System.out.println("4 - Categoria");
        System.out.println("5 - Empréstimo");
        System.out.println("6 - Reserva");
        System.out.println("7 - Multa");
        System.out.println("8 - Relatório");
        System.out.println("0 - Sair");
        System.out.println("-------------------------------------");
    }

    private static void menuCategoria() {
        MenuUtil.limparConsole();
        new CategoriaMenu(scanner);
    }

    private static void menuUsuario() {
        MenuUtil.limparConsole();
        new UsuarioMenu(scanner);
    }

    private static void menuLivro() {
        MenuUtil.limparConsole();
        new LivroMenu(scanner);
    }

    private static void menuAutor() {
        MenuUtil.limparConsole();
        new br.unisales.menu.AutorMenu(scanner);
    }

    private static void menuEmprestimo() {
        MenuUtil.limparConsole();
        new br.unisales.menu.EmprestimoMenu(scanner);
    }

    private static void menuReserva() {
        MenuUtil.limparConsole();
        new ReservaMenu(scanner);
    }

    private static void menuMulta() {
        MenuUtil.limparConsole();
        new MultaMenu(scanner);
    }

    private static void menuRelatorio() {
        MenuUtil.limparConsole();
        new RelatorioMenu(scanner);
    }

    private static Integer lerInteiro(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido. Digite um numero inteiro.");
            }
        }
    }
}
