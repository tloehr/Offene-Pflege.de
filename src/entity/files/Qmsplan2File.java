package entity.files;

import entity.qms.Qmsplan;
import entity.system.Users;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by tloehr on 19.05.14.
 */
@Entity
@Table(name = "qmsplan2file")
public class Qmsplan2File {

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

    @JoinColumn(name = "qmsplanid", referencedColumnName = "id")
    @ManyToOne
    private Qmsplan qmsplan;

    @JoinColumn(name = "fid", referencedColumnName = "OCFID")
    @ManyToOne
    private SYSFiles sysfile;



    public Qmsplan2File() {
    }

    public Qmsplan2File(SYSFiles sysfile, Qmsplan qmsplan, Users editor, Date pit) {
        this.sysfile = sysfile;
        this.qmsplan = qmsplan;
        this.editor = editor;
        this.pit = pit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Qmsplan2File that = (Qmsplan2File) o;

        if (id != that.id) return false;
        if (editor != null ? !editor.equals(that.editor) : that.editor != null) return false;
        if (pit != null ? !pit.equals(that.pit) : that.pit != null) return false;
        if (sysfile != null ? !sysfile.equals(that.sysfile) : that.sysfile != null) return false;
        if (qmsplan != null ? !qmsplan.equals(that.qmsplan) : that.qmsplan != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (pit != null ? pit.hashCode() : 0);
        result = 31 * result + (editor != null ? editor.hashCode() : 0);
        result = 31 * result + (qmsplan != null ? qmsplan.hashCode() : 0);
        result = 31 * result + (sysfile != null ? sysfile.hashCode() : 0);
        return result;
    }
}
