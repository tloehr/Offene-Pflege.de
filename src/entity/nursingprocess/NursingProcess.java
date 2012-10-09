/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.nursingprocess;

import entity.info.ResInfoCategory;
import entity.system.Users;
import entity.info.Resident;
import entity.process.QProcess;
import entity.process.QProcessElement;
import entity.process.SYSNP2PROCESS;
import op.OPDE;
import op.tools.SYSConst;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author tloehr
 */
@Entity
@Table(name = "Planung")
@NamedQueries({
        @NamedQuery(name = "Planung.findAll", query = "SELECT p FROM NursingProcess p"),
        @NamedQuery(name = "Planung.findByPlanID", query = "SELECT p FROM NursingProcess p WHERE p.id = :planID"),
        @NamedQuery(name = "Planung.findByVorgang", query = " "
                + " SELECT p FROM NursingProcess p "
                + " JOIN p.attachedQProcessConnections av"
                + " JOIN av.vorgang v"
                + " WHERE v = :vorgang "),
        @NamedQuery(name = "Planung.findByStichwort", query = "SELECT p FROM NursingProcess p WHERE p.topic = :stichwort"),
        @NamedQuery(name = "Planung.findByVon", query = "SELECT p FROM NursingProcess p WHERE p.von = :von"),
        @NamedQuery(name = "Planung.findByBis", query = "SELECT p FROM NursingProcess p WHERE p.to = :bis"),
        @NamedQuery(name = "Planung.findByPlanKennung", query = "SELECT p FROM NursingProcess p WHERE p.npseries = :planKennung"),
        @NamedQuery(name = "Planung.findByNKontrolle", query = "SELECT p FROM NursingProcess p WHERE p.nextEval = :nKontrolle")})
public class NursingProcess implements Serializable, QProcessElement, Comparable<NursingProcess>, Cloneable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PlanID")
    private Long id;
    @Basic(optional = false)
    @Column(name = "Stichwort")
    private String topic;
    @Lob
    @Column(name = "Situation")
    private String situation;
    @Lob
    @Column(name = "Ziel")
    private String goal;
    //    @Basic(optional = false)
//    @Column(name = "BWIKID")
//    private long bwikid;
    @Basic(optional = false)
    @Column(name = "Von")
    @Temporal(TemporalType.TIMESTAMP)
    private Date from;
    @Basic(optional = false)
    @Column(name = "Bis")
    @Temporal(TemporalType.TIMESTAMP)
    private Date to;
    @Basic(optional = false)
    @Column(name = "PlanKennung")
    private long npseries;
    @Basic(optional = false)
    @Column(name = "NKontrolle")
    @Temporal(TemporalType.DATE)
    private Date nextEval;
    @Version
    @Column(name = "version")
    private Long version;

    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "AnUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users userON;
    @JoinColumn(name = "AbUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users userOFF;
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;
    @JoinColumn(name = "BWIKID", referencedColumnName = "BWIKID")
    @ManyToOne
    private ResInfoCategory category;

    // ==
    // 1:N Relationen
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "nursingProcess")
    private Collection<SYSNP2PROCESS> attachedQProcessConnections;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "nursingProcess")
    private Collection<NPControl> kontrollen;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "nursingProcess")
