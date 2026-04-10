package br.unisales.menu;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import br.unisales.database.table.Emprestimo;
import br.unisales.database.table.Livro;
import br.unisales.database.table.Usuario;
import br.unisales.manager_factory.ManagerFactory;
import br.unisales.menu.util.MenuUtil;
import br.unisales.service.EmprestimoService;

public final class EmprestimoMenu {

    private final Scanner scanner;
    private final EmprestimoService emprestimoService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public EmprestimoMenu(Scanner scanner) {
        this.scanner = scanner;
        System.out.println("==========================================");
        System.out.println("       EMPRÉSTIMO       ");
        System.out.println("==========================================");

        ManagerFactory emf = new ManagerFactory("SQLitePU");
        this.emprestimoService = new EmprestimoService(emf.get());

        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opção: ");
            switch (opcao) {
                case 1 -> emprestarExemplar();
                case 2 -> devolverExemplar();
                case 3 -> renovar();
                case 4 -> calcularMultaMenu();
                case 5 -> listar();
                case 6 -> buscarPorId();
                case 7 -> atualizar();
                case 8 -> excluir();
                case 100 -> System.out.println("Voltando para o menu principal...");
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
            System.out.println();
        } while (opcao != 100);

        emf.close();
    }

    private static void exibirMenu() {
        System.out.println("--------------- MENU ----------------");
        System.out.println("1 - Emprestar exemplar");
        System.out.println("2 - Devolver exemplar");
        System.out.println("3 - Renovar empréstimo");
        System.out.println("4 - Calcular multa");
        System.out.println("5 - Listar empréstimos");
        System.out.println("6 - Buscar empréstimo por ID");
        System.out.println("7 - Atualizar empréstimo");
        System.out.println("8 - Excluir empréstimo");
        System.out.println("100 - Voltar");
        System.out.println("-------------------------------------");
    }

    private void emprestarExemplar() {
        MenuUtil.limparConsole();
        System.out.println("=== EMPRESTAR EXEMPLAR ===");

        Integer usuarioId = lerInteiro("Informe o ID do usuário: ");
        Integer exemplarId = lerInteiro("Informe o ID do exemplar: ");
        String isbn = lerTexto("Informe o ISBN do livro: ");
        LocalDate dataPrevista = lerData("Informe a data prevista de devolução (dd/MM/yyyy): ");

        if (emprestimoService.emprestarExemplar(usuarioId, exemplarId, isbn, dataPrevista)) {
            System.out.println("Empréstimo registrado com sucesso.");
        } else {
            System.out.println("Falha ao registrar empréstimo. Verifique os dados informados.");
        }
    }

    private void listar() {
        MenuUtil.limparConsole();
        System.out.println("=== LISTAR EMPRÉSTIMOS ===");

        List<Emprestimo> lista = emprestimoService.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum empréstimo encontrado.");
            return;
        }

