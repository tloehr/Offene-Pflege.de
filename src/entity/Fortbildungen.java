/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "Fortbildungen")
@NamedQueries({
    @NamedQuery(name = "Fortbildungen.findAll", query = "SELECT f FROM Fortbildungen f"),
    @NamedQuery(name = "Fortbildungen.findById", query = "SELECT f FROM Fortbildungen f WHERE f.id = :id"),
    @NamedQuery(name = "Fortbildungen.findByTitel", query = "SELECT f FROM Fortbildungen f WHERE f.titel = :titel"),
    @NamedQuery(name = "Fortbildungen.findByDatum", query = "SELECT f FROM Fortbildungen f WHERE f.datum = :datum"),
    @NamedQuery(name = "Fortbildungen.findByIntern", query = "SELECT f FROM Fortbildungen f WHERE f.intern = :intern"),
    @NamedQuery(name = "Fortbildungen.findByUrl", query = "SELECT f FROM Fortbildungen f WHERE f.url = :url")})
public class Fortbildungen implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Basic(optional = false)
    @Column(name = "Titel")
    private String titel;
    @Basic(optional = false)
    @Column(name = "Datum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datum;
    @Basic(optional = false)
    @Column(name = "Intern")
    private boolean intern;
    @Column(name = "URL")
    private String url;

    public Fortbildungen() {
    }

    public Fortbildungen(Long id) {
        this.id = id;
    }

    public Fortbildungen(Long id, String titel, Date datum, boolean intern) {
        this.id = id;
        this.titel = titel;
        this.datum = datum;
        this.intern = intern;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public boolean getIntern() {
        return intern;
    }

    public void setIntern(boolean intern) {
        this.intern = intern;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
        if (!(object instanceof Fortbildungen)) {
            return false;
        }
        Fortbildungen other = (Fortbildungen) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.Fortbildungen[id=" + id + "]";
    }

}
