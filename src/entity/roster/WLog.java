package entity.roster;

import entity.Homes;
import entity.system.Users;
import op.OPDE;
import op.tools.SYSTools;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 14.08.13
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class WLog implements Comparable<WLog> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "version", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Version
    private long version;
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

    @Column(name = "timestamp", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    @Basic
    private Date timestamp;

    public WLog() {
    }

    public WLog(Rplan rplan) {
        this.rplan = rplan;
        this.creator = OPDE.getLogin().getUser();
        this.actual = null;
        this.homeactual = null;
        timestamp = new Date();
    }

//    public WLog(BigDecimal hours, BigDecimal percent, String actual, Homes homeactual, Rplan rplan, int type) {
//        this.hours = hours;
//        this.percent = percent;
//        this.actual = actual;
//        this.homeactual = homeactual;
//        this.rplan = rplan;
//        this.type = type;
//        this.state = WorkinglogTools.STATE_UNUSED;
//        this.creator = OPDE.getLogin().getUser();
//        this.start = null;
//        this.end = null;
//        timestamp = new Date();
//    }
//
//    public WLog(BigDecimal hours, Date start, Date end, Rplan rplan, String text, int type) {
//        this.hours = hours;
//        this.percent = BigDecimal.ZERO;
//        this.start = start;
//        this.end = end;
//        this.rplan = rplan;
//        this.type = type;
//        this.state = WorkinglogTools.STATE_UNUSED;
//        this.creator = OPDE.getLogin().getUser();
//        this.text = text;
//        timestamp = new Date();
//    }


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

    @Override
    public String toString() {
        return "WLog{" +
                "id=" + id +
                ", version=" + version +
                ", actual='" + actual + '\'' +
                ", homeactual=" + homeactual +
                ", rplan=" + rplan +
                ", creator=" + creator +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WLog wLog = (WLog) o;

        if (id != wLog.id) return false;
        if (version != wLog.version) return false;
        if (actual != null ? !actual.equals(wLog.actual) : wLog.actual != null) return false;
        if (creator != null ? !creator.equals(wLog.creator) : wLog.creator != null) return false;
        if (homeactual != null ? !homeactual.equals(wLog.homeactual) : wLog.homeactual != null) return false;
        if (rplan != null ? !rplan.equals(wLog.rplan) : wLog.rplan != null) return false;
        if (timestamp != null ? !timestamp.equals(wLog.timestamp) : wLog.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (actual != null ? actual.hashCode() : 0);
        result = 31 * result + (homeactual != null ? homeactual.hashCode() : 0);
        result = 31 * result + (rplan != null ? rplan.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(WLog o) {

        int sort = SYSTools.nullCompare(getRplan(), o.getRplan()) * -1;
        if (sort == 0) {
            sort = new Long(id).compareTo(new Long(o.getId()));
        }
        return sort;

    }


}
