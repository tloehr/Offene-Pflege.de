package entity.files;

import entity.staff.Training2Users;
import entity.system.Users;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by tloehr on 19.05.14.
 */
@Entity
@Table(name = "trainatt2file")
public class TrainingAttendee2File implements SYSFilesLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "pit", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;

    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

    @JoinColumn(name = "editor", referencedColumnName = "UKennung")
    @ManyToOne
    private Users editor;

    @JoinColumn(name = "train2uid", referencedColumnName = "id")
    @ManyToOne
    private Training2Users training2Users;

    @Override
    public SYSFilesContainer getSYSFilesContainer() {
        return training2Users;
    }

    @JoinColumn(name = "fid", referencedColumnName = "OCFID")
    @ManyToOne
    private SYSFiles sysfile;

    @Override
    public SYSFiles getSysfile() {
        return sysfile;
    }

    public TrainingAttendee2File() {
    }

    public TrainingAttendee2File(SYSFiles sysfile, Training2Users training2Users, Users editor, Date pit) {
        this.sysfile = sysfile;
        this.training2Users = training2Users;
        this.editor = editor;
        this.pit = pit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrainingAttendee2File that = (TrainingAttendee2File) o;

        if (id != that.id) return false;
        if (editor != null ? !editor.equals(that.editor) : that.editor != null) return false;
        if (pit != null ? !pit.equals(that.pit) : that.pit != null) return false;
        if (sysfile != null ? !sysfile.equals(that.sysfile) : that.sysfile != null) return false;
        if (training2Users != null ? !training2Users.equals(that.training2Users) : that.training2Users != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (pit != null ? pit.hashCode() : 0);
        result = 31 * result + (editor != null ? editor.hashCode() : 0);
        result = 31 * result + (training2Users != null ? training2Users.hashCode() : 0);
        result = 31 * result + (sysfile != null ? sysfile.hashCode() : 0);
        return result;
    }
}
