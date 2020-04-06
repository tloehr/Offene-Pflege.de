/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.backend.entity.process;

import de.offene_pflege.backend.entity.done.Resident;
import de.offene_pflege.backend.entity.system.OPUsers;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "preport")

public class PReport implements Serializable, QElement {

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
    private OPUsers user;

    public PReport() {
    }

    public PReport(String text, short art, QProcess qProcess) {
        this.text = SYSTools.tidy(text);
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
        this.text = SYSTools.tidy(text);
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

    public OPUsers findOwner() {
        return user;
    }

    public void setUser(OPUsers user) {
        this.user = user;
    }

    public boolean isSystem() {
        return art != PReportTools.PREPORT_TYPE_USER;
    }

    public boolean isPDCA() {
        return art == PReportTools.PREPORT_TYPE_SET_PDCA_PLAN || art == PReportTools.PREPORT_TYPE_SET_PDCA_DO || art == PReportTools.PREPORT_TYPE_SET_PDCA_CHECK || art == PReportTools.PREPORT_TYPE_SET_PDCA_ACT;
    }

    @Override
    public String contentAsHTML() {
        return PReportTools.getBerichtAsHTML(this);
    }

    @Override
    public ArrayList<QProcess> findAttachedProcesses() {
        ArrayList<QProcess> list = new ArrayList<QProcess>();
        list.add(qProcess);
        return list;
    }


    @Override
    public long pitInMillis() {
        return pit.getTime();
    }

    @Override
    public String pitAsHTML() {
        return PReportTools.getPITAsHTML(this);
    }

    @Override
    public String titleAsString() {
        if (isPDCA()) return PReportTools.getPDCA(this);
        return text;
    }

    @Override
    public long getID() {
        return vbid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PReport pReport = (PReport) o;

        if (art != pReport.art) return false;
        if (pit != null ? !pit.equals(pReport.pit) : pReport.pit != null) return false;
        if (qProcess != null ? !qProcess.equals(pReport.qProcess) : pReport.qProcess != null) return false;
        if (text != null ? !text.equals(pReport.text) : pReport.text != null) return false;
        if (user != null ? !user.equals(pReport.user) : pReport.user != null) return false;
        if (vbid != null ? !vbid.equals(pReport.vbid) : pReport.vbid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = vbid != null ? vbid.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (pit != null ? pit.hashCode() : 0);
        result = 31 * result + (int) art;
        result = 31 * result + (qProcess != null ? qProcess.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "entity.process.PReport[vbid=" + vbid + "]";
    }
}
