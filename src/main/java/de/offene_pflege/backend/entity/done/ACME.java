package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "acme")
public class ACME extends DefaultEntity {
    private String name;
    private String street;
    private String zipcode;
    private String city;
    private String tel;
    private String fax;
    private String www;

    public ACME() {
    }

    @Basic(optional = false)
    @Column(name = "Firma")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "Strasse")
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Column(name = "PLZ")
    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    @Column(name = "Ort")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "Tel")
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Column(name = "Fax")
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Column(name = "WWW")
    public String getWww() {
        return www;
    }

    public void setWww(String www) {
        this.www = www;
    }
}