package br.edu.biblioteca.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class EmprestimoTest {

    private Emprestimo criarEmprestimo(LocalDate dataEmprestimo, LocalDate dataPrevista) {
        return new Emprestimo(1, 10, "978-1", dataEmprestimo, dataPrevista);
    }

    @Test
    void emprestimo_novo_nao_esta_devolvido() {
        Emprestimo emp = criarEmprestimo(LocalDate.now(), LocalDate.now().plusDays(14));
        assertFalse(emp.isDevolvido());
        assertNull(emp.getDataDevolucao());
    }

    @Test
    void devolver_marca_como_devolvido() {
        Emprestimo emp = criarEmprestimo(LocalDate.now(), LocalDate.now().plusDays(14));
        emp.devolver(LocalDate.now());

        assertTrue(emp.isDevolvido());
        assertNotNull(emp.getDataDevolucao());
    }

    @Test
    void multa_zero_quando_devolvido_no_prazo() {
        LocalDate hoje = LocalDate.now();
        Emprestimo emp = criarEmprestimo(hoje.minusDays(5), hoje.plusDays(7));
        emp.devolver(hoje);

        assertEquals(0.0, emp.calcularMulta(), 0.001);
    }

    @Test
    void multa_calculada_com_atraso() {
        LocalDate hoje = LocalDate.now();
        Emprestimo emp = criarEmprestimo(hoje.minusDays(20), hoje.minusDays(10));
        emp.devolver(hoje);

        // 10 dias de atraso x R$1,50 = R$15,00
        assertEquals(15.0, emp.calcularMulta(), 0.001);
    }

    @Test
    void emprestimo_ativo_em_atraso_tem_multa() {
        LocalDate hoje = LocalDate.now();
        Emprestimo emp = criarEmprestimo(hoje.minusDays(20), hoje.minusDays(5));

        assertTrue(emp.estaEmAtraso());
        assertTrue(emp.calcularMulta() > 0);
    }

    @Test
    void emprestimo_ativo_no_prazo_sem_multa() {
        LocalDate hoje = LocalDate.now();
        Emprestimo emp = criarEmprestimo(hoje.minusDays(3), hoje.plusDays(7));

        assertFalse(emp.estaEmAtraso());
        assertEquals(0.0, emp.calcularMulta(), 0.001);
    }

    @Test
    void toCsv_e_fromCsv_sao_inversos() {
        LocalDate hoje = LocalDate.now();
        Emprestimo original = criarEmprestimo(hoje, hoje.plusDays(14));
        original.devolver(hoje.plusDays(10));

        Emprestimo recuperado = Emprestimo.fromCsv(original.toCsv());

        assertEquals(original.getId(), recuperado.getId());
        assertEquals(original.getUsuarioId(), recuperado.getUsuarioId());
        assertEquals(original.getIsbnLivro(), recuperado.getIsbnLivro());
        assertEquals(original.isDevolvido(), recuperado.isDevolvido());
        assertEquals(original.getDataDevolucao(), recuperado.getDataDevolucao());
    }

    @Test
    void dias_atraso_nunca_negativos() {
        LocalDate hoje = LocalDate.now();
        Emprestimo emp = criarEmprestimo(hoje, hoje.plusDays(30));
        assertEquals(0, emp.diasAtraso());
    }
}
