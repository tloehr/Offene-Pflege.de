package entity.planung;

import entity.Bewohner;
import entity.Massnahmen;
import entity.Users;
import entity.verordnungen.BHPTools;
import entity.verordnungen.MedBuchungen;
import entity.verordnungen.Verordnung;
import entity.verordnungen.VerordnungPlanung;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

@Entity
@Table(name = "DFN")
@NamedQueries({
    @NamedQuery(name = "Dfn.findAll", query = "SELECT d FROM DFN d"),
    @NamedQuery(name = "Dfn.findByDfnid", query = "SELECT d FROM DFN d WHERE d.dfnid = :dfnid"),
    @NamedQuery(name = "Dfn.findByMassID", query = "SELECT d FROM DFN d WHERE d.massID = :massID"),
    @NamedQuery(name = "Dfn.findBySoll", query = "SELECT d FROM DFN d WHERE d.soll = :soll"),
    @NamedQuery(name = "Dfn.findByIst", query = "SELECT d FROM DFN d WHERE d.ist = :ist"),
    @NamedQuery(name = "Dfn.findByStDatum", query = "SELECT d FROM DFN d WHERE d.stDatum = :stDatum"),
    @NamedQuery(name = "Dfn.findBySZeit", query = "SELECT d FROM DFN d WHERE d.sZeit = :sZeit"),
    @NamedQuery(name = "Dfn.findByIZeit", query = "SELECT d FROM DFN d WHERE d.iZeit = :iZeit"),
    @NamedQuery(name = "Dfn.findByStatus", query = "SELECT d FROM DFN d WHERE d.status = :status"),
    @NamedQuery(name = "Dfn.findByErforderlich", query = "SELECT d FROM DFN d WHERE d.erforderlich = :erforderlich"),
    @NamedQuery(name = "Dfn.findByDauer", query = "SELECT d FROM DFN d WHERE d.dauer = :dauer"),
    @NamedQuery(name = "Dfn.findByMdate", query = "SELECT d FROM DFN d WHERE d.mdate = :mdate")})
public class DFN implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "DFNID")
    private Long dfnid;
    @Basic(optional = false)
    @Column(name = "Soll")
    @Temporal(TemporalType.TIMESTAMP)
    private Date soll;
    @Column(name = "Ist")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ist;
    @Basic(optional = false)
    @Column(name = "StDatum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date stDatum;
    @Column(name = "SZeit")
    private Byte sZeit;
    @Column(name = "IZeit")
    private Byte iZeit;
    @Basic(optional = false)
    @Column(name = "Status")
    private Byte status;
    @Basic(optional = false)
    @Column(name = "Erforderlich")
    private boolean erforderlich;
    @Basic(optional = false)
    @Column(name = "Dauer")
    private BigDecimal dauer;
    @Basic(optional = false)
    @Column(name = "MDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mdate;
    @Version
    @Column(name="version")
    private Long version;


    @JoinColumn(name = "PlanID", referencedColumnName = "PlanID")
    @ManyToOne
    private Planung planung;

    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Bewohner bewohner;

    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    @JoinColumn(name = "MassID", referencedColumnName = "MassID")
    @ManyToOne
    private Massnahmen massnahme;

    @JoinColumn(name = "TermID", referencedColumnName = "TermID")
    @ManyToOne
    private MassTermin massTermin;

    public Massnahmen getMassnahme() {
        return massnahme;
    }

    public void setMassnahme(Massnahmen massnahme) {
        this.massnahme = massnahme;
    }

    public DFN() {
    }


    public DFN(MassTermin massTermin, Date soll, Byte sZeit) {
        // Das sieht redundant aus, dient aber der Vereinfachung
        this.massTermin = massTermin;
        this.massnahme = massTermin.getMassnahme();
        this.planung = massTermin.getPlanung();
        this.dauer = massTermin.getDauer();
        this.bewohner = massTermin.getPlanung().getBewohner();
        this.soll = soll;
        this.version = 0l;
        this.sZeit = sZeit;
        this.status = DFNTools.STATUS_OFFEN;
        this.mdate = new Date();
    }

    public Long getDfnid() {
        return dfnid;
    }

    public void setDfnid(Long dfnid) {
        this.dfnid = dfnid;
    }

    public Byte getsZeit() {
        return sZeit;
    }

    public void setsZeit(Byte sZeit) {
        this.sZeit = sZeit;
    }

    public Byte getiZeit() {
        return iZeit;
    }

    public void setiZeit(Byte iZeit) {
        this.iZeit = iZeit;
    }

    public MassTermin getMassTermin() {
        return massTermin;
    }

    public void setMassTermin(MassTermin massTermin) {
        this.massTermin = massTermin;
    }

    public Date getSoll() {
        return soll;
    }

    public void setSoll(Date soll) {
        this.soll = soll;
    }

    public Date getIst() {
        return ist;
    }

    public void setIst(Date ist) {
        this.ist = ist;
    }

    public Date getStDatum() {
        return stDatum;
    }

    public void setStDatum(Date stDatum) {
        this.stDatum = stDatum;
    }

    public boolean isErforderlich() {
        return erforderlich;
    }

    public void setErforderlich(boolean erforderlich) {
        this.erforderlich = erforderlich;
    }

    public BigDecimal getDauer() {
        return dauer;
    }

    public void setDauer(BigDecimal dauer) {
        this.dauer = dauer;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public Planung getPlanung() {
        return planung;
    }

    public void setPlanung(Planung planung) {
        this.planung = planung;
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
    public int hashCode() {
        int hash = 0;
        hash += (dfnid != null ? dfnid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DFN)) {
            return false;
        }
        DFN other = (DFN) object;
        if ((this.dfnid == null && other.dfnid != null) || (this.dfnid != null && !this.dfnid.equals(other.dfnid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.Dfn[dfnid=" + dfnid + "]";
    }

}

