package entity.planung;

import entity.Bewohner;
import entity.Users;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "DFN")
@NamedQueries({
    @NamedQuery(name = "Dfn.findAll", query = "SELECT d FROM DFN d"),
    @NamedQuery(name = "Dfn.findByDfnid", query = "SELECT d FROM DFN d WHERE d.dfnid = :dfnid"),
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
    private NursingProcess nursingProcess;

    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Bewohner bewohner;

    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    @JoinColumn(name = "MassID", referencedColumnName = "MassID")
    @ManyToOne
    private Intervention intervention;

    @JoinColumn(name = "TermID", referencedColumnName = "TermID")
    @ManyToOne
    private InterventionSchedule interventionSchedule;

    public Intervention getIntervention() {
        return intervention;
    }

    public void setIntervention(Intervention intervention) {
        this.intervention = intervention;
    }

    public DFN() {
    }


    public DFN(InterventionSchedule interventionSchedule, Date soll, Byte sZeit) {
        // Das sieht redundant aus, dient aber der Vereinfachung
        this.interventionSchedule = interventionSchedule;
        this.intervention = interventionSchedule.getIntervention();
        this.nursingProcess = interventionSchedule.getNursingProcess();
        this.dauer = interventionSchedule.getDauer();
        this.bewohner = interventionSchedule.getNursingProcess().getBewohner();
        this.erforderlich = interventionSchedule.isFloating();
        this.soll = soll;
        this.version = 0l;
        this.sZeit = sZeit;
        this.status = DFNTools.STATUS_OFFEN;
        this.mdate = new Date();
        this.stDatum = new Date();
    }

    public Long getDfnid() {
        return dfnid;
    }

    public void setDfnid(Long dfnid) {
        this.dfnid = dfnid;
    }

    public Byte getSollZeit() {
        return sZeit;
    }

    public void setSollZeit(Byte sZeit) {
        this.sZeit = sZeit;
    }

    public Byte getiZeit() {
        return iZeit;
    }

    public void setiZeit(Byte iZeit) {
        this.iZeit = iZeit;
    }

    public InterventionSchedule getInterventionSchedule() {
        return interventionSchedule;
    }

    public void setInterventionSchedule(InterventionSchedule interventionSchedule) {
        this.interventionSchedule = interventionSchedule;
    }

    public Date getSoll() {
        return soll;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getsZeit() {
        return sZeit;
    }

    public void setsZeit(Byte sZeit) {
        this.sZeit = sZeit;
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

    public NursingProcess getNursingProcess() {
        return nursingProcess;
    }

    public void setNursingProcess(NursingProcess nursingProcess) {
        this.nursingProcess = nursingProcess;
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

