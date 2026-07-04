package de.offene_pflege.entity.reports;

import de.offene_pflege.entity.DefaultEntity;
import de.offene_pflege.entity.files.SYSNR2FILE;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.info.ResidentTools;
import de.offene_pflege.entity.prescription.BHP;
import de.offene_pflege.entity.process.QProcess;
import de.offene_pflege.entity.process.QProcessElement;
import de.offene_pflege.entity.process.SYSNR2PROCESS;
import de.offene_pflege.entity.system.Commontags;
import de.offene_pflege.entity.system.OPUsers;
import de.offene_pflege.interfaces.Attachable;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSTools;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

@Entity
@Table(name = "nreports")
@Getter
@Setter
@NoArgsConstructor
public class NReport extends DefaultEntity implements Serializable, QProcessElement, Comparable<NReport>, Attachable {
    private Date pit;
    private Date newPIT;
    private Date editedPIT;
    private Date delPIT;
    @Lob
    private String text;

    @JoinColumn(name = "NewBy", referencedColumnName = "UKennung")
    @ManyToOne
    private OPUsers newBy;

    @JoinColumn(name = "DeletedBy", referencedColumnName = "UKennung")
    @ManyToOne
    private OPUsers deletedBy;

    @JoinColumn(name = "BWKennung", referencedColumnName = "id")
    @ManyToOne
    private Resident resident;

    @JoinColumn(name = "EditedBy", referencedColumnName = "UKennung")
    @ManyToOne
    private OPUsers editedBy;

    @JoinColumn(name = "ReplacedBy", referencedColumnName = "id")
    @OneToOne
    private NReport replacedBy;

    @JoinColumn(name = "ReplacementFor", referencedColumnName = "id")
    @OneToOne
    private NReport replacementFor;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "nReport")
    private Collection<SYSNR2FILE> attachedFilesConnections;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bericht", fetch = FetchType.EAGER)
    private List<NR2User> usersAcknowledged;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "outcome_report", fetch = FetchType.LAZY)
    private Collection<BHP> outcomes;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "nreport")
    private Collection<SYSNR2PROCESS> attachedProcessConnections;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "nreports2tags", joinColumns =
    @JoinColumn(name = "pbid"), inverseJoinColumns =
    @JoinColumn(name = "ctagid"))
    private Collection<Commontags> commontags;

    @Transient
    public boolean isReplaced() {
        return replacedBy != null;
    }

    @Transient
    public boolean isReplacement() {
        return replacementFor != null;
    }

    @Transient
    public boolean isAddedLater() {
        return Seconds.secondsBetween(new DateTime(pit), new DateTime(newPIT)).isGreaterThan(Seconds.seconds(NReportTools.IGNORED_AMOUNT_SECONDS_TILL_THE_CLOCK_TURNS_UP));
    }

    @Transient
    public boolean isDeleted() {
        return delPIT != null;
    }

    @Transient
    public boolean isObsolete() {
        return isDeleted() || isReplaced();
    }

    @Transient
    @Override
    public OPUsers getUser() {
        return newBy;
    }

    @Transient
    @Override
    public String getContentAsHTML() {
        return NReportTools.getNReportAsHTML(this, false);
    }

    @Transient
    @Override
    public String getPITAsHTML() {
        return NReportTools.getPITAsHTML(this);
    }

    @Transient
    @Override
    public long getID() {
        return id;
    }

    @Transient
    @Override
    public ArrayList<QProcess> getAttachedProcesses() {
        ArrayList<QProcess> list = new ArrayList<>();
        for (SYSNR2PROCESS att : attachedProcessConnections) {
            list.add(att.getQProcess());
        }
        return list;
    }

    @Transient
    @Override
    public String getTitle() {
        return SYSTools.xx("misc.msg.report") + ": " + text;
    }

    @Transient
    @Override
    public long getPITInMillis() {
        return pit.getTime();
    }

    @Override
    public int compareTo(NReport other) {
        return pit.compareTo(other.getPit()) * -1;
    }

    @Transient
    @Override
    public boolean isActive() {
        return ResidentTools.isActive(resident) && !isObsolete();
    }

    @Transient
    public boolean isMine() {
        return newBy.equals(OPDE.getMe());
    }
}

