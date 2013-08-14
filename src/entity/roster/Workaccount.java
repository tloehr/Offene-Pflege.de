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
public class Workaccount {
    private long id;

    @javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private Long workinglogid;

    @javax.persistence.Column(name = "workinglogid", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public Long getWorkinglogid() {
        return workinglogid;
    }

    public void setWorkinglogid(Long workinglogid) {
        this.workinglogid = workinglogid;
    }

    private Timestamp date;

    @javax.persistence.Column(name = "date", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
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

    private BigDecimal value;

    @javax.persistence.Column(name = "value", nullable = true, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
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

    private Integer reference;

    @javax.persistence.Column(name = "reference", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Integer getReference() {
        return reference;
    }

    public void setReference(Integer reference) {
        this.reference = reference;
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

        Workaccount that = (Workaccount) o;

        if (id != that.id) return false;
        if (type != that.type) return false;
        if (version != that.version) return false;
        if (creator != null ? !creator.equals(that.creator) : that.creator != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (reference != null ? !reference.equals(that.reference) : that.reference != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (workinglogid != null ? !workinglogid.equals(that.workinglogid) : that.workinglogid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (workinglogid != null ? workinglogid.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + type;
        result = 31 * result + (reference != null ? reference.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }
}
