package de.offene_pflege.entity.prescription;

import de.offene_pflege.entity.DefaultEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "acme")
@ToString
@Getter
@Setter
@NoArgsConstructor
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

}
