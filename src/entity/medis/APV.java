package entity.medis;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class APV {
    private long apvid;

    @javax.persistence.Column(name = "APVID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getApvid() {
        return apvid;
    }

    public void setApvid(long apvid) {
        this.apvid = apvid;
    }

    private long dafId;

    @javax.persistence.Column(name = "DafID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getDafId() {
        return dafId;
    }

    public void setDafId(long dafId) {
        this.dafId = dafId;
    }

    private String bwKennung;

    @javax.persistence.Column(name = "BWKennung", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getBwKennung() {
        return bwKennung;
    }

    public void setBwKennung(String bwKennung) {
        this.bwKennung = bwKennung;
    }

    private BigDecimal apv;

    @javax.persistence.Column(name = "APV", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    public BigDecimal getApv() {
        return apv;
    }

    public void setApv(BigDecimal apv) {
        this.apv = apv;
    }

    private boolean tauschen;

    @javax.persistence.Column(name = "Tauschen", nullable = false, insertable = true, updatable = true, length = 0, precision = 0)
    @Basic
    public boolean isTauschen() {
        return tauschen;
    }

    public void setTauschen(boolean tauschen) {
        this.tauschen = tauschen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        APV APV = (APV) o;

        if (apvid != APV.apvid) return false;
        if (dafId != APV.dafId) return false;
        if (tauschen != APV.tauschen) return false;
        if (apv != null ? !apv.equals(APV.apv) : APV.apv != null) return false;
        if (bwKennung != null ? !bwKennung.equals(APV.bwKennung) : APV.bwKennung != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (apvid ^ (apvid >>> 32));
        result = 31 * result + (int) (dafId ^ (dafId >>> 32));
        result = 31 * result + (bwKennung != null ? bwKennung.hashCode() : 0);
        result = 31 * result + (apv != null ? apv.hashCode() : 0);
        result = 31 * result + (tauschen ? 1 : 0);
        return result;
    }
}
