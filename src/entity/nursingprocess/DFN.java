package entity.nursingprocess;

import entity.system.Users;
import entity.info.Resident;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSTools;

import javax.persistence.*;
import java.awt.*;
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
        @NamedQuery(name = "Dfn.findByErforderlich", query = "SELECT d FROM DFN d WHERE d.floating = :erforderlich"),
        @NamedQuery(name = "Dfn.findByDauer", query = "SELECT d FROM DFN d WHERE d.dauer = :dauer"),
        @NamedQuery(name = "Dfn.findByMdate", query = "SELECT d FROM DFN d WHERE d.mdate = :mdate")})
public class DFN implements Serializable, Comparable<DFN> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private boolean floating;
    @Basic(optional = false)
    @Column(name = "Dauer")
    private BigDecimal dauer;
    @Basic(optional = false)
    @Column(name = "MDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mdate;
    @Version
    @Column(name = "version")
    private Long version;


    @JoinColumn(name = "PlanID", referencedColumnName = "PlanID")
    @ManyToOne
    private NursingProcess nursingProcess;

    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;

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

    /**
     * standard constructor for new, unassigned DFNs
     *
     * @param resident
     * @param intervention
     */
    public DFN(Resident resident, Intervention intervention) {

        Date now = new Date();

        this.interventionSchedule = null;
        this.intervention = intervention;
        this.nursingProcess = null;
        this.dauer = intervention.getDauer();
        this.resident = resident;
        this.floating = false;
        this.soll = now;
        this.sZeit = SYSCalendar.whatTimeIDIs(now);
        this.ist = now;
        this.iZeit = this.sZeit;
        this.version = 0l;
        this.status = DFNTools.STATE_DONE;
        this.user = OPDE.getLogin().getUser();
        this.resident = resident;
        this.mdate = now;
        this.stDatum = now;
    }

    public DFN(InterventionSchedule interventionSchedule, Date soll, Byte sZeit) {
        // looks redundant but simplifies enormously
        this.interventionSchedule = interventionSchedule;
        this.intervention = interventionSchedule.getIntervention();
        this.nursingProcess = interventionSchedule.getNursingProcess();
        this.dauer = interventionSchedule.getDauer();
        this.resident = interventionSchedule.getNursingProcess().getResident();
        this.floating = interventionSchedule.isFloating();
        this.soll = soll;
        this.version = 0l;
        this.sZeit = sZeit;
        this.status = DFNTools.STATE_OPEN;
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

    // SOll-IST   Target Actual
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

    public boolean isFloating() {
        return floating;
    }

    public void setFloating(boolean floating) {
        this.floating = floating;
    }

    public BigDecimal getMinutes() {
        return dauer;
    }

    public void setMinutes(BigDecimal minutes) {
        this.dauer = minutes;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public Byte getShift() {
        if (isOnDemand()) {
            return DFNTools.SHIFT_ON_DEMAND;
        }
        if (sZeit == DFNTools.BYTE_TIMEOFDAY) {
            return SYSCalendar.whatShiftIs(this.soll);
        }
        return SYSCalendar.whatShiftIs(this.sZeit);
    }

    public NursingProcess getNursingProcess() {
        return nursingProcess;
    }

    public Color getFG() {
        if (isOnDemand()) {
            return SYSTools.getColor(OPDE.getProps().getProperty("ON_DEMAND_FGBHP"));
        }
        return SYSTools.getColor(OPDE.getProps().getProperty(DFNTools.SHIFT_KEY_TEXT[getShift()] + "_FGBHP"));
    }

    public Color getBG() {
        if (isOnDemand()) {
            return SYSTools.getColor(OPDE.getProps().getProperty("ON_DEMAND_BGBHP"));
        }
        return SYSTools.getColor(OPDE.getProps().getProperty(DFNTools.SHIFT_KEY_TEXT[getShift()] + "_BGBHP"));
    }

    public void setNursingProcess(NursingProcess nursingProcess) {
        this.nursingProcess = nursingProcess;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident bewohner) {
        this.resident = bewohner;
    }

    public Users getUser() {
        return user;
    }

    public boolean isOnDemand() {
        return nursingProcess == null;
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

    @Override
    public int compareTo(DFN other) {
        int result = this.getShift().compareTo(other.getShift());
        if (result == 0) {
            result = SYSTools.nullCompare(this.nursingProcess, other.getNursingProcess());
        }
        if (result == 0) {
            if (this.getNursingProcess() != null){
                result = this.nursingProcess.getID().compareTo(other.getNursingProcess().getID());
            }
        }
        if (result == 0) {
            result = sZeit.compareTo(other.getSollZeit());
        }
        if (result == 0) {
            if (sZeit == DFNTools.BYTE_TIMEOFDAY) {
                result = soll.compareTo(other.getSoll());
            }
        }
        if (result == 0) {
            result = intervention.getBezeichnung().compareTo(other.getIntervention().getBezeichnung());
        }
        if (result == 0) {
            result = dfnid.compareTo(other.getDfnid());
        }
        return result;
    }
}

