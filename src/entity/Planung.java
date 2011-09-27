/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
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
@Table(name = "Planung")
@NamedQueries({
    @NamedQuery(name = "Planung.findAll", query = "SELECT p FROM Planung p"),
    @NamedQuery(name = "Planung.findByPlanID", query = "SELECT p FROM Planung p WHERE p.planID = :planID"),
    @NamedQuery(name = "Planung.findByStichwort", query = "SELECT p FROM Planung p WHERE p.stichwort = :stichwort"),
    @NamedQuery(name = "Planung.findByVon", query = "SELECT p FROM Planung p WHERE p.von = :von"),
    @NamedQuery(name = "Planung.findByBis", query = "SELECT p FROM Planung p WHERE p.bis = :bis"),
    @NamedQuery(name = "Planung.findByPlanKennung", query = "SELECT p FROM Planung p WHERE p.planKennung = :planKennung"),
    @NamedQuery(name = "Planung.findByNKontrolle", query = "SELECT p FROM Planung p WHERE p.nKontrolle = :nKontrolle")})
public class Planung implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "PlanID")
    private Long planID;
    @Basic(optional = false)
    @Column(name = "Stichwort")
    private String stichwort;
    @Lob
    @Column(name = "Situation")
    private String situation;
    @Lob
    @Column(name = "Ziel")
    private String ziel;
//    @Basic(optional = false)
//    @Column(name = "BWIKID")
//    private long bwikid;
    @Basic(optional = false)
    @Column(name = "Von")
    @Temporal(TemporalType.TIMESTAMP)
    private Date von;
    @Basic(optional = false)
    @Column(name = "Bis")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bis;
    @Basic(optional = false)
    @Column(name = "PlanKennung")
    private long planKennung;
    @Basic(optional = false)
    @Column(name = "NKontrolle")
    @Temporal(TemporalType.DATE)
    private Date nKontrolle;
    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "AnUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users angesetztDurch;
    @JoinColumn(name = "AbUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users abgesetztDurch;
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Bewohner bewohner;
    @JoinColumn(name = "BWIKID", referencedColumnName = "BWIKID")
    @ManyToOne
    private BWInfoKat kategorie;
    // ==
    // M:N Relationen
    // ==
    @ManyToMany
    @JoinTable(name = "SYSPLAN2VORGANG", joinColumns =
    @JoinColumn(name = "PlanID"), inverseJoinColumns =
    @JoinColumn(name = "VorgangID"))
    private Collection<Vorgaenge> vorgaenge;

    public Planung() {
    }

    public Long getPlanID() {
        return planID;
    }

    public void setPlanID(Long planID) {
        this.planID = planID;
    }


    public String getStichwort() {
        return stichwort;
    }

    public void setStichwort(String stichwort) {
        this.stichwort = stichwort;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getZiel() {
        return ziel;
    }

    public void setZiel(String ziel) {
        this.ziel = ziel;
    }

    public Date getVon() {
        return von;
    }

    public void setVon(Date von) {
        this.von = von;
    }

    public Date getBis() {
        return bis;
    }

    public void setBis(Date bis) {
        this.bis = bis;
    }


    public long getPlanKennung() {
        return planKennung;
    }

    public void setPlanKennung(long planKennung) {
        this.planKennung = planKennung;
    }

    public Date getNKontrolle() {
        return nKontrolle;
    }

    public void setNKontrolle(Date nKontrolle) {
        this.nKontrolle = nKontrolle;
    }

    public Collection<Vorgaenge> getVorgaenge() {
        return vorgaenge;
    }

    public Users getAbgesetztDurch() {
        return abgesetztDurch;
    }

    public void setAbgesetztDurch(Users abgesetztDurch) {
        this.abgesetztDurch = abgesetztDurch;
    }

    public Users getAngesetztDurch() {
        return angesetztDurch;
    }

    public void setAngesetztDurch(Users angesetztDurch) {
        this.angesetztDurch = angesetztDurch;
    }

    public Bewohner getBewohner() {
        return bewohner;
    }

    public void setBewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
    }

    public BWInfoKat getKategorie() {
        return kategorie;
    }

    public void setKategorie(BWInfoKat kategorie) {
        this.kategorie = kategorie;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (planID != null ? planID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Planung)) {
            return false;
        }
        Planung other = (Planung) object;
        if ((this.planID == null && other.planID != null) || (this.planID != null && !this.planID.equals(other.planID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Planung[planID=" + planID + "]";
    }
}
