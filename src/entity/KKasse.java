/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "KKasse")
@NamedQueries({
    @NamedQuery(name = "KKasse.findAll", query = "SELECT k FROM KKasse k"),
    @NamedQuery(name = "KKasse.findByKassID", query = "SELECT k FROM KKasse k WHERE k.kassID = :kassID"),
    @NamedQuery(name = "KKasse.findByName", query = "SELECT k FROM KKasse k WHERE k.name = :name"),
    @NamedQuery(name = "KKasse.findByStrasse", query = "SELECT k FROM KKasse k WHERE k.strasse = :strasse"),
    @NamedQuery(name = "KKasse.findByPlz", query = "SELECT k FROM KKasse k WHERE k.plz = :plz"),
    @NamedQuery(name = "KKasse.findByOrt", query = "SELECT k FROM KKasse k WHERE k.ort = :ort"),
    @NamedQuery(name = "KKasse.findByTel", query = "SELECT k FROM KKasse k WHERE k.tel = :tel"),
    @NamedQuery(name = "KKasse.findByFax", query = "SELECT k FROM KKasse k WHERE k.fax = :fax"),
    @NamedQuery(name = "KKasse.findByKNr", query = "SELECT k FROM KKasse k WHERE k.kNr = :kNr")})
public class KKasse implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "KassID")
    private Long kassID;
    @Basic(optional = false)
    @Column(name = "Name")
    private String name;
    @Basic(optional = false)
    @Column(name = "Strasse")
    private String strasse;
    @Basic(optional = false)
    @Column(name = "PLZ")
    private String plz;
    @Basic(optional = false)
    @Column(name = "Ort")
    private String ort;
    @Basic(optional = false)
    @Column(name = "Tel")
    private String tel;
    @Basic(optional = false)
    @Column(name = "Fax")
    private String fax;
    @Basic(optional = false)
    @Column(name = "KNr")
    private String kNr;

    public KKasse() {
    }

    public KKasse(Long kassID) {
        this.kassID = kassID;
    }

    public KKasse(Long kassID, String name, String strasse, String plz, String ort, String tel, String fax, String kNr) {
        this.kassID = kassID;
        this.name = name;
        this.strasse = strasse;
        this.plz = plz;
        this.ort = ort;
        this.tel = tel;
        this.fax = fax;
        this.kNr = kNr;
    }

    public Long getKassID() {
        return kassID;
    }

    public void setKassID(Long kassID) {
        this.kassID = kassID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getKNr() {
        return kNr;
    }

    public void setKNr(String kNr) {
        this.kNr = kNr;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (kassID != null ? kassID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KKasse)) {
            return false;
        }
        KKasse other = (KKasse) object;
        if ((this.kassID == null && other.kassID != null) || (this.kassID != null && !this.kassID.equals(other.kassID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.KKasse[kassID=" + kassID + "]";
    }

}
