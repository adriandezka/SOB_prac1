/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import java.io.Serializable;

/**
 *
 * @authors Lucas Sierra, Adrian Rusu
 */
@Entity
@Table(name = "CUSTOMER")
@NamedQueries({
    @NamedQuery(name = "Customer.findAll", query = "SELECT c FROM Customer c"),
    @NamedQuery(name = "Customer.findByUser", query = "SELECT c FROM Customer c WHERE LOWER(c.user) = LOWER(:user)")
})
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false)
    private String nom;
    
    @Column(name = "username", nullable = false, unique = true)
    private String user;
    
    @JsonbTransient
    @Column(name = "password", nullable = false)
    private String pass;
    
    public Customer(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
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
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        return this.id != null && this.id.equals(other.id);
    }
}
