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
public class Workinglog {
    private long id;

    @javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
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

    private String owner;

    @javax.persistence.Column(name = "owner", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    private String creator;

    @javax.persistence.Column(name = "creator", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    private String controller;

    @javax.persistence.Column(name = "controller", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    private long waccountid;

    @javax.persistence.Column(name = "waccountid", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getWaccountid() {
        return waccountid;
    }

    public void setWaccountid(long waccountid) {
        this.waccountid = waccountid;
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

    private String editby;

    @javax.persistence.Column(name = "editby", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getEditby() {
        return editby;
    }

    public void setEditby(String editby) {
        this.editby = editby;
    }

    private Long replacedby;

    @javax.persistence.Column(name = "replacedby", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public Long getReplacedby() {
        return replacedby;
    }

    public void setReplacedby(Long replacedby) {
        this.replacedby = replacedby;
    }

    private Long replacementfor;

    @javax.persistence.Column(name = "replacementfor", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public Long getReplacementfor() {
        return replacementfor;
    }

    public void setReplacementfor(Long replacementfor) {
        this.replacementfor = replacementfor;
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

        Workinglog that = (Workinglog) o;

        if (id != that.id) return false;
        if (rplanid != that.rplanid) return false;
        if (version != that.version) return false;
        if (waccountid != that.waccountid) return false;
        if (breaktime != null ? !breaktime.equals(that.breaktime) : that.breaktime != null) return false;
        if (controller != null ? !controller.equals(that.controller) : that.controller != null) return false;
        if (creator != null ? !creator.equals(that.creator) : that.creator != null) return false;
        if (editby != null ? !editby.equals(that.editby) : that.editby != null) return false;
        if (end != null ? !end.equals(that.end) : that.end != null) return false;
        if (hours != null ? !hours.equals(that.hours) : that.hours != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (replacedby != null ? !replacedby.equals(that.replacedby) : that.replacedby != null) return false;
        if (replacementfor != null ? !replacementfor.equals(that.replacementfor) : that.replacementfor != null)
            return false;
        if (start != null ? !start.equals(that.start) : that.start != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (rplanid ^ (rplanid >>> 32));
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (hours != null ? hours.hashCode() : 0);
        result = 31 * result + (breaktime != null ? breaktime.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (controller != null ? controller.hashCode() : 0);
        result = 31 * result + (int) (waccountid ^ (waccountid >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (editby != null ? editby.hashCode() : 0);
        result = 31 * result + (replacedby != null ? replacedby.hashCode() : 0);
        result = 31 * result + (replacementfor != null ? replacementfor.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }
}
