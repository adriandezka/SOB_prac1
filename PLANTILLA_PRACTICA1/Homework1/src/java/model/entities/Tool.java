/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author adrian_sru
 */
@Entity
public class Tool implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nom;
    
    private String descripcio;
    
    @Column(nullable = false)
    private String llenguatge;
    
    @Lob
    @Column(nullable = false)
    private String codi;
    
    @ManyToOne
    private Customer autor;
    
    private String versio;
    
    @Column(nullable = false)
    private LocalDate dataCreacio;
    
    @Column(nullable = false)
    private boolean privat;
    
    public long getId() {
        return this.id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getNom() {
        return this.nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getDescripcio() {
        return this.descripcio;
    }
    
    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }
    
    public String getLlenguatge() {
        return this.llenguatge;
    }
    
    public void setLlenguatge(String llenguatge) {
        this.llenguatge = llenguatge;
    }
    
    public String getCodi() {
        return this.codi;
    }
    
    public void setCodi(String codi) {
        this.codi = codi;
    }   
    
    public Customer getAutor() {
        return this.autor;
    }
    
    public void setAutor(Customer autor) {
        this.autor = autor;
    }
    
    public String getVersio() {
        return this.versio;
    }
    
    public void setVersio(String versio) {
        this.versio = versio;
    }
    
    public LocalDate getDataCreacio() {
        return this.dataCreacio;
    }
    
    public void setDataCreacio(LocalDate dataCreacio) {
        this.dataCreacio = dataCreacio;
    }
    
    public Boolean getPrivat(){
        return this.privat;
    }
    
    public void setPrivat(Boolean privat){
        this.privat = privat;
    }
}
