package br.unisales.database.table.primery_key;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class LivroCategoriaId implements Serializable {
    @Column(name = "livro_isbn", length = 20)
    private String livroIsbn;
    
    @Column(name = "categoria_id")
    private Integer categoriaId;

    public LivroCategoriaId() {
    }

    public LivroCategoriaId(String livroIsbn, Integer categoriaId) {
        this.livroIsbn = livroIsbn;
        this.categoriaId = categoriaId;
    }

    public String getLivroIsbn() {
        return livroIsbn;
    }

    public void setLivroIsbn(String livroIsbn) {
        this.livroIsbn = livroIsbn;
    }

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LivroCategoriaId that = (LivroCategoriaId) o;
        return Objects.equals(livroIsbn, that.livroIsbn) && Objects.equals(categoriaId, that.categoriaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(livroIsbn, categoriaId);
    }
}
