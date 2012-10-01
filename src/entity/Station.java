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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "Station")
@NamedQueries({
    @NamedQuery(name = "Stationen.findAllSorted", query = "SELECT s FROM Station s ORDER BY s.bezeichnung "),
    @NamedQuery(name = "Stationen.findByStatID", query = "SELECT s FROM Station s WHERE s.statID = :statID"),
    @NamedQuery(name = "Stationen.findByBezeichnung", query = "SELECT s FROM Station s WHERE s.bezeichnung = :bezeichnung")})
public class Station implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StatID")
    private Long statID;
    @Basic(optional = false)
    @Column(name = "Name")
    private String bezeichnung;
    @JoinColumn(name = "EID", referencedColumnName = "EID")
    @ManyToOne
    private Homes einrichtung;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    private Collection<Rooms> rooms;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    private Collection<Resident> bewohnerAufDieserStation;

    public Station() {
    }

    public Long getStatID() {
        return statID;
    }

    public void setStatID(Long statID) {
        this.statID = statID;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public Homes getEinrichtung() {
        return einrichtung;
    }

    public void setEinrichtung(Homes einrichtung) {
        this.einrichtung = einrichtung;
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
        return bezeichnung;
    }

}
