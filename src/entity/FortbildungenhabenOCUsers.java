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
@Table(name = "Fortbildungen_haben_OCUsers")
@NamedQueries({
    @NamedQuery(name = "FortbildungenhabenOCUsers.findAll", query = "SELECT f FROM FortbildungenhabenOCUsers f"),
    @NamedQuery(name = "FortbildungenhabenOCUsers.findById", query = "SELECT f FROM FortbildungenhabenOCUsers f WHERE f.id = :id"),
    @NamedQuery(name = "FortbildungenhabenOCUsers.findByUKennung", query = "SELECT f FROM FortbildungenhabenOCUsers f WHERE f.uKennung = :uKennung"),
    @NamedQuery(name = "FortbildungenhabenOCUsers.findByFortID", query = "SELECT f FROM FortbildungenhabenOCUsers f WHERE f.fortID = :fortID")})
public class FortbildungenhabenOCUsers implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Basic(optional = false)
    @Column(name = "UKennung")
    private String uKennung;
    @Basic(optional = false)
    @Column(name = "FortID")
    private long fortID;

    public FortbildungenhabenOCUsers() {
    }

    public FortbildungenhabenOCUsers(Long id) {
        this.id = id;
    }

    public FortbildungenhabenOCUsers(Long id, String uKennung, long fortID) {
        this.id = id;
        this.uKennung = uKennung;
        this.fortID = fortID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUKennung() {
        return uKennung;
    }

    public void setUKennung(String uKennung) {
        this.uKennung = uKennung;
    }

    public long getFortID() {
        return fortID;
    }

    public void setFortID(long fortID) {
        this.fortID = fortID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FortbildungenhabenOCUsers)) {
            return false;
        }
        FortbildungenhabenOCUsers other = (FortbildungenhabenOCUsers) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.FortbildungenhabenOCUsers[id=" + id + "]";
    }

}
