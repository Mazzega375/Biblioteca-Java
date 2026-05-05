package br.unisales.menu;

import java.util.List;
import java.util.Scanner;

import br.unisales.database.table.Autor;
import br.unisales.manager_factory.ManagerFactory;
import br.unisales.menu.util.MenuUtil;
import br.unisales.service.AutorService;

public final class AutorMenu {
    private final Scanner scanner;

    public AutorMenu(Scanner scanner) {
        this.scanner = scanner;
        System.out.println("==========================================");
        System.out.println("    AUTOR MENU     ");
        System.out.println("==========================================");
        
        
        ManagerFactory emf = new ManagerFactory("SQLitePU");
        AutorService autorService = new AutorService(emf.get());
        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1 -> cadastrar(autorService);
                case 2 -> listar(autorService);
                case 100 -> System.out.println("Voltando para o menu principal...");
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
            System.out.println();
        } while (opcao != 100);
        emf.close();
    }

    /**
     * Exibe o menu principal do sistema.
     */
    private static void exibirMenu() {
        
        System.out.println("1 - Cadastrar autor");
        System.out.println("2 - Listar autores");
        System.out.println("100 - Voltar");
        System.out.println("-------------------------------------");
    }

    /**
     * Realiza o cadastro de um novo autor.
     */
    private void cadastrar(AutorService autorService) {
        MenuUtil.limparConsole();
        System.out.println("=== CADASTRAR AUTOR ===");
        String nome = this.lerTexto("Informe o nome: ");
        Autor item = new Autor(null, nome, null);
        autorService.inserir(item);
    }

    /**
     * Lista todos os autores cadastrados.
     */
    private static void listar(AutorService autorService) {
        MenuUtil.limparConsole();
        System.out.println("=== LISTAR AUTORES ===");
        List<Autor> lista = autorService.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum autor cadastrado.");
            return;
        }
        for (Autor item : lista) {
            System.out.println("-------------------------------------");
            System.out.println("Nome: " + item.getNome());
        }
        System.out.println("-------------------------------------");
    }

    /**
     * Lê um número inteiro digitado pelo usuário.
     */
    private Integer lerInteiro(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return Integer.parseInt(this.scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite um número inteiro.");
            }
        }
    }

    /**
     * Lê um texto digitado pelo usuário.
     */
    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return this.scanner.nextLine();
    }

}
