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
public class User2File {
    private long id;
    private String uid;
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
    @Column(name = "uid", nullable = false, insertable = true, updatable = true, length = 10)
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

        User2File user2File = (User2File) o;

        if (fid != user2File.fid) return false;
        if (id != user2File.id) return false;
        if (version != user2File.version) return false;
        if (editor != null ? !editor.equals(user2File.editor) : user2File.editor != null) return false;
        if (pit != null ? !pit.equals(user2File.pit) : user2File.pit != null) return false;
        if (uid != null ? !uid.equals(user2File.uid) : user2File.uid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (int) (fid ^ (fid >>> 32));
        result = 31 * result + (pit != null ? pit.hashCode() : 0);
        result = 31 * result + (editor != null ? editor.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }
}
