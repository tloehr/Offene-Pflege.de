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
@Table(name = "Stationen")
@NamedQueries({
    @NamedQuery(name = "Stationen.findAllSorted", query = "SELECT s FROM Stationen s ORDER BY s.bezeichnung "),
    @NamedQuery(name = "Stationen.findByStatID", query = "SELECT s FROM Stationen s WHERE s.statID = :statID"),
    @NamedQuery(name = "Stationen.findByBezeichnung", query = "SELECT s FROM Stationen s WHERE s.bezeichnung = :bezeichnung")})
public class Stationen implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "StatID")
    private Long statID;
    @Basic(optional = false)
    @Column(name = "Bezeichnung")
    private String bezeichnung;
    @JoinColumn(name = "EKennung", referencedColumnName = "EKennung")
    @ManyToOne
    private Einrichtungen einrichtung;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    private Collection<Raeume> raeume;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    private Collection<Bewohner> bewohnerAufDieserStation;

    public Stationen() {
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

    public Einrichtungen getEinrichtung() {
        return einrichtung;
    }

    public void setEinrichtung(Einrichtungen einrichtung) {
        this.einrichtung = einrichtung;
    }

    public Collection<Bewohner> getBewohnerAufDieserStation() {
        return bewohnerAufDieserStation;
    }

    public Collection<Raeume> getRaeume() {
        return raeume;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (statID != null ? statID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Stationen)) {
            return false;
        }
        Stationen other = (Stationen) object;
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
