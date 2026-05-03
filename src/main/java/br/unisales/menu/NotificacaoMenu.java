package br.unisales.menu;

import java.util.List;
import java.util.Scanner;

import br.unisales.database.table.Notificacao;
import br.unisales.manager_factory.ManagerFactory;
import br.unisales.menu.util.MenuUtil;
import br.unisales.service.NotificacaoService;

public final class NotificacaoMenu {

    private final Scanner scanner;

    public NotificacaoMenu(Scanner scanner) {
        this.scanner = scanner;
        System.out.println("==========================================");
        System.out.println("    NOTIFICACOES     ");
        System.out.println("==========================================");

        ManagerFactory emf = new ManagerFactory("SQLitePU");
        NotificacaoService notificacaoService = new NotificacaoService(emf.get());

        System.out.println("Verificando reservas atendidas...");
        notificacaoService.gerarNotificacoesDeReservasAtendidas();

        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opcao: ");

            switch (opcao) {
                case 1 -> listarTodos(notificacaoService);
                case 2 -> listarNaoLidas(notificacaoService);
                case 3 -> marcarComoLida(notificacaoService);
                case 4 -> excluir(notificacaoService);
                case 100 -> System.out.println("Voltando para o menu principal...");
                default -> System.out.println("Opcao invalida. Tente novamente.");
            }
            System.out.println();
        } while (opcao != 100);

        emf.close();
    }

    private static void exibirMenu() {
        System.out.println("--------------- MENU ----------------");
        System.out.println("1 - Listar todas as notificacoes");
        System.out.println("2 - Listar nao lidas por usuario");
        System.out.println("3 - Marcar notificacao como lida");
        System.out.println("4 - Excluir notificacao");
        System.out.println("100 - Voltar");
        System.out.println("-------------------------------------");
    }

    private static void listarTodos(NotificacaoService notificacaoService) {
        MenuUtil.limparConsole();
        System.out.println("=== TODAS AS NOTIFICACOES ===");
        List<Notificacao> lista = notificacaoService.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma notificacao cadastrada.");
            return;
        }
        for (Notificacao item : lista) {
            System.out.println("-------------------------------------");
            System.out.println("ID: " + item.getId());
            System.out.println("Usuario ID: " + item.getUsuarioId());
            System.out.println("Mensagem: " + item.getMensagem());
            System.out.println("Data: " + item.getData());
            System.out.println("Lida: " + (item.getLida() ? "Sim" : "Nao"));
        }
        System.out.println("-------------------------------------");
    }

    private void listarNaoLidas(NotificacaoService notificacaoService) {
        MenuUtil.limparConsole();
        System.out.println("=== NOTIFICACOES NAO LIDAS ===");
        Integer usuarioId = lerInteiro("Informe o ID do usuario: ");
        List<Notificacao> lista = notificacaoService.listarNaoLidasPorUsuario(usuarioId);
        if (lista.isEmpty()) {
            System.out.println("Nenhuma notificacao nao lida para este usuario.");
            return;
        }
        for (Notificacao item : lista) {
            System.out.println("-------------------------------------");
            System.out.println("ID: " + item.getId());
            System.out.println("Usuario ID: " + item.getUsuarioId());
            System.out.println("Mensagem: " + item.getMensagem());
            System.out.println("Data: " + item.getData());
            System.out.println("Lida: Nao");
        }
        System.out.println("-------------------------------------");
    }

    private void marcarComoLida(NotificacaoService notificacaoService) {
        MenuUtil.limparConsole();
        System.out.println("=== MARCAR NOTIFICACAO COMO LIDA ===");
        Integer id = lerInteiro("Informe o ID da notificacao: ");
        notificacaoService.marcarComoLida(id);
    }

    private void excluir(NotificacaoService notificacaoService) {
        MenuUtil.limparConsole();
        System.out.println("=== EXCLUIR NOTIFICACAO ===");
        Integer id = lerInteiro("Informe o ID da notificacao que sera excluida: ");
        String confirmacao = lerTexto("Deseja realmente excluir esta notificacao? (S/N): ");
        if (confirmacao.equalsIgnoreCase("S")) {
            notificacaoService.deletar(id);
        } else {
            System.out.println("Exclusao cancelada.");
        }
    }

    private Integer lerInteiro(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return Integer.parseInt(this.scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido. Digite um numero inteiro.");
            }
        }
    }

    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return this.scanner.nextLine();
    }
}