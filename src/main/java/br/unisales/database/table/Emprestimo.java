package br.unisales.database.table;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "emprestimo")
public class Emprestimo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "exemplar_id", nullable = false)
    private Integer exemplarId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livro_isbn", nullable = false)
    private Livro livro;

    @Column(name = "data_emprestimo", nullable = false)
    private LocalDate dataEmprestimo;

    @Column(name = "data_prevista", nullable = false)
    private LocalDate dataPrevista;

    @Column(name = "data_devolucao")
    private LocalDate dataDevolucao;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "EMPRESTADO";

    @Default
    @Column(name = "devolvido", nullable = false)
    private Boolean devolvido = Boolean.FALSE;

    public void devolver(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
        this.devolvido = Boolean.TRUE;
        this.status = "DEVOLVIDO";
    }

    public void renovar(LocalDate novaDataPrevista) {
        this.dataPrevista = novaDataPrevista;
        this.status = "RENOVADO";
    }
}
