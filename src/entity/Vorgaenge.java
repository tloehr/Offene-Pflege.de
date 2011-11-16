/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "Vorgaenge")
@NamedQueries({
        @NamedQuery(name = "Vorgaenge.findAll", query = "SELECT v FROM Vorgaenge v "),
        @NamedQuery(name = "Vorgaenge.findAllActiveSorted", query = "SELECT v FROM Vorgaenge v WHERE v.bis = '9999-12-31 23:59:59' ORDER BY v.titel"),
        @NamedQuery(name = "Vorgaenge.findByVorgangID", query = "SELECT v FROM Vorgaenge v WHERE v.vorgangID = :vorgangID"),
        @NamedQuery(name = "Vorgaenge.findByTitel", query = "SELECT v FROM Vorgaenge v WHERE v.titel = :titel"),
        @NamedQuery(name = "Vorgaenge.findActiveByBesitzer", query = "SELECT v FROM Vorgaenge v WHERE v.besitzer = :besitzer AND v.bis = '9999-12-31 23:59:59' ORDER BY v.titel"),
        @NamedQuery(name = "Vorgaenge.findInactiveByBesitzer", query = "SELECT v FROM Vorgaenge v WHERE v.besitzer = :besitzer AND v.bis < '9999-12-31 23:59:59' ORDER BY v.titel"),
        @NamedQuery(name = "Vorgaenge.findActiveByBewohner", query = "SELECT v FROM Vorgaenge v WHERE v.bewohner = :bewohner AND v.bis = '9999-12-31 23:59:59' ORDER BY v.titel"),
        @NamedQuery(name = "Vorgaenge.findActiveRunningOut", query = "SELECT v FROM Vorgaenge v WHERE v.bis = '9999-12-31 23:59:59' AND v.wv <= :wv ORDER BY v.wv"),
        @NamedQuery(name = "Vorgaenge.findByVon", query = "SELECT v FROM Vorgaenge v WHERE v.von = :von"),
        @NamedQuery(name = "Vorgaenge.findByWv", query = "SELECT v FROM Vorgaenge v WHERE v.wv = :wv"),
        @NamedQuery(name = "Vorgaenge.findByBis", query = "SELECT v FROM Vorgaenge v WHERE v.bis = :bis"),
        @NamedQuery(name = "Vorgaenge.findByPdca", query = "SELECT v FROM Vorgaenge v WHERE v.pdca = :pdca")})
public class Vorgaenge implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "VorgangID")
    private Long vorgangID;
    @Basic(optional = false)
    @Column(name = "Titel")
    private String titel;
    @Basic(optional = false)
    @Column(name = "Von")
    @Temporal(TemporalType.TIMESTAMP)
    private Date von;
    @Basic(optional = false)
    @Column(name = "WV")
    @Temporal(TemporalType.TIMESTAMP)
    private Date wv;
    @Basic(optional = false)
    @Column(name = "Bis")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bis;
    @Basic(optional = false)
    @Column(name = "PDCA")
    private short pdca;
    @JoinColumn(name = "Ersteller", referencedColumnName = "UKennung")
    @ManyToOne
    private Users ersteller;
    @JoinColumn(name = "Besitzer", referencedColumnName = "UKennung")
    @ManyToOne
    private Users besitzer;
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Bewohner bewohner;
    @JoinColumn(name = "VKatID", referencedColumnName = "VKatID")
    @ManyToOne
    private VKat kategorie;
    //
    // 1:n Relationen
    //
    //cascade={CascadeType.PERSIST, CascadeType.REMOVE}
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vorgang")
    private Collection<VBericht> vorgangsBerichte;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vorgang")
    private Collection<SYSPB2VORGANG> attachedPflegeberichte;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vorgang")
    private Collection<SYSVER2VORGANG> attachedVerordnungen;

    // ==
    // M:N Relationen
    // ==
//    @ManyToMany
//    @JoinTable(name = "SYSPB2VORGANG", joinColumns =
//    @JoinColumn(name = "VorgangID"), inverseJoinColumns =
//    @JoinColumn(name = "PBID"))
//    private Collection<Pflegeberichte> pflegeberichte;
//    @ManyToMany
//    @JoinTable(name = "SYSBWI2VORGANG", joinColumns =
//    @JoinColumn(name = "VorgangID"), inverseJoinColumns =
//    @JoinColumn(name = "BWInfoID"))
//    private Collection<BWInfo> bwinfos;
//    @ManyToMany
//    @JoinTable(name = "SYSPLAN2VORGANG", joinColumns =
//    @JoinColumn(name = "VorgangID"), inverseJoinColumns =
//    @JoinColumn(name = "PlanID"))
//    private Collection<Planung> planungen;
//    @ManyToMany
//    @JoinTable(name = "SYSVER2VORGANG", joinColumns =
//    @JoinColumn(name = "VorgangID"), inverseJoinColumns =
//    @JoinColumn(name = "VerID"))
//    private Collection<Verordnung> verordnungen;
//    @ManyToMany
//    @JoinTable(name = "SYSBWERTE2VORGANG", joinColumns =
//    @JoinColumn(name = "VorgangID"), inverseJoinColumns =
//    @JoinColumn(name = "BWID"))
//    private Collection<BWerte> bwerte;

    public Vorgaenge() {
    }

    public Vorgaenge(String titel, Bewohner bewohner, VKat kategorie) {
        this.titel = titel;
        this.von = new Date();
        this.wv = SYSCalendar.addDate(new Date(), 7);
        this.bis = SYSConst.DATE_BIS_AUF_WEITERES;
        this.ersteller = OPDE.getLogin().getUser();
        this.besitzer = OPDE.getLogin().getUser();
        this.bewohner = bewohner;
        this.kategorie = kategorie;
    }


    public Long getVorgangID() {
        return vorgangID;
    }

    public void setVorgangID(Long vorgangID) {
        this.vorgangID = vorgangID;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public Date getVon() {
        return von;
    }

    public void setVon(Date von) {
        this.von = von;
    }

    public Date getWv() {
        return wv;
    }

    public void setWv(Date wv) {
        this.wv = wv;
    }

    public Date getBis() {
        return bis;
    }

    public void setBis(Date bis) {
        this.bis = bis;
    }

    public short getPdca() {
        return pdca;
    }

    public void setPdca(short pdca) {
        this.pdca = pdca;
    }

    public Users getBesitzer() {
        return besitzer;
    }

    public void setBesitzer(Users besitzer) {
        this.besitzer = besitzer;
    }

    public Bewohner getBewohner() {
        return bewohner;
    }

    public void setBewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
    }

    public Users getErsteller() {
        return ersteller;
    }

    public void setErsteller(Users ersteller) {
        this.ersteller = ersteller;
    }

    public VKat getKategorie() {
        return kategorie;
    }

    public void setKategorie(VKat kategorie) {
        this.kategorie = kategorie;
    }

    public boolean isAbgeschlossen(){
        return !bis.equals(SYSConst.DATE_BIS_AUF_WEITERES);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (vorgangID != null ? vorgangID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Vorgaenge)) {
            return false;
        }
        Vorgaenge other = (Vorgaenge) object;
        if ((this.vorgangID == null && other.vorgangID != null) || (this.vorgangID != null && !this.vorgangID.equals(other.vorgangID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Vorgaenge[vorgangID=" + vorgangID + "]";
    }
}
