/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.nursingprocess;

import entity.files.SYSNP2FILE;
import entity.info.ResInfoCategory;
import entity.info.Resident;
import entity.process.QProcess;
import entity.process.QProcessElement;
import entity.process.SYSNP2PROCESS;
import entity.system.Users;
import op.OPDE;
import op.care.nursingprocess.PnlNursingProcess;
import op.care.prescription.PnlPrescription;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author tloehr
 */
@Entity
@Table(name = "Planung")
//@NamedQueries({
//        @NamedQuery(name = "Planung.findAll", query = "SELECT p FROM NursingProcess p"),
//        @NamedQuery(name = "Planung.findByPlanID", query = "SELECT p FROM NursingProcess p WHERE p.id = :planID"),
//        @NamedQuery(name = "Planung.findByVorgang", query = " "
//                + " SELECT p FROM NursingProcess p "
//                + " JOIN p.attachedQProcessConnections av"
//                + " JOIN av.vorgang v"
//                + " WHERE v = :vorgang "),
//        @NamedQuery(name = "Planung.findByStichwort", query = "SELECT p FROM NursingProcess p WHERE p.topic = :stichwort"),
//        @NamedQuery(name = "Planung.findByVon", query = "SELECT p FROM NursingProcess p WHERE p.von = :von"),
//        @NamedQuery(name = "Planung.findByBis", query = "SELECT p FROM NursingProcess p WHERE p.to = :bis"),
//        @NamedQuery(name = "Planung.findByPlanKennung", query = "SELECT p FROM NursingProcess p WHERE p.npseries = :planKennung"),
//        @NamedQuery(name = "Planung.findByNKontrolle", query = "SELECT p FROM NursingProcess p WHERE p.nextEval = :nKontrolle")})
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
    private Collection<SYSNP2FILE> attachedFilesConnections;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "nursingProcess")
    private Collection<SYSNP2PROCESS> attachedQProcessConnections;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "nursingProcess")
    private List<NPControl> npControls;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "nursingProcess")
    private List<InterventionSchedule> interventionSchedules;

    public NursingProcess() {
    }

    public NursingProcess(Resident resident) {
        this.resident = resident;
        this.userON = OPDE.getLogin().getUser();
        interventionSchedules = new ArrayList<InterventionSchedule>();
        npControls = new ArrayList<NPControl>();
        attachedQProcessConnections = new ArrayList<SYSNP2PROCESS>();
        nextEval = new DateTime().plusWeeks(4).toDate();
        from = new Date();
        to = SYSConst.DATE_UNTIL_FURTHER_NOTICE;
        this.npseries = -1l;
    }

    @Override
    public long getID() {
        if (id == null) {
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

    public Collection<SYSNP2FILE> getAttachedFilesConnections() {
        return attachedFilesConnections;
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
        return to.before(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
    }

    public List<NPControl> getEvaluations() {
        return npControls;
    }

    public List<InterventionSchedule> getInterventionSchedule() {
        return interventionSchedules;
    }

    @Override
    public String getTitle() {
        return OPDE.lang.getString(PnlNursingProcess.internalClassID)+ ": " + topic;
    }

    @Override
    public long getPITInMillis() {
        return from.getTime();
    }

    @Override
    public String getContentAsHTML() {
        return NursingProcessTools.getAsHTML(this, false, false, false, false);
    }

    @Override
    public String getPITAsHTML() {
        String result = "";
        DateFormat df = DateFormat.getDateInstance();

        if (isClosed()) {

            result += "<table id=\"fonttext\" border=\"0\" cellspacing=\"0\">";
            result += "<tr>";
            result += "<td valign=\"top\">" + df.format(from) + "</td>";
            result += "<td valign=\"top\">&raquo;</td>";
            result += "<td valign=\"top\">" + df.format(to) + "</td>";
            result += "</tr>\n";
            result += "<tr>";
            result += "<td valign=\"top\">" + userON.getFullname() + "</td>";
            result += "<td valign=\"top\">&raquo;</td>";
            result += "<td valign=\"top\">" + userOFF.getFullname() + "</td>";
            result += "</tr>\n";
            result += "</table>\n";

        } else {
            result += df.format(from) + "&nbsp;&raquo;&raquo;" +
                    "<br/>" +
                    userON.getFullname();
        }
        result += "<br/>[" + getID() + "]";

        return result;
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
            list.add(att.getQProcess());
        }
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NursingProcess that = (NursingProcess) o;

        if (npseries != that.npseries) return false;
        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (goal != null ? !goal.equals(that.goal) : that.goal != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (nextEval != null ? !nextEval.equals(that.nextEval) : that.nextEval != null) return false;
        if (npControls != null ? !npControls.equals(that.npControls) : that.npControls != null) return false;
        if (resident != null ? !resident.equals(that.resident) : that.resident != null) return false;
        if (situation != null ? !situation.equals(that.situation) : that.situation != null) return false;
        if (to != null ? !to.equals(that.to) : that.to != null) return false;
        if (topic != null ? !topic.equals(that.topic) : that.topic != null) return false;
        if (userOFF != null ? !userOFF.equals(that.userOFF) : that.userOFF != null) return false;
        if (userON != null ? !userON.equals(that.userON) : that.userON != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (topic != null ? topic.hashCode() : 0);
        result = 31 * result + (situation != null ? situation.hashCode() : 0);
        result = 31 * result + (goal != null ? goal.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (int) (npseries ^ (npseries >>> 32));
        result = 31 * result + (nextEval != null ? nextEval.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (userON != null ? userON.hashCode() : 0);
        result = 31 * result + (userOFF != null ? userOFF.hashCode() : 0);
        result = 31 * result + (resident != null ? resident.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (npControls != null ? npControls.hashCode() : 0);
        return result;
    }

    //    @Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (id != null ? id.hashCode() : 0);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object object) {
//
//        if (!(object instanceof NursingProcess)) {
//            return false;
//        }
//        NursingProcess other = (NursingProcess) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public int compareTo(NursingProcess that) {
        int result = SYSTools.nullCompare(this, that);
        try {
            if (result == 0 && !isClosed() && that.isClosed()) {
                result = -1;
            }

            if (result == 0 && isClosed() && !that.isClosed()) {
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

    public NursingProcess(String topic, String situation, String goal, Date from, Date to, long npseries, Date nextEval, Long version, Users userON, Users userOFF, Resident bewohner, ResInfoCategory category, Collection<SYSNP2PROCESS> attachedQProcessConnections, List<NPControl> npControls, List<InterventionSchedule> interventionSchedules) {
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
        this.npControls = npControls;
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
