package br.unisales.database.table;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "multa")
public class Multa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "emprestimo_id", nullable = false)
    private Integer emprestimoId;

    @Column(name = "valor", nullable = false)
    private Double valor;

    @Column(name = "dias_atraso", nullable = false)
    private Integer diasAtraso;

    @Column(name = "quitada", nullable = false)
    private Boolean quitada = Boolean.FALSE;

    public Multa() {
    }

    public Multa(Integer id, Integer emprestimoId, Double valor, Integer diasAtraso, Boolean quitada) {
        this.id = id;
        this.emprestimoId = emprestimoId;
        this.valor = valor;
        this.diasAtraso = diasAtraso;
        this.quitada = quitada;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmprestimoId() {
        return emprestimoId;
    }

    public void setEmprestimoId(Integer emprestimoId) {
        this.emprestimoId = emprestimoId;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Integer getDiasAtraso() {
        return diasAtraso;
    }

    public void setDiasAtraso(Integer diasAtraso) {
        this.diasAtraso = diasAtraso;
    }

    public Boolean getQuitada() {
        return quitada;
    }

    public void setQuitada(Boolean quitada) {
        this.quitada = quitada;
    }
}