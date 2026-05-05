package br.unisales.menu;

import java.util.List;
import java.util.Scanner;

import br.unisales.Enumeration.UsuarioTipoEnum;
import br.unisales.database.table.Usuario;
import br.unisales.manager_factory.ManagerFactory;
import br.unisales.menu.util.MenuUtil;
import br.unisales.service.UsuarioService;

public final class UsuarioMenu {
    private final Scanner scanner;

    public UsuarioMenu(Scanner scanner) {
        this.scanner = scanner;
        System.out.println("==========================================");
        System.out.println("    USUÁRIO MENU     ");
        System.out.println("==========================================");
        /*
         * Cria a fábrica de EntityManager com base na persistence-unit
         * definida no arquivo persistence.xml.
         */
        ManagerFactory emf = new ManagerFactory("SQLitePU");
        UsuarioService usuarioService = new UsuarioService(emf.get());
        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1 -> cadastrar(usuarioService);
                case 2 -> listar(usuarioService);
                case 3 -> bloquearDesbloquear(usuarioService);
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
        
        System.out.println("1 - Cadastrar usuário");
        System.out.println("2 - Listar usuários");
        System.out.println("3 - Bloquear/Desbloquear usuário");
        System.out.println("100 - Voltar");
        System.out.println("-------------------------------------");
    }

    /**
     * Realiza o cadastro de um novo usuário.
     */
    private void cadastrar(UsuarioService usuarioService) {
        MenuUtil.limparConsole();
        System.out.println("=== CADASTRAR USUÁRIO ===");
        String nome = this.lerTexto("Informe o nome: ");
        String email = this.lerTexto("Informe o e-mail: ");
        String senha = this.lerTexto("Informe senha: ");
        String tipo = this.lerTexto("Informe o tipo (ALUNO, PROFESSOR, SERVIDOR): ");
        UsuarioTipoEnum stn = UsuarioTipoEnum.ALUNO;
        if (tipo.toLowerCase().equals("professor"))
            stn = UsuarioTipoEnum.PROFESSOR;
        else if (tipo.toLowerCase().equals("servidor"))
            stn = UsuarioTipoEnum.SERVIDOR;
        Usuario item = new Usuario(null, nome, stn, email, senha);
        usuarioService.inserir(item);
    }

    /**
     * Lista todos os usuários cadastrados.
     */
    private static void listar(UsuarioService usuarioService) {
        MenuUtil.limparConsole();
        System.out.println("=== LISTAR USUÁRIOS ===");
        List<Usuario> lista = usuarioService.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }
        for (Usuario item : lista) {
            System.out.println("-------------------------------------");
            System.out.println("ID: " + item.getId());
            System.out.println("Nome: " + item.getNome());
            System.out.println("E-mail: " + item.getEmail());
            System.out.println("Tipo: " + item.getTipo());
            System.out.println("Bloqueado: " + (Boolean.TRUE.equals(item.getBloqueado()) ? "Sim" : "Não"));
        }
        System.out.println("-------------------------------------");
    }

    /**
     * Bloqueia ou desbloqueia um usuário pelo ID.
     */
    private void bloquearDesbloquear(UsuarioService usuarioService) {
        MenuUtil.limparConsole();
        System.out.println("=== BLOQUEAR / DESBLOQUEAR USUÁRIO ===");
        Integer id = this.lerInteiro("Informe o ID do usuário: ");
        Usuario item = usuarioService.buscarPorId(id);
        if (item == null) {
            System.out.println("Usuário não encontrado.");
            return;
        }

        System.out.println("Usuário localizado:");
        System.out.println("ID: " + item.getId());
        System.out.println("Nome: " + item.getNome());
        System.out.println("E-mail: " + item.getEmail());
        System.out.println("Bloqueado: " + (Boolean.TRUE.equals(item.getBloqueado()) ? "Sim" : "Não"));

        if (Boolean.TRUE.equals(item.getBloqueado())) {
            String confirmacao = this.lerTexto("Deseja desbloquear este usuário? (S/N): ");
            if (confirmacao.equalsIgnoreCase("S")) {
                usuarioService.desbloquear(id);
            } else {
                System.out.println("Operação cancelada.");
            }
        } else {
            String confirmacao = this.lerTexto("Deseja bloquear este usuário? (S/N): ");
            if (confirmacao.equalsIgnoreCase("S")) {
                usuarioService.bloquear(id);
            } else {
                System.out.println("Operação cancelada.");
            }
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
