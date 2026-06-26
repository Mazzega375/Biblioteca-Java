package br.edu.biblioteca.action;

import br.edu.biblioteca.model.Reserva;
import br.edu.biblioteca.repository.ReservaRepository;

public class AcaoCancelarReserva implements Acao {

    private final ReservaRepository reservaRepo;
    private final Reserva reserva;
    private Reserva.Status statusAnterior;

    public AcaoCancelarReserva(ReservaRepository reservaRepo, Reserva reserva) {
        this.reservaRepo = reservaRepo;
        this.reserva = reserva;
    }

    @Override
    public void executar() {
        statusAnterior = reserva.getStatus();
        reserva.setStatus(Reserva.Status.CANCELADA);
        reservaRepo.salvar(reserva);
        System.out.println("✔ Reserva cancelada: " + reserva);
    }

    @Override
    public void desfazer() {
        reserva.setStatus(statusAnterior != null ? statusAnterior : Reserva.Status.AGUARDANDO);
        reservaRepo.salvar(reserva);
        System.out.println("↶ Cancelamento desfeito: " + reserva);
    }

    @Override
    public String descricao() {
        return String.format("Cancelamento da reserva #%d (ISBN '%s')", reserva.getId(), reserva.getIsbnLivro());
    }
}
