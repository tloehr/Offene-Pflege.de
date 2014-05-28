package entity.files;

import entity.system.Users;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by tloehr on 19.05.14.
 */
@Entity
@Table(name = "user2file")
public class User2File {


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

    @JoinColumn(name = "uid", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    @JoinColumn(name = "fid", referencedColumnName = "OCFID")
    @ManyToOne
    private SYSFiles sysfile;

    public User2File() {
    }

    public User2File(SYSFiles sysfile, Users user, Users editor, Date pit) {
        this.pit = pit;
        this.editor = editor;
        this.user = user;
        this.sysfile = sysfile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User2File user2File = (User2File) o;

        if (id != user2File.id) return false;
        if (version != user2File.version) return false;
        if (editor != null ? !editor.equals(user2File.editor) : user2File.editor != null) return false;
        if (pit != null ? !pit.equals(user2File.pit) : user2File.pit != null) return false;
        if (sysfile != null ? !sysfile.equals(user2File.sysfile) : user2File.sysfile != null) return false;
        if (user != null ? !user.equals(user2File.user) : user2File.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (pit != null ? pit.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (editor != null ? editor.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (sysfile != null ? sysfile.hashCode() : 0);
        return result;
    }
}
