/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "BHPVerordnung")
@NamedQueries({
    @NamedQuery(name = "Verordnung.findAll", query = "SELECT b FROM Verordnung b"),
    @NamedQuery(name = "Verordnung.findByVerID", query = "SELECT b FROM Verordnung b WHERE b.verID = :verID"),
    @NamedQuery(name = "Verordnung.findByAnDatum", query = "SELECT b FROM Verordnung b WHERE b.anDatum = :anDatum"),
    @NamedQuery(name = "Verordnung.findByAbDatum", query = "SELECT b FROM Verordnung b WHERE b.abDatum = :abDatum"),
    @NamedQuery(name = "Verordnung.findByAnKHID", query = "SELECT b FROM Verordnung b WHERE b.anKHID = :anKHID"),
    @NamedQuery(name = "Verordnung.findByAbKHID", query = "SELECT b FROM Verordnung b WHERE b.abKHID = :abKHID"),
    @NamedQuery(name = "Verordnung.findByAnArztID", query = "SELECT b FROM Verordnung b WHERE b.anArztID = :anArztID"),
    @NamedQuery(name = "Verordnung.findByAbArztID", query = "SELECT b FROM Verordnung b WHERE b.abArztID = :abArztID"),
    @NamedQuery(name = "Verordnung.findByBisPackEnde", query = "SELECT b FROM Verordnung b WHERE b.bisPackEnde = :bisPackEnde"),
    @NamedQuery(name = "Verordnung.findByVerKennung", query = "SELECT b FROM Verordnung b WHERE b.verKennung = :verKennung"),
    @NamedQuery(name = "Verordnung.findByMassID", query = "SELECT b FROM Verordnung b WHERE b.massID = :massID"),
    @NamedQuery(name = "Verordnung.findByDafID", query = "SELECT b FROM Verordnung b WHERE b.dafID = :dafID"),
    @NamedQuery(name = "Verordnung.findBySitID", query = "SELECT b FROM Verordnung b WHERE b.sitID = :sitID"),
    @NamedQuery(name = "Verordnung.findByStellplan", query = "SELECT b FROM Verordnung b WHERE b.stellplan = :stellplan")})
public class Verordnung implements Serializable, VorgangElement {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "VerID")
    private Long verID;
    @Basic(optional = false)
    @Column(name = "AnDatum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date anDatum;
    @Basic(optional = false)
    @Column(name = "AbDatum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date abDatum;
    @Column(name = "AnKHID")
    private BigInteger anKHID;
    @Column(name = "AbKHID")
    private BigInteger abKHID;
    @Column(name = "AnArztID")
    private BigInteger anArztID;
    @Column(name = "AbArztID")
    private BigInteger abArztID;
    @Column(name = "BisPackEnde")
    private Boolean bisPackEnde;
    @Basic(optional = false)
    @Column(name = "VerKennung")
    private long verKennung;
    @Lob
    @Column(name = "Bemerkung")
    private String bemerkung;
    @Basic(optional = false)
    @Column(name = "MassID")
    private long massID;
    @Column(name = "DafID")
    private BigInteger dafID;
    @Column(name = "SitID")
    private BigInteger sitID;
    @Basic(optional = false)
    @Column(name = "Stellplan")
    private boolean stellplan;

    // ==
    // 1:N Relationen
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "verordnung")
    private Collection<Sysver2file> verFilesCollection;
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
    // ==
    // M:N Relationen
    // ==
    @ManyToMany
    @JoinTable(name = "SYSVER2VORGANG", joinColumns =
    @JoinColumn(name = "VerID"), inverseJoinColumns =
    @JoinColumn(name = "VorgangID"))
    private Collection<Vorgaenge> vorgaenge;

    public Verordnung() {
    }

  
    public Long getVerID() {
        return verID;
    }

    public void setVerID(Long verID) {
        this.verID = verID;
    }

    public Date getAnDatum() {
        return anDatum;
    }

    public void setAnDatum(Date anDatum) {
        this.anDatum = anDatum;
    }

    public Date getAbDatum() {
        return abDatum;
    }

    public void setAbDatum(Date abDatum) {
        this.abDatum = abDatum;
    }

    public BigInteger getAnKHID() {
        return anKHID;
    }

    public void setAnKHID(BigInteger anKHID) {
        this.anKHID = anKHID;
    }

    public BigInteger getAbKHID() {
        return abKHID;
    }

    public void setAbKHID(BigInteger abKHID) {
        this.abKHID = abKHID;
    }

    public BigInteger getAnArztID() {
        return anArztID;
    }

    public void setAnArztID(BigInteger anArztID) {
        this.anArztID = anArztID;
    }

    public BigInteger getAbArztID() {
        return abArztID;
    }

    public void setAbArztID(BigInteger abArztID) {
        this.abArztID = abArztID;
    }

    public Boolean getBisPackEnde() {
        return bisPackEnde;
    }

    public void setBisPackEnde(Boolean bisPackEnde) {
        this.bisPackEnde = bisPackEnde;
    }

    public long getVerKennung() {
        return verKennung;
    }

    public void setVerKennung(long verKennung) {
        this.verKennung = verKennung;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public long getMassID() {
        return massID;
    }

    public void setMassID(long massID) {
        this.massID = massID;
    }

    public BigInteger getDafID() {
        return dafID;
    }

    public void setDafID(BigInteger dafID) {
        this.dafID = dafID;
    }

    public BigInteger getSitID() {
        return sitID;
    }

    public void setSitID(BigInteger sitID) {
        this.sitID = sitID;
    }

    public boolean getStellplan() {
        return stellplan;
    }

    public void setStellplan(boolean stellplan) {
        this.stellplan = stellplan;
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

    public Collection<Sysver2file> getVerFilesCollection() {
        return verFilesCollection;
    }

    public Collection<Vorgaenge> getVorgaenge() {
        return vorgaenge;
    }

    @Override
    public long getPITInMillis() {
        return anDatum.getTime();
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
        return verID;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (verID != null ? verID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Verordnung)) {
            return false;
        }
        Verordnung other = (Verordnung) object;
        if ((this.verID == null && other.verID != null) || (this.verID != null && !this.verID.equals(other.verID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Verordnung[verID=" + verID + "]";
    }

}
