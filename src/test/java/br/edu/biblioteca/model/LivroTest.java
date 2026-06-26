package br.edu.biblioteca.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LivroTest {

    @Test
    void criacao_com_dados_validos() {
        Livro livro = new Livro("978-1", "Clean Code", "Robert Martin", "Prentice Hall", 2008, "Programacao");

        assertEquals("978-1", livro.getIsbn());
        assertEquals("Clean Code", livro.getTitulo());
        assertEquals("Robert Martin", livro.getAutor());
        assertEquals(2008, livro.getAno());
    }

    @Test
    void toString_contem_isbn_e_titulo() {
        Livro livro = new Livro("978-1", "Clean Code", "Robert Martin", "Prentice Hall", 2008, "Programacao");
        String texto = livro.toString();

        assertTrue(texto.contains("978-1"));
        assertTrue(texto.contains("Clean Code"));
    }

    @Test
    void toCsv_e_fromCsv_sao_inversos() {
        Livro original = new Livro("978-2", "Domain Driven Design", "Eric Evans", "Addison", 2003, "Arquitetura");
        String csv = original.toCsv();
        Livro recuperado = Livro.fromCsv(csv);

        assertEquals(original.getIsbn(), recuperado.getIsbn());
        assertEquals(original.getTitulo(), recuperado.getTitulo());
        assertEquals(original.getAutor(), recuperado.getAutor());
        assertEquals(original.getAno(), recuperado.getAno());
        assertEquals(original.getCategoria(), recuperado.getCategoria());
    }

    @Test
    void fromCsv_com_linha_invalida_lanca_excecao() {
        assertThrows(IllegalArgumentException.class, () -> Livro.fromCsv("dados;incompletos"));
    }

    @Test
    void setters_atualizam_campos() {
        Livro livro = new Livro();
        livro.setIsbn("978-3");
        livro.setTitulo("Refactoring");
        livro.setAutor("Fowler");
        livro.setAno(1999);
        livro.setEditora("Addison");
        livro.setCategoria("Qualidade");

        assertEquals("978-3", livro.getIsbn());
        assertEquals("Refactoring", livro.getTitulo());
        assertEquals(1999, livro.getAno());
    }
}
