package br.unisales.database.table;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "categoria")
public class Categoria {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "nome", nullable = false, length = 100, unique = true)
    private String nome;
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LivroCategoria> livroCategorias = new ArrayList<>();

    public Categoria() {
    }

    public Categoria(Integer id, String nome, List<LivroCategoria> livroCategorias) {
        this.id = id;
        this.nome = nome;
        this.livroCategorias = livroCategorias;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<LivroCategoria> getLivroCategorias() {
        return livroCategorias;
    }

    public void setLivroCategorias(List<LivroCategoria> livroCategorias) {
        this.livroCategorias = livroCategorias;
    }
}
