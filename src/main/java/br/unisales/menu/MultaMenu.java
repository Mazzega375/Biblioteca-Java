package br.unisales.menu;

import br.unisales.database.table.Multa;
import br.unisales.manager_factory.ManagerFactory;
import br.unisales.menu.util.MenuUtil;
import br.unisales.service.MultaService;

import java.util.List;
import java.util.Scanner;

public final class MultaMenu {

    private final Scanner scanner;

    public MultaMenu(Scanner scanner) {
        this.scanner = scanner;
        System.out.println("==========================================");
        System.out.println("         MULTA MENU         ");
        System.out.println("==========================================");

        ManagerFactory emf = new ManagerFactory("SQLitePU");
        MultaService multaService = new MultaService(emf.get());

        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1 -> listarTodas(multaService);
                case 2 -> listarPendentes(multaService);
                case 3 -> buscarPorEmprestimo(multaService);
                case 4 -> quitarMulta(multaService);
                case 100 -> System.out.println("Voltando para o menu principal...");
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
            System.out.println();
        } while (opcao != 100);

        emf.close();
    }

    private static void exibirMenu() {
        System.out.println("--------------- MULTA MENU ----------------");
        System.out.println("1 - Listar multas");
        System.out.println("2 - Listar multas pendentes");
        System.out.println("3 - Buscar multa por empréstimo");
        System.out.println("4 - Quitar multa");
        System.out.println("100 - Voltar");
        System.out.println("-------------------------------------");
    }

    private static void listarTodas(MultaService multaService) {
        MenuUtil.limparConsole();
        System.out.println("=== LISTAR MULTAS ===");

        List<Multa> multas = multaService.listarTodos();
        if (multas.isEmpty()) {
            System.out.println("Nenhuma multa registrada.");
            return;
        }

        for (Multa multa : multas) {
            imprimirMulta(multa);
            System.out.println("-------------------------------------");
        }
    }

    private static void listarPendentes(MultaService multaService) {
        MenuUtil.limparConsole();
        System.out.println("=== MULTAS PENDENTES ===");

        List<Multa> multas = multaService.listarPendentes();
        if (multas.isEmpty()) {
            System.out.println("Nenhuma multa pendente.");
            return;
        }

        for (Multa multa : multas) {
            imprimirMulta(multa);
            System.out.println("-------------------------------------");
        }
    }

    private void buscarPorEmprestimo(MultaService multaService) {
        MenuUtil.limparConsole();
        System.out.println("=== BUSCAR MULTA POR EMPRÉSTIMO ===");

        Integer emprestimoId = lerInteiro("Informe o ID do empréstimo: ");
        Multa multa = multaService.buscarPorEmprestimoId(emprestimoId);

        if (multa == null) {
            System.out.println("Nenhuma multa encontrada para este empréstimo.");
            return;
        }

        imprimirMulta(multa);
    }

    private void quitarMulta(MultaService multaService) {
        MenuUtil.limparConsole();
        System.out.println("=== QUITAR MULTA ===");

        Integer multaId = lerInteiro("Informe o ID da multa: ");
        if (multaService.quitarMulta(multaId)) {
            System.out.println("Multa quitada com sucesso.");
        } else {
            System.out.println("Multa não encontrada.");
        }
    }

    private static void imprimirMulta(Multa multa) {
        System.out.println("ID: " + multa.getId());
        System.out.println("Empréstimo ID: " + multa.getEmprestimoId());
        System.out.printf("Valor: R$ %.2f%n", multa.getValor());
        System.out.println("Dias de atraso: " + multa.getDiasAtraso());
        System.out.println("Quitada: " + (Boolean.TRUE.equals(multa.getQuitada()) ? "Sim" : "Não"));
    }

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
}