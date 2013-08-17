package entity.roster;

import javax.persistence.*;
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
    @Column(name = "id", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    private long id;
    @Column(name = "day", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    private Date day;
    @Column(name = "uid", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    private String uid;
    @Column(name = "amount", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    private BigDecimal amount;
    @Column(name = "type", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    private int type;
    @Column(name = "version", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Version
    private long version;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
