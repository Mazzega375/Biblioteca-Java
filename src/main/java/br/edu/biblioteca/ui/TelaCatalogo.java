package br.edu.biblioteca.ui;

import br.edu.biblioteca.action.AcaoCadastrarLivro;
import br.edu.biblioteca.action.AcaoRemoverLivro;
import br.edu.biblioteca.model.Exemplar;
import br.edu.biblioteca.model.Livro;

import java.util.List;
import java.util.Scanner;

/**
 * Tela de gerenciamento do catálogo de livros.
 * Permite cadastrar, remover, buscar e listar livros do acervo.
 */
public class TelaCatalogo {

    private final BibliotecaContext ctx;
    private final Scanner scanner;

    public TelaCatalogo(BibliotecaContext ctx, Scanner scanner) {
        this.ctx     = ctx;
        this.scanner = scanner;
    }

    public void exibir() {
        boolean sair = false;
        while (!sair) {
            
            System.out.println("\n       - CATÁLOGO DE LIVROS -    ");
           
            System.out.println("  1. Cadastrar livro          ");
            System.out.println("  2. Remover livro            ");
            System.out.println("  3. Buscar por título        ");
            System.out.println("  4. Buscar por autor         ");
            System.out.println("  5. Buscar por ISBN          ");
            System.out.println("  6. Listar (ord. título)     ");
            System.out.println("  7. Listar (ord. autor)      ");
            System.out.println("  8. Listar (ord. ano)        ");
            System.out.println("  9. Adicionar exemplar       ");
            System.out.println("  0. Voltar                   ");
            System.out.print("Opção: ");

            String opcao = scanner.nextLine().trim();
            switch (opcao) {
                case "1" -> cadastrarLivro();
                case "2" -> removerLivro();
                case "3" -> buscarPorTitulo();
                case "4" -> buscarPorAutor();
                case "5" -> buscarPorIsbn();
                case "6" -> listar(ctx.livroRepo.ordenarPorTitulo(), "Ordenado por Título");
                case "7" -> listar(ctx.livroRepo.ordenarPorAutor(), "Ordenado por Autor");
                case "8" -> listar(ctx.livroRepo.ordenarPorAno(),   "Ordenado por Ano");
                case "9" -> adicionarExemplar();
                case "0" -> sair = true;
                default  -> System.out.println("⚠  Opção inválida.");
            }
        }
    }

    // -------------------------------------------------------------------------

    private void cadastrarLivro() {
        System.out.println("\n--- Cadastrar Livro ---");
        System.out.print("ISBN: ");         String isbn     = scanner.nextLine().trim();
        System.out.print("Título: ");       String titulo   = scanner.nextLine().trim();
        System.out.print("Autor: ");        String autor    = scanner.nextLine().trim();
        System.out.print("Editora: ");      String editora  = scanner.nextLine().trim();
        System.out.print("Ano: ");          int ano         = lerInt();
        System.out.print("Categoria: ");    String cat      = scanner.nextLine().trim();

        if (ctx.livroRepo.buscarPorIsbn(isbn) != null) {
            System.out.println("⚠  Já existe um livro com esse ISBN.");
            return;
        }

        Livro livro = new Livro(isbn, titulo, autor, editora, ano, cat);
        ctx.executar(new AcaoCadastrarLivro(ctx.livroRepo, livro));
    }

    private void removerLivro() {
        System.out.println("\n--- Remover Livro ---");
        System.out.print("ISBN do livro: ");
        String isbn = scanner.nextLine().trim();
        Livro livro = ctx.livroRepo.buscarPorIsbn(isbn);
        if (livro == null) {
            System.out.println("⚠  Livro não encontrado.");
            return;
        }
        System.out.println("Livro: " + livro);
        System.out.print("Confirmar remoção? (s/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
            ctx.executar(new AcaoRemoverLivro(ctx.livroRepo, livro));
        } else {
            System.out.println("Remoção cancelada.");
        }
    }

    private void buscarPorTitulo() {
        System.out.print("Termo de busca (título): ");
        String termo = scanner.nextLine().trim();
        listar(ctx.livroRepo.buscarPorTitulo(termo), "Resultados para \"" + termo + "\"");
    }

    private void buscarPorAutor() {
        System.out.print("Nome do autor: ");
        String autor = scanner.nextLine().trim();
        listar(ctx.livroRepo.buscarPorAutor(autor), "Resultados para autor \"" + autor + "\"");
    }

    private void buscarPorIsbn() {
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();
        Livro livro = ctx.livroRepo.buscarPorIsbn(isbn);
        if (livro == null) {
            System.out.println("⚠  Livro não encontrado.");
        } else {
            System.out.println(livro);
            long disp = ctx.exemplarRepo.contarDisponiveis(isbn);
            long total = ctx.exemplarRepo.contarTotal(isbn);
            System.out.printf("   Exemplares: %d/%d disponíveis%n", disp, total);
        }
    }

    private void adicionarExemplar() {
        System.out.print("ISBN do livro: ");
        String isbn = scanner.nextLine().trim();
        if (ctx.livroRepo.buscarPorIsbn(isbn) == null) {
            System.out.println("⚠  Livro não encontrado.");
            return;
        }
        System.out.print("Quantidade de exemplares a adicionar: ");
        int qtd = lerInt();
        for (int i = 0; i < qtd; i++) {
            ctx.exemplarRepo.salvar(new Exemplar(0, isbn));
        }
        System.out.printf("✔ %d exemplar(es) adicionado(s) para ISBN %s%n", qtd, isbn);
    }

    private void listar(List<Livro> livros, String titulo) {
        System.out.println("\n--- " + titulo + " (" + livros.size() + " livro(s)) ---");
        if (livros.isEmpty()) {
            System.out.println("  (nenhum livro encontrado)");
        } else {
            livros.forEach(l -> {
                long disp  = ctx.exemplarRepo.contarDisponiveis(l.getIsbn());
                long total = ctx.exemplarRepo.contarTotal(l.getIsbn());
                System.out.printf("  %s  [Ex: %d/%d]%n", l, disp, total);
            });
        }
    }

    private int lerInt() {
        try {
            String linha = scanner.nextLine().trim();
            return Integer.parseInt(linha);
        } catch (NumberFormatException e) {
            System.out.println("⚠  Valor inválido, usando 0.");
            return 0;
        }
    }
}
