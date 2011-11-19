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
import javax.persistence.Lob;
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
@Table(name = "PlanKontrolle")
@NamedQueries({
    @NamedQuery(name = "PlanKontrolle.findAll", query = "SELECT p FROM PlanKontrolle p"),
    @NamedQuery(name = "PlanKontrolle.findByPKonID", query = "SELECT p FROM PlanKontrolle p WHERE p.pKonID = :pKonID"),
    @NamedQuery(name = "PlanKontrolle.findByPlanID", query = "SELECT p FROM PlanKontrolle p WHERE p.planID = :planID"),
    @NamedQuery(name = "PlanKontrolle.findByUKennung", query = "SELECT p FROM PlanKontrolle p WHERE p.uKennung = :uKennung"),
    @NamedQuery(name = "PlanKontrolle.findByDatum", query = "SELECT p FROM PlanKontrolle p WHERE p.datum = :datum"),
    @NamedQuery(name = "PlanKontrolle.findByAbschluss", query = "SELECT p FROM PlanKontrolle p WHERE p.abschluss = :abschluss")})
public class PlanKontrolle implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "PKonID")
    private Long pKonID;
    @Basic(optional = false)
    @Column(name = "PlanID")
    private long planID;
    @Basic(optional = false)
    @Column(name = "UKennung")
    private String uKennung;
    @Lob
    @Column(name = "Bemerkung")
    private String bemerkung;
    @Basic(optional = false)
    @Column(name = "Datum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datum;
    @Column(name = "Abschluss")
    private Boolean abschluss;

    public PlanKontrolle() {
    }

    public PlanKontrolle(Long pKonID) {
        this.pKonID = pKonID;
    }

    public PlanKontrolle(Long pKonID, long planID, String uKennung, Date datum) {
        this.pKonID = pKonID;
        this.planID = planID;
        this.uKennung = uKennung;
        this.datum = datum;
    }

    public Long getPKonID() {
        return pKonID;
    }

    public void setPKonID(Long pKonID) {
        this.pKonID = pKonID;
    }

    public long getPlanID() {
        return planID;
    }

    public void setPlanID(long planID) {
        this.planID = planID;
    }

    public String getUKennung() {
        return uKennung;
    }

    public void setUKennung(String uKennung) {
        this.uKennung = uKennung;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public Boolean getAbschluss() {
        return abschluss;
    }

    public void setAbschluss(Boolean abschluss) {
        this.abschluss = abschluss;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pKonID != null ? pKonID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PlanKontrolle)) {
            return false;
        }
        PlanKontrolle other = (PlanKontrolle) object;
        if ((this.pKonID == null && other.pKonID != null) || (this.pKonID != null && !this.pKonID.equals(other.pKonID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.PlanKontrolle[pKonID=" + pKonID + "]";
    }

}
