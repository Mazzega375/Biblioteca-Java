package br.unisales.database.table.primery_key;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class LivroAutorId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "livro_isbn", nullable = false, length = 20)
    private String livroIsbn;

    @Column(name = "autor_id", nullable = false)
    private Integer autorId;

    public LivroAutorId() {
    }

    public LivroAutorId(String livroIsbn, Integer autorId) {
        this.livroIsbn = livroIsbn;
        this.autorId = autorId;
    }

    public String getLivroIsbn() {
        return livroIsbn;
    }

    public void setLivroIsbn(String livroIsbn) {
        this.livroIsbn = livroIsbn;
    }

    public Integer getAutorId() {
        return autorId;
    }

    public void setAutorId(Integer autorId) {
        this.autorId = autorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LivroAutorId that = (LivroAutorId) o;
        return Objects.equals(livroIsbn, that.livroIsbn) && Objects.equals(autorId, that.autorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(livroIsbn, autorId);
    }
}
