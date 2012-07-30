/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "Einrichtungen")
@NamedQueries({
    @NamedQuery(name = "Einrichtungen.findAll", query = "SELECT e FROM Einrichtungen e ORDER BY e.eKennung "),
    @NamedQuery(name = "Einrichtungen.findByEKennung", query = "SELECT e FROM Einrichtungen e WHERE e.eKennung = :eKennung"),
    @NamedQuery(name = "Einrichtungen.findByBezeichnung", query = "SELECT e FROM Einrichtungen e WHERE e.bezeichnung = :bezeichnung"),
    @NamedQuery(name = "Einrichtungen.findByStrasse", query = "SELECT e FROM Einrichtungen e WHERE e.strasse = :strasse"),
    @NamedQuery(name = "Einrichtungen.findByPlz", query = "SELECT e FROM Einrichtungen e WHERE e.plz = :plz"),
    @NamedQuery(name = "Einrichtungen.findByOrt", query = "SELECT e FROM Einrichtungen e WHERE e.ort = :ort"),
    @NamedQuery(name = "Einrichtungen.findByTel", query = "SELECT e FROM Einrichtungen e WHERE e.tel = :tel"),
    @NamedQuery(name = "Einrichtungen.findByFax", query = "SELECT e FROM Einrichtungen e WHERE e.fax = :fax")})
public class Einrichtungen implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "EKennung")
    private String eKennung;
    @Column(name = "Bezeichnung")
    private String bezeichnung;
    @Column(name = "Strasse")
    private String strasse;
    @Column(name = "PLZ")
    private String plz;
    @Column(name = "Ort")
    private String ort;
    @Column(name = "Tel")
    private String tel;
    @Column(name = "Fax")
    private String fax;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "einrichtung")
    private Collection<Uebergabebuch> uebergabeberichte;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "einrichtung")
    private Collection<Stationen> stationen;

    public Einrichtungen() {
    }

    public Collection<Stationen> getStationen() {
        return stationen;
    }

    public Collection<Uebergabebuch> getUebergabeberichte() {
        return uebergabeberichte;
    }

    public String getEKennung() {
        return eKennung;
    }

    public void setEKennung(String eKennung) {
        this.eKennung = eKennung;
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

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
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
        hash += (eKennung != null ? eKennung.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Einrichtungen)) {
            return false;
        }
        Einrichtungen other = (Einrichtungen) object;
        if ((this.eKennung == null && other.eKennung != null) || (this.eKennung != null && !this.eKennung.equals(other.eKennung))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return bezeichnung;
    }
}
