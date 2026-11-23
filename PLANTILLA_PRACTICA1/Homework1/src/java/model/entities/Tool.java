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
 * @author Lucas Sierra, Adrián Rusu
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Tool.findAll", query = "SELECT t FROM Tool t ORDER BY t.nom ASC")
})
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
    
    public Tool() {}
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
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
    
    public boolean isPrivat(){
        return this.privat;
    }
    
    public void setPrivat(boolean privat){
        this.privat = privat;
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
        if (!(object instanceof Tool)) {
            return false;
        }
        Tool other = (Tool) object;
        return this.id != null && this.id.equals(other.id);
    }
}
