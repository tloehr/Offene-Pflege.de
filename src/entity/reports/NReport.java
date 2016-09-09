package entity.reports;

import entity.Ownable;
import entity.files.SYSNR2FILE;
import entity.info.Resident;
import entity.process.QProcess;
import entity.process.QProcessElement;
import entity.process.SYSNR2PROCESS;
import entity.system.Commontags;
import entity.system.Users;
import op.OPDE;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * This is the entity class responsible for the storage of nursing reports. As this is a documentation, there are some features
 * with this database table that guarantees the correct handling, even when reports are deleted or changed.
 * <p>
 * The reports are <b>never</b> really deleted or altered. They are <b>marked</b> as deleted or altered.
 * <p>
 * <ul>
 * <li><b>DELETE</b> - see remarks for delPit</li>
 * <li><b>CHANGE</b> - when a report is changed:
 * <ol>
 * <li>a new report is cloned from the old one.</li>
 * <li>the new one is marked as replacement for the old one</li>
 * <li>the old report is marked as replaced by the new report</li>
 * <li>the old report looses all attached files and qprocess connections (which the new one inherited of course)</li>
 * <li>the old report is marked with the editor and the PIT (editedBy, editedPIT)</li>
 * </ol>
 * </li>
 * </ul>
 *
 * @author tloehr
 */
