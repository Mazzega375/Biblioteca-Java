package br.unisales.menu;

import java.util.List;
import java.util.Scanner;

import br.unisales.database.table.Reserva;
import br.unisales.manager_factory.ManagerFactory;
import br.unisales.menu.util.MenuUtil;
import br.unisales.service.ReservaService;

public final class ReservaMenu {
    private final Scanner scanner;

    public ReservaMenu(Scanner scanner) {
        this.scanner = scanner;
        System.out.println("==========================================");
        System.out.println("    RESERVA     ");
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
        ReservaService reservaService = new ReservaService(emf.get());
        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1 -> cadastrar(reservaService);
                case 2 -> listar(reservaService);
                case 3 -> excluir(reservaService);
                case 4 -> atenderProximaReserva(reservaService);
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
        System.out.println("1 - Cadastrar reserva");
        System.out.println("2 - Listar reservas");
        System.out.println("3 - Cancelar reserva");
        System.out.println("4 - Atender próxima reserva");
        System.out.println("100 - Voltar");
        System.out.println("-------------------------------------");
    }

    /**
     * Atende a próxima reserva ativa para o livro informado pelo ISBN.
     */
    private void atenderProximaReserva(ReservaService reservaService) {
        MenuUtil.limparConsole();
        System.out.println("=== ATENDER PRÓXIMA RESERVA ===");
        String isbnLivro = this.lerTexto("Informe o ISBN do livro: ");
        if (!reservaService.atenderProximaReserva(isbnLivro)) {
            System.out.println("Não foi possível atender a próxima reserva para este ISBN.");
        }
    }

    /**
     * Realiza o cadastro de uma nova reserva.
     */
    private void cadastrar(ReservaService reservaService) {
        MenuUtil.limparConsole();
        System.out.println("=== CADASTRAR RESERVA ===");
        Integer usuarioId = this.lerInteiro("Informe o ID do usuário: ");
        String isbnLivro = this.lerTexto("Informe o ISBN do livro: ");
        Reserva item = new Reserva(null, usuarioId, isbnLivro, null, null);
        reservaService.inserir(item);
    }

    /**
     * Lista todas as reservas cadastradas.
     */
    private static void listar(ReservaService reservaService) {
        MenuUtil.limparConsole();
        System.out.println("=== LISTAR RESERVAS ===");
        List<Reserva> lista = reservaService.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma reserva cadastrada.");
            return;
        }
        for (Reserva item : lista) {
            System.out.println("-------------------------------------");
            System.out.println("ID: " + item.getId());
            System.out.println("Usuário ID: " + item.getUsuarioId());
            System.out.println("ISBN Livro: " + item.getIsbnLivro());
            System.out.println("Data Reserva: " + item.getDataReserva());
            System.out.println("Status: " + item.getStatus());
        }
        System.out.println("-------------------------------------");
    }

    /**
     * Exclui uma reserva pelo ID.
     */
    private void excluir(ReservaService reservaService) {
        MenuUtil.limparConsole();
        System.out.println("=== EXCLUIR RESERVA ===");
        Integer id = this.lerInteiro("Informe o ID da reserva que será excluída: ");
        String confirmacao = this.lerTexto("Deseja realmente excluir esta reserva? (S/N): ");
        if (confirmacao.equalsIgnoreCase("S")) {
            reservaService.deletar(id);
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