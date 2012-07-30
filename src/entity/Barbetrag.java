/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

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
        @NamedQuery(name = "Taschengeld.findAll", query = "SELECT t FROM Barbetrag t"),
        @NamedQuery(name = "Taschengeld.findByTgid", query = "SELECT t FROM Barbetrag t WHERE t.tgid = :tgid"),
        @NamedQuery(name = "Taschengeld.findByBelegDatum", query = "SELECT t FROM Barbetrag t WHERE t.belegDatum = :belegDatum"),
        @NamedQuery(name = "Taschengeld.findByBelegtext", query = "SELECT t FROM Barbetrag t WHERE t.belegtext = :belegtext")})
public class Barbetrag implements Serializable, Comparable<Barbetrag> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "TGID")
    private Long tgid;
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

    public Barbetrag() {
    }

    public Barbetrag(Date belegDatum, String belegtext, BigDecimal betrag, Bewohner bewohner, Users erstelltVon) {
        this.belegDatum = belegDatum;
        this.belegtext = belegtext;
        this.betrag = betrag;
        this.bewohner = bewohner;
        this.erstelltVon = erstelltVon;
        this.bearbeitetAm = new Date();
        this.erstelltAm = bearbeitetAm;
    }

    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Bewohner bewohner;
    @JoinColumn(name = "_editor", referencedColumnName = "UKennung")
    @ManyToOne
    private Users bearbeitetVon;
    @JoinColumn(name = "_creator", referencedColumnName = "UKennung")
    @ManyToOne
    private Users erstelltVon;

    public Long getTgid() {
        return tgid;
    }

    public void setTgid(Long tgid) {
        this.tgid = tgid;
    }


    public Date getBelegDatum() {
        return belegDatum;
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


    public Bewohner getBewohner() {
        return bewohner;
    }

    public void setBewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
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
        hash += (tgid != null ? tgid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Barbetrag)) {
            return false;
        }
        Barbetrag other = (Barbetrag) object;
        if ((this.tgid == null && other.tgid != null) || (this.tgid != null && !this.tgid.equals(other.tgid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Taschengeld{" +
                "tgid=" + tgid +
                ", belegDatum=" + belegDatum +
                ", belegtext='" + belegtext + '\'' +
                ", betrag=" + betrag +
                ", bearbeitetAm=" + bearbeitetAm +
                ", erstelltAm=" + erstelltAm +
                ", bewohner=" + bewohner +
                ", bearbeitetVon=" + bearbeitetVon +
                ", erstelltVon=" + erstelltVon +
                '}';
    }

    @Override
    public int compareTo(Barbetrag other) {
        return belegDatum.compareTo(other.getBelegDatum());
    }
}
