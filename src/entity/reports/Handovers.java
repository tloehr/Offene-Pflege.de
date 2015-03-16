/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.reports;

import entity.Homes;
import entity.info.Resident;
import entity.process.QProcess;
import entity.process.QProcessElement;
import entity.system.Users;
import op.OPDE;
import op.tools.SYSTools;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author tloehr
 */
@Entity
@Table(name = "handovers")

public class Handovers implements Serializable, QProcessElement, Comparable<Handovers> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HID")
    private Long hid;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;
    @Lob
    @Column(name = "Text")
    private String text;
    @JoinColumn(name = "EID", referencedColumnName = "EID")
    @ManyToOne
    private Homes home;
    @JoinColumn(name = "UID", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bericht", fetch = FetchType.EAGER)
    private List<Handover2User> usersAcknowledged;

    public Handovers() {
    }

    public Handovers(Homes home) {
        this.pit = new Date();
        this.text = null;
        this.home = home;
        this.user = OPDE.getLogin().getUser();
        this.usersAcknowledged = new ArrayList<Handover2User>();
    }

    public Long getUebid() {
        return hid;
    }

    public void setUebid(Long uebid) {
        this.hid = uebid;
    }

    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = SYSTools.tidy(text);
    }

    public Homes getHome() {
        return home;
    }

    public void setHome(Homes home) {
        this.home = home;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public List<Handover2User> getUsersAcknowledged() {
        return usersAcknowledged;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (hid != null ? hid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Handovers)) {
            return false;
        }
        Handovers other = (Handovers) object;
        if ((this.hid == null && other.hid != null) || (this.hid != null && !this.hid.equals(other.hid))) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Handovers o) {
        return pit.compareTo(o.getPit());
    }

    @Override
    public long getPITInMillis() {
        return pit.getTime();
    }

    @Override
    public ArrayList<QProcess> getAttachedProcesses() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getContentAsHTML() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getTitle() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getPITAsHTML() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getID() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Resident getResident() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return "entity.reports.Handovers[uebid=" + hid + "]";
    }
}
