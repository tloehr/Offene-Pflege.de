package entity.roster;

import entity.Homes;
import entity.system.Users;
import op.OPDE;
import op.tools.SYSTools;

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
public class Workinglog implements Comparable<Workinglog> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "start", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    @Basic
    private Date start;
    @Column(name = "end", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    @Basic
    private Date end;


    @Column(name = "hours", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    private BigDecimal hours;
    @Column(name = "percent", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    private BigDecimal percent;
    @Column(name = "text", nullable = true, insertable = true, updatable = true, length = 1024, precision = 0)
    @Basic
    private String text;
    @Column(name = "type", nullable = false, insertable = true, updatable = true, length = 6, precision = 0)
    @Basic
    private int type;
    @Column(name = "actualkey", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    private long actualkey;
    @Column(name = "version", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Version
    private long version;
    @Column(name = "state", nullable = false, insertable = true, updatable = true, length = 6, precision = 0)
    @Basic
    private int state;
    @Column(name = "actual", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    private String actual;
    @JoinColumn(name = "homeactual", referencedColumnName = "EID")
    @ManyToOne
    private Homes homeactual;

    // ---
    @JoinColumn(name = "rplanid", referencedColumnName = "id")
    @ManyToOne
    private Rplan rplan;
    @JoinColumn(name = "creator", referencedColumnName = "UKennung")
    @ManyToOne
    private Users creator;
    @JoinColumn(name = "owner", referencedColumnName = "UKennung")
    @ManyToOne
    private Users owner;

    @Column(name = "timestamp", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    @Basic
    private Date timestamp;

    public Workinglog() {
    }

    public Workinglog(Rplan rplan) {
        this.hours = BigDecimal.ZERO;
        this.percent = BigDecimal.ZERO;
        this.rplan = rplan;
        this.type = WorkinglogTools.TYPE_TIMECLOCK;
        this.state = WorkinglogTools.STATE_UNUSED;
        this.creator = OPDE.getLogin().getUser();
        this.owner = rplan.getOwner();
        this.start = null;
        this.end = null;
        timestamp = new Date();
    }

    public Workinglog(BigDecimal hours, BigDecimal percent, String actual, Homes homeactual, Rplan rplan, int type) {
        this.hours = hours;
        this.percent = percent;
        this.actual = actual;
        this.homeactual = homeactual;
        this.rplan = rplan;
        this.type = type;
        this.state = WorkinglogTools.STATE_UNUSED;
        this.creator = OPDE.getLogin().getUser();
        this.start = null;
        this.end = null;
        timestamp = new Date();
    }

    public Workinglog(BigDecimal hours, Date start, Date end, Rplan rplan, String text, int type) {
        this.hours = hours;
        this.percent = BigDecimal.ZERO;
        this.start = start;
        this.end = end;
        this.rplan = rplan;
        this.type = type;
        this.state = WorkinglogTools.STATE_UNUSED;
        this.creator = OPDE.getLogin().getUser();
        this.text = text;
        timestamp = new Date();
    }

    @Override
    public String toString() {
        return "Workinglog{" +
                "id=" + id +
                ", start=" + start +
                ", end=" + end +
                ", hours=" + hours +
                ", percent=" + percent +
                ", text='" + text + '\'' +
                ", type=" + type +
                ", state=" + state +
                ", actualkey=" + actualkey +
                ", version=" + version +
                ", actual='" + actual + '\'' +
                ", homeactual=" + homeactual +
                ", rplan=" + rplan +
                ", creator=" + creator +
                ", owner=" + owner +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Workinglog that = (Workinglog) o;

        if (actualkey != that.actualkey) return false;
        if (id != that.id) return false;
        if (type != that.type) return false;
        if (version != that.version) return false;
        if (actual != null ? !actual.equals(that.actual) : that.actual != null) return false;
        if (creator != null ? !creator.equals(that.creator) : that.creator != null) return false;
        if (end != null ? !end.equals(that.end) : that.end != null) return false;
        if (homeactual != null ? !homeactual.equals(that.homeactual) : that.homeactual != null) return false;
        if (hours != null ? !hours.equals(that.hours) : that.hours != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (percent != null ? !percent.equals(that.percent) : that.percent != null) return false;
        if (rplan != null ? !rplan.equals(that.rplan) : that.rplan != null) return false;
        if (start != null ? !start.equals(that.start) : that.start != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (hours != null ? hours.hashCode() : 0);
        result = 31 * result + (percent != null ? percent.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + type;
        result = 31 * result + (int) (actualkey ^ (actualkey >>> 32));
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (actual != null ? actual.hashCode() : 0);
        result = 31 * result + (homeactual != null ? homeactual.hashCode() : 0);
        result = 31 * result + (rplan != null ? rplan.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        return result;
    }

    public BigDecimal getHours() {
        return hours;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        timestamp = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
        timestamp = new Date();
    }


    public Rplan getRplan() {
        return rplan;
    }

    public void setRplan(Rplan rplan) {
        this.rplan = rplan;
    }


    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
        timestamp = new Date();
    }

    public Homes getHomeactual() {
        return homeactual;
    }

    public void setHomeactual(Homes homeactual) {
        this.homeactual = homeactual;
    }

    public Users getOwner() {
        return owner;
    }

    public int getType() {
        return type;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
        timestamp = new Date();
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
        timestamp = new Date();
    }

    public boolean isAuto() {
        return type != WorkinglogTools.TYPE_ADDITIONAL && type != WorkinglogTools.TYPE_MANUAL && type != WorkinglogTools.TYPE_TIMECLOCK;
    }


    public boolean isTimeClock(){
        return type == WorkinglogTools.TYPE_TIMECLOCK;
    }

    public void setActualKey(long actual) {
        this.actualkey = actual;
    }


    @Override
    public int compareTo(Workinglog o) {

        int sort = SYSTools.nullCompare(getRplan(), o.getRplan()) * -1;
        if (sort == 0) {
            sort = new Integer(type).compareTo(new Integer(o.getType()));
        }
        if (sort == 0) {
            sort = new Long(id).compareTo(new Long(o.getId()));
        }
        return sort;

    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


}
