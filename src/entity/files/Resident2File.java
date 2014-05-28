package entity.files;

import entity.info.Resident;
import entity.system.Users;
import javax.persistence.*;
import java.util.Date;

/**
 * Created by tloehr on 19.05.14.
 */
@Entity
@Table(name = "resident2file")
public class Resident2File {

    @Id
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

    @Basic

    @Column(name = "version", nullable = false, insertable = true, updatable = true)
    private long version;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }


    @JoinColumn(name = "editor", referencedColumnName = "UKennung")
    @ManyToOne
    private Users editor;

    @JoinColumn(name = "rid", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;

    @JoinColumn(name = "fid", referencedColumnName = "OCFID")
    @ManyToOne
    private SYSFiles sysfile;


    public Resident2File() {
    }

    public Resident2File(SYSFiles sysfile, Resident resident, Users editor, Date pit) {
        this.pit = pit;
        this.editor = editor;
        this.resident = resident;
        this.sysfile = sysfile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resident2File that = (Resident2File) o;

        if (id != that.id) return false;
        if (version != that.version) return false;
        if (editor != null ? !editor.equals(that.editor) : that.editor != null) return false;
        if (pit != null ? !pit.equals(that.pit) : that.pit != null) return false;
        if (resident != null ? !resident.equals(that.resident) : that.resident != null) return false;
        if (sysfile != null ? !sysfile.equals(that.sysfile) : that.sysfile != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (pit != null ? pit.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (editor != null ? editor.hashCode() : 0);
        result = 31 * result + (resident != null ? resident.hashCode() : 0);
        result = 31 * result + (sysfile != null ? sysfile.hashCode() : 0);
        return result;
    }
}
