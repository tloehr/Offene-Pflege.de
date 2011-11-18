/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import entity.medis.Darreichung;
import entity.medis.Situationen;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "BHPVerordnung")
@NamedQueries({
        @NamedQuery(name = "Verordnung.findAll", query = "SELECT b FROM Verordnung b"),
        @NamedQuery(name = "Verordnung.findByVerID", query = "SELECT b FROM Verordnung b WHERE b.verid = :verid"),
        @NamedQuery(name = "Verordnung.findByAnDatum", query = "SELECT b FROM Verordnung b WHERE b.anDatum = :anDatum"),
        @NamedQuery(name = "Verordnung.findByVorgang", query = " "
                + " SELECT ve, av.pdca FROM Verordnung ve "
                + " JOIN ve.attachedVorgaenge av"
                + " JOIN av.vorgang v"
                + " WHERE v = :vorgang "),
        @NamedQuery(name = "Verordnung.findByAbDatum", query = "SELECT b FROM Verordnung b WHERE b.abDatum = :abDatum"),
        @NamedQuery(name = "Verordnung.findByBisPackEnde", query = "SELECT b FROM Verordnung b WHERE b.bisPackEnde = :bisPackEnde"),
        @NamedQuery(name = "Verordnung.findByVerKennung", query = "SELECT b FROM Verordnung b WHERE b.verKennung = :verKennung"),
        @NamedQuery(name = "Verordnung.findByStellplan", query = "SELECT b FROM Verordnung b WHERE b.stellplan = :stellplan")})
public class Verordnung implements Serializable, VorgangElement {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "VerID")
    private Long verid;
    @Basic(optional = false)
    @Column(name = "AnDatum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date anDatum;
    @Basic(optional = false)
    @Column(name = "AbDatum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date abDatum;
    @Column(name = "BisPackEnde")
    private Boolean bisPackEnde;
    @Basic(optional = false)
    @Column(name = "VerKennung")
    private long verKennung;
    @Lob
    @Column(name = "Bemerkung")
    private String bemerkung;
    @Basic(optional = false)
    @Column(name = "Stellplan")
    private boolean stellplan;

    // ==
    // 1:N Relationen
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "verordnung")
    private Collection<Sysver2file> attachedFiles;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "verordnung")
    private Collection<SYSVER2VORGANG> attachedVorgaenge;
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
    @JoinColumn(name = "MassID", referencedColumnName = "MassID")
    @ManyToOne
    private Massnahmen massnahme;
    @JoinColumn(name = "DafID", referencedColumnName = "DafID")
    @ManyToOne
    private Darreichung darreichung;
    @JoinColumn(name = "SitID", referencedColumnName = "SitID")
    @ManyToOne
    private Situationen situation;
    @JoinColumn(name = "AnKHID", referencedColumnName = "KHID")
    @ManyToOne
    private Krankenhaus anKH;
    @JoinColumn(name = "AbKHID", referencedColumnName = "KHID")
    @ManyToOne
    private Krankenhaus abKH;
    @JoinColumn(name = "AnArztID", referencedColumnName = "ArztID")
    @ManyToOne
    private Arzt anArzt;
    @JoinColumn(name = "AbArztID", referencedColumnName = "ArztID")
    @ManyToOne
    private Arzt abArzt;


    public Verordnung() {
    }

    public Long getVerid() {
        return verid;
    }

    public void setVerid(Long verid) {
        this.verid = verid;
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

    public Krankenhaus getAnKH() {
        return anKH;
    }

    public void setAnKH(Krankenhaus anKH) {
        this.anKH = anKH;
    }

    public Krankenhaus getAbKH() {
        return abKH;
    }

    public void setAbKH(Krankenhaus abKH) {
        this.abKH = abKH;
    }

    public Arzt getAnArzt() {
        return anArzt;
    }

    public void setAnArzt(Arzt anArzt) {
        this.anArzt = anArzt;
    }

    public Arzt getAbArzt() {
        return abArzt;
    }

    public void setAbArzt(Arzt abArzt) {
        this.abArzt = abArzt;
    }

    public Boolean isBisPackEnde() {
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
        return SYSTools.catchNull(bemerkung);
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public boolean isStellplan() {
        return stellplan;
    }

    public void setStellplan(boolean stellplan) {
        this.stellplan = stellplan;
    }

    public Users getAbgesetztDurch() {
        return abgesetztDurch;
    }

    public Situationen getSituation() {
        return situation;
    }

    public boolean hasMedi() {
        return darreichung != null;
    }

    public void setSituation(Situationen situation) {
        this.situation = situation;
    }

    public Darreichung getDarreichung() {
        return darreichung;
    }

    public void setDarreichung(Darreichung darreichung) {
        this.darreichung = darreichung;
    }

    public Massnahmen getMassnahme() {
        return massnahme;
    }

    public void setMassnahme(Massnahmen massnahme) {
        this.massnahme = massnahme;
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

    public Collection<Sysver2file> getAttachedFiles() {
        return attachedFiles;
    }

    public Collection<SYSVER2VORGANG> getAttachedVorgaenge() {
        return attachedVorgaenge;
    }

    public Bewohner getBewohner() {
        return bewohner;
    }

    public void setBewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
    }

    public boolean isAbgesetzt() {
        return abDatum.before(SYSConst.DATE_BIS_AUF_WEITERES);
    }

    public boolean isBedarf() {
        return situation != null;
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
        return verid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (verid != null ? verid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Verordnung)) {
            return false;
        }
        Verordnung other = (Verordnung) object;
        if ((this.verid == null && other.verid != null) || (this.verid != null && !this.verid.equals(other.verid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Verordnung[verID=" + verid + "]";
    }

}
