package br.unisales.database.table;

import br.unisales.Enumeration.ExemplarStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "exemplar")
public class Exemplar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "livro_isbn", nullable = false, length = 20)
    private String isbnLivro;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ExemplarStatusEnum status = ExemplarStatusEnum.DISPONIVEL;

    public Exemplar() {
    }

    public Exemplar(Integer id, String isbnLivro, ExemplarStatusEnum status) {
        this.id = id;
        this.isbnLivro = isbnLivro;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIsbnLivro() {
        return isbnLivro;
    }

    public void setIsbnLivro(String isbnLivro) {
        this.isbnLivro = isbnLivro;
    }

    public ExemplarStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ExemplarStatusEnum status) {
        this.status = status;
    }
}
