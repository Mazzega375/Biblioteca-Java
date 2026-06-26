package br.edu.biblioteca.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExemplarTest {

    @Test
    void exemplar_novo_esta_disponivel() {
        Exemplar exemplar = new Exemplar(1, "978-1");
        assertTrue(exemplar.isDisponivel());
        assertEquals(Exemplar.Status.DISPONIVEL, exemplar.getStatus());
    }

    @Test
    void mudanca_de_status_para_emprestado() {
        Exemplar exemplar = new Exemplar(1, "978-1");
        exemplar.setStatus(Exemplar.Status.EMPRESTADO);

        assertFalse(exemplar.isDisponivel());
        assertEquals(Exemplar.Status.EMPRESTADO, exemplar.getStatus());
    }

    @Test
    void toCsv_e_fromCsv_sao_inversos() {
        Exemplar original = new Exemplar(4, "978-3");
        original.setStatus(Exemplar.Status.RESERVADO);

        Exemplar recuperado = Exemplar.fromCsv(original.toCsv());

        assertEquals(original.getId(), recuperado.getId());
        assertEquals(original.getIsbnLivro(), recuperado.getIsbnLivro());
        assertEquals(original.getStatus(), recuperado.getStatus());
    }

    @Test
    void toString_contem_id_e_isbn() {
        Exemplar exemplar = new Exemplar(7, "978-9");
        String texto = exemplar.toString();

        assertTrue(texto.contains("7"));
        assertTrue(texto.contains("978-9"));
    }
}
