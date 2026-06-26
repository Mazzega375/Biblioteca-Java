package br.edu.biblioteca.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void usuario_novo_nao_esta_bloqueado() {
        Usuario usuario = new Usuario(1, "Ana Silva", "ana@email.com", Usuario.Tipo.ALUNO);
        assertFalse(usuario.isBloqueado());
    }

    @Test
    void bloqueio_e_desbloqueio() {
        Usuario usuario = new Usuario(1, "Ana Silva", "ana@email.com", Usuario.Tipo.ALUNO);

        usuario.setBloqueado(true);
        assertTrue(usuario.isBloqueado());

        usuario.setBloqueado(false);
        assertFalse(usuario.isBloqueado());
    }

    @Test
    void toString_contem_nome_e_email() {
        Usuario usuario = new Usuario(2, "Carlos", "carlos@email.com", Usuario.Tipo.PROFESSOR);
        String texto = usuario.toString();

        assertTrue(texto.contains("Carlos"));
        assertTrue(texto.contains("carlos@email.com"));
    }

    @Test
    void toString_usuario_bloqueado_indica_status() {
        Usuario usuario = new Usuario(3, "Pedro", "pedro@email.com", Usuario.Tipo.ALUNO);
        usuario.setBloqueado(true);

        assertTrue(usuario.toString().contains("BLOQUEADO"));
    }

    @Test
    void toCsv_e_fromCsv_sao_inversos() {
        Usuario original = new Usuario(5, "Maria", "maria@email.com", Usuario.Tipo.BIBLIOTECARIO);
        original.setBloqueado(true);

        Usuario recuperado = Usuario.fromCsv(original.toCsv());

        assertEquals(original.getId(), recuperado.getId());
        assertEquals(original.getNome(), recuperado.getNome());
        assertEquals(original.getEmail(), recuperado.getEmail());
        assertEquals(original.getTipo(), recuperado.getTipo());
        assertEquals(original.isBloqueado(), recuperado.isBloqueado());
    }

    @Test
    void fromCsv_com_linha_invalida_lanca_excecao() {
        assertThrows(IllegalArgumentException.class, () -> Usuario.fromCsv("1;so_nome"));
    }
}
