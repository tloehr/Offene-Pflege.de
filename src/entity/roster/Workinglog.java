package entity.roster;

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
    @Column(name = "text", nullable = true, insertable = true, updatable = true, length = 400, precision = 0)
    @Basic
    private String text;
    @Column(name = "type", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    private int type;
    @Column(name = "actualkey", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    private long actualkey;
    @Column(name = "version", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Version
    private long version;


    // ---
    @JoinColumn(name = "rplanid", referencedColumnName = "id")
    @ManyToOne
    private Rplan rplan;
    @JoinColumn(name = "creator", referencedColumnName = "UKennung")
    @ManyToOne
    private Users creator;
    @JoinColumn(name = "controller", referencedColumnName = "UKennung")
    @ManyToOne
    private Users controller;
    @JoinColumn(name = "editBy", referencedColumnName = "UKennung")
    @ManyToOne
    private Users editedBy;
    @JoinColumn(name = "ReplacedBy", referencedColumnName = "id")
    @OneToOne
    private Workinglog replacedBy;
    @JoinColumn(name = "ReplacementFor", referencedColumnName = "id")
    @OneToOne
    private Workinglog replacementFor;

    public Workinglog() {
    }

    public Workinglog(BigDecimal hours, BigDecimal percent, Rplan rplan, int type, long actualkey) {
        this.hours = hours;
        this.percent = percent;
        this.rplan = rplan;
        this.type = type;
        this.actualkey = actualkey;
        this.creator = OPDE.getLogin().getUser();
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
                ", actualkey=" + actualkey +
                ", version=" + version +
                ", rplan=" + rplan +
                ", creator=" + creator +
                ", controller=" + controller +
                ", editedBy=" + editedBy +
                ", replacedBy=" + replacedBy +
                ", replacementFor=" + replacementFor +
                "} " + super.toString();
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
        if (controller != null ? !controller.equals(that.controller) : that.controller != null) return false;
        if (creator != null ? !creator.equals(that.creator) : that.creator != null) return false;
        if (editedBy != null ? !editedBy.equals(that.editedBy) : that.editedBy != null) return false;
        if (end != null ? !end.equals(that.end) : that.end != null) return false;
        if (hours != null ? !hours.equals(that.hours) : that.hours != null) return false;
        if (percent != null ? !percent.equals(that.percent) : that.percent != null) return false;
        if (replacedBy != null ? !replacedBy.equals(that.replacedBy) : that.replacedBy != null) return false;
        if (replacementFor != null ? !replacementFor.equals(that.replacementFor) : that.replacementFor != null)
            return false;
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
        result = 31 * result + (rplan != null ? rplan.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (controller != null ? controller.hashCode() : 0);
        result = 31 * result + (editedBy != null ? editedBy.hashCode() : 0);
        result = 31 * result + (replacedBy != null ? replacedBy.hashCode() : 0);
        result = 31 * result + (replacementFor != null ? replacementFor.hashCode() : 0);
        return result;
    }

    public BigDecimal getHours() {
        return hours;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
    }


    public Users getCreator() {
        return creator;
    }

    public void setCreator(Users creator) {
        this.creator = creator;
    }

    public Users getController() {
        return controller;
    }

    public void setController(Users controller) {
        this.controller = controller;
    }

    public Rplan getRplan() {
        return rplan;
    }

    public void setRplan(Rplan rplan) {
        this.rplan = rplan;
    }

    public Users getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(Users editedBy) {
        this.editedBy = editedBy;
    }

    public Workinglog getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(Workinglog replacedBy) {
        this.replacedBy = replacedBy;
    }

    public Workinglog getReplacementFor() {
        return replacementFor;
    }

    public void setReplacementFor(Workinglog replacementFor) {
        this.replacementFor = replacementFor;
    }

    public boolean isReplaced() {
        return replacedBy != null;

    }

    public boolean isReplacement() {
        return replacementFor != null;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getActualKey() {
        return actualkey;
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


    public void setActualKey(long actual) {
        this.actualkey = actual;
    }

    public boolean isDeleted() {
        return editedBy != null && replacedBy == null && replacementFor == null;
    }

    @Override
    public int compareTo(Workinglog o) {

        int sort = SYSTools.nullCompare(getRplan(), o.getRplan()) * -1;
        if (sort == 0) {
            sort = new Long(id).compareTo(new Long(o.getId()));
        }
        return sort;

    }


}
