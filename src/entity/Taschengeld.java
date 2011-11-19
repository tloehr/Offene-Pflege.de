/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
@Table(name = "Taschengeld")
@NamedQueries({
    @NamedQuery(name = "Taschengeld.findAll", query = "SELECT t FROM Taschengeld t"),
    @NamedQuery(name = "Taschengeld.findByTgid", query = "SELECT t FROM Taschengeld t WHERE t.tgid = :tgid"),
    @NamedQuery(name = "Taschengeld.findByBWKennung", query = "SELECT t FROM Taschengeld t WHERE t.bWKennung = :bWKennung"),
    @NamedQuery(name = "Taschengeld.findByBelegDatum", query = "SELECT t FROM Taschengeld t WHERE t.belegDatum = :belegDatum"),
    @NamedQuery(name = "Taschengeld.findByBelegtext", query = "SELECT t FROM Taschengeld t WHERE t.belegtext = :belegtext"),
    @NamedQuery(name = "Taschengeld.findByBetrag", query = "SELECT t FROM Taschengeld t WHERE t.betrag = :betrag"),
    @NamedQuery(name = "Taschengeld.findByCancel", query = "SELECT t FROM Taschengeld t WHERE t.cancel = :cancel"),
    @NamedQuery(name = "Taschengeld.findByCreator", query = "SELECT t FROM Taschengeld t WHERE t.creator = :creator"),
    @NamedQuery(name = "Taschengeld.findByEditor", query = "SELECT t FROM Taschengeld t WHERE t.editor = :editor"),
    @NamedQuery(name = "Taschengeld.findByEdate", query = "SELECT t FROM Taschengeld t WHERE t.edate = :edate"),
    @NamedQuery(name = "Taschengeld.findByCdate", query = "SELECT t FROM Taschengeld t WHERE t.cdate = :cdate")})
public class Taschengeld implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "TGID")
    private Long tgid;
    @Basic(optional = false)
    @Column(name = "BWKennung")
    private String bWKennung;
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
    @Column(name = "_cancel")
    private BigInteger cancel;
    @Basic(optional = false)
    @Column(name = "_creator")
    private String creator;
    @Column(name = "_editor")
    private String editor;
    @Basic(optional = false)
    @Column(name = "_edate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date edate;
    @Basic(optional = false)
    @Column(name = "_cdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cdate;

    public Taschengeld() {
    }

    public Taschengeld(Long tgid) {
        this.tgid = tgid;
    }

    public Taschengeld(Long tgid, String bWKennung, Date belegDatum, String belegtext, BigDecimal betrag, String creator, Date edate, Date cdate) {
        this.tgid = tgid;
        this.bWKennung = bWKennung;
        this.belegDatum = belegDatum;
        this.belegtext = belegtext;
        this.betrag = betrag;
        this.creator = creator;
        this.edate = edate;
        this.cdate = cdate;
    }

    public Long getTgid() {
        return tgid;
    }

    public void setTgid(Long tgid) {
        this.tgid = tgid;
    }

    public String getBWKennung() {
        return bWKennung;
    }

    public void setBWKennung(String bWKennung) {
        this.bWKennung = bWKennung;
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

    public BigInteger getCancel() {
        return cancel;
    }

    public void setCancel(BigInteger cancel) {
        this.cancel = cancel;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public Date getEdate() {
        return edate;
    }

    public void setEdate(Date edate) {
        this.edate = edate;
    }

    public Date getCdate() {
        return cdate;
    }

    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tgid != null ? tgid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Taschengeld)) {
            return false;
        }
        Taschengeld other = (Taschengeld) object;
        if ((this.tgid == null && other.tgid != null) || (this.tgid != null && !this.tgid.equals(other.tgid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.Taschengeld[tgid=" + tgid + "]";
    }

}
