package entity.roster;

import entity.system.Users;
import op.OPDE;

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
public class Workaccount {
    @javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;
    @javax.persistence.Column(name = "date", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @javax.persistence.Column(name = "value", nullable = true, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    private BigDecimal value;
    @javax.persistence.Column(name = "type", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    private int type;
    @javax.persistence.Column(name = "reference", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    private Integer reference;
    @javax.persistence.Column(name = "text", nullable = true, insertable = true, updatable = true, length = 200, precision = 0)
    @Basic
    private String text;
    @javax.persistence.Column(name = "version", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Version
    private long version;
    // ---
    @JoinColumn(name = "owner", referencedColumnName = "UKennung")
    @ManyToOne
    private Users owner;
    @JoinColumn(name = "creator", referencedColumnName = "UKennung")
    @ManyToOne
    private Users creator;
//    @JoinColumn(name = "workinglogid", referencedColumnName = "id")
//    @ManyToOne
//    private WLog WLog;

    public Workaccount() {
    }

    public Workaccount(Date date, BigDecimal value, int type, Users owner) {
        this.date = date;
        this.value = value;
        this.type = type;
        this.owner = owner;
        this.creator = OPDE.getLogin().getUser();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


//    public WLog getWLog() {
//        return WLog;
//    }
//
//    public void setWLog(WLog WLog) {
//        this.WLog = WLog;
//    }

    public Users getOwner() {
        return owner;
    }

    public void setOwner(Users owner) {
        this.owner = owner;
    }

    public Users getCreator() {
        return creator;
    }

    public void setCreator(Users creator) {
        this.creator = creator;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public Integer getReference() {
        return reference;
    }

    public void setReference(Integer reference) {
        this.reference = reference;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


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

        Workaccount that = (Workaccount) o;

        if (id != that.id) return false;
        if (type != that.type) return false;
        if (version != that.version) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (reference != null ? !reference.equals(that.reference) : that.reference != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;


        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));

        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + type;
        result = 31 * result + (reference != null ? reference.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }
}
