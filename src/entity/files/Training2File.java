package entity.files;

import entity.staff.Training;
import entity.values.ResValue;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by tloehr on 19.05.14.
 */
@Entity
public class Training2File {
    private Timestamp pit;
    private String editor;
    private long version;

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
    public Timestamp getPit() {
        return pit;
    }

    public void setPit(Timestamp pit) {
        this.pit = pit;
    }

    @Basic
    @Column(name = "editor", nullable = false, insertable = true, updatable = true, length = 10)
    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    @Basic
    @Column(name = "version", nullable = false, insertable = true, updatable = true)
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @JoinColumn(name = "trainid", referencedColumnName = "id")
    @ManyToOne
    private Training training;

    @JoinColumn(name = "fid", referencedColumnName = "OCFID")
    @ManyToOne
    private SYSFiles sysfile;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Training2File that = (Training2File) o;

        if (id != that.id) return false;
        if (version != that.version) return false;
        if (editor != null ? !editor.equals(that.editor) : that.editor != null) return false;
        if (pit != null ? !pit.equals(that.pit) : that.pit != null) return false;
        if (sysfile != null ? !sysfile.equals(that.sysfile) : that.sysfile != null) return false;
        if (training != null ? !training.equals(that.training) : that.training != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (pit != null ? pit.hashCode() : 0);
        result = 31 * result + (editor != null ? editor.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (training != null ? training.hashCode() : 0);
        result = 31 * result + (sysfile != null ? sysfile.hashCode() : 0);
        return result;
    }
}
