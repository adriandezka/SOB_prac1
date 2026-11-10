/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;
import java.time.LocalDate;
import java.io.Serializable;

/**
 *
 * @author adrian_sru
 */
public class Tool implements Serializable{
    
    private long id;
    private String nom;
    private String descripcio;
    private String llenguatge;  // canviar a enum?
    private String codi;        // ?
    private Customer autor;
    private String versio;
    private LocalDate dataCreacio;
    private Boolean privat;
    
    public long getId() {
        return this.id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String nom() {
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
    
    public Boolean isPrivat(){
        return this.privat;
    }
    
    public void setPrivat(Boolean privat){
        this.privat = privat;
    }
}
