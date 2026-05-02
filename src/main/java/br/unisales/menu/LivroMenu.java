package br.unisales.menu;

import java.util.List;
import java.util.Scanner;

import br.unisales.database.table.Autor;
import br.unisales.database.table.Exemplar;
import br.unisales.database.table.Livro;
import br.unisales.manager_factory.ManagerFactory;
import br.unisales.menu.util.MenuUtil;
import br.unisales.service.ExemplarService;
import br.unisales.service.LivroService;

public final class LivroMenu {
    private final Scanner scanner;
    private final LivroService livroService;
    private final ExemplarService exemplarService;

    public LivroMenu(Scanner scanner) {
        this.scanner = scanner;
        System.out.println("==========================================");
        System.out.println("        LIVRO     ");
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
        this.livroService = new LivroService(emf.get());
        this.exemplarService = new ExemplarService(emf.get());
        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1 -> cadastrar();
                case 2 -> listar();
                case 3 -> buscarPorTitulo();
                case 4 -> excluir();
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
        System.out.println("1 - Cadastrar livro");
        System.out.println("2 - Listar livros");
        System.out.println("3 - Buscar livro por título");
        System.out.println("4 - Remover livro");
        System.out.println("100 - Voltar");
        System.out.println("-------------------------------------");
    }

    /**
     * Realiza o cadastro de um novo livro.
     */
    private void cadastrar() {
        MenuUtil.limparConsole();
        System.out.println("=== CADASTRAR LIVRO ===");
        String isbn = this.lerTexto("Informe o ISBN: ");
        if (!isbn.matches("\\d{13}")) {
    throw new IllegalArgumentException("ISBN deve conter exatamente 13 dígitos numéricos.");
}

        String titulo = this.lerTexto("Informe o título: ");
        Integer ano = this.lerInteiro("Informe o ano de publicação: ");

        Livro livro = new Livro();
        livro.setIsbn(isbn);
        livro.setTitulo(titulo);
        livro.setAno(ano);

        // Adicionar palavras-chave (opcional)
        String adicionarPalavras = this.lerTexto("Deseja adicionar palavras-chave? (sim/não): ");
        if (adicionarPalavras.equalsIgnoreCase("sim")) {
            adicionarPalavrasChave(livro);
        }

        // Adicionar autores (opcional)
        String adicionarAutores = this.lerTexto("Deseja adicionar autores? (sim/não): ");
        if (adicionarAutores.equalsIgnoreCase("sim")) {
            adicionarAutores(livro);
        }

        livroService.inserir(livro);

        // Criar exemplar automaticamente
        Exemplar exemplar = new Exemplar();
        exemplar.setIsbnLivro(isbn);
        exemplarService.inserir(exemplar);
    }

    /**
     * Lista todos os livros cadastrados.
     */
    private void listar() {
        MenuUtil.limparConsole();
        System.out.println("=== LISTAR LIVROS ===");
        List<Livro> lista = livroService.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
            return;
        }
        for (Livro livro : lista) {
            exibirDetalhesLivro(livro);
        }
        System.out.println("-------------------------------------");
    }


    /**
     * Busca livros por título.
     */
    private void buscarPorTitulo() {
        MenuUtil.limparConsole();
        System.out.println("=== BUSCAR LIVRO POR TÍTULO ===");
        String titulo = this.lerTexto("Informe o título (ou parte dele): ");
        List<Livro> lista = livroService.buscarPorTitulo(titulo);
        if (lista.isEmpty()) {
            System.out.println("Nenhum livro encontrado com este título.");
            return;
        }
        for (Livro livro : lista) {
            exibirDetalhesLivro(livro);
        }
    }


    /**
     * Exclui um livro do banco de dados.
     */
    private void excluir() {
        MenuUtil.limparConsole();
        System.out.println("=== EXCLUIR LIVRO ===");
        String titulo = this.lerTexto("Informe o título do livro a excluir: ");
        Livro livro = livroService.buscarPorTitulo(titulo).stream().findFirst().orElse(null);
        if (livro == null) {
            System.out.println("Livro não encontrado.");
            return;
        }

        exibirDetalhesLivro(livro);
        String confirmacao = this.lerTexto("Tem certeza que deseja excluir este livro? (sim/não): ");
        if (confirmacao.equalsIgnoreCase("sim")) {
            livroService.deletar(livro.getIsbn());
        } else {
            System.out.println("Exclusão cancelada.");
        }
    }

    /**
     * Adiciona palavras-chave ao livro.
     */
    private void adicionarPalavrasChave(Livro livro) {
        String adicionarMais = "sim";
        while (adicionarMais.equalsIgnoreCase("sim")) {
            String palavra = this.lerTexto("Informe uma palavra-chave: ");
            if (!palavra.isEmpty()) {
                livro.getPalavrasChave().add(palavra);
                System.out.println("Palavra-chave adicionada.");
            }
            adicionarMais = this.lerTexto("Deseja adicionar mais palavras-chave? (sim/não): ");
        }
    }

    /**
     * Adiciona autores ao livro.
     */
    private void adicionarAutores(Livro livro) {
        List<Autor> autoresDisponiveis = livroService.listarTodosAutores();
        
        if (autoresDisponiveis.isEmpty()) {
            System.out.println("Não há autores cadastrados no sistema.");
            return;
        }

        String adicionarMais = "sim";
        while (adicionarMais.equalsIgnoreCase("sim")) {
            System.out.println("\n--- Autores disponíveis ---");
            for (Autor autor : autoresDisponiveis) {
                System.out.println("ID: " + autor.getId() + " - Nome: " + autor.getNome());
            }
            System.out.println("----------------------------");

            Integer autorId = this.lerInteiro("Informe o ID do autor: ");
            Autor autor = livroService.buscarAutorPorId(autorId);
            
            if (autor != null) {
                livro.addAutor(autor);
                System.out.println("Autor adicionado ao livro.");
            } else {
                System.out.println("Autor não encontrado.");
            }

            adicionarMais = this.lerTexto("Deseja adicionar mais autores? (sim/não): ");
        }
    }

    /**
     * Exibe os detalhes completos de um livro.
     */
    private void exibirDetalhesLivro(Livro livro) {
        System.out.println("-------------------------------------");
        System.out.println("ISBN: " + livro.getIsbn());
        System.out.println("Título: " + livro.getTitulo());
        System.out.println("Ano: " + livro.getAno());
        
        // Buscar e exibir exemplares
        List<Exemplar> exemplares = livroService.listarExemplaresPorLivro(livro.getIsbn());
        if (!exemplares.isEmpty()) {
            System.out.print("ID Exemplar: ");
            for (int i = 0; i < exemplares.size(); i++) {
                System.out.print(exemplares.get(i).getId());
                if (i < exemplares.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println();
        } else {
            System.out.println("Nenhum exemplar cadastrado.");
        }
        
        if (!livro.getPalavrasChave().isEmpty()) {
            System.out.println("Palavras-chave: " + String.join(", ", livro.getPalavrasChave()));
        }
        
        if (!livro.getLivroAutores().isEmpty()) {
            System.out.println("Autores:");
            for (var la : livro.getLivroAutores()) {
                if (la.getAutor() != null) {
                    System.out.println("  - " + la.getAutor().getNome());
                }
            }
        }
    }

    /**
     * Lê um texto do usuário.
     */
    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    /**
     * Lê um inteiro do usuário.
     */
    private Integer lerInteiro(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite um número inteiro.");
            }
        }
    }
}

