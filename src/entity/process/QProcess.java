/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.process;

import entity.info.ResInfo;
import entity.values.ResValue;
import entity.system.Users;
import entity.info.Resident;
import entity.nursingprocess.NursingProcess;
import entity.prescription.Prescription;
import entity.reports.NReport;
import op.OPDE;
import op.tools.SYSConst;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * @author tloehr
 */
@Entity
@Table(name = "Vorgaenge")
//@NamedQueries({
//        @NamedQuery(name = "QProcess.findAll", query = "SELECT v FROM QProcess v "),
//        @NamedQuery(name = "QProcess.findAllActiveSorted", query = "SELECT v FROM QProcess v WHERE v.to = '9999-12-31 23:59:59' ORDER BY v.title"),
//        @NamedQuery(name = "QProcess.findByVorgangID", query = "SELECT v FROM QProcess v WHERE v.pkid = :vorgangID"),
//        @NamedQuery(name = "QProcess.findByTitel", query = "SELECT v FROM QProcess v WHERE v.title = :titel"),
//        @NamedQuery(name = "QProcess.findActiveByBesitzer", query = "SELECT v FROM QProcess v WHERE v.owner = :besitzer AND v.to = '9999-12-31 23:59:59' ORDER BY v.title"),
//        @NamedQuery(name = "QProcess.findInactiveByBesitzer", query = "SELECT v FROM QProcess v WHERE v.owner = :besitzer AND v.to < '9999-12-31 23:59:59' ORDER BY v.title"),
//        @NamedQuery(name = "QProcess.findActiveByBewohner", query = "SELECT v FROM QProcess v WHERE v.resident = :bewohner AND v.to = '9999-12-31 23:59:59' ORDER BY v.title"),
//        @NamedQuery(name = "QProcess.findActiveRunningOut", query = "SELECT v FROM QProcess v WHERE v.to = '9999-12-31 23:59:59' AND v.revision <= :wv ORDER BY v.revision"),
//        @NamedQuery(name = "QProcess.findByVon", query = "SELECT v FROM QProcess v WHERE v.from = :from"),
//        @NamedQuery(name = "QProcess.findByWv", query = "SELECT v FROM QProcess v WHERE v.revision = :wv"),
//        @NamedQuery(name = "QProcess.findByBis", query = "SELECT v FROM QProcess v WHERE v.to = :bis")})
public class QProcess implements Serializable, Comparable<QProcess> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VorgangID")
    private Long pkid;
    @Basic(optional = false)
    @Column(name = "Titel")
    private String title;
    @Basic(optional = false)
    @Column(name = "Von")
    @Temporal(TemporalType.TIMESTAMP)
    private Date from;
    @Basic(optional = false)
    @Column(name = "WV")
    @Temporal(TemporalType.TIMESTAMP)
    private Date revision;
    @Basic(optional = false)
    @Column(name = "Bis")
    @Temporal(TemporalType.TIMESTAMP)
    private Date to;
    @Version
    @Column(name = "version")
    private Long version;
    @JoinColumn(name = "Ersteller", referencedColumnName = "UKennung")
    @ManyToOne
    private Users creator;
    @JoinColumn(name = "Besitzer", referencedColumnName = "UKennung")
    @ManyToOne
    private Users owner;
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;
    @JoinColumn(name = "VKatID", referencedColumnName = "VKatID")
    @ManyToOne
    private PCat pcat;
    //
    // 1:n Relationen
    //
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "qProcess")
    private Collection<PReport> PReports;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "qProcess")
    private Collection<SYSNR2PROCESS> attachedNReportConnections;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "qProcess")
    private Collection<SYSPRE2PROCESS> attachedPrescriptionConnections;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vorgang")
    private Collection<SYSINF2PROCESS> attachedResInfoConnections;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vorgang")
    private Collection<SYSNP2PROCESS> attachedNursingProcesses;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vorgang")
    private Collection<SYSVAL2PROCESS> attachedResValueConnections;

    // ==
    // M:N Relationen
    // ==
//    @ManyToMany
//    @JoinTable(name = "SYSNR2PROCESS", joinColumns =
//    @JoinColumn(name = "VorgangID"), inverseJoinColumns =
//    @JoinColumn(name = "PBID"))
//    private Collection<NReport> pflegeberichte;
//    @ManyToMany
//    @JoinTable(name = "SYSINF2PROCESS", joinColumns =
//    @JoinColumn(name = "VorgangID"), inverseJoinColumns =
//    @JoinColumn(name = "BWInfoID"))
//    private Collection<ResInfo> bwinfos;
//    @ManyToMany
//    @JoinTable(name = "SYSNP2PROCESS", joinColumns =
//    @JoinColumn(name = "VorgangID"), inverseJoinColumns =
//    @JoinColumn(name = "PlanID"))
//    private Collection<Planung> planungen;
//    @ManyToMany
//    @JoinTable(name = "SYSPRE2PROCESS", joinColumns =
//    @JoinColumn(name = "VorgangID"), inverseJoinColumns =
//    @JoinColumn(name = "VerID"))
//    private Collection<Verordnung> prescription;
//    @ManyToMany
//    @JoinTable(name = "SYSBWERTE2VORGANG", joinColumns =
//    @JoinColumn(name = "VorgangID"), inverseJoinColumns =
//    @JoinColumn(name = "BWID"))
//    private Collection<ResValue> bwerte;

    public QProcess() {
    }

    public QProcess(Resident resident) {
        this.title = "";
        this.from = new Date();
        this.revision = new DateTime(new Date()).plusWeeks(2).toDate();
        this.to = SYSConst.DATE_BIS_AUF_WEITERES;
        this.creator = OPDE.getLogin().getUser();
        this.owner = OPDE.getLogin().getUser();
        this.resident = resident;
        this.pcat = null;
        this.PReports = new ArrayList<PReport>();
        this.PReports.add(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_CREATE), PReportTools.PREPORT_TYPE_CREATE, this));
        this.attachedNReportConnections = new ArrayList<SYSNR2PROCESS>();
        this.attachedPrescriptionConnections = new ArrayList<SYSPRE2PROCESS>();
        this.attachedResInfoConnections = new ArrayList<SYSINF2PROCESS>();
        this.attachedNursingProcesses = new ArrayList<SYSNP2PROCESS>();
        this.attachedResValueConnections = new ArrayList<SYSVAL2PROCESS>();
    }

    public QProcess(String title, Resident resident, PCat pcat) {
        this(resident);
        this.title = title;
        this.pcat = pcat;
    }

    public void removeElement(QProcessElement element) {
        if (element instanceof NReport) {
            getAttachedNReportConnections().remove(element);
        } else if (element instanceof ResValue) {
            getAttachedResValueConnections().remove(element);
        } else if (element instanceof Prescription) {
            getAttachedPrescriptionConnections().remove(element);
        } else if (element instanceof ResInfo) {
            getAttachedResInfoConnections().remove(element);
        } else if (element instanceof NursingProcess) {
            getAttachedNursingProcesses().remove(element);
        } else {

        }
    }

    public Collection<SYSNR2PROCESS> getAttachedNReportConnections() {
        return attachedNReportConnections;
    }

    public ArrayList<NReport> getAttachedNReports() {
        ArrayList<NReport> list = new ArrayList<NReport>();
        for (SYSNR2PROCESS att : attachedNReportConnections) {
            list.add(att.getNReport());
        }
        return list;
    }

    public Collection<SYSPRE2PROCESS> getAttachedPrescriptionConnections() {
        return attachedPrescriptionConnections;
    }

    public Collection<SYSINF2PROCESS> getAttachedResInfoConnections() {
        return attachedResInfoConnections;
    }

    public Collection<SYSNP2PROCESS> getAttachedNursingProcesses() {
        return attachedNursingProcesses;
    }

    public Collection<SYSVAL2PROCESS> getAttachedResValueConnections() {
        return attachedResValueConnections;
    }

    public Long getPkid() {
        return pkid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getRevision() {
        return revision;
    }

    public void setRevision(Date revision) {
        this.revision = revision;
    }

    public Date getTo() {
        return to;
    }

    public boolean isYours() {
        return /* OPDE.isAdmin() || */ owner.equals(OPDE.getLogin().getUser());
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Users getCreator() {
        return creator;
    }

    public void setCreator(Users creator) {
        this.creator = creator;
    }

    public Users getOwner() {
        return owner;
    }

    public void setOwner(Users owner) {
        this.owner = owner;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public PCat getPcat() {
        return pcat;
    }

    public void setPcat(PCat pcat) {
        this.pcat = pcat;
    }

    public List<QProcessElement> getElements() {
        ArrayList<QProcessElement> elements = new ArrayList<QProcessElement>();
        elements.addAll(PReports);
        for (SYSNR2PROCESS att : attachedNReportConnections) {
            elements.add(att.getNReport());
        }
        for (SYSNP2PROCESS att : attachedNursingProcesses) {
            elements.add(att.getNursingProcess());
        }
        for (SYSINF2PROCESS att : attachedResInfoConnections) {
            elements.add(att.getResInfo());
        }
        for (SYSPRE2PROCESS att : attachedPrescriptionConnections) {
            elements.add(att.getPrescription());
        }
        for (SYSVAL2PROCESS att : attachedResValueConnections) {
            elements.add(att.getResValue());
        }


        OPDE.debug(this);

        Collections.sort(elements, new Comparator<QProcessElement>() {
            @Override
            public int compare(QProcessElement o1, QProcessElement o2) {
                return new Long(o1.getPITInMillis()).compareTo(o2.getPITInMillis());
            }
        });
        return elements;
    }

    public Collection<PReport> getPReports() {
        return PReports;
    }

    public boolean isClosed() {
        return !to.equals(SYSConst.DATE_BIS_AUF_WEITERES);
    }

    public boolean isCommon() {
        return resident == null;
    }

    public boolean isRevisionDue() {
        return !isClosed() && revision.before(new DateMidnight().plusDays(6).toDateTime().minusSeconds(1).toDate());
    }

    public boolean isRevisionPastDue() {
        return !isClosed() && new DateTime(revision).isAfterNow();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pkid != null ? pkid.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(QProcess other) {
        int result = new Boolean(isClosed()).compareTo(other.isClosed());
        if (result == 0) {
            result = from.compareTo(other.getFrom()) * -1;
        }
        return result;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof QProcess)) {
            return false;
        }
        QProcess other = (QProcess) object;
        if ((this.pkid == null && other.pkid != null) || (this.pkid != null && !this.pkid.equals(other.pkid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return title;
    }
}
