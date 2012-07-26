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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "Raeume")
@NamedQueries({
    @NamedQuery(name = "Raeume.findAll", query = "SELECT r FROM Raeume r"),
    @NamedQuery(name = "Raeume.findByRaumID", query = "SELECT r FROM Raeume r WHERE r.raumID = :raumID"),
    @NamedQuery(name = "Raeume.findByBezeichnung", query = "SELECT r FROM Raeume r WHERE r.bezeichnung = :bezeichnung"),
    @NamedQuery(name = "Raeume.findByEtage", query = "SELECT r FROM Raeume r WHERE r.etage = :etage"),
    @NamedQuery(name = "Raeume.findByEinzel", query = "SELECT r FROM Raeume r WHERE r.einzel = :einzel"),
    @NamedQuery(name = "Raeume.findByBad", query = "SELECT r FROM Raeume r WHERE r.bad = :bad")})
public class Raeume implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "RaumID")
    private Long raumID;
    @Column(name = "Bezeichnung")
    private String bezeichnung;
    @Column(name = "Etage")
    private Short etage;
    @Column(name = "Einzel")
    private Boolean einzel;
    @Column(name = "Bad")
    private Boolean bad;
    @JoinColumn(name = "StatID", referencedColumnName = "StatID")
    @ManyToOne
    private Stationen station;

    public Raeume() {
    }

    public Long getRaumID() {
        return raumID;
    }

    public void setRaumID(Long raumID) {
        this.raumID = raumID;
    }

    public Stationen getStation() {
        return station;
    }

    public void setStation(Stationen station) {
        this.station = station;
    }
    
    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public Short getEtage() {
        return etage;
    }

    public void setEtage(Short etage) {
        this.etage = etage;
    }

    public Boolean getEinzel() {
        return einzel;
    }

    public void setEinzel(Boolean einzel) {
        this.einzel = einzel;
    }

    public Boolean getBad() {
        return bad;
    }

    public void setBad(Boolean bad) {
        this.bad = bad;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (raumID != null ? raumID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Raeume)) {
            return false;
        }
        Raeume other = (Raeume) object;
        if ((this.raumID == null && other.raumID != null) || (this.raumID != null && !this.raumID.equals(other.raumID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Raeume[raumID=" + raumID + "]";
    }

}
