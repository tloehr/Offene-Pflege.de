package de.offene_pflege.entity.prescription;

import de.offene_pflege.entity.DefaultEntity;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "acme")
@NoArgsConstructor
@ToString
public class ACME extends DefaultEntity implements Serializable {
    @Basic(optional = false)
    @Column(name = "Firma")
    private String name;
    @Column(name = "Strasse")
    private String street;
    @Column(name = "PLZ")
    private String zipcode;
    @Column(name = "Ort")
    private String city;
    @Column(name = "Tel")
    private String tel;
    @Column(name = "Fax")
    private String fax;
    @Column(name = "WWW")
    private String www;

    public ACME(String name, String street, String zipcode, String city, String tel, String fax, String www) {
        this.name = name;
        this.street = street;
        this.zipcode = zipcode;
        this.city = city;
        this.tel = tel;
        this.fax = fax;
        this.www = www;
    }
    public String getName() {
        return name;
    }

    public void setName(String firma) {
        this.name = firma;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String strasse) {
        this.street = strasse;
    }

    public String getPlz() {
        return zipcode;
    }

    public void setPlz(String plz) {
        this.zipcode = plz;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String ort) {
        this.city = ort;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getWww() {
        return www;
    }

    public void setWww(String www) {
        this.www = www;
    }

}
