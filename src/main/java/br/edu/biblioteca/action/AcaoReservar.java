package br.edu.biblioteca.action;

import br.edu.biblioteca.model.Reserva;
import br.edu.biblioteca.repository.ReservaRepository;

public class AcaoReservar implements Acao {

    private final ReservaRepository reservaRepo;
    private final Reserva reserva;

    public AcaoReservar(ReservaRepository reservaRepo, Reserva reserva) {
        this.reservaRepo = reservaRepo;
        this.reserva = reserva;
    }

    @Override
    public void executar() {
        reservaRepo.salvar(reserva);
        System.out.println("✔ Reserva criada: " + reserva);
    }

    @Override
    public void desfazer() {
        reserva.setStatus(Reserva.Status.CANCELADA);
        reservaRepo.salvar(reserva);
        System.out.println("↶ Reserva cancelada: " + reserva);
    }

    @Override
    public String descricao() {
        return String.format("Reserva do ISBN '%s' pelo usuário %d em %s",
                reserva.getIsbnLivro(), reserva.getUsuarioId(), reserva.getDataReserva());
    }
}
