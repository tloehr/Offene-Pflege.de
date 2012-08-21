/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.process;

import entity.info.Resident;
import entity.Users;
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
        @NamedQuery(name = "Vorgaenge.findAll", query = "SELECT v FROM QProcess v "),
        @NamedQuery(name = "Vorgaenge.findAllActiveSorted", query = "SELECT v FROM QProcess v WHERE v.bis = '9999-12-31 23:59:59' ORDER BY v.titel"),
        @NamedQuery(name = "Vorgaenge.findByVorgangID", query = "SELECT v FROM QProcess v WHERE v.vorgangID = :vorgangID"),
        @NamedQuery(name = "Vorgaenge.findByTitel", query = "SELECT v FROM QProcess v WHERE v.titel = :titel"),
        @NamedQuery(name = "Vorgaenge.findActiveByBesitzer", query = "SELECT v FROM QProcess v WHERE v.besitzer = :besitzer AND v.bis = '9999-12-31 23:59:59' ORDER BY v.titel"),
        @NamedQuery(name = "Vorgaenge.findInactiveByBesitzer", query = "SELECT v FROM QProcess v WHERE v.besitzer = :besitzer AND v.bis < '9999-12-31 23:59:59' ORDER BY v.titel"),
        @NamedQuery(name = "Vorgaenge.findActiveByBewohner", query = "SELECT v FROM QProcess v WHERE v.bewohner = :bewohner AND v.bis = '9999-12-31 23:59:59' ORDER BY v.titel"),
        @NamedQuery(name = "Vorgaenge.findActiveRunningOut", query = "SELECT v FROM QProcess v WHERE v.bis = '9999-12-31 23:59:59' AND v.wv <= :wv ORDER BY v.wv"),
        @NamedQuery(name = "Vorgaenge.findByVon", query = "SELECT v FROM QProcess v WHERE v.von = :von"),
        @NamedQuery(name = "Vorgaenge.findByWv", query = "SELECT v FROM QProcess v WHERE v.wv = :wv"),
        @NamedQuery(name = "Vorgaenge.findByBis", query = "SELECT v FROM QProcess v WHERE v.bis = :bis"),
        @NamedQuery(name = "Vorgaenge.findByPdca", query = "SELECT v FROM QProcess v WHERE v.pdca = :pdca")})
public class QProcess implements Serializable {

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
    @JoinColumn(name = "Ersteller", referencedColumnName = "UKennung")
    @ManyToOne
    private Users ersteller;
    @JoinColumn(name = "Besitzer", referencedColumnName = "UKennung")
    @ManyToOne
    private Users besitzer;
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident bewohner;
    @JoinColumn(name = "VKatID", referencedColumnName = "VKatID")
    @ManyToOne
    private PCat kategorie;
    //
    // 1:n Relationen
    //
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vorgang")
    private Collection<PReport> vorgangsBerichte;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vorgang")
    private Collection<SYSNR2PROCESS> attachedPflegeberichte;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vorgang")
    private Collection<SYSPRE2PROCESS> attachedVerordnungen;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vorgang")
    private Collection<SYSINF2PROCESS> attachedBWInfos;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vorgang")
    private Collection<SYSNP2PROCESS> attachedPlanungen;

    // ==
    // M:N Relationen
    // ==
//    @ManyToMany
//    @JoinTable(name = "SYSNR2PROCESS", joinColumns =
//    @JoinColumn(name = "VorgangID"), inverseJoinColumns =
//    @JoinColumn(name = "PBID"))
//    private Collection<NReport> pflegeberichte;
//    @ManyToMany
//    @JoinTable(name = "SYSINF2PROCESS", joinColumns =
//    @JoinColumn(name = "VorgangID"), inverseJoinColumns =
//    @JoinColumn(name = "BWInfoID"))
//    private Collection<BWInfo> bwinfos;
//    @ManyToMany
//    @JoinTable(name = "SYSNP2PROCESS", joinColumns =
//    @JoinColumn(name = "VorgangID"), inverseJoinColumns =
//    @JoinColumn(name = "PlanID"))
//    private Collection<Planung> planungen;
//    @ManyToMany
//    @JoinTable(name = "SYSPRE2PROCESS", joinColumns =
//    @JoinColumn(name = "VorgangID"), inverseJoinColumns =
//    @JoinColumn(name = "VerID"))
//    private Collection<Verordnung> prescription;
//    @ManyToMany
//    @JoinTable(name = "SYSBWERTE2VORGANG", joinColumns =
//    @JoinColumn(name = "VorgangID"), inverseJoinColumns =
//    @JoinColumn(name = "BWID"))
//    private Collection<BWerte> bwerte;

    public QProcess() {
    }

    public QProcess(String titel, Resident bewohner, PCat kategorie) {
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

    public Users getBesitzer() {
        return besitzer;
    }

    public void setBesitzer(Users besitzer) {
        this.besitzer = besitzer;
    }

    public Resident getBewohner() {
        return bewohner;
    }

    public void setBewohner(Resident bewohner) {
        this.bewohner = bewohner;
    }

    public Users getErsteller() {
        return ersteller;
    }

    public void setErsteller(Users ersteller) {
        this.ersteller = ersteller;
    }

    public PCat getKategorie() {
        return kategorie;
    }

    public void setKategorie(PCat kategorie) {
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

        if (!(object instanceof QProcess)) {
            return false;
        }
        QProcess other = (QProcess) object;
        if ((this.vorgangID == null && other.vorgangID != null) || (this.vorgangID != null && !this.vorgangID.equals(other.vorgangID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.process.QProcess[vorgangID=" + vorgangID + "]";
    }
}
