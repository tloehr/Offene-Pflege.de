/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.nursingprocess;

import entity.info.BWInfoKat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author tloehr
 */
@Entity
@Table(name = "Massnahmen")
@NamedQueries({
        @NamedQuery(name = "Massnahmen.findAll", query = "SELECT m FROM Intervention m"),
        @NamedQuery(name = "Massnahmen.findByMassID", query = "SELECT m FROM Intervention m WHERE m.massID = :massID"),
        @NamedQuery(name = "Massnahmen.findByBezeichnung", query = "SELECT m FROM Intervention m WHERE m.bezeichnung = :bezeichnung"),
        @NamedQuery(name = "Massnahmen.findByDauer", query = "SELECT m FROM Intervention m WHERE m.dauer = :dauer"),
        @NamedQuery(name = "Massnahmen.findByMassArt", query = "SELECT m FROM Intervention m WHERE m.massArt = :massArt"),
        @NamedQuery(name = "Massnahmen.findByAktiv", query = "SELECT m FROM Intervention m WHERE m.aktiv = :aktiv")})
public class Intervention implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(name = "Aktiv")
    private Boolean aktiv;

    @JoinColumn(name = "BWIKID", referencedColumnName = "BWIKID")
    @ManyToOne
    private BWInfoKat kategorie;

    public Intervention() {
    }

    public Intervention(Long massID) {
        this.massID = massID;
    }

    public Intervention(String bezeichnung, BigDecimal dauer, int massArt, BWInfoKat kategorie) {
        this.bezeichnung = bezeichnung;
        this.dauer = dauer;
        this.massArt = massArt;
        this.kategorie = kategorie;
        this.aktiv = true;
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

    public BWInfoKat getKategorie() {
        return kategorie;
    }

    public void setKategorie(BWInfoKat kategorie) {
        this.kategorie = kategorie;
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
        if (!(object instanceof Intervention)) {
            return false;
        }
        Intervention other = (Intervention) object;
        if ((this.massID == null && other.massID != null) || (this.massID != null && !this.massID.equals(other.massID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return bezeichnung;
    }

}
