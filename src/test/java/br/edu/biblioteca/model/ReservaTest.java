package br.edu.biblioteca.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class ReservaTest {

    @Test
    void reserva_nova_tem_status_aguardando() {
        Reserva reserva = new Reserva(1, 5, "978-1", LocalDate.now());
        assertEquals(Reserva.Status.AGUARDANDO, reserva.getStatus());
    }

    @Test
    void mudanca_de_status() {
        Reserva reserva = new Reserva(1, 5, "978-1", LocalDate.now());

        reserva.setStatus(Reserva.Status.ATENDIDA);
        assertEquals(Reserva.Status.ATENDIDA, reserva.getStatus());

        reserva.setStatus(Reserva.Status.CANCELADA);
        assertEquals(Reserva.Status.CANCELADA, reserva.getStatus());
    }

    @Test
    void toCsv_e_fromCsv_sao_inversos() {
        Reserva original = new Reserva(3, 7, "978-2", LocalDate.of(2024, 6, 15));
        original.setStatus(Reserva.Status.ATENDIDA);

        Reserva recuperado = Reserva.fromCsv(original.toCsv());

        assertEquals(original.getId(), recuperado.getId());
        assertEquals(original.getUsuarioId(), recuperado.getUsuarioId());
        assertEquals(original.getIsbnLivro(), recuperado.getIsbnLivro());
        assertEquals(original.getDataReserva(), recuperado.getDataReserva());
        assertEquals(original.getStatus(), recuperado.getStatus());
    }

    @Test
    void toString_contem_id_e_isbn() {
        Reserva reserva = new Reserva(10, 2, "978-5", LocalDate.now());
        String texto = reserva.toString();

        assertTrue(texto.contains("10"));
        assertTrue(texto.contains("978-5"));
    }
}
