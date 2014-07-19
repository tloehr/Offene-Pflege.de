package entity.staff;

import entity.files.Qms2File;
import entity.files.Training2File;
import entity.system.Commontags;
import entity.system.Users;
import op.tools.SYSTools;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by tloehr on 17.05.14.
 */
@Entity
@Table(name = "training")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    private long id;

    public long getId() {
        return id;
    }

    @Basic
    @Column(name = "startingon", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date starting;

    public Date getStarting() {
        return starting;
    }

    public void setStarting(Date date) {
        this.starting = date;
    }

    @Basic
    @Column(name = "title", nullable = false, insertable = true, updatable = true, length = 200)
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = SYSTools.tidy(title);
    }

    @Basic
    @Column(name = "docent", nullable = true, insertable = true, updatable = true, length = 200)
    private String docent;

    public String getDocent() {
        return SYSTools.tidy(docent);
    }

    public void setDocent(String docent) {
        this.docent = docent;
    }

    @Basic
    @Column(name = "text", nullable = true, insertable = true, updatable = true, length = 16777215)
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = SYSTools.tidy(text);
    }

    @Basic
    @Column(name = "state", nullable = false, insertable = true, updatable = true)
    private byte state;

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "training")
    private Collection<Training2File> attachedFilesConnections;

    @OneToMany(mappedBy = "training")
    private Collection<Training2Users> attendees;

    public Collection<Training2Users> getAttendees() {
        return attendees;
    }

    @ManyToMany
    @JoinTable(name = "training2tags", joinColumns =
    @JoinColumn(name = "trainid"), inverseJoinColumns =
    @JoinColumn(name = "ctagid"))
    private Collection<Commontags> commontags;

    public Collection<Training2File> getAttachedFilesConnections() {
        return attachedFilesConnections;
    }

    @Version
    @Column(name = "version")
    private Long version;


    public Collection<Commontags> getCommontags() {
        return commontags;
    }

    public void setCommontags(Collection<Commontags> commontags) {
        this.commontags = commontags;
    }

    public Training() {
        commontags = new ArrayList<>();
        state = TrainingTools.STATE_INTERNAL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Training training = (Training) o;

        if (id != training.id) return false;
        if (state != training.state) return false;
        if (attachedFilesConnections != null ? !attachedFilesConnections.equals(training.attachedFilesConnections) : training.attachedFilesConnections != null)
            return false;
        if (attendees != null ? !attendees.equals(training.attendees) : training.attendees != null) return false;
        if (commontags != null ? !commontags.equals(training.commontags) : training.commontags != null) return false;
        if (docent != null ? !docent.equals(training.docent) : training.docent != null) return false;
        if (starting != null ? !starting.equals(training.starting) : training.starting != null) return false;
        if (text != null ? !text.equals(training.text) : training.text != null) return false;
        if (title != null ? !title.equals(training.title) : training.title != null) return false;
        if (version != null ? !version.equals(training.version) : training.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (starting != null ? starting.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (docent != null ? docent.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (int) state;
        result = 31 * result + (attachedFilesConnections != null ? attachedFilesConnections.hashCode() : 0);
        result = 31 * result + (attendees != null ? attendees.hashCode() : 0);
        result = 31 * result + (commontags != null ? commontags.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
