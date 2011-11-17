package entity.medis;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class MpBuchung {
    private long buchId;

    @javax.persistence.Column(name = "BuchID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getBuchId() {
        return buchId;
    }

    public void setBuchId(long buchId) {
        this.buchId = buchId;
    }

    private long bestId;

    @javax.persistence.Column(name = "BestID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getBestId() {
        return bestId;
    }

    public void setBestId(long bestId) {
        this.bestId = bestId;
    }

    private long bhpid;

    @javax.persistence.Column(name = "BHPID", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getBhpid() {
        return bhpid;
    }

    public void setBhpid(long bhpid) {
        this.bhpid = bhpid;
    }

    private BigDecimal menge;

    @javax.persistence.Column(name = "Menge", nullable = false, insertable = true, updatable = true, length = 11, precision = 4)
    @Basic
    public BigDecimal getMenge() {
        return menge;
    }

    public void setMenge(BigDecimal menge) {
        this.menge = menge;
    }

    private String text;

    @javax.persistence.Column(name = "Text", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    private byte status;

    @javax.persistence.Column(name = "Status", nullable = false, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    private Timestamp pit;

    @javax.persistence.Column(name = "PIT", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getPit() {
        return pit;
    }

    public void setPit(Timestamp pit) {
        this.pit = pit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MpBuchung mpBuchung = (MpBuchung) o;

        if (bestId != mpBuchung.bestId) return false;
        if (bhpid != mpBuchung.bhpid) return false;
        if (buchId != mpBuchung.buchId) return false;
        if (status != mpBuchung.status) return false;
        if (menge != null ? !menge.equals(mpBuchung.menge) : mpBuchung.menge != null) return false;
        if (pit != null ? !pit.equals(mpBuchung.pit) : mpBuchung.pit != null) return false;
        if (text != null ? !text.equals(mpBuchung.text) : mpBuchung.text != null) return false;
        if (uKennung != null ? !uKennung.equals(mpBuchung.uKennung) : mpBuchung.uKennung != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (buchId ^ (buchId >>> 32));
        result = 31 * result + (int) (bestId ^ (bestId >>> 32));
        result = 31 * result + (int) (bhpid ^ (bhpid >>> 32));
        result = 31 * result + (menge != null ? menge.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (uKennung != null ? uKennung.hashCode() : 0);
        result = 31 * result + (int) status;
        result = 31 * result + (pit != null ? pit.hashCode() : 0);
        return result;
    }
}
