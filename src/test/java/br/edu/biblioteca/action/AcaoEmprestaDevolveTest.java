package br.edu.biblioteca.action;

import br.edu.biblioteca.model.Emprestimo;
import br.edu.biblioteca.model.Exemplar;
import br.edu.biblioteca.repository.EmprestimoRepository;
import br.edu.biblioteca.repository.ExemplarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AcaoEmprestaDevolveTest {

    private static class EmprestimoRepositoryFake extends EmprestimoRepository {
        final List<Emprestimo> emprestimos = new ArrayList<>();
        final List<Integer> removidos = new ArrayList<>();

        EmprestimoRepositoryFake() {}

        @Override public void salvar(Emprestimo e) { emprestimos.add(e); }
        @Override public void remover(int id) { removidos.add(id); }
    }

    private static class ExemplarRepositoryFake extends ExemplarRepository {
        final List<Exemplar> exemplares = new ArrayList<>();

        ExemplarRepositoryFake() {}

        @Override public void salvar(Exemplar e) { exemplares.add(e); }
    }

    private EmprestimoRepositoryFake emprestimoRepo;
    private ExemplarRepositoryFake exemplarRepo;
    private Emprestimo emprestimo;
    private Exemplar exemplar;

    @BeforeEach
    void setUp() {
        emprestimoRepo = new EmprestimoRepositoryFake();
        exemplarRepo = new ExemplarRepositoryFake();

        LocalDate hoje = LocalDate.now();
        emprestimo = new Emprestimo(1, 10, "978-1", hoje, hoje.plusDays(14));
        exemplar = new Exemplar(1, "978-1");
    }

    @Test
    void emprestar_marca_exemplar_como_emprestado() {
        AcaoEmpresta acao = new AcaoEmpresta(emprestimoRepo, exemplarRepo, emprestimo, exemplar);
        acao.executar();

        assertEquals(Exemplar.Status.EMPRESTADO, exemplar.getStatus());
    }

    @Test
    void emprestar_persiste_emprestimo() {
        AcaoEmpresta acao = new AcaoEmpresta(emprestimoRepo, exemplarRepo, emprestimo, exemplar);
        acao.executar();

        assertEquals(1, emprestimoRepo.emprestimos.size());
    }

    @Test
    void desfazer_emprestimo_remove_registro() {
        AcaoEmpresta acao = new AcaoEmpresta(emprestimoRepo, exemplarRepo, emprestimo, exemplar);
        acao.executar();
        acao.desfazer();

        assertTrue(emprestimoRepo.removidos.contains(1));
        assertEquals(Exemplar.Status.DISPONIVEL, exemplar.getStatus());
    }

    @Test
    void devolver_no_prazo_sem_multa() {
        AcaoEmpresta empr = new AcaoEmpresta(emprestimoRepo, exemplarRepo, emprestimo, exemplar);
        empr.executar();

        LocalDate devolucao = LocalDate.now().plusDays(7);
        AcaoDevolver dev = new AcaoDevolver(emprestimoRepo, exemplarRepo, emprestimo, exemplar, devolucao);
        dev.executar();

        assertTrue(emprestimo.isDevolvido());
        assertEquals(0.0, emprestimo.calcularMulta(), 0.001);
        assertEquals(Exemplar.Status.DISPONIVEL, exemplar.getStatus());
    }

    @Test
    void devolver_com_atraso_gera_multa() {
        LocalDate hoje = LocalDate.now();
        Emprestimo atrasado = new Emprestimo(2, 10, "978-2", hoje.minusDays(20), hoje.minusDays(5));

        AcaoDevolver dev = new AcaoDevolver(emprestimoRepo, exemplarRepo, atrasado, exemplar, hoje);
        dev.executar();

        assertTrue(atrasado.calcularMulta() > 0);
    }

    @Test
    void desfazer_devolucao_restaura_estado_anterior() {
        LocalDate devolucao = LocalDate.now().plusDays(3);
        AcaoDevolver dev = new AcaoDevolver(emprestimoRepo, exemplarRepo, emprestimo, exemplar, devolucao);
        dev.executar();
        dev.desfazer();

        assertFalse(emprestimo.isDevolvido());
        assertNull(emprestimo.getDataDevolucao());
    }

    @Test
    void descricao_emprestimo_contem_isbn_e_usuario() {
        AcaoEmpresta acao = new AcaoEmpresta(emprestimoRepo, exemplarRepo, emprestimo, exemplar);
        String desc = acao.descricao();

        assertTrue(desc.contains("978-1"));
        assertTrue(desc.contains("10"));
    }
}
