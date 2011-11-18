package entity.medis;

import op.tools.SYSTools;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "MPFormen")
public class MedFormen {
    private long formId;

    @javax.persistence.Column(name = "FormID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getFormId() {
        return formId;
    }

    public void setFormId(long formId) {
        this.formId = formId;
    }

    private String zubereitung;

    @javax.persistence.Column(name = "Zubereitung", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getZubereitung() {
        return SYSTools.catchNull(zubereitung);
    }

    public void setZubereitung(String zubereitung) {
        this.zubereitung = zubereitung;
    }

    private String anwText;

    @javax.persistence.Column(name = "AnwText", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getAnwText() {
        return SYSTools.catchNull(anwText);
    }

    public void setAnwText(String anwText) {
        this.anwText = anwText;
    }

    private byte anwEinheit;

    @javax.persistence.Column(name = "AnwEinheit", nullable = false, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getAnwEinheit() {
        return anwEinheit;
    }

    public void setAnwEinheit(byte anwEinheit) {
        this.anwEinheit = anwEinheit;
    }

    private byte packEinheit;

    @javax.persistence.Column(name = "PackEinheit", nullable = false, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getPackEinheit() {
        return packEinheit;
    }

    public void setPackEinheit(byte packEinheit) {
        this.packEinheit = packEinheit;
    }

    private long massId;

    @javax.persistence.Column(name = "MassID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getMassId() {
        return massId;
    }

    public void setMassId(long massId) {
        this.massId = massId;
    }

    private byte stellplan;

    @javax.persistence.Column(name = "Stellplan", nullable = false, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getStellplan() {
        return stellplan;
    }

    public void setStellplan(byte stellplan) {
        this.stellplan = stellplan;
    }

    private byte status;

    @javax.persistence.Column(name = "Status", nullable = false, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    private int equiv;

    @javax.persistence.Column(name = "Equiv", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public int getEquiv() {
        return equiv;
    }

    public void setEquiv(int equiv) {
        this.equiv = equiv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedFormen medFormen = (MedFormen) o;

        if (anwEinheit != medFormen.anwEinheit) return false;
        if (equiv != medFormen.equiv) return false;
        if (formId != medFormen.formId) return false;
        if (massId != medFormen.massId) return false;
        if (packEinheit != medFormen.packEinheit) return false;
        if (status != medFormen.status) return false;
        if (stellplan != medFormen.stellplan) return false;
        if (anwText != null ? !anwText.equals(medFormen.anwText) : medFormen.anwText != null) return false;
        if (zubereitung != null ? !zubereitung.equals(medFormen.zubereitung) : medFormen.zubereitung != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (formId ^ (formId >>> 32));
        result = 31 * result + (zubereitung != null ? zubereitung.hashCode() : 0);
        result = 31 * result + (anwText != null ? anwText.hashCode() : 0);
        result = 31 * result + (int) anwEinheit;
        result = 31 * result + (int) packEinheit;
        result = 31 * result + (int) (massId ^ (massId >>> 32));
        result = 31 * result + (int) stellplan;
        result = 31 * result + (int) status;
        result = 31 * result + equiv;
        return result;
    }
}
