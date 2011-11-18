package entity.medis;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "MPZusammensetzung")
public class MedZusammensetzung {
    private long zusId;

    @javax.persistence.Column(name = "ZusID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getZusId() {
        return zusId;
    }

    public void setZusId(long zusId) {
        this.zusId = zusId;
    }

    private long stoffId;

    @javax.persistence.Column(name = "StoffID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getStoffId() {
        return stoffId;
    }

    public void setStoffId(long stoffId) {
        this.stoffId = stoffId;
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

    private BigDecimal staerke;

    @javax.persistence.Column(name = "Staerke", nullable = false, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    public BigDecimal getStaerke() {
        return staerke;
    }

    public void setStaerke(BigDecimal staerke) {
        this.staerke = staerke;
    }

    private byte dimension;

    @javax.persistence.Column(name = "Dimension", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getDimension() {
        return dimension;
    }

    public void setDimension(byte dimension) {
        this.dimension = dimension;
    }

    private byte stofftyp;

    @javax.persistence.Column(name = "Stofftyp", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getStofftyp() {
        return stofftyp;
    }

    public void setStofftyp(byte stofftyp) {
        this.stofftyp = stofftyp;
    }

    private String uKennung;

    @javax.persistence.Column(name = "UKennung", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getuKennung() {
        return uKennung;
    }

    public void setuKennung(String uKennung) {
        this.uKennung = uKennung;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedZusammensetzung that = (MedZusammensetzung) o;

        if (dafId != that.dafId) return false;
        if (dimension != that.dimension) return false;
        if (stoffId != that.stoffId) return false;
        if (stofftyp != that.stofftyp) return false;
        if (zusId != that.zusId) return false;
        if (staerke != null ? !staerke.equals(that.staerke) : that.staerke != null) return false;
        if (uKennung != null ? !uKennung.equals(that.uKennung) : that.uKennung != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (zusId ^ (zusId >>> 32));
        result = 31 * result + (int) (stoffId ^ (stoffId >>> 32));
        result = 31 * result + (int) (dafId ^ (dafId >>> 32));
        result = 31 * result + (staerke != null ? staerke.hashCode() : 0);
        result = 31 * result + (int) dimension;
        result = 31 * result + (int) stofftyp;
        result = 31 * result + (uKennung != null ? uKennung.hashCode() : 0);
        return result;
    }
}
