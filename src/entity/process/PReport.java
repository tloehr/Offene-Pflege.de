/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.process;

import entity.system.Users;
import entity.info.Resident;
import op.OPDE;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "VBericht")
@NamedQueries({
        @NamedQuery(name = "VBericht.findAll", query = "SELECT v FROM PReport v"),
        @NamedQuery(name = "VBericht.findByVbid", query = "SELECT v FROM PReport v WHERE v.vbid = :vbid"),
        @NamedQuery(name = "VBericht.findByPit", query = "SELECT v FROM PReport v WHERE v.pit = :pit"),
        @NamedQuery(name = "VBericht.findByArt", query = "SELECT v FROM PReport v WHERE v.art = :art"),
        @NamedQuery(name = "VBericht.findByVorgang", query = "SELECT v FROM PReport v WHERE v.qProcess = :vorgang "),
        // 0 heisst nur normale Berichte.
        // PREPORT_TYPE_USER = 0;
        @NamedQuery(name = "VBericht.findByVorgangOhneSystem", query = "SELECT v FROM PReport v WHERE v.qProcess = :vorgang AND v.art = 0")})
public class PReport implements Serializable, QProcessElement {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VBID")
    private Long vbid;
    @Lob
    @Column(name = "Text")
    private String text;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;
    @Basic(optional = false)
    @Column(name = "Art")
    private short art;
    @JoinColumn(name = "VorgangID", referencedColumnName = "VorgangID")
    @ManyToOne
    private QProcess qProcess;
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    public PReport() {
    }

    public PReport(String text, short art, QProcess qProcess) {
        this.text = text;
        this.art = art;
        this.qProcess = qProcess;
        this.pit = new Date();
        this.user = OPDE.getLogin().getUser();
    }

    public Long getVbid() {
        return vbid;
    }

    public void setVbid(Long vbid) {
        this.vbid = vbid;
    }

    public QProcess getQProcess() {
        return qProcess;
    }

    @Override
    public Resident getResident() {
        return qProcess.getResident();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

    public short getArt() {
        return art;
    }

    public void setArt(short art) {
        this.art = art;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public boolean isSystem() {
        return art != PReportTools.PREPORT_TYPE_USER;
    }

    @Override
    public String getContentAsHTML() {
        return PReportTools.getBerichtAsHTML(this);
    }

@Override
    public ArrayList<QProcess> getAttachedProcesses(){
        ArrayList<QProcess> list = new ArrayList<QProcess>();
        list.add(qProcess);
        return list;
    }


    @Override
    public long getPITInMillis() {
        return pit.getTime();
    }

    @Override
    public String getPITAsHTML() {
        return PReportTools.getPITAsHTML(this);
    }

    @Override
    public String getTitle() {
        return text;
    }

    @Override
    public long getID() {
        return vbid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (vbid != null ? vbid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof PReport)) {
            return false;
        }
        PReport other = (PReport) object;
        if ((this.vbid == null && other.vbid != null) || (this.vbid != null && !this.vbid.equals(other.vbid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.process.PReport[vbid=" + vbid + "]";
    }
}
