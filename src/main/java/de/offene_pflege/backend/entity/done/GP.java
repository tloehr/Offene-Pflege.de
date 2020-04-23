package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "gp")
public class GP extends DefaultEntity {
    private String anrede;
    private String titel;
    private String name;
    private String vorname;
    private String strasse;
    private String plz;
    private String ort;
    private String tel;
    private String fax;
    private String mobil;
    private String eMail;
    private Integer status;
    private boolean neurologist;
    private boolean dermatology;


    @Basic(optional = false)
    @Column(name = "Anrede")
    public String getAnrede() {
        return anrede;
    }

    public void setAnrede(String anrede) {
        this.anrede = anrede;
    }

    @Basic(optional = false)
    @Column(name = "Titel")
    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
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
    @Column(name = "Vorname")
    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
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

    @Column(name = "Mobil")
    public String getMobil() {
        return mobil;
    }

    public void setMobil(String mobil) {
        this.mobil = mobil;
    }

    @Column(name = "EMail")
    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    @Column(name = "Status")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Basic(optional = false)
    @Column(name = "neurologist")
    public boolean isNeurologist() {
        return neurologist;
    }

    public void setNeurologist(boolean neurologist) {
        this.neurologist = neurologist;
    }

    @Basic(optional = false)
    @Column(name = "skin")
    public boolean isDermatology() {
        return dermatology;
    }

    public void setDermatology(boolean dermatology) {
        this.dermatology = dermatology;
    }

    public GP() {
    }
}
