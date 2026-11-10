/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

/**
 *
 * @author adrian_sru
 */
public class Tool {
    @Id
    private long id;
    private String nom;
    private String descripcio;
    private String llenguatge;  // canviar a enum?
    private String codi;        // ?
    private String autor;
    private String versio;
    private String dataCreacio; // YYYY-MM-DD
    
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
    
    public String getAutor() {
        return this.autor;
    }
    
    public void setAutor(String autor) {
        this.autor = autor;
    }
    
    public String getVersio() {
        return this.versio;
    }
    
    public void setVersio(String versio) {
        this.versio = versio;
    }
    
    public String getDataCreacio() {
        return this.dataCreacio;
    }
    
    public void setDataCreacio(String dataCreacio) {
        this.dataCreacio = dataCreacio;
    }
}
