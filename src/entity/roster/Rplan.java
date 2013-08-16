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
public class Rplan {
    private long id;
    private String symbolp;
    private String symbolr;
    private Date start;
    private Date end;
    private BigDecimal basehours;
    private BigDecimal extrahours;
    private BigDecimal breaktime;
    private int type;
    private String text;


    @JoinColumn(name = "owner", referencedColumnName = "UKennung")
       @ManyToOne
       private Users owner;

       @JoinColumn(name = "homeid", referencedColumnName = "EID")
       @ManyToOne
       private Homes home;

       @JoinColumn(name = "rosterid", referencedColumnName = "id")
       @ManyToOne
       private Rosters roster;

       @javax.persistence.Column(name = "version", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
          @Version
          private long version;

    @javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @javax.persistence.Column(name = "symbolp", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public String getSymbolp() {
        return symbolp;
    }

    public void setSymbolp(String symbolp) {
        this.symbolp = symbolp;
    }

    @javax.persistence.Column(name = "symbolr", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public String getSymbolr() {
        return symbolr;
    }

    public void setSymbolr(String symbolr) {
        this.symbolr = symbolr;
    }

    @javax.persistence.Column(name = "start", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    @Basic
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    @javax.persistence.Column(name = "end", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    @Basic
    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @javax.persistence.Column(name = "basehours", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    public BigDecimal getBasehours() {
        return basehours;
    }

    public void setBasehours(BigDecimal basehours) {
        this.basehours = basehours;
    }

    @javax.persistence.Column(name = "extrahours", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    public BigDecimal getExtrahours() {
        return extrahours;
    }

    public void setExtrahours(BigDecimal extrahours) {
        this.extrahours = extrahours;
    }

    @javax.persistence.Column(name = "break", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    public BigDecimal getBreaktime() {
        return breaktime;
    }

    public void setBreaktime(BigDecimal breaktime) {
        this.breaktime = breaktime;
    }


    @javax.persistence.Column(name = "type", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @javax.persistence.Column(name = "text", nullable = true, insertable = true, updatable = true, length = 200, precision = 0)
    @Basic
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }




}
