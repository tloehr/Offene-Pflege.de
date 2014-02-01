package entity.roster;

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
public class WLogDetails implements Comparable<WLogDetails> {
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
    @Column(name = "version", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Version
    private long version;

    // ---
    @JoinColumn(name = "rplanid", referencedColumnName = "id")
    @ManyToOne
    private Rplan rplan;

    @Column(name = "timestamp", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    @Basic
    private Date timestamp;

    public WLogDetails() {
    }

    public WLogDetails(BigDecimal hours, BigDecimal percent, int type, Rplan rplan) {
        this.start = null;
        this.end = null;
        this.hours = hours;
        this.percent = percent;
        this.type = type;
        this.rplan = rplan;
        this.timestamp = new Date();
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

//    public boolean isAuto() {
//        return type != WLogTools.TYPE_ADDITIONAL && type != WLogTools.TYPE_MANUAL && type != WLogTools.TYPE_TIMECLOCK;
//    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    public void setType(int type) {
        this.type = type;
    }


    @Override
    public int compareTo(WLogDetails o) {
        int sort = new Integer(type).compareTo(new Integer(o.getType()));
        if (sort == 0) {
            sort = new Long(id).compareTo(new Long(o.getId()));
        }
        return sort;

    }


}
