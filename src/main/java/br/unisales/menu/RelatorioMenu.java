package br.unisales.menu;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import br.unisales.database.table.Emprestimo;
import br.unisales.manager_factory.ManagerFactory;
import br.unisales.menu.util.MenuUtil;
import br.unisales.service.RelatorioService;
import br.unisales.structures.Matriz;

public final class RelatorioMenu {

    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public RelatorioMenu(Scanner scanner) {
        this.scanner = scanner;
        System.out.println("==========================================");
        System.out.println("       RELATÓRIO       ");
        System.out.println("==========================================");

        ManagerFactory emf = new ManagerFactory("SQLitePU");
        RelatorioService relatorioService = new RelatorioService(emf.get());

        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opção: ");
            switch (opcao) {
                case 1 -> topMaisEmprestados(relatorioService);
                case 2 -> emAtraso(relatorioService);
                case 3 -> usuariosComMaisAtrasos(relatorioService);
                case 4 -> estatisticasMensais(relatorioService);
                case 100 -> System.out.println("Voltando para o menu principal...");
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
            System.out.println();
        } while (opcao != 100);

        emf.close();
    }

    private static void exibirMenu() {
        System.out.println("--------------- MENU ----------------");
        System.out.println("1 - Top mais emprestados");
        System.out.println("2 - Empréstimos em atraso");
        System.out.println("3 - Usuários com mais atrasos");
        System.out.println("4 - Estatísticas mensais");
        System.out.println("100 - Voltar");
        System.out.println("-------------------------------------");
    }

    private void topMaisEmprestados(RelatorioService relatorioService) {
        MenuUtil.limparConsole();
        System.out.println("=== TOP MAIS EMPRÉSTADOS ===");
        int limite = lerInteiro("Informe a quantidade de resultados desejada: ");
        List<Object[]> resultados = relatorioService.topMaisEmprestados(limite);
        if (resultados.isEmpty()) {
            System.out.println("Nenhum registro encontrado.");
            return;
        }
        System.out.printf("%-20s %-40s %-10s%n", "ISBN", "Título", "Total");
        System.out.println("--------------------------------------------------------------------------------");
        for (Object[] linha : resultados) {
            String isbn = linha[0] != null ? linha[0].toString() : "N/A";
            String titulo = linha[1] != null ? linha[1].toString() : "N/A";
            String total = linha[2] != null ? linha[2].toString() : "0";
            System.out.printf("%-20s %-40s %-10s%n", isbn, titulo, total);
        }
    }

    private void emAtraso(RelatorioService relatorioService) {
        MenuUtil.limparConsole();
        System.out.println("=== EMPRÉSTIMOS EM ATRASO ===");
        List<Emprestimo> resultados = relatorioService.emAtraso();
        if (resultados.isEmpty()) {
            System.out.println("Nenhum empréstimo em atraso.");
            return;
        }
        for (Emprestimo emprestimo : resultados) {
            System.out.println("-------------------------------------");
            System.out.println("ID: " + emprestimo.getId());
            System.out.println("Usuário ID: " + (emprestimo.getUsuario() != null ? emprestimo.getUsuario().getId() : "N/A"));
            System.out.println("Usuário: " + (emprestimo.getUsuario() != null ? emprestimo.getUsuario().getNome() : "N/A"));
            System.out.println("ISBN do livro: " + (emprestimo.getLivro() != null ? emprestimo.getLivro().getIsbn() : "N/A"));
            System.out.println("Título do livro: " + (emprestimo.getLivro() != null ? emprestimo.getLivro().getTitulo() : "N/A"));
            System.out.println("Data prevista de devolução: " + emprestimo.getDataPrevista().format(DATE_FORMATTER));
            System.out.println("Status: " + emprestimo.getStatus());
        }
        System.out.println("-------------------------------------");
    }

    private void usuariosComMaisAtrasos(RelatorioService relatorioService) {
        MenuUtil.limparConsole();
        System.out.println("=== USUÁRIOS COM MAIS ATRASOS ===");
        int limite = lerInteiro("Informe a quantidade de resultados desejada: ");
        List<Object[]> resultados = relatorioService.usuariosComMaisAtrasos(limite);
        if (resultados.isEmpty()) {
            System.out.println("Nenhum usuário encontrado.");
            return;
        }
        System.out.printf("%-10s %-30s %-10s%n", "ID", "Nome", "Atrasos");
        System.out.println("----------------------------------------------------------");
        for (Object[] linha : resultados) {
            String id = linha[0] != null ? linha[0].toString() : "N/A";
            String nome = linha[1] != null ? linha[1].toString() : "N/A";
            String total = linha[2] != null ? linha[2].toString() : "0";
            System.out.printf("%-10s %-30s %-10s%n", id, nome, total);
        }
    }

    private void estatisticasMensais(RelatorioService relatorioService) {
        MenuUtil.limparConsole();
        System.out.println("=== ESTATÍSTICAS MENSAIS ===");
        Matriz<Integer> matriz = relatorioService.estatisticasMensais();
        if (matriz == null) {
            System.out.println("Não foi possível gerar as estatísticas.");
            return;
        }
        System.out.println("Mês | Categoria 0 | Categoria 1 | Categoria 2 | Categoria 3 | Categoria 4 | Categoria 5 | Categoria 6 | Categoria 7 | Categoria 8 | Categoria 9");
        System.out.println("---------------------------------------------------------------------------------------------------------------");
        String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        for (int i = 0; i < matriz.getLinhas(); i++) {
            System.out.printf("%-3s", meses[i]);
            for (int j = 0; j < matriz.getColunas(); j++) {
                Integer valor = matriz.get(i, j);
                System.out.printf(" | %-10s", valor != null ? valor : 0);
            }
            System.out.println();
        }
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
