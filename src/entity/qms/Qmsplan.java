package entity.qms;

import entity.system.Commontags;
import entity.system.Users;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by tloehr on 28.05.14.
 */
@Entity
@Table(name = "qmsplan")
public class Qmsplan {

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
    @Column(name = "title", nullable = false, insertable = true, updatable = true, length = 200)
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "description", nullable = true, insertable = true, updatable = true, length = 16777215)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "from", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date from;

    @Basic
    @Column(name = "to", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date to;

    @Version
    @Column(name = "version", nullable = false, insertable = true, updatable = true)
    private long version;

    @JoinColumn(name = "uidon", referencedColumnName = "UKennung")
    @ManyToOne
    private Users userON;
    @JoinColumn(name = "uidoff", referencedColumnName = "UKennung")
    @ManyToOne
    private Users userOFF;

    @ManyToMany
    @JoinTable(name = "qmsp2tags", joinColumns =
    @JoinColumn(name = "qmspid"), inverseJoinColumns =
    @JoinColumn(name = "ctagid"))
    private Collection<Commontags> commontags;


    public Qmsplan() {
    }

    public Qmsplan(String title) {
        this.title = title;
        this.userON = OPDE.getLogin().getUser();
        this.from = new Date();
        this.to = SYSConst.DATE_UNTIL_FURTHER_NOTICE;
        this.userOFF = null;
        commontags = new HashSet<>();
    }

    public Collection<Commontags> getCommontags() {
        return commontags;
    }

    public Date getFrom() {

        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Users getUserON() {
        return userON;
    }

    public void setUserON(Users userON) {
        this.userON = userON;
    }

    public Users getUserOFF() {
        return userOFF;
    }

    public void setUserOFF(Users userOFF) {
        this.userOFF = userOFF;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Qmsplan qmsplan = (Qmsplan) o;

        if (id != qmsplan.id) return false;
        if (version != qmsplan.version) return false;
        if (commontags != null ? !commontags.equals(qmsplan.commontags) : qmsplan.commontags != null) return false;
        if (description != null ? !description.equals(qmsplan.description) : qmsplan.description != null) return false;
        if (from != null ? !from.equals(qmsplan.from) : qmsplan.from != null) return false;
        if (title != null ? !title.equals(qmsplan.title) : qmsplan.title != null) return false;
        if (to != null ? !to.equals(qmsplan.to) : qmsplan.to != null) return false;
        if (userOFF != null ? !userOFF.equals(qmsplan.userOFF) : qmsplan.userOFF != null) return false;
        if (userON != null ? !userON.equals(qmsplan.userON) : qmsplan.userON != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (userON != null ? userON.hashCode() : 0);
        result = 31 * result + (userOFF != null ? userOFF.hashCode() : 0);
        result = 31 * result + (commontags != null ? commontags.hashCode() : 0);
        return result;
    }
}
