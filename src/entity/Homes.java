/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "Homes")
//@NamedQueries({
//    @NamedQuery(name = "Einrichtungen.findAll", query = "SELECT e FROM Homes e ORDER BY e.eKennung "),
//    @NamedQuery(name = "Einrichtungen.findByEKennung", query = "SELECT e FROM Homes e WHERE e.eKennung = :eKennung"),
//    @NamedQuery(name = "Einrichtungen.findByBezeichnung", query = "SELECT e FROM Homes e WHERE e.bezeichnung = :bezeichnung"),
//    @NamedQuery(name = "Einrichtungen.findByStrasse", query = "SELECT e FROM Homes e WHERE e.strasse = :strasse"),
//    @NamedQuery(name = "Einrichtungen.findByPlz", query = "SELECT e FROM Homes e WHERE e.plz = :plz"),
//    @NamedQuery(name = "Einrichtungen.findByOrt", query = "SELECT e FROM Homes e WHERE e.ort = :ort"),
//    @NamedQuery(name = "Einrichtungen.findByTel", query = "SELECT e FROM Homes e WHERE e.tel = :tel"),
//    @NamedQuery(name = "Einrichtungen.findByFax", query = "SELECT e FROM Homes e WHERE e.fax = :fax")})
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "einrichtung")
    private Collection<Handovers> handovers;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "home")
    private Collection<Station> station;

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
}
