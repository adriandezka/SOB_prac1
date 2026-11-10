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
public class Prompt implements Serializable {
    private long id;
    private String titol;
    private String descripcio;
    private String plantilla;
    private Customer autor;
    private Set<String> etiquetes = new HashSet<>();   // @elementCollection?
    private LocalDate dataCreacio; // YYYY-MM-DD
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
    
    
    
}