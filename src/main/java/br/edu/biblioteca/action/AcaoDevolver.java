package br.edu.biblioteca.action;

import br.edu.biblioteca.model.Emprestimo;
import br.edu.biblioteca.model.Exemplar;
import br.edu.biblioteca.repository.EmprestimoRepository;
import br.edu.biblioteca.repository.ExemplarRepository;

import java.time.LocalDate;

public class AcaoDevolver implements Acao {

    private final EmprestimoRepository emprestimoRepo;
    private final ExemplarRepository exemplarRepo;
    private final Emprestimo emprestimo;
    private final Exemplar exemplar;
    private final LocalDate dataDevolucao;

    private LocalDate dataDevolucaoAnterior;
    private boolean devolvidoAnterior;
    private Exemplar.Status statusExemplarAnterior;

    public AcaoDevolver(EmprestimoRepository emprestimoRepo,
                        ExemplarRepository exemplarRepo,
                        Emprestimo emprestimo,
                        Exemplar exemplar,
                        LocalDate dataDevolucao) {
        this.emprestimoRepo = emprestimoRepo;
        this.exemplarRepo = exemplarRepo;
        this.emprestimo = emprestimo;
        this.exemplar = exemplar;
        this.dataDevolucao = dataDevolucao;
    }

    @Override
    public void executar() {
        dataDevolucaoAnterior = emprestimo.getDataDevolucao();
        devolvidoAnterior = emprestimo.isDevolvido();
        statusExemplarAnterior = exemplar.getStatus();

        emprestimo.devolver(dataDevolucao);
        emprestimoRepo.salvar(emprestimo);

        exemplar.setStatus(Exemplar.Status.DISPONIVEL);
        exemplarRepo.salvar(exemplar);

        double multa = emprestimo.calcularMulta();
        if (multa > 0) {
            System.out.printf(" Atraso de %d dia(s). Multa: R$ %.2f%n", emprestimo.diasAtraso(), multa);
        } else {
            System.out.println("Devolução registrada: " + emprestimo);
        }
    }

    @Override
    public void desfazer() {
        emprestimo.setDataDevolucao(dataDevolucaoAnterior);
        emprestimo.setDevolvido(devolvidoAnterior);
        emprestimoRepo.salvar(emprestimo);

        exemplar.setStatus(statusExemplarAnterior);
        exemplarRepo.salvar(exemplar);

        System.out.println("↶ Devolução desfeita: " + emprestimo);
    }

    @Override
    public String descricao() {
        return String.format("Devolução do empréstimo #%d (ISBN '%s') em %s",
                emprestimo.getId(), emprestimo.getIsbnLivro(), dataDevolucao);
    }
}

