package entity.roster;

import entity.Homes;
import entity.system.Users;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 16.08.13
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class RPlan {
    @Column(name = "id", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    private long id;
    @Column(name = "p1", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    private String p1;
    @Column(name = "p2", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    private String p2;
    @Column(name = "p3", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    private String p3;
    @Column(name = "start", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    @Basic
    private Date start;
    @Column(name = "end", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    @Basic
    private Date end;
    @Column(name = "basehours", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    private BigDecimal basehours;
    @Column(name = "extrahours", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    private BigDecimal extrahours;
    @Column(name = "break", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    private BigDecimal breaktime;
    @Column(name = "type", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    private int type;
    @Column(name = "text", nullable = true, insertable = true, updatable = true, length = 200, precision = 0)
    @Basic
    private String text;
    // -----------
    @JoinColumn(name = "owner", referencedColumnName = "UKennung")
    @ManyToOne
    private Users owner;
    @JoinColumn(name = "homeid", referencedColumnName = "EID")
    @ManyToOne
    private Homes home;
    @JoinColumn(name = "rosterid", referencedColumnName = "id")
    @ManyToOne
    private Rosters roster;
    @Column(name = "version", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Version
    private long version;


    public Users getOwner() {
        return owner;
    }

    public void setOwner(Users owner) {
        this.owner = owner;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    public void setP2(String p2) {
        this.p2 = p2;
    }

    public void setP3(String p3) {
        this.p3 = p3;
    }

    public String getP1() {
        return p1;
    }

    public String getP2() {
        return p2;
    }

    public String getP3() {
        return p3;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }


    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }


    public BigDecimal getBasehours() {
        return basehours;
    }

    public void setBasehours(BigDecimal basehours) {
        this.basehours = basehours;
    }


    public BigDecimal getExtrahours() {
        return extrahours;
    }

    public void setExtrahours(BigDecimal extrahours) {
        this.extrahours = extrahours;
    }


    public BigDecimal getBreaktime() {
        return breaktime;
    }

    public void setBreaktime(BigDecimal breaktime) {
        this.breaktime = breaktime;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
