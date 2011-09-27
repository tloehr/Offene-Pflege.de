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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "SYSBWERTE2FILE")
@NamedQueries({
    @NamedQuery(name = "Sysbwerte2file.findAll", query = "SELECT s FROM Sysbwerte2file s"),
    @NamedQuery(name = "Sysbwerte2file.findById", query = "SELECT s FROM Sysbwerte2file s WHERE s.id = :id"),
    @NamedQuery(name = "Sysbwerte2file.findByBemerkung", query = "SELECT s FROM Sysbwerte2file s WHERE s.bemerkung = :bemerkung"),
    @NamedQuery(name = "Sysbwerte2file.findByPit", query = "SELECT s FROM Sysbwerte2file s WHERE s.pit = :pit")})
public class Sysbwerte2file implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "Bemerkung")
    private String bemerkung;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;
    @JoinColumn(name = "FID", referencedColumnName = "OCFID")
    @ManyToOne
    private SYSFiles sysfile;
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;
    @JoinColumn(name = "BWID", referencedColumnName = "BWID")
    @ManyToOne
    private BWerte wert;

    public Sysbwerte2file() {
    }

    public Sysbwerte2file(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
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
        if (!(object instanceof Sysbwerte2file)) {
            return false;
        }
        Sysbwerte2file other = (Sysbwerte2file) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Sysbwerte2file[id=" + id + "]";
    }
}
