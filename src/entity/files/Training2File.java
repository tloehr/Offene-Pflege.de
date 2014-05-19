package entity.files;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * Created by tloehr on 19.05.14.
 */
@Entity
public class Training2File {
    private long id;
    private long trainid;
    private long fid;
    private Timestamp pit;
    private String editor;
    private long version;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "trainid", nullable = false, insertable = true, updatable = true)
    public long getTrainid() {
        return trainid;
    }

    public void setTrainid(long trainid) {
        this.trainid = trainid;
    }

    @Basic
    @Column(name = "fid", nullable = false, insertable = true, updatable = true)
    public long getFid() {
        return fid;
    }

    public void setFid(long fid) {
        this.fid = fid;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Training2File that = (Training2File) o;

        if (fid != that.fid) return false;
        if (id != that.id) return false;
        if (trainid != that.trainid) return false;
        if (version != that.version) return false;
        if (editor != null ? !editor.equals(that.editor) : that.editor != null) return false;
        if (pit != null ? !pit.equals(that.pit) : that.pit != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (trainid ^ (trainid >>> 32));
        result = 31 * result + (int) (fid ^ (fid >>> 32));
        result = 31 * result + (pit != null ? pit.hashCode() : 0);
        result = 31 * result + (editor != null ? editor.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }
}
