/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
@Table(name = "BWerte")
@NamedQueries({
    @NamedQuery(name = "BWerte.findAll", query = "SELECT b FROM BWerte b"),
    @NamedQuery(name = "BWerte.findByBwid", query = "SELECT b FROM BWerte b WHERE b.bwid = :bwid"),
    @NamedQuery(name = "BWerte.findByPit", query = "SELECT b FROM BWerte b WHERE b.pit = :pit"),
    @NamedQuery(name = "BWerte.findByWert", query = "SELECT b FROM BWerte b WHERE b.wert = :wert"),
    @NamedQuery(name = "BWerte.findByBeziehung", query = "SELECT b FROM BWerte b WHERE b.beziehung = :beziehung"),
    @NamedQuery(name = "BWerte.findBySortierung", query = "SELECT b FROM BWerte b WHERE b.sortierung = :sortierung"),
    @NamedQuery(name = "BWerte.findByReplacedBy", query = "SELECT b FROM BWerte b WHERE b.replacedBy = :replacedBy"),
    @NamedQuery(name = "BWerte.findByReplacementFor", query = "SELECT b FROM BWerte b WHERE b.replacementFor = :replacementFor"),
    @NamedQuery(name = "BWerte.findByEditBy", query = "SELECT b FROM BWerte b WHERE b.editBy = :editBy"),
    @NamedQuery(name = "BWerte.findByCdate", query = "SELECT b FROM BWerte b WHERE b.cdate = :cdate"),
    @NamedQuery(name = "BWerte.findByMdate", query = "SELECT b FROM BWerte b WHERE b.mdate = :mdate")})
public class BWerte implements Serializable, VorgangElement {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BWID")
    private Long bwid;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;
    @Basic(optional = false)
    @Column(name = "Wert")
    private BigDecimal wert;
    @Lob
    @Column(name = "Bemerkung")
    private String bemerkung;
    @Column(name = "Beziehung")
    private BigInteger beziehung;
    @Column(name = "Sortierung")
    private Short sortierung;
    @Lob
    @Column(name = "XML")
    private String xml;
    @Basic(optional = false)
    @Column(name = "ReplacedBy")
    private long replacedBy;
    @Basic(optional = false)
    @Column(name = "ReplacementFor")
    private long replacementFor;
    @Column(name = "EditBy")
    private String editBy;
    @Basic(optional = false)
    @Column(name = "_cdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cdate;
    @Basic(optional = false)
    @Column(name = "_mdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mdate;
    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Bewohner bewohner;
    // ==
    // M:N Relationen
    // ==
    @ManyToMany
    @JoinTable(name = "SYSBWERTE2VORGANG", joinColumns =
    @JoinColumn(name = "BWID"), inverseJoinColumns =
    @JoinColumn(name = "VorgangID"))
    private Collection<Vorgaenge> vorgaenge;

    public BWerte() {
    }

    public Long getBwid() {
        return bwid;
    }

    public void setBwid(Long bwid) {
        this.bwid = bwid;
    }

    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

    public BigDecimal getWert() {
        return wert;
    }

    public void setWert(BigDecimal wert) {
        this.wert = wert;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public BigInteger getBeziehung() {
        return beziehung;
    }

    public void setBeziehung(BigInteger beziehung) {
        this.beziehung = beziehung;
    }

    public Short getSortierung() {
        return sortierung;
    }

    public void setSortierung(Short sortierung) {
        this.sortierung = sortierung;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public long getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(long replacedBy) {
        this.replacedBy = replacedBy;
    }

    public long getReplacementFor() {
        return replacementFor;
    }

    public void setReplacementFor(long replacementFor) {
        this.replacementFor = replacementFor;
    }

    public String getEditBy() {
        return editBy;
    }

    public void setEditBy(String editBy) {
        this.editBy = editBy;
    }

    public Date getCdate() {
        return cdate;
    }

    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public Collection<Vorgaenge> getVorgaenge() {
        return vorgaenge;
    }

    public Bewohner getBewohner() {
        return bewohner;
    }

    public void setBewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    @Override
    public long getPITInMillis() {
        return pit.getTime();
    }

    @Override
    public String getContentAsHTML() {
        // TODO: fehlt noch
        return "<html>not yet</html>";
    }

    @Override
    public String getPITAsHTML() {
        // TODO: fehlt noch
        return "<html>not yet</html>";
    }

    @Override
    public long getID() {
        return bwid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bwid != null ? bwid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BWerte)) {
            return false;
        }
        BWerte other = (BWerte) object;
        if ((this.bwid == null && other.bwid != null) || (this.bwid != null && !this.bwid.equals(other.bwid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.BWerte[bwid=" + bwid + "]";
    }
}