@Entity
@Table(name = "nreports")
public class NReport extends Ownable implements Serializable, QProcessElement, Comparable<NReport>, Cloneable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PBID")
    private Long pbid;
    @Version
    @Column(name = "version")
    private Long version;

    /**
     * When did the event depicted in this report really happen ?
     */
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;

    /**
     * This is the time, when the new report was entered. A replacement report has also the PIT of the replacement time.
     * It does NOT carry over the newPIT from the old report.
     */
    @Basic(optional = false)
    @Column(name = "NewPIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date newPIT;

    /**
     * The time when this report was edited. null if it wasn't.
     */
    @Basic(optional = true)
    @Column(name = "EditedPIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editedPIT;

    /**
     * The time when this report was deleted. null if it wasn't.
     */
    @Basic(optional = true)
    @Column(name = "DelPIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date delPIT;

    /**
     * the text of the report
     */
    @Lob
    @Column(name = "Text")
    private String text;

    /**
     * the amount of time the events in this text took to happen. (how much work was it ?)
     */
    @Basic(optional = false)
    @Column(name = "Dauer")
    private int minutes;

    /**
     * the user who entered this report.
     */
    @JoinColumn(name = "NewBy", referencedColumnName = "UKennung")
    @ManyToOne
    private Users newBy;


    /**
     * The user who deleted this report. null if its not deleted.
     */
    @JoinColumn(name = "DeletedBy", referencedColumnName = "UKennung")
    @ManyToOne
    private Users deletedBy;

    /**
     * the resident who <i>owns</i> this report
     */
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;

    /**
     * the user who edited this report. null, if it is still unchanged.
     */
    @JoinColumn(name = "EditedBy", referencedColumnName = "UKennung")
    @ManyToOne
    private Users editedBy;

    /**
     * if this report has been replaced by another one, it is stored here. null, if its not replaced.
     */
    @JoinColumn(name = "ReplacedBy", referencedColumnName = "PBID")
    @OneToOne
    private NReport replacedBy;

    /**
     * if this report is a replacement for another report, then this report is stored here. null, if its no replacement.
     */
    @JoinColumn(name = "ReplacementFor", referencedColumnName = "PBID")
    @OneToOne
    private NReport replacementFor;

    /**
     * the list of attached files.
     */
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "nReport")
    private Collection<SYSNR2FILE> attachedFilesConnections;

    /**
     * for handovers only. the list of users who acknowledged this report.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bericht", fetch = FetchType.EAGER)
    private List<NR2User> usersAcknowledged;

    /**
     * the list of processes which this report was attached to
     */
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "nreport")
    private Collection<SYSNR2PROCESS> attachedProcessConnections;

    /**
     * the list of tags which were sticked to this report.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "nreports2tags", joinColumns =
    @JoinColumn(name = "pbid"), inverseJoinColumns =
    @JoinColumn(name = "ctagid"))
    private Collection<Commontags> commontags;

    public NReport() {
    }

    public NReport(Resident resident) {
        this.pit = new Date();
        this.newPIT = new Date();
        this.text = "";
        this.minutes = 3;
        this.resident = resident;
        this.newBy = OPDE.getLogin().getUser();
        this.attachedFilesConnections = new ArrayList<SYSNR2FILE>();
        this.commontags = new ArrayList<Commontags>();
        this.attachedProcessConnections = new ArrayList<SYSNR2PROCESS>();
        this.usersAcknowledged = new ArrayList<NR2User>();
        this.version = 0l;
    }

    /**
     * private constructor for the cloning only
     *
     * @param pit
     * @param newPIT
     * @param editedPIT
     * @param text
     * @param minutes
     * @param newBy
     * @param resident
     * @param editedBy
     * @param replacedBy
     * @param replacementFor
     */
    private NReport(Date pit, Date newPIT, Date editedPIT, String text, int minutes, Users newBy, Resident resident, Users editedBy, NReport replacedBy, NReport replacementFor) {
        this.pit = pit;
        this.newPIT = newPIT;
        this.editedPIT = editedPIT;
        this.text = SYSTools.tidy(text);
        this.minutes = minutes;
        this.newBy = newBy;
        this.resident = resident;
        this.editedBy = editedBy;
        this.replacedBy = replacedBy;
        this.replacementFor = replacementFor;
        this.attachedFilesConnections = new ArrayList<SYSNR2FILE>();
        this.commontags = new ArrayList<Commontags>();
        this.attachedProcessConnections = new ArrayList<SYSNR2PROCESS>();
        this.usersAcknowledged = new ArrayList<NR2User>();
        this.version = 0l;
    }

    public Long getPbid() {
        return pbid;
    }

    public void setPbid(Long pbid) {
        this.pbid = pbid;
    }

    public List<NR2User> getUsersAcknowledged() {
        return usersAcknowledged;
    }

    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

    public Date getEditedPIT() {
        return editedPIT;
    }

    public String getText() {
        SYSTools.anonymizeText(resident.getNameNeverAnonymous(), text);
        return text;
    }

    public void setText(String text) {
        this.text = SYSTools.tidy(text);
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int dauer) {
        this.minutes = dauer;
    }

    public Resident getResident() {
        return resident;
    }


    public Collection<SYSNR2FILE> getAttachedFilesConnections() {
        return attachedFilesConnections;
    }

    public Date getNewPIT() {
        return newPIT;
    }

    public void setNewPIT(Date newPIT) {
        this.newPIT = newPIT;
    }

    public void setEditedPIT(Date editedPIT) {
        this.editedPIT = editedPIT;
    }

    public Date getDelPIT() {
        return delPIT;
    }

    public void setDelPIT(Date delPIT) {
        this.delPIT = delPIT;
    }

    public Users getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Users deletedBy) {
        this.deletedBy = deletedBy;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Users getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(Users editedBy) {
        this.editedBy = editedBy;
    }

    public NReport getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(NReport replacedBy) {
        this.replacedBy = replacedBy;
    }

    public boolean isReplaced() {
        return replacedBy != null;
    }

    public boolean isReplacement() {
        return replacementFor != null;
    }

    public boolean isAddedLater() {
        return Seconds.secondsBetween(new DateTime(pit), new DateTime(newPIT)).isGreaterThan(Seconds.seconds(NReportTools.IGNORED_AMOUNT_SECONDS_TILL_THE_CLOCK_TURNS_UP));
    }

    public boolean isDeleted() {
        return delPIT != null;
    }

    /**
     * @return true if and only if the report is deleted or replaced
     */
    public boolean isObsolete() {
        return isDeleted() || isReplaced();
    }

    /**
     * Ein "Ersatzbericht", "weiss" wer er mal war, indem er in <code>replacementFor</code> auf den alten,
     * ersetzten Bericht zeigt. Normale Berichte geben hier <code>null</code> zurück.
     *
     * @return
     */
    public NReport getReplacementFor() {
        return replacementFor;
    }

    public void setReplacementFor(NReport replacementFor) {
        this.replacementFor = replacementFor;
    }

    public Collection<Commontags> getCommontags() {
        return commontags;
    }


    public Users getNewBy() {
        return newBy;
    }

    @Override
    public Users getUser() {
        return newBy;
    }

    public void setNewBy(Users user) {
        this.newBy = user;
    }

    @Override
    public Users getOwner() {
        return newBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NReport nReport = (NReport) o;

        if (minutes != nReport.minutes) return false;
//        if (editedBy != null ? !editedBy.equals(nReport.editedBy) : nReport.editedBy != null) return false;
        if (editedPIT != null ? !editedPIT.equals(nReport.editedPIT) : nReport.editedPIT != null) return false;
        if (newPIT != null ? !newPIT.equals(nReport.newPIT) : nReport.newPIT != null) return false;
        if (delPIT != null ? !delPIT.equals(nReport.delPIT) : nReport.delPIT != null) return false;
        if (pbid != null ? !pbid.equals(nReport.pbid) : nReport.pbid != null) return false;
        if (pit != null ? !pit.equals(nReport.pit) : nReport.pit != null) return false;
//        if (replacedBy != null ? !replacedBy.equals(nReport.replacedBy) : nReport.replacedBy != null) return false;
//        if (replacementFor != null ? !replacementFor.equals(nReport.replacementFor) : nReport.replacementFor != null)
//            return false;
        if (resident != null ? !resident.equals(nReport.resident) : nReport.resident != null) return false;
        if (commontags != null ? !commontags.equals(nReport.commontags) : nReport.commontags != null) return false;
        if (text != null ? !text.equals(nReport.text) : nReport.text != null) return false;
        if (newBy != null ? !newBy.equals(nReport.newBy) : nReport.newBy != null) return false;
        if (version != null ? !version.equals(nReport.version) : nReport.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pbid != null ? pbid.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (pit != null ? pit.hashCode() : 0);
        result = 31 * result + (editedPIT != null ? editedPIT.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + minutes;
        result = 31 * result + (newBy != null ? newBy.hashCode() : 0);
        result = 31 * result + (resident != null ? resident.hashCode() : 0);
//        result = 31 * result + (editedBy != null ? editedBy.hashCode() : 0);
//        result = 31 * result + (replacedBy != null ? replacedBy.hashCode() : 0);
//        result = 31 * result + (replacementFor != null ? replacementFor.hashCode() : 0);
        result = 31 * result + (commontags != null ? commontags.hashCode() : 0);
        return result;
    }

    @Override
    public String getContentAsHTML() {
        return NReportTools.getNReportAsHTML(this, false);
    }

    @Override
    public String getPITAsHTML() {
        return NReportTools.getPITAsHTML(this);
    }

    @Override
    public long getID() {
        return pbid;
    }


    @Override
    public String toString() {
        return "entity.reports.NReport[pbid=" + pbid + "]";
    }

    @Override
    public ArrayList<QProcess> getAttachedProcesses() {
        ArrayList<QProcess> list = new ArrayList<QProcess>();
        for (SYSNR2PROCESS att : attachedProcessConnections) {
            list.add(att.getQProcess());
        }
        return list;
    }

    public Collection<SYSNR2PROCESS> getAttachedQProcessConnections() {
        return attachedProcessConnections;
    }

    @Override
    public String getTitle() {
        return SYSTools.xx("misc.msg.report") + ": " + text;
    }

    @Override
    public NReport clone() {

        final NReport clonedReport = new NReport(pit, newPIT, editedPIT, text, minutes, newBy, resident, editedBy, null, null);

        CollectionUtils.forAllDo(commontags, new Closure() {
            public void execute(Object o) {
                clonedReport.commontags.add((Commontags) o);
            }
        });

        CollectionUtils.forAllDo(attachedProcessConnections, new Closure() {
            public void execute(Object o) {
                SYSNR2PROCESS oldAssignment = (SYSNR2PROCESS) o;
                clonedReport.attachedProcessConnections.add(new SYSNR2PROCESS(oldAssignment.getQProcess(), clonedReport));
            }
        });

        CollectionUtils.forAllDo(attachedFilesConnections, new Closure() {
            public void execute(Object o) {
                SYSNR2FILE oldAssignment = (SYSNR2FILE) o;
                clonedReport.attachedFilesConnections.add(new SYSNR2FILE(oldAssignment.getSysfile(), clonedReport, clonedReport.getNewBy(), clonedReport.getPit()));
            }
        });
        return clonedReport;
    }

    @Override
    public long getPITInMillis() {
        return pit.getTime();
    }

    @Override
    public int compareTo(NReport other) {
        return pit.compareTo(other.getPit()) * -1;
    }
}
