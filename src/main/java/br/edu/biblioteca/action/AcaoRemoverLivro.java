package br.edu.biblioteca.action;

import br.edu.biblioteca.model.Livro;
import br.edu.biblioteca.repository.LivroRepository;

public class AcaoRemoverLivro implements Acao {

    private final LivroRepository livroRepo;
    private final Livro livro;

    public AcaoRemoverLivro(LivroRepository livroRepo, Livro livro) {
        this.livroRepo = livroRepo;
        this.livro = livro;
    }

    @Override
    public void executar() {
        livroRepo.remover(livro.getIsbn());
        System.out.println("✔ Livro removido: " + livro.getTitulo());
    }

    @Override
    public void desfazer() {
        livroRepo.salvar(livro);
        System.out.println("↶ Remoção desfeita: " + livro.getTitulo());
    }

    @Override
    public String descricao() {
        return String.format("Remoção do livro '%s' (ISBN %s)", livro.getTitulo(), livro.getIsbn());
    }
}
