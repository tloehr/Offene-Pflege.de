package entity.dutyroster;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 20.07.13
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Shiftswap {
    private long id;
    private long shift1;
    private long shift2;
    private String emp1;
    private String emp2;
    private String controller;

    @javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @javax.persistence.Column(name = "shift1", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getShift1() {
        return shift1;
    }

    public void setShift1(long shift1) {
        this.shift1 = shift1;
    }

    @javax.persistence.Column(name = "shift2", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getShift2() {
        return shift2;
    }

    public void setShift2(long shift2) {
        this.shift2 = shift2;
    }

    @javax.persistence.Column(name = "emp1", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getEmp1() {
        return emp1;
    }

    public void setEmp1(String emp1) {
        this.emp1 = emp1;
    }

    @javax.persistence.Column(name = "emp2", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getEmp2() {
        return emp2;
    }

    public void setEmp2(String emp2) {
        this.emp2 = emp2;
    }

    @javax.persistence.Column(name = "controller", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shiftswap shiftswap = (Shiftswap) o;

        if (id != shiftswap.id) return false;
        if (shift1 != shiftswap.shift1) return false;
        if (shift2 != shiftswap.shift2) return false;
        if (controller != null ? !controller.equals(shiftswap.controller) : shiftswap.controller != null) return false;
        if (emp1 != null ? !emp1.equals(shiftswap.emp1) : shiftswap.emp1 != null) return false;
        if (emp2 != null ? !emp2.equals(shiftswap.emp2) : shiftswap.emp2 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (shift1 ^ (shift1 >>> 32));
        result = 31 * result + (int) (shift2 ^ (shift2 >>> 32));
        result = 31 * result + (emp1 != null ? emp1.hashCode() : 0);
        result = 31 * result + (emp2 != null ? emp2.hashCode() : 0);
        result = 31 * result + (controller != null ? controller.hashCode() : 0);
        return result;
    }
}