        for (Emprestimo item : lista) {
            imprimirEmprestimo(item);
            System.out.println("-------------------------------------");
        }
    }

    private void buscarPorId() {
        MenuUtil.limparConsole();
        System.out.println("=== BUSCAR EMPRÉSTIMO POR ID ===");

        Integer id = lerInteiro("Informe o ID do empréstimo: ");
        Emprestimo emprestimo = emprestimoService.buscarPorId(id);
        if (emprestimo == null) {
            System.out.println("Empréstimo não encontrado.");
            return;
        }

        imprimirEmprestimo(emprestimo);
    }

    private void atualizar() {
        MenuUtil.limparConsole();
        System.out.println("=== ATUALIZAR EMPRÉSTIMO ===");

        Integer id = lerInteiro("Informe o ID do empréstimo: ");
        Emprestimo emprestimo = emprestimoService.buscarPorId(id);
        if (emprestimo == null) {
            System.out.println("Empréstimo não encontrado.");
            return;
        }

        LocalDate dataPrevista = lerData("Informe a nova data prevista de devolução (dd/MM/yyyy): ");
        emprestimo.setDataPrevista(dataPrevista);

        String devolvido = lerTexto("O livro foi devolvido? (S/N): ");
        if (devolvido.equalsIgnoreCase("S")) {
            LocalDate dataDevolucao = lerData("Informe a data de devolução real (dd/MM/yyyy): ");
            emprestimo.devolver(dataDevolucao);
        } else {
            emprestimo.setDevolvido(Boolean.FALSE);
            emprestimo.setDataDevolucao(null);
            emprestimo.setStatus("EMPRESTADO");
        }

        if (emprestimoService.atualizar(emprestimo)) {
            System.out.println("Empréstimo atualizado com sucesso.");
        } else {
            System.out.println("Falha ao atualizar empréstimo.");
        }
    }

    private void excluir() {
        MenuUtil.limparConsole();
        System.out.println("=== EXCLUIR EMPRÉSTIMO ===");

        Integer id = lerInteiro("Informe o ID do empréstimo: ");
        if (emprestimoService.deletar(id)) {
            System.out.println("Empréstimo excluído com sucesso.");
        } else {
            System.out.println("Empréstimo não encontrado.");
        }
    }

    private void devolverExemplar() {
        MenuUtil.limparConsole();
        System.out.println("=== DEVOLVER EXEMPLAR ===");

        Integer id = lerInteiro("Informe o ID do empréstimo: ");
        LocalDate dataDevolucao = lerData("Informe a data de devolução (dd/MM/yyyy): ");

        if (emprestimoService.devolverExemplar(id, dataDevolucao)) {
            System.out.println("Exemplar devolvido com sucesso.");
        } else {
            System.out.println("Falha ao devolver exemplar.");
        }
    }

    private void renovar() {
        MenuUtil.limparConsole();
        System.out.println("=== RENOVAR EMPRÉSTIMO ===");

        Integer id = lerInteiro("Informe o ID do empréstimo: ");
        LocalDate novaDataPrevista = lerData("Informe a nova data prevista de devolução (dd/MM/yyyy): ");

        if (emprestimoService.renovar(id, novaDataPrevista)) {
            System.out.println("Empréstimo renovado com sucesso.");
        } else {
            System.out.println("Falha ao renovar empréstimo.");
        }
    }

    private void calcularMultaMenu() {
        MenuUtil.limparConsole();
        System.out.println("=== CALCULAR MULTA ===");

        Integer id = lerInteiro("Informe o ID do empréstimo: ");
        double multa = emprestimoService.calcularMulta(id);
        System.out.printf("Multa atual: R$ %.2f%n", multa);
    }

    private void imprimirEmprestimo(Emprestimo emprestimo) {
        System.out.println("ID: " + emprestimo.getId());
        System.out.println("Usuário ID: " + emprestimo.getUsuario().getId());
        System.out.println("Usuário: " + emprestimo.getUsuario().getNome());
        System.out.println("Exemplar ID: " + emprestimo.getExemplarId());
        System.out.println("ISBN do livro: " + emprestimo.getLivro().getIsbn());
        System.out.println("Título do livro: " + emprestimo.getLivro().getTitulo());
        System.out.println("Data do empréstimo: " + emprestimo.getDataEmprestimo().format(DATE_FORMATTER));
        System.out.println("Data prevista de devolução: " + emprestimo.getDataPrevista().format(DATE_FORMATTER));
        if (emprestimo.getDataDevolucao() != null) {
            System.out.println("Data de devolução: " + emprestimo.getDataDevolucao().format(DATE_FORMATTER));
        }
        System.out.println("Status: " + emprestimo.getStatus());
        System.out.println("Devolvido: " + (emprestimo.getDevolvido() ? "Sim" : "Não"));
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

    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return this.scanner.nextLine();
    }

    private LocalDate lerData(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return LocalDate.parse(this.scanner.nextLine(), DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido. Use dd/MM/yyyy.");
            }
        }
    }
}
