package entity.staff;

import entity.files.Training2File;
import entity.system.Users;
import op.tools.SYSTools;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

/**
 * Created by tloehr on 17.05.14.
 */
@Entity
public class Training {

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    private long id;

    public long getId() {
        return id;
    }

    @Basic
    @Column(name = "date", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
    @Column(name = "internal", nullable = false, insertable = true, updatable = true)
    private boolean internal;

    public boolean getInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "training")
    private Collection<Training2File> attachedFilesConnections;

    @ManyToMany(mappedBy = "trainings")
    private Collection<Users> attendees;


    public Collection<Training2File> getAttachedFilesConnections() {
        return attachedFilesConnections;
    }

    @Version
    @Column(name = "version")
    private Long version;

    public Collection<Users> getAttendees() {
        return attendees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Training training = (Training) o;

        if (id != training.id) return false;
        if (internal != training.internal) return false;
        if (date != null ? !date.equals(training.date) : training.date != null) return false;
        if (text != null ? !text.equals(training.text) : training.text != null) return false;
        if (title != null ? !title.equals(training.title) : training.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);

        return result;
    }
}
