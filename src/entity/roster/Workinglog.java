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
public class Workinglog {
    @Basic(optional = false)
    @Column(name = "start")
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;

    @Basic(optional = false)
    @Column(name = "end")
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private long rplanid;

    @javax.persistence.Column(name = "rplanid", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getRplanid() {
        return rplanid;
    }

    public void setRplanid(long rplanid) {
        this.rplanid = rplanid;
    }


    private BigDecimal hours;

    @javax.persistence.Column(name = "hours", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    public BigDecimal getHours() {
        return hours;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }

    private BigDecimal breaktime;

    @javax.persistence.Column(name = "break", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    public BigDecimal getBreaktime() {
        return breaktime;
    }

    public void setBreaktime(BigDecimal breaktime) {
        this.breaktime = breaktime;
    }


    private String text;

    @javax.persistence.Column(name = "text", nullable = true, insertable = true, updatable = true, length = 200, precision = 0)
    @Basic
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @javax.persistence.Column(name = "version", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Version
    private long version;


    @JoinColumn(name = "owner", referencedColumnName = "UKennung")
    @ManyToOne
    private Users owner;

    @JoinColumn(name = "homeid", referencedColumnName = "EID")
    @ManyToOne
    private Homes home;

}
