package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.backend.entity.system.OPUsers;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by tloehr on 19.05.14.
 */
@Entity
@Table(name = "resident2file")
public class Resident2File extends DefaultEntity {
    private Date pit;
    private OPUsers editor;
    private Resident resident;
    private SYSFiles sysfile;

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

    @JoinColumn(name = "rid", referencedColumnName = "id")
    @ManyToOne
    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    @JoinColumn(name = "fid", referencedColumnName = "id")
    @ManyToOne
    public SYSFiles getSysfile() {
        return sysfile;
    }

    public void setSysfile(SYSFiles sysfile) {
        this.sysfile = sysfile;
    }

    public Resident2File() {
    }

}
