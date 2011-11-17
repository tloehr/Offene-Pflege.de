package entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class BHP {
    private long bhpid;

    @javax.persistence.Column(name = "BHPID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getBhpid() {
        return bhpid;
    }

    public void setBhpid(long bhpid) {
        this.bhpid = bhpid;
    }

    private long bhppid;

    @javax.persistence.Column(name = "BHPPID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getBhppid() {
        return bhppid;
    }

    public void setBhppid(long bhppid) {
        this.bhppid = bhppid;
    }

    private String uKennung;

    @javax.persistence.Column(name = "UKennung", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getuKennung() {
        return uKennung;
    }

    public void setuKennung(String uKennung) {
        this.uKennung = uKennung;
    }

    private Timestamp soll;

    @javax.persistence.Column(name = "Soll", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getSoll() {
        return soll;
    }

    public void setSoll(Timestamp soll) {
        this.soll = soll;
    }

    private Timestamp ist;

    @javax.persistence.Column(name = "Ist", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getIst() {
        return ist;
    }

    public void setIst(Timestamp ist) {
        this.ist = ist;
    }

    private boolean sZeit;

    @javax.persistence.Column(name = "SZeit", nullable = true, insertable = true, updatable = true, length = 0, precision = 0)
    @Basic
    public boolean issZeit() {
        return sZeit;
    }

    public void setsZeit(boolean sZeit) {
        this.sZeit = sZeit;
    }

    private boolean iZeit;

    @javax.persistence.Column(name = "IZeit", nullable = true, insertable = true, updatable = true, length = 0, precision = 0)
    @Basic
    public boolean isiZeit() {
        return iZeit;
    }

    public void setiZeit(boolean iZeit) {
        this.iZeit = iZeit;
    }

    private BigDecimal dosis;

    @javax.persistence.Column(name = "Dosis", nullable = true, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    public BigDecimal getDosis() {
        return dosis;
    }

    public void setDosis(BigDecimal dosis) {
        this.dosis = dosis;
    }

    private boolean status;

    @javax.persistence.Column(name = "Status", nullable = true, insertable = true, updatable = true, length = 0, precision = 0)
    @Basic
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    private String bemerkung;

    @javax.persistence.Column(name = "Bemerkung", nullable = true, insertable = true, updatable = true, length = 16777215, precision = 0)
    @Basic
    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    private Timestamp mdate;

    @javax.persistence.Column(name = "_mdate", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getMdate() {
        return mdate;
    }

    public void setMdate(Timestamp mdate) {
        this.mdate = mdate;
    }

    private short dauer;

    @javax.persistence.Column(name = "Dauer", nullable = true, insertable = true, updatable = true, length = 5, precision = 0)
    @Basic
    public short getDauer() {
        return dauer;
    }

    public void setDauer(short dauer) {
        this.dauer = dauer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BHP bhp = (BHP) o;

        if (bhpid != bhp.bhpid) return false;
        if (bhppid != bhp.bhppid) return false;
        if (dauer != bhp.dauer) return false;
        if (iZeit != bhp.iZeit) return false;
        if (sZeit != bhp.sZeit) return false;
        if (status != bhp.status) return false;
        if (bemerkung != null ? !bemerkung.equals(bhp.bemerkung) : bhp.bemerkung != null) return false;
        if (dosis != null ? !dosis.equals(bhp.dosis) : bhp.dosis != null) return false;
        if (ist != null ? !ist.equals(bhp.ist) : bhp.ist != null) return false;
        if (mdate != null ? !mdate.equals(bhp.mdate) : bhp.mdate != null) return false;
        if (soll != null ? !soll.equals(bhp.soll) : bhp.soll != null) return false;
        if (uKennung != null ? !uKennung.equals(bhp.uKennung) : bhp.uKennung != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (bhpid ^ (bhpid >>> 32));
        result = 31 * result + (int) (bhppid ^ (bhppid >>> 32));
        result = 31 * result + (uKennung != null ? uKennung.hashCode() : 0);
        result = 31 * result + (soll != null ? soll.hashCode() : 0);
        result = 31 * result + (ist != null ? ist.hashCode() : 0);
        result = 31 * result + (sZeit ? 1 : 0);
        result = 31 * result + (iZeit ? 1 : 0);
        result = 31 * result + (dosis != null ? dosis.hashCode() : 0);
        result = 31 * result + (status ? 1 : 0);
        result = 31 * result + (bemerkung != null ? bemerkung.hashCode() : 0);
        result = 31 * result + (mdate != null ? mdate.hashCode() : 0);
        result = 31 * result + (int) dauer;
        return result;
    }
}
