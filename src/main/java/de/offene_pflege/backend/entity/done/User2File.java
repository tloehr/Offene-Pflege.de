package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.backend.entity.system.OPUsers;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by tloehr on 19.05.14.
 */
@Entity
@Table(name = "user2file")
public class User2File extends DefaultEntity {
    private Date pit;
    private OPUsers editor;
    private OPUsers opUsers;
    private SYSFiles sysfiles;


    public User2File() {
    }

    @Basic
    @Column(name = "pit", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

    @JoinColumn(name = "editor", referencedColumnName = "id")
    @ManyToOne

    public OPUsers getEditor() {
        return editor;
    }

    public void setEditor(OPUsers editor) {
        this.editor = editor;
    }

    @JoinColumn(name = "uid", referencedColumnName = "id")
    @ManyToOne
    public OPUsers getOpUsers() {
        return opUsers;
    }

    public void setOpUsers(OPUsers opUsers) {
        this.opUsers = opUsers;
    }

    @JoinColumn(name = "fid", referencedColumnName = "id")
    @ManyToOne

    public SYSFiles getSysfiles() {
        return sysfiles;
    }

    public void setSysfiles(SYSFiles sysfiles) {
        this.sysfiles = sysfiles;
    }
}
