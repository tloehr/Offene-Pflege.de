/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.entity.info;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author tloehr
 */
@Entity
@Table(name = "hinsurance")
public class HInsurance implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Version
    @Column(name = "version")
    private Long version;

    public HInsurance() {
    }

    public HInsurance(Long kassID) {
        this.kassID = kassID;
    }

    public HInsurance(Long kassID, String name, String strasse, String plz, String ort, String tel, String fax, String kNr) {
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

        if (!(object instanceof HInsurance)) {
            return false;
        }
        HInsurance other = (HInsurance) object;
        if ((this.kassID == null && other.kassID != null) || (this.kassID != null && !this.kassID.equals(other.kassID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.HInsurance[kassID=" + kassID + "]";
    }

}
