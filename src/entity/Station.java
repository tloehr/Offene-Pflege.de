/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import entity.info.Resident;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "station")

public class Station implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StatID")
    private Long statID;
    @Basic(optional = false)
    @Column(name = "Name")
    private String name;
    @JoinColumn(name = "EID", referencedColumnName = "EID")
    @ManyToOne
    private Homes home;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    private Collection<Rooms> rooms;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    private Collection<Resident> bewohnerAufDieserStation;

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Long getStatID() {
        return statID;
    }

    public void setStatID(Long statID) {
        this.statID = statID;
    }

    public String getName() {
        return name;
    }

    public void setBezeichnung(String bezeichnung) {
        this.name = bezeichnung;
    }

    public Homes getHome() {
        return home;
    }

    public void setEinrichtung(Homes einrichtung) {
        this.home = einrichtung;
    }

    public Collection<Resident> getBewohnerAufDieserStation() {
        return bewohnerAufDieserStation;
    }

    public Collection<Rooms> getRooms() {
        return rooms;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (statID != null ? statID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Station)) {
            return false;
        }
        Station other = (Station) object;
        if ((this.statID == null && other.statID != null) || (this.statID != null && !this.statID.equals(other.statID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

}
