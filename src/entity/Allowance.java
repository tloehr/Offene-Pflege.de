/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import entity.info.Resident;
import entity.system.Users;
import op.OPDE;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "Taschengeld")
@NamedQueries({
        @NamedQuery(name = "Taschengeld.findAll", query = "SELECT t FROM Allowance t"),
        @NamedQuery(name = "Taschengeld.findByTgid", query = "SELECT t FROM Allowance t WHERE t.id = :tgid"),
        @NamedQuery(name = "Taschengeld.findByBelegDatum", query = "SELECT t FROM Allowance t WHERE t.belegDatum = :belegDatum"),
        @NamedQuery(name = "Taschengeld.findByBelegtext", query = "SELECT t FROM Allowance t WHERE t.belegtext = :belegtext")})
public class Allowance implements Serializable, Comparable<Allowance> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TGID")
    private Long id;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "BelegDatum")
    @Temporal(TemporalType.DATE)
    private Date belegDatum;
    @Basic(optional = false)
    @Column(name = "Belegtext")
    private String belegtext;
    @Basic(optional = false)
    @Column(name = "Betrag")
    private BigDecimal betrag;
    @Basic(optional = false)
    @Column(name = "_edate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bearbeitetAm;
    @Basic(optional = false)
    @Column(name = "_cdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date erstelltAm;

    public Allowance() {
    }

    public Allowance(Resident resident) {
        this.resident = resident;
        this.erstelltVon = OPDE.getLogin().getUser();
        this.erstelltAm = new Date();
    }

    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;
    @JoinColumn(name = "_editor", referencedColumnName = "UKennung")
    @ManyToOne
    private Users bearbeitetVon;
    @JoinColumn(name = "_creator", referencedColumnName = "UKennung")
    @ManyToOne
    private Users erstelltVon;

    public Date getBelegDatum() {
        return belegDatum;
    }

    public Long getID() {
        return id;
    }

    public void setBelegDatum(Date belegDatum) {
        this.belegDatum = belegDatum;
    }

    public String getBelegtext() {
        return belegtext;
    }

    public void setBelegtext(String belegtext) {
        this.belegtext = belegtext;
    }

    public BigDecimal getBetrag() {
        return betrag;
    }

    public void setBetrag(BigDecimal betrag) {
        this.betrag = betrag;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Date getBearbeitetAm() {
        return bearbeitetAm;
    }

    public void setBearbeitetAm(Date bearbeitetAm) {
        this.bearbeitetAm = bearbeitetAm;
    }

    public Date getErstelltAm() {
        return erstelltAm;
    }

    public void setErstelltAm(Date erstelltAm) {
        this.erstelltAm = erstelltAm;
    }

    public Users getBearbeitetVon() {
        return bearbeitetVon;
    }

    public void setBearbeitetVon(Users bearbeitetVon) {
        this.bearbeitetVon = bearbeitetVon;
    }

    public Users getErstelltVon() {
        return erstelltVon;
    }

    public void setErstelltVon(Users erstelltVon) {
        this.erstelltVon = erstelltVon;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Allowance)) {
            return false;
        }
        Allowance other = (Allowance) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Taschengeld{" +
                "tgid=" + id +
                ", belegDatum=" + belegDatum +
                ", belegtext='" + belegtext + '\'' +
                ", betrag=" + betrag +
                ", bearbeitetAm=" + bearbeitetAm +
                ", erstelltAm=" + erstelltAm +
                ", bewohner=" + resident +
                ", bearbeitetVon=" + bearbeitetVon +
                ", erstelltVon=" + erstelltVon +
                '}';
    }

    @Override
    public int compareTo(Allowance other) {
        int result = belegDatum.compareTo(other.getBelegDatum()) * -1;
        if (result == 0) {
            result = id.compareTo(other.getID()) * -1;
        }
        return result;
    }
}
