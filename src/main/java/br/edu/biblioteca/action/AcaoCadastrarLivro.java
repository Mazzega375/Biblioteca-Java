package br.edu.biblioteca.action;

import br.edu.biblioteca.model.Livro;
import br.edu.biblioteca.repository.LivroRepository;

public class AcaoCadastrarLivro implements Acao {

    private final LivroRepository livroRepo;
    private final Livro livro;

    public AcaoCadastrarLivro(LivroRepository livroRepo, Livro livro) {
        this.livroRepo = livroRepo;
        this.livro = livro;
    }

    @Override
    public void executar() {
        livroRepo.salvar(livro);
        System.out.println("✔ Livro cadastrado: " + livro);
    }

    @Override
    public void desfazer() {
        livroRepo.remover(livro.getIsbn());
        System.out.println("↶ Cadastro desfeito: " + livro.getTitulo());
    }

    @Override
    public String descricao() {
        return String.format("Cadastro do livro '%s' (ISBN %s)", livro.getTitulo(), livro.getIsbn());
    }
}
