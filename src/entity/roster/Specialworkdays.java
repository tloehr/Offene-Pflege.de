package entity.roster;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 14.08.13
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Specialworkdays {
    private long id;

    @javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private Date day;

    @javax.persistence.Column(name = "day", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    private String uid;

    @javax.persistence.Column(name = "uid", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private BigDecimal amount;

    @javax.persistence.Column(name = "amount", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

        Specialworkdays that = (Specialworkdays) o;

        if (id != that.id) return false;
        if (type != that.type) return false;
        if (version != that.version) return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (day != null ? !day.equals(that.day) : that.day != null) return false;
        if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (day != null ? day.hashCode() : 0);
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + type;
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }
}
