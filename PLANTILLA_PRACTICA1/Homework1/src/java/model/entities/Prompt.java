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
 * @author adrian_sru
 */
@Entity
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
    
    // Format: ",ia,python," (coma al principi i al final)
    @Column(length = 1000)
    private String etiquetes;
    
    @Column(nullable = false)
    private LocalDate dataCreacio;
    
    @Column(nullable = false)
    private boolean privat;
    
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Tool> tools = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getEtiquetes() {
        return etiquetes;
    }

    public void setEtiquetes(String etiquetes) {
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
    
    
    
}