package br.edu.biblioteca.action;

import br.edu.biblioteca.model.Reserva;
import br.edu.biblioteca.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AcaoReservaTest {

    private static class ReservaRepositoryFake extends ReservaRepository {
        final List<Reserva> reservas = new ArrayList<>();

        ReservaRepositoryFake() {}

        @Override public void salvar(Reserva r) { reservas.add(r); }
    }

    private ReservaRepositoryFake repo;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        repo = new ReservaRepositoryFake();
        reserva = new Reserva(1, 5, "978-1", LocalDate.now());
    }

    @Test
    void reservar_persiste_reserva() {
        AcaoReservar acao = new AcaoReservar(repo, reserva);
        acao.executar();

        assertEquals(1, repo.reservas.size());
        assertEquals(Reserva.Status.AGUARDANDO, repo.reservas.get(0).getStatus());
    }

    @Test
    void desfazer_reserva_muda_status_para_cancelada() {
        AcaoReservar acao = new AcaoReservar(repo, reserva);
        acao.executar();
        acao.desfazer();

        assertEquals(Reserva.Status.CANCELADA, reserva.getStatus());
    }

    @Test
    void cancelar_reserva_muda_status() {
        AcaoCancelarReserva acao = new AcaoCancelarReserva(repo, reserva);
        acao.executar();

        assertEquals(Reserva.Status.CANCELADA, reserva.getStatus());
    }

    @Test
    void desfazer_cancelamento_restaura_status_aguardando() {
        AcaoCancelarReserva acao = new AcaoCancelarReserva(repo, reserva);
        acao.executar();
        acao.desfazer();

        assertEquals(Reserva.Status.AGUARDANDO, reserva.getStatus());
    }

    @Test
    void descricao_reservar_contem_isbn_e_usuario() {
        AcaoReservar acao = new AcaoReservar(repo, reserva);
        String desc = acao.descricao();

        assertTrue(desc.contains("978-1"));
        assertTrue(desc.contains("5"));
    }

    @Test
    void descricao_cancelar_contem_id_da_reserva() {
        AcaoCancelarReserva acao = new AcaoCancelarReserva(repo, reserva);
        String desc = acao.descricao();

        assertTrue(desc.contains("1"));
        assertTrue(desc.contains("978-1"));
    }
}
