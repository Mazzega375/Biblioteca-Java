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
        System.out.println("    AUTOR     ");
        System.out.println("==========================================");
        /*
         * Cria a fábrica de EntityManager com base na persistence-unit
         * definida no arquivo persistence.xml.
         *
         * Troque "SQLitePU" por:
         * - "MySQLPU"
         * - "PostgresPU"
         * - "SqlServerPU"
         * conforme o banco desejado.
         */
        ManagerFactory emf = new ManagerFactory("SQLitePU");
        AutorService autorService = new AutorService(emf.get());
        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1 -> cadastrar(autorService);
                case 2 -> listar(autorService);
                case 3 -> buscarPorId(autorService);
                case 4 -> excluir(autorService);
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
        System.out.println("--------------- MENU ----------------");
        System.out.println("1 - Cadastrar autor");
        System.out.println("2 - Listar autores");
        System.out.println("3 - Buscar autor por ID");
        System.out.println("4 - Atualizar autor");
        System.out.println("5 - Excluir autor");
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
     * Busca um autor pelo ID.
     */
    private void buscarPorId(AutorService autorService) {
        MenuUtil.limparConsole();
        System.out.println("=== BUSCAR AUTOR POR ID ===");
        Integer id = this.lerInteiro("Informe o ID do autor: ");
        Autor item = autorService.buscarPorId(id);
        if (item == null) {
            System.out.println("Autor não encontrado.");
            return;
        }
        System.out.println("Autor encontrado:");
        System.out.println("-------------------------------------");
        System.out.println("ID: " + item.getId());
        System.out.println("Nome: " + item.getNome());
        System.out.println("-------------------------------------");
    }

    /**
     * Exclui um autor pelo ID.
     */
    private void excluir(AutorService autorService) {
        MenuUtil.limparConsole();
        System.out.println("=== EXCLUIR AUTOR ===");
        Integer id = this.lerInteiro("Informe o ID do autor que será excluído: ");
        Autor item = autorService.buscarPorId(id);
        if (item == null) {
            System.out.println("Autor não encontrado.");
            return;
        }
        System.out.println("Autor localizado:");
        System.out.println("Nome: " + item.getNome());
        String confirmacao = this.lerTexto("Deseja realmente excluir este autor? (S/N): ");
        if (confirmacao.equalsIgnoreCase("S")) {
            autorService.deletar(id);
        } else {
            System.out.println("Exclusão cancelada.");
        }
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
