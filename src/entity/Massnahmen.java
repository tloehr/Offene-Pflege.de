/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "Massnahmen")
@NamedQueries({
    @NamedQuery(name = "Massnahmen.findAll", query = "SELECT m FROM Massnahmen m"),
    @NamedQuery(name = "Massnahmen.findByMassID", query = "SELECT m FROM Massnahmen m WHERE m.massID = :massID"),
    @NamedQuery(name = "Massnahmen.findByBezeichnung", query = "SELECT m FROM Massnahmen m WHERE m.bezeichnung = :bezeichnung"),
    @NamedQuery(name = "Massnahmen.findByDauer", query = "SELECT m FROM Massnahmen m WHERE m.dauer = :dauer"),
    @NamedQuery(name = "Massnahmen.findByMassArt", query = "SELECT m FROM Massnahmen m WHERE m.massArt = :massArt"),
    @NamedQuery(name = "Massnahmen.findByBwikid", query = "SELECT m FROM Massnahmen m WHERE m.bwikid = :bwikid"),
    @NamedQuery(name = "Massnahmen.findByPsid", query = "SELECT m FROM Massnahmen m WHERE m.psid = :psid"),
    @NamedQuery(name = "Massnahmen.findByAktiv", query = "SELECT m FROM Massnahmen m WHERE m.aktiv = :aktiv")})
public class Massnahmen implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "MassID")
    private Long massID;
    @Basic(optional = false)
    @Column(name = "Bezeichnung")
    private String bezeichnung;
    @Basic(optional = false)
    @Column(name = "Dauer")
    private BigDecimal dauer;
    @Basic(optional = false)
    @Column(name = "MassArt")
    private int massArt;
    @Basic(optional = false)
    @Column(name = "BWIKID")
    private long bwikid;
    @Lob
    @Column(name = "XMLT")
    private String xmlt;
    @Column(name = "PSID")
    private Short psid;
    @Column(name = "Aktiv")
    private Boolean aktiv;

    public Massnahmen() {
    }

    public Massnahmen(Long massID) {
        this.massID = massID;
    }

    public Massnahmen(Long massID, String bezeichnung, BigDecimal dauer, int massArt, long bwikid) {
        this.massID = massID;
        this.bezeichnung = bezeichnung;
        this.dauer = dauer;
        this.massArt = massArt;
        this.bwikid = bwikid;
    }

    public Long getMassID() {
        return massID;
    }

    public void setMassID(Long massID) {
        this.massID = massID;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public BigDecimal getDauer() {
        return dauer;
    }

    public void setDauer(BigDecimal dauer) {
        this.dauer = dauer;
    }

    public int getMassArt() {
        return massArt;
    }

    public void setMassArt(int massArt) {
        this.massArt = massArt;
    }

    public long getBwikid() {
        return bwikid;
    }

    public void setBwikid(long bwikid) {
        this.bwikid = bwikid;
    }

    public String getXmlt() {
        return xmlt;
    }

    public void setXmlt(String xmlt) {
        this.xmlt = xmlt;
    }

    public Short getPsid() {
        return psid;
    }

    public void setPsid(Short psid) {
        this.psid = psid;
    }

    public Boolean getAktiv() {
        return aktiv;
    }

    public void setAktiv(Boolean aktiv) {
        this.aktiv = aktiv;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (massID != null ? massID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Massnahmen)) {
            return false;
        }
        Massnahmen other = (Massnahmen) object;
        if ((this.massID == null && other.massID != null) || (this.massID != null && !this.massID.equals(other.massID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.Massnahmen[massID=" + massID + "]";
    }

}
