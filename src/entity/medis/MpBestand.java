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
public class MpBestand {
    private long bestId;

    @javax.persistence.Column(name = "BestID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getBestId() {
        return bestId;
    }

    public void setBestId(long bestId) {
        this.bestId = bestId;
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

    private long mpid;

    @javax.persistence.Column(name = "MPID", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getMpid() {
        return mpid;
    }

    public void setMpid(long mpid) {
        this.mpid = mpid;
    }

    private long vorId;

    @javax.persistence.Column(name = "VorID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getVorId() {
        return vorId;
    }

    public void setVorId(long vorId) {
        this.vorId = vorId;
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

    private Timestamp ein;

    @javax.persistence.Column(name = "Ein", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getEin() {
        return ein;
    }

    public void setEin(Timestamp ein) {
        this.ein = ein;
    }

    private Timestamp anbruch;

    @javax.persistence.Column(name = "Anbruch", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getAnbruch() {
        return anbruch;
    }

    public void setAnbruch(Timestamp anbruch) {
        this.anbruch = anbruch;
    }

    private long nextBest;

    @javax.persistence.Column(name = "NextBest", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public long getNextBest() {
        return nextBest;
    }

    public void setNextBest(long nextBest) {
        this.nextBest = nextBest;
    }

    private Timestamp aus;

    @javax.persistence.Column(name = "Aus", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getAus() {
        return aus;
    }

    public void setAus(Timestamp aus) {
        this.aus = aus;
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

    private BigDecimal apv;

    @javax.persistence.Column(name = "APV", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    public BigDecimal getApv() {
        return apv;
    }

    public void setApv(BigDecimal apv) {
        this.apv = apv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MpBestand mpBestand = (MpBestand) o;

        if (bestId != mpBestand.bestId) return false;
        if (dafId != mpBestand.dafId) return false;
        if (mpid != mpBestand.mpid) return false;
        if (nextBest != mpBestand.nextBest) return false;
        if (vorId != mpBestand.vorId) return false;
        if (anbruch != null ? !anbruch.equals(mpBestand.anbruch) : mpBestand.anbruch != null) return false;
        if (apv != null ? !apv.equals(mpBestand.apv) : mpBestand.apv != null) return false;
        if (aus != null ? !aus.equals(mpBestand.aus) : mpBestand.aus != null) return false;
        if (ein != null ? !ein.equals(mpBestand.ein) : mpBestand.ein != null) return false;
        if (text != null ? !text.equals(mpBestand.text) : mpBestand.text != null) return false;
        if (uKennung != null ? !uKennung.equals(mpBestand.uKennung) : mpBestand.uKennung != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (bestId ^ (bestId >>> 32));
        result = 31 * result + (int) (dafId ^ (dafId >>> 32));
        result = 31 * result + (int) (mpid ^ (mpid >>> 32));
        result = 31 * result + (int) (vorId ^ (vorId >>> 32));
        result = 31 * result + (uKennung != null ? uKennung.hashCode() : 0);
        result = 31 * result + (ein != null ? ein.hashCode() : 0);
        result = 31 * result + (anbruch != null ? anbruch.hashCode() : 0);
        result = 31 * result + (int) (nextBest ^ (nextBest >>> 32));
        result = 31 * result + (aus != null ? aus.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (apv != null ? apv.hashCode() : 0);
        return result;
    }
}
