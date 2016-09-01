/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import org.joda.time.Minutes;
import org.joda.time.Seconds;

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
     * Auf welchen Zeitpunkt bezieht sich diese Bericht ?
     */
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;

    /**
     * Wann wurde der Bericht eingetragen ? Das ist immer die aktuelle Zeit. Auch wenn jemand einen bestehenden Bericht ändert,
     * dann übernimmt der die Eigentümerschaft und die newPIT wird auf den jeweiligen Bearbeitungszeitpunkt gesetzt.
     */
    @Basic(optional = false)
    @Column(name = "NewPIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date newPIT;

    /**
     * Wann wurde der Bericht korrigiert ?
     */
    @Basic(optional = true)
    @Column(name = "EditedPIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editedPIT;

    /**
     * Wann wurde der Bericht gelöscht ?
     */
    @Basic(optional = true)
    @Column(name = "DelPIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date delPIT;

    /**
     * der Bericht selbst
     */
    @Lob
    @Column(name = "Text")
    private String text;

    /**
     * Die Angabe der Pflegezeit die gebraucht wurde.
     */
    @Basic(optional = false)
    @Column(name = "Dauer")
    private int minutes;

    /**
     * Wer hat diesen Bericht eingetragen.
     */
    @JoinColumn(name = "NewBy", referencedColumnName = "UKennung")
    @ManyToOne
    private Users newBy;


    /**
     * Wer hat diesen Bericht gelöscht.
     */
    @JoinColumn(name = "DeletedBy", referencedColumnName = "UKennung")
    @ManyToOne
    private Users deletedBy;

    /**
     * zu welchem BW gehört dieser Bericht ?
     */
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;

    /**
     * Wer hat diesen Bericht geändert.
     */
    @JoinColumn(name = "EditedBy", referencedColumnName = "UKennung")
    @ManyToOne
    private Users editedBy;

    /**
     * Bei einer Nachkorrektur: durch welchen Bericht wurde dieses hier ersetzt.
     */
    @JoinColumn(name = "ReplacedBy", referencedColumnName = "PBID")
    @OneToOne
    private NReport replacedBy;

    /**
     * Bei einer Nachkorrektur: welcher Bericht wird duch diesen hier ersetzt.
     */
    @JoinColumn(name = "ReplacementFor", referencedColumnName = "PBID")
    @OneToOne
    private NReport replacementFor;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "nReport")
    private Collection<SYSNR2FILE> attachedFilesConnections;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bericht", fetch = FetchType.EAGER)
    private List<NR2User> usersAcknowledged;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "nreport")
    private Collection<SYSNR2PROCESS> attachedProcessConnections;

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

    /**
     * PIT steht für Point In Time. In diesem Fall Datum und Uhrzeit des Bericht Eintrages.
     *
     * @return
     */
    public Date getPit() {
        return pit;
    }

    /**
     * @param pit steht für Point In Time. In diesem Fall Datum und Uhrzeit des Bericht Eintrages.
     */
    public void setPit(Date pit) {
        this.pit = pit;
    }

    /**
     * Dieses Date ist aus technischen Gründen vorhanden. Es gibt mehrere Möglichkeiten, wie diese PIT zu verstehen ist:
     * <ul>
     * <li>Falls ein Bericht nachgetragen wird, dann steht hier immer der wirkliche Zeitpunkt des Eintrags drin. Im Gegensatz zu <code>PIT</code>, der sich auf den <b>fachlichen</b> Zeitpunkt bezeichnet.</li>
     * <p>
     * <li> Bei einem gelöschten Beitrag steht hier der Zeitpunkt der Löschung drin.</li>
     * <li> Wurde dieser Eintrag durch einen anderen ersetzt, steht hier drin wann das passiert ist.</li>
     * <li> Ist dies ein Eintrag, der einen anderen ersetzt hat dann steht hier <code>null</code>.</li>
     * <li> Wurde der Eintrag nicht geändert, dann steht hier ebenfalls <code>null</code>.</li>
     * <ul>
     *
     * @return
     */
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

//    public boolean isSpecial() {
//        boolean found = false;
//
//        Iterator<NReportTAGS> it = tags.iterator();
//        while (!found && it.hasNext()) {
//            found = it.next().isBesonders();
//        }
//        return found;
//    }


    public Users getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(Users editedBy) {
        this.editedBy = editedBy;
    }

    /**
     * Geänderte Berichte treten immer im Doppel auf. In diesem Fall enthält der alte Bericht in <code>replacedBy</code> einen Verweis auf
     * den Bericht, "der ihn ersetzt". Normale Berichte geben hier <code>null</code> zurück.
     *
     * @return
     */
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
        // OPDE considers up to 2 minutes as NOT added later
        int ignoredDeviation = 120;
        return Seconds.secondsBetween(new DateTime(pit), new DateTime(newPIT)).isGreaterThan(Seconds.seconds(ignoredDeviation));
//            return pit.compareTo(newPIT) != 0;
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
