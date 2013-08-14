package entity.roster;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 14.08.13
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Rplan {
    private long id;

    @javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private long rosterid;

    @javax.persistence.Column(name = "rosterid", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getRosterid() {
        return rosterid;
    }

    public void setRosterid(long rosterid) {
        this.rosterid = rosterid;
    }

    private String symbolp;

    @javax.persistence.Column(name = "symbolp", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public String getSymbolp() {
        return symbolp;
    }

    public void setSymbolp(String symbolp) {
        this.symbolp = symbolp;
    }

    private String symbolr;

    @javax.persistence.Column(name = "symbolr", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public String getSymbolr() {
        return symbolr;
    }

    public void setSymbolr(String symbolr) {
        this.symbolr = symbolr;
    }

    private Timestamp start;

    @javax.persistence.Column(name = "start", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    private Timestamp end;

    @javax.persistence.Column(name = "end", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    private BigDecimal basehours;

    @javax.persistence.Column(name = "basehours", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    public BigDecimal getBasehours() {
        return basehours;
    }

    public void setBasehours(BigDecimal basehours) {
        this.basehours = basehours;
    }

    private BigDecimal extrahours;

    @javax.persistence.Column(name = "extrahours", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    public BigDecimal getExtrahours() {
        return extrahours;
    }

    public void setExtrahours(BigDecimal extrahours) {
        this.extrahours = extrahours;
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

    private String owner;

    @javax.persistence.Column(name = "owner", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    private int type;

    @javax.persistence.Column(name = "type", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    private String homeid;

    @javax.persistence.Column(name = "homeid", nullable = false, insertable = true, updatable = true, length = 15, precision = 0)
    @Basic
    public String getHomeid() {
        return homeid;
    }

    public void setHomeid(String homeid) {
        this.homeid = homeid;
    }

    private long version;

    @javax.persistence.Column(name = "version", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rplan rplan = (Rplan) o;

        if (id != rplan.id) return false;
        if (rosterid != rplan.rosterid) return false;
        if (type != rplan.type) return false;
        if (version != rplan.version) return false;
        if (basehours != null ? !basehours.equals(rplan.basehours) : rplan.basehours != null) return false;
        if (breaktime != null ? !breaktime.equals(rplan.breaktime) : rplan.breaktime != null) return false;
        if (end != null ? !end.equals(rplan.end) : rplan.end != null) return false;
        if (extrahours != null ? !extrahours.equals(rplan.extrahours) : rplan.extrahours != null) return false;
        if (homeid != null ? !homeid.equals(rplan.homeid) : rplan.homeid != null) return false;
        if (owner != null ? !owner.equals(rplan.owner) : rplan.owner != null) return false;
        if (start != null ? !start.equals(rplan.start) : rplan.start != null) return false;
        if (symbolp != null ? !symbolp.equals(rplan.symbolp) : rplan.symbolp != null) return false;
        if (symbolr != null ? !symbolr.equals(rplan.symbolr) : rplan.symbolr != null) return false;
        if (text != null ? !text.equals(rplan.text) : rplan.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (rosterid ^ (rosterid >>> 32));
        result = 31 * result + (symbolp != null ? symbolp.hashCode() : 0);
        result = 31 * result + (symbolr != null ? symbolr.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (basehours != null ? basehours.hashCode() : 0);
        result = 31 * result + (extrahours != null ? extrahours.hashCode() : 0);
        result = 31 * result + (breaktime != null ? breaktime.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + type;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (homeid != null ? homeid.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }
}
