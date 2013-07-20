package entity.dutyroster;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 20.07.13
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Shifts {
    private long id;
    private String owner;
    private long rosterid;
    private String plan;
    private String actual;
    private String creator;
    private String editor;
    private String controller;
    private BigDecimal additional;
    private Date date;

    @javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @javax.persistence.Column(name = "owner", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @javax.persistence.Column(name = "rosterid", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getRosterid() {
        return rosterid;
    }

    public void setRosterid(long rosterid) {
        this.rosterid = rosterid;
    }

    @javax.persistence.Column(name = "plan", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    @javax.persistence.Column(name = "actual", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    @javax.persistence.Column(name = "creator", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @javax.persistence.Column(name = "editor", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    @javax.persistence.Column(name = "controller", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    @javax.persistence.Column(name = "additional", nullable = true, insertable = true, updatable = true, length = 11, precision = 4)
    @Basic
    public BigDecimal getAdditional() {
        return additional;
    }

    public void setAdditional(BigDecimal additional) {
        this.additional = additional;
    }

    @javax.persistence.Column(name = "date", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shifts shifts = (Shifts) o;

        if (id != shifts.id) return false;
        if (rosterid != shifts.rosterid) return false;
        if (actual != null ? !actual.equals(shifts.actual) : shifts.actual != null) return false;
        if (additional != null ? !additional.equals(shifts.additional) : shifts.additional != null) return false;
        if (controller != null ? !controller.equals(shifts.controller) : shifts.controller != null) return false;
        if (creator != null ? !creator.equals(shifts.creator) : shifts.creator != null) return false;
        if (date != null ? !date.equals(shifts.date) : shifts.date != null) return false;
        if (editor != null ? !editor.equals(shifts.editor) : shifts.editor != null) return false;
        if (owner != null ? !owner.equals(shifts.owner) : shifts.owner != null) return false;
        if (plan != null ? !plan.equals(shifts.plan) : shifts.plan != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (int) (rosterid ^ (rosterid >>> 32));
        result = 31 * result + (plan != null ? plan.hashCode() : 0);
        result = 31 * result + (actual != null ? actual.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (editor != null ? editor.hashCode() : 0);
        result = 31 * result + (controller != null ? controller.hashCode() : 0);
        result = 31 * result + (additional != null ? additional.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
