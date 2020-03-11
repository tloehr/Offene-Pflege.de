package de.offene_pflege.entity.nursingprocess;


import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.system.Users;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSCalendar;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "dfn")

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
    private Byte state;
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

    @JoinColumn(name = "BWKennung", referencedColumnName = "id")
    @ManyToOne
    private Resident resident;

    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

//    public NReport getNReport() {
//        return nReport;
//    }
//
//    public void setNReport(NReport nReport) {
//        this.nReport = nReport;
//    }
//
//    @JoinColumn(name = "NReportID", referencedColumnName = "pbid")
//    @ManyToOne
//    private NReport nReport;

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
        this.state = DFNTools.STATE_DONE;
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
        this.state = DFNTools.STATE_OPEN;
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

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
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

    public boolean isOpen() {
        return state == DFNTools.STATE_OPEN;
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
            return SYSCalendar.SHIFT_ON_DEMAND;
        }
        if (sZeit == SYSCalendar.BYTE_TIMEOFDAY) {
            return SYSCalendar.whatShiftIs(this.soll);
        }
        return SYSCalendar.whatShiftIs(this.sZeit);
    }

    public NursingProcess getNursingProcess() {
        return nursingProcess;
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
//        int result = this.getShift().compareTo(other.getShift());
        int result = 0;
        // first by the nursing process, even if there isn't any
        if (result == 0) {
            result = SYSTools.nullCompare(this.nursingProcess, other.getNursingProcess());
        }
        // then by the topic of the nursing process
        if (result == 0 && this.nursingProcess != null) {
            result = this.nursingProcess.getTopic().compareTo(other.getNursingProcess().getTopic());
        }
        // then by the name of the intervention
        if (result == 0) {
            result = intervention.getBezeichnung().compareTo(other.getIntervention().getBezeichnung());
        }
        // then, as a catch all case, simply by the id of the dfn
        if (result == 0) {
            result = dfnid.compareTo(other.getDfnid());
        }
        return result;
    }
}

