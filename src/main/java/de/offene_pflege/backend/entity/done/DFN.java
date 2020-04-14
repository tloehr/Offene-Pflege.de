package de.offene_pflege.backend.entity.done;


import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.backend.entity.system.OPUsers;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "dfn")
public class DFN extends DefaultEntity implements Comparable<DFN> {
    private Date soll;
    private Date ist;
    private Date stDatum;
    private Byte sZeit;
    private Byte iZeit;
    private Byte state;
    private boolean floating;
    private Date mdate;
    private NursingProcess nursingProcess;
    private Resident resident;
    private OPUsers user;
    private Intervention intervention;
    private InterventionSchedule interventionSchedule;


    @Basic(optional = false)
    @Column(name = "Soll")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getSoll() {
        return soll;
    }

    public void setSoll(Date soll) {
        this.soll = soll;
    }

    @Column(name = "Ist")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getIst() {
        return ist;
    }

    public void setIst(Date ist) {
        this.ist = ist;
    }

    @Basic(optional = false)
    @Column(name = "StDatum")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStDatum() {
        return stDatum;
    }

    public void setStDatum(Date stDatum) {
        this.stDatum = stDatum;
    }

    @Column(name = "SZeit")
    public Byte getsZeit() {
        return sZeit;
    }

    public void setsZeit(Byte sZeit) {
        this.sZeit = sZeit;
    }

    @Column(name = "IZeit")
    public Byte getiZeit() {
        return iZeit;
    }

    public void setiZeit(Byte iZeit) {
        this.iZeit = iZeit;
    }

    @Basic(optional = false)
    @Column(name = "Status")
    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    @Basic(optional = false)
    @Column(name = "Erforderlich")
    public boolean isFloating() {
        return floating;
    }

    public void setFloating(boolean floating) {
        this.floating = floating;
    }

    @Basic(optional = false)
    @Column(name = "MDate")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    @JoinColumn(name = "PlanID", referencedColumnName = "id")
    @ManyToOne
    public NursingProcess getNursingProcess() {
        return nursingProcess;
    }

    public void setNursingProcess(NursingProcess nursingProcess) {
        this.nursingProcess = nursingProcess;
    }

    @JoinColumn(name = "BWKennung", referencedColumnName = "id")
    @ManyToOne
    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    @JoinColumn(name = "UKennung", referencedColumnName = "id")
    @ManyToOne
    public OPUsers getUser() {
        return user;
    }

    public void setUser(OPUsers user) {
        this.user = user;
    }

    @JoinColumn(name = "MassID", referencedColumnName = "id")
    @ManyToOne
    public Intervention getIntervention() {
        return intervention;
    }

    public void setIntervention(Intervention intervention) {
        this.intervention = intervention;
    }

    @JoinColumn(name = "TermID", referencedColumnName = "id")
    @ManyToOne
    public InterventionSchedule getInterventionSchedule() {
        return interventionSchedule;
    }

    public void setInterventionSchedule(InterventionSchedule interventionSchedule) {
        this.interventionSchedule = interventionSchedule;
    }

    public DFN() {
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
            result = getId().compareTo(other.getId());
        }
        return result;
    }
}

