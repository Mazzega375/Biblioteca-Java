package br.edu.biblioteca.action;

import br.edu.biblioteca.model.Livro;
import br.edu.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AcaoCadastrarLivroTest {

    // Repositório fake em memória — sem banco de dados
    private static class LivroRepositoryFake extends LivroRepository {
        final List<Livro> livros = new ArrayList<>();
        final List<String> removidos = new ArrayList<>();

        LivroRepositoryFake() { /* não chama super() que conecta ao banco */ }

        @Override public void salvar(Livro l) { livros.add(l); }
        @Override public void remover(String isbn) { removidos.add(isbn); }
        @Override public Livro buscarPorIsbn(String isbn) {
            return livros.stream().filter(l -> l.getIsbn().equals(isbn)).findFirst().orElse(null);
        }
    }

    private LivroRepositoryFake repo;
    private Livro livro;

    @BeforeEach
    void setUp() {
        repo = new LivroRepositoryFake();
        livro = new Livro("978-1", "Clean Code", "Robert Martin", "Prentice Hall", 2008, "Programacao");
    }

    @Test
    void executar_salva_livro_no_repositorio() {
        AcaoCadastrarLivro acao = new AcaoCadastrarLivro(repo, livro);
        acao.executar();

        assertEquals(1, repo.livros.size());
        assertEquals("978-1", repo.livros.get(0).getIsbn());
    }

    @Test
    void desfazer_remove_livro_cadastrado() {
        AcaoCadastrarLivro acao = new AcaoCadastrarLivro(repo, livro);
        acao.executar();
        acao.desfazer();

        assertTrue(repo.removidos.contains("978-1"));
    }

    @Test
    void descricao_contem_titulo_e_isbn() {
        AcaoCadastrarLivro acao = new AcaoCadastrarLivro(repo, livro);
        String desc = acao.descricao();

        assertTrue(desc.contains("Clean Code"));
        assertTrue(desc.contains("978-1"));
    }
}
