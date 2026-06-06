package de.offene_pflege.entity.files;

import de.offene_pflege.entity.DefaultEntity;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.system.OPUsers;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by tloehr on 19.05.14.
 */
@Entity
@Table(name = "resident2file")
@NoArgsConstructor
public class Resident2File extends DefaultEntity {

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
    private OPUsers editor;

    @JoinColumn(name = "rid", referencedColumnName = "id")
    @ManyToOne
    private Resident resident;

    @JoinColumn(name = "fid", referencedColumnName = "OCFID")
    @ManyToOne
    private SYSFiles sysfile;


    public Resident2File(SYSFiles sysfile, Resident resident, OPUsers editor, Date pit) {
        this.pit = pit;
        this.editor = editor;
        this.resident = resident;
        this.sysfile = sysfile;
    }
}
