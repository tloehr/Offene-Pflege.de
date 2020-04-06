/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.backend.entity.nursingprocess;

import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.backend.entity.done.Resident;
import de.offene_pflege.backend.entity.done.SYSNP2FILE;
import de.offene_pflege.backend.entity.info.ResInfoCategory;
import de.offene_pflege.backend.entity.process.QElement;
import de.offene_pflege.backend.entity.process.QProcess;
import de.offene_pflege.backend.entity.process.SYSNP2PROCESS;
import de.offene_pflege.backend.entity.system.Commontags;
import de.offene_pflege.backend.entity.system.OPUsers;
import de.offene_pflege.backend.services.NursingProcessService;
import de.offene_pflege.backend.services.ResidentTools;
import de.offene_pflege.interfaces.Attachable;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author tloehr
 */
@Entity
@Table(name = "nursingprocess")
public class NursingProcess extends DefaultEntity implements QElement, Comparable<NursingProcess>, Cloneable, Attachable {
    private String topic;
    private String situation;
    private String goal;
    private Date from;
    private Date to;
    private long npseries;
    private Date nextEval;
    private OPUsers userON;
    private OPUsers userOFF;
    private Resident resident;
    private ResInfoCategory category;
    private Collection<SYSNP2FILE> attachedFilesConnections;
    private Collection<SYSNP2PROCESS> attachedQProcessConnections;
    private List<NPControl> npControls;
    private List<InterventionSchedule> interventionSchedules;
    private Collection<Commontags> commontags;

    public NursingProcess() {
    }

    @Basic(optional = false)
    @Column(name = "Stichwort")
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Lob
    @Column(name = "Situation")
    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    @Lob
    @Column(name = "Ziel")
    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    @Basic(optional = false)
    @Column(name = "Von")
    @Temporal(TemporalType.TIMESTAMP)

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    @Basic(optional = false)
    @Column(name = "Bis")
    @Temporal(TemporalType.TIMESTAMP)

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    @Basic(optional = false)
    @Column(name = "PlanKennung")
    public long getNpseries() {
        return npseries;
    }

    public void setNpseries(long npseries) {
        this.npseries = npseries;
    }

    @Basic(optional = false)
    @Column(name = "NKontrolle")
    @Temporal(TemporalType.DATE)
    public Date getNextEval() {
        return nextEval;
    }

    public void setNextEval(Date nextEval) {
        this.nextEval = nextEval;
    }

    @JoinColumn(name = "AnUKennung", referencedColumnName = "id")
    @ManyToOne
    public OPUsers getUserON() {
        return userON;
    }

    public void setUserON(OPUsers userON) {
        this.userON = userON;
    }

    @JoinColumn(name = "AbUKennung", referencedColumnName = "id")
    @ManyToOne
    public OPUsers getUserOFF() {
        return userOFF;
    }

    public void setUserOFF(OPUsers userOFF) {
        this.userOFF = userOFF;
    }

    @JoinColumn(name = "BWKennung", referencedColumnName = "id")
    @ManyToOne
    @Override
    public Resident getResident() {
        return resident;
    }

    @Override
    public long findPrimaryKey() {
        return getId();
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    @JoinColumn(name = "BWIKID", referencedColumnName = "id")
    @ManyToOne
    public ResInfoCategory getCategory() {
        return category;
    }

    public void setCategory(ResInfoCategory category) {
        this.category = category;
    }

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "nursingProcess")
    public Collection<SYSNP2FILE> getAttachedFilesConnections() {
        return attachedFilesConnections;
    }

    public void setAttachedFilesConnections(Collection<SYSNP2FILE> attachedFilesConnections) {
        this.attachedFilesConnections = attachedFilesConnections;
    }

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "nursingProcess")
    public Collection<SYSNP2PROCESS> getAttachedQProcessConnections() {
        return attachedQProcessConnections;
    }

    public void setAttachedQProcessConnections(Collection<SYSNP2PROCESS> attachedQProcessConnections) {
        this.attachedQProcessConnections = attachedQProcessConnections;
    }

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "nursingProcess")
    @OrderBy("datum DESC")
    public List<NPControl> getNpControls() {
        return npControls;
    }

    public void setNpControls(List<NPControl> npControls) {
        this.npControls = npControls;
    }

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "nursingProcess", fetch = FetchType.EAGER)
    public List<InterventionSchedule> getInterventionSchedules() {
        return interventionSchedules;
    }

    public void setInterventionSchedules(List<InterventionSchedule> interventionSchedules) {
        this.interventionSchedules = interventionSchedules;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "np2tags", joinColumns =
    @JoinColumn(name = "npid"), inverseJoinColumns =
    @JoinColumn(name = "ctagid"))
    public Collection<Commontags> getCommontags() {
        return commontags;
    }

    public void setCommontags(Collection<Commontags> commontags) {
        this.commontags = commontags;
    }

    @Override
    public String titleAsString() {
        return SYSTools.xx("nursingrecords.nursingprocess") + ": " + topic;
    }

    @Override
    public long pitInMillis() {
        return from.getTime();
    }

    @Override
    public String contentAsHTML() {
        return NursingProcessService.getAsHTML(this, false, false, false, false);
    }

    @Override
    public String pitAsHTML() {
        return NursingProcessService.getPitAsHTML(this);
    }

    @Override
    public OPUsers findOwner() {
        return userON;
    }

    @Override
    public ArrayList<QProcess> findAttachedProcesses() {
        ArrayList<QProcess> list = new ArrayList<QProcess>();
        for (SYSNP2PROCESS att : attachedQProcessConnections) {
            list.add(att.getQProcess());
        }
        return list;
    }


    @Override
    public int compareTo(NursingProcess that) {
        int result = SYSTools.nullCompare(this, that);
        try {
            if (result == 0 && !NursingProcessService.isClosed(this) && NursingProcessService.isClosed(that)) {
                result = -1;
            }

            if (result == 0 && NursingProcessService.isClosed(this) && !NursingProcessService.isClosed(that)) {
                result = 1;
            }

            if (result == 0 && topic.compareTo(that.getTopic()) != 0) {
                result = topic.compareTo(that.getTopic());
            }

            if (result == 0) {
                result = from.compareTo(that.getFrom()) * -1;
            }
        } catch (NullPointerException n) {
            OPDE.error(n);
            result = 0;
        }

        return result;
    }


    @Override
    public boolean active() {
        return ResidentTools.isActive(resident) && !NursingProcessService.isClosed(this);
    }
}
