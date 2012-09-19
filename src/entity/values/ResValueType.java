package entity.values;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 19.09.12
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "ResValueTypes")
public class ResValueType {
    private long id;

    @javax.persistence.Column(name = "ID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getID() {
        return id;
    }

    public void setID(long id) {
        this.id = id;
    }

    private String text;

    @javax.persistence.Column(name = "Text", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String unit;

    @javax.persistence.Column(name = "Unit", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResValueType that = (ResValueType) o;

        if (id != that.id) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (unit != null ? !unit.equals(that.unit) : that.unit != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        return result;
    }
}
