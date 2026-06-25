package br.edu.biblioteca.action;

import br.edu.biblioteca.model.Emprestimo;
import br.edu.biblioteca.model.Exemplar;
import br.edu.biblioteca.repository.EmprestimoRepository;
import br.edu.biblioteca.repository.ExemplarRepository;


/**
 * Ação de empréstimo de livro.
 * Marca o exemplar como EMPRESTADO e persiste o registro de empréstimo.
 * Ao desfazer, remove o empréstimo e restaura o exemplar para DISPONIVEL.
 */
public class AcaoEmpresta implements Acao {

    private final EmprestimoRepository emprestimoRepo;
    private final ExemplarRepository exemplarRepo;
    private final Emprestimo emprestimo;
    private final Exemplar exemplar;

    public AcaoEmpresta(EmprestimoRepository emprestimoRepo,
                        ExemplarRepository exemplarRepo,
                        Emprestimo emprestimo,
                        Exemplar exemplar) {
        this.emprestimoRepo = emprestimoRepo;
        this.exemplarRepo   = exemplarRepo;
        this.emprestimo     = emprestimo;
        this.exemplar       = exemplar;
    }

    @Override
    public void executar() {
        exemplar.setStatus(Exemplar.Status.EMPRESTADO);
        exemplarRepo.salvar(exemplar);
        emprestimoRepo.salvar(emprestimo);
        System.out.println("✔ Empréstimo registrado: " + emprestimo);
    }

    @Override
    public void desfazer() {
        emprestimoRepo.remover(emprestimo.getId());
        exemplar.setStatus(Exemplar.Status.DISPONIVEL);
        exemplarRepo.salvar(exemplar);
        System.out.println("↶ Empréstimo desfeito: " + emprestimo);
    }

    @Override
    public String descricao() {
        return String.format("Empréstimo do ISBN '%s' para usuário %d em %s",
                emprestimo.getIsbnLivro(),
                emprestimo.getUsuarioId(),
                emprestimo.getDataEmprestimo());
    }
}