//    @OrderBy("intervention.bezeichnung ASC")
    private List<InterventionSchedule> interventionSchedules;

    public NursingProcess() {
    }

    public NursingProcess(Resident bewohner) {
        this.resident = bewohner;
        this.userON = OPDE.getLogin().getUser();
        interventionSchedules = new ArrayList<InterventionSchedule>();
        kontrollen = new ArrayList<NPControl>();
        attachedQProcessConnections = new ArrayList<SYSNP2PROCESS>();
        nextEval = new DateTime().plusWeeks(4).toDate();
        from = new Date();
        to = SYSConst.DATE_BIS_AUF_WEITERES;
        this.npseries = -1l;
    }

    @Override
    public long getID() {
        if (id == null){
            return 0;
        }
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String stichwort) {
        this.topic = stichwort;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String ziel) {
        this.goal = ziel;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date von) {
        this.from = von;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date bis) {
        this.to = bis;
    }


    public long getNPSeries() {
        return npseries;
    }

    public void setNPSeries(long planKennung) {
        this.npseries = planKennung;
    }

    public Date getNextEval() {
        return nextEval;
    }

    public void setNextEval(Date nKontrolle) {
        this.nextEval = nKontrolle;
    }

    public Users getUserOFF() {
        return userOFF;
    }

    public void setUserOFF(Users abgesetztDurch) {
        this.userOFF = abgesetztDurch;
    }

    public Users getUserON() {
        return userON;
    }

    public void setUserON(Users angesetztDurch) {
        this.userON = angesetztDurch;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident bewohner) {
        this.resident = bewohner;
    }

    public ResInfoCategory getCategory() {
        return category;
    }

    public void setCategory(ResInfoCategory kategorie) {
        this.category = kategorie;
    }

    public Collection<SYSNP2PROCESS> getAttachedQProcessConnections() {
        return attachedQProcessConnections;
    }

    public boolean isClosed() {
        return to.before(SYSConst.DATE_BIS_AUF_WEITERES);
    }

    public Collection<NPControl> getEvaluations() {
        return kontrollen;
    }

    public List<InterventionSchedule> getInterventionSchedule() {
        return interventionSchedules;
    }

    @Override
    public String getTitle() {
        return topic;
    }

    @Override
    public long getPITInMillis() {
        return from.getTime();
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

//    @Override
//    public long getID() {
//        return id;
//    }

    @Override
    public Users getUser() {
        return userON;
    }

    @Override
    public ArrayList<QProcess> getAttachedProcesses() {
        ArrayList<QProcess> list = new ArrayList<QProcess>();
        for (SYSNP2PROCESS att : attachedQProcessConnections) {
            list.add(att.getVorgang());
        }
        return list;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof NursingProcess)) {
            return false;
        }
        NursingProcess other = (NursingProcess) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(NursingProcess nursingProcess) {
        if (topic.compareTo(nursingProcess.getTopic()) != 0) {
            return topic.compareTo(nursingProcess.getTopic());
        }

        return from.compareTo(nursingProcess.getFrom());
    }

    public NursingProcess(String topic, String situation, String goal, Date from, Date to, long npseries, Date nextEval, Long version, Users userON, Users userOFF, Resident bewohner, ResInfoCategory category, Collection<SYSNP2PROCESS> attachedQProcessConnections, Collection<NPControl> kontrollen, List<InterventionSchedule> interventionSchedules) {
        this.topic = topic;
        this.situation = situation;
        this.goal = goal;
        this.from = from;
        this.to = to;
        this.npseries = npseries;
        this.nextEval = nextEval;
        this.version = version;
        this.userON = userON;
        this.userOFF = userOFF;
        this.resident = bewohner;
        this.category = category;
        this.attachedQProcessConnections = attachedQProcessConnections;
        this.kontrollen = kontrollen;
        this.interventionSchedules = interventionSchedules;
    }

    @Override
    public NursingProcess clone() {
        NursingProcess myNewNP = new NursingProcess(topic, situation, goal, from, to, npseries, nextEval, 0l, userON, userOFF, resident, category, new ArrayList<SYSNP2PROCESS>(), new ArrayList<NPControl>(), new ArrayList<InterventionSchedule>());
        for (InterventionSchedule is : interventionSchedules) {
            InterventionSchedule myIS = is.clone();
            myIS.setNursingProcess(myNewNP);
            myNewNP.getInterventionSchedule().add(myIS);
        }
        return myNewNP;
    }

    @Override
    public String toString() {
        return "entity.nursingprocess.Planung[planID=" + id + "]";
    }
}
