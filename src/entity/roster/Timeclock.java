package entity.roster;

import entity.Homes;
import entity.system.Users;
import op.tools.SYSConst;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by tloehr on 10.01.14.
 */
@Entity
public class Timeclock {
    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "begin", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    private Date begin;

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
    private Date end;

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;

    }

    @Basic
    @Column(name = "text", nullable = true, insertable = true, updatable = true, length = 16777215, precision = 0)
    private String text;

    @Basic
    @Column(name = "state", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    private int state;

    @JoinColumn(name = "owner", referencedColumnName = "UKennung")
    @ManyToOne
    private Users owner;

    @JoinColumn(name = "home", referencedColumnName = "EID")
    @ManyToOne
    private Homes home;

    @Column(name = "version", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Version
    private long version;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


    public Users getOwner() {
        return owner;
    }

    public void setOwner(Users owner) {
        this.owner = owner;
    }

    public Homes getHome() {
        return home;
    }

    public void setHome(Homes home) {
        this.home = home;
    }

    public Timeclock() {
    }

    public Timeclock(Users owner, Homes home) {
        this.owner = owner;
        this.begin = new Date();
        this.end = SYSConst.DATE_UNTIL_FURTHER_NOTICE;
        this.state = TimeclockTools.STATE_UNCHECKED;
        this.home = home;
    }

    public boolean isOpen() {
        return this.end.equals(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Timeclock timeclock = (Timeclock) o;

        if (id != timeclock.id) return false;
        if (state != timeclock.state) return false;
        if (version != timeclock.version) return false;
        if (begin != null ? !begin.equals(timeclock.begin) : timeclock.begin != null) return false;
        if (end != null ? !end.equals(timeclock.end) : timeclock.end != null) return false;
        if (owner != null ? !owner.equals(timeclock.owner) : timeclock.owner != null) return false;
        if (text != null ? !text.equals(timeclock.text) : timeclock.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (begin != null ? begin.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + state;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }
}
