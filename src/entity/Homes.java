/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import entity.reports.Handovers;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author tloehr
 */
@Entity
@Table(name = "homes")

public class Homes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "EID")
    private String eid;
    @Column(name = "Name")
    private String bezeichnung;
    @Column(name = "Str")
    private String strasse;
    @Column(name = "ZIP")
    private String zip;
    @Column(name = "City")
    private String ort;
    @Column(name = "Tel")
    private String tel;
    @Column(name = "Fax")
    private String fax;
    @Version
    @Column(name = "version")
    private Long version;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "home")
    private Collection<Handovers> handovers;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "home")
    private List<Station> station;

    public Homes() {
    }

    public String getEID() {
        return eid;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getZIP() {
        return zip;
    }


    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (eid != null ? eid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Homes)) {
            return false;
        }
        Homes other = (Homes) object;
        if ((this.eid == null && other.eid != null) || (this.eid != null && !this.eid.equals(other.eid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return bezeichnung;
    }

    public List<Station> getStations() {
        return station;
    }
}
