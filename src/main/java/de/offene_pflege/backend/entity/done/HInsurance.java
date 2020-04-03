/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author tloehr
 */
@Entity
@Table(name = "hinsurance")
public class HInsurance extends DefaultEntity {
    private String name;
    private String strasse;
    private String plz;
    private String ort;
    private String tel;
    private String fax;
    private String kNr;

    public HInsurance() {
    }

    @Basic(optional = false)
    @Column(name = "Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic(optional = false)
    @Column(name = "Strasse")
    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    @Basic(optional = false)
    @Column(name = "PLZ")
    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    @Basic(optional = false)
    @Column(name = "Ort")
    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    @Basic(optional = false)
    @Column(name = "Tel")
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Basic(optional = false)
    @Column(name = "Fax")
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Basic(optional = false)
    @Column(name = "KNr")
    public String getkNr() {
        return kNr;
    }

    public void setkNr(String kNr) {
        this.kNr = kNr;
    }
}
