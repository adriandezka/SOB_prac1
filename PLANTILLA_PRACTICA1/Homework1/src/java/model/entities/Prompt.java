/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Lucas Sierra, Adrián Rusu
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Prompt.findAll", query = "SELECT p FROM Prompt p ORDER BY p.titol ASC")
})
public class Prompt implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String titol;
    
    private String descripcio;
    
    @Lob
    @Column(nullable = false)
    private String plantilla;
    
    @ManyToOne
    private Customer autor;
    
    //Es crea tabla PROMPT_ETIQUETES(PROMPT_ID, ETIQUETA)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "PROMPT_ETIQUETES", joinColumns = @JoinColumn(name = "PROMPT_ID"))
    @Column(name = "ETIQUETA")
    private Set<String> etiquetes = new HashSet<>();
    
    @Column(nullable = false)
    private LocalDate dataCreacio;
    
    @Column(nullable = false)
    private boolean privat;
    
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Tool> tools = new HashSet<>();
    
    public Prompt(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitol() {
        return titol;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    public String getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }

    public Customer getAutor() {
        return autor;
    }

    public void setAutor(Customer autor) {
        this.autor = autor;
    }

    public Set<String> getEtiquetes() {
        return etiquetes;
    }

    public void setEtiquetes(Set<String> etiquetes) {
        this.etiquetes = etiquetes;
    }

    public LocalDate getDataCreacio() {
        return dataCreacio;
    }

    public void setDataCreacio(LocalDate dataCreacio) {
        this.dataCreacio = dataCreacio;
    }

    public boolean isPrivat() {
        return privat;
    }

    public void setPrivat(boolean privat) {
        this.privat = privat;
    }

    public Set<Tool> getTools() {
        return tools;
    }

    public void setTools(Set<Tool> tools) {
        this.tools = tools;
    }

    @Override
    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Prompt)) {
            return false;
        }
        Prompt other = (Prompt) object;
        return this.id != null && this.id.equals(other.id);
    }
}