package entity.roster;

import entity.Homes;
import entity.system.Users;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 14.08.13
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class WorkingLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Basic(optional = false)
    @Column(name = "start")
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;
    @Basic(optional = false)
    @Column(name = "end")
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;
    @Column(name = "rplanid", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    private long rplanid;
    @Column(name = "hours", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    private BigDecimal hours;
    @Column(name = "break", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    private BigDecimal breaktime;
    @Column(name = "text", nullable = true, insertable = true, updatable = true, length = 200, precision = 0)
    @Basic
    private String text;
    @Column(name = "version", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Version
    private long version;
    // ---
    @JoinColumn(name = "owner", referencedColumnName = "UKennung")
    @ManyToOne
    private Users owner;
    @JoinColumn(name = "homeid", referencedColumnName = "EID")
    @ManyToOne
    private Homes home;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public long getRplanid() {
        return rplanid;
    }

    public void setRplanid(long rplanid) {
        this.rplanid = rplanid;
    }


    public BigDecimal getHours() {
        return hours;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }


    public BigDecimal getBreaktime() {
        return breaktime;
    }

    public void setBreaktime(BigDecimal breaktime) {
        this.breaktime = breaktime;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkingLog that = (WorkingLog) o;

        if (id != that.id) return false;
        if (rplanid != that.rplanid) return false;
        if (version != that.version) return false;
        if (breaktime != null ? !breaktime.equals(that.breaktime) : that.breaktime != null) return false;
        if (end != null ? !end.equals(that.end) : that.end != null) return false;
        if (home != null ? !home.equals(that.home) : that.home != null) return false;
        if (hours != null ? !hours.equals(that.hours) : that.hours != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (start != null ? !start.equals(that.start) : that.start != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (int) (rplanid ^ (rplanid >>> 32));
        result = 31 * result + (hours != null ? hours.hashCode() : 0);
        result = 31 * result + (breaktime != null ? breaktime.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (home != null ? home.hashCode() : 0);
        return result;
    }
}
