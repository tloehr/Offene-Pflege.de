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
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Dfn {
    private long dfnid;

    @javax.persistence.Column(name = "DFNID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getDfnid() {
        return dfnid;
    }

    public void setDfnid(long dfnid) {
        this.dfnid = dfnid;
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

    private long termId;

    @javax.persistence.Column(name = "TermID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getTermId() {
        return termId;
    }

    public void setTermId(long termId) {
        this.termId = termId;
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

    private long massId;

    @javax.persistence.Column(name = "MassID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getMassId() {
        return massId;
    }

    public void setMassId(long massId) {
        this.massId = massId;
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

    private Timestamp stDatum;

    @javax.persistence.Column(name = "StDatum", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getStDatum() {
        return stDatum;
    }

    public void setStDatum(Timestamp stDatum) {
        this.stDatum = stDatum;
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

    private boolean status;

    @javax.persistence.Column(name = "Status", nullable = false, insertable = true, updatable = true, length = 0, precision = 0)
    @Basic
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    private boolean erforderlich;

    @javax.persistence.Column(name = "Erforderlich", nullable = false, insertable = true, updatable = true, length = 0, precision = 0)
    @Basic
    public boolean isErforderlich() {
        return erforderlich;
    }

    public void setErforderlich(boolean erforderlich) {
        this.erforderlich = erforderlich;
    }

    private BigDecimal dauer;

    @javax.persistence.Column(name = "Dauer", nullable = false, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    public BigDecimal getDauer() {
        return dauer;
    }

    public void setDauer(BigDecimal dauer) {
        this.dauer = dauer;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dfn dfn = (Dfn) o;

        if (dfnid != dfn.dfnid) return false;
        if (erforderlich != dfn.erforderlich) return false;
        if (iZeit != dfn.iZeit) return false;
        if (massId != dfn.massId) return false;
        if (sZeit != dfn.sZeit) return false;
        if (status != dfn.status) return false;
        if (termId != dfn.termId) return false;
        if (bwKennung != null ? !bwKennung.equals(dfn.bwKennung) : dfn.bwKennung != null) return false;
        if (dauer != null ? !dauer.equals(dfn.dauer) : dfn.dauer != null) return false;
        if (ist != null ? !ist.equals(dfn.ist) : dfn.ist != null) return false;
        if (mdate != null ? !mdate.equals(dfn.mdate) : dfn.mdate != null) return false;
        if (soll != null ? !soll.equals(dfn.soll) : dfn.soll != null) return false;
        if (stDatum != null ? !stDatum.equals(dfn.stDatum) : dfn.stDatum != null) return false;
        if (uKennung != null ? !uKennung.equals(dfn.uKennung) : dfn.uKennung != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (dfnid ^ (dfnid >>> 32));
        result = 31 * result + (bwKennung != null ? bwKennung.hashCode() : 0);
        result = 31 * result + (int) (termId ^ (termId >>> 32));
        result = 31 * result + (uKennung != null ? uKennung.hashCode() : 0);
        result = 31 * result + (int) (massId ^ (massId >>> 32));
        result = 31 * result + (soll != null ? soll.hashCode() : 0);
        result = 31 * result + (ist != null ? ist.hashCode() : 0);
        result = 31 * result + (stDatum != null ? stDatum.hashCode() : 0);
        result = 31 * result + (sZeit ? 1 : 0);
        result = 31 * result + (iZeit ? 1 : 0);
        result = 31 * result + (status ? 1 : 0);
        result = 31 * result + (erforderlich ? 1 : 0);
        result = 31 * result + (dauer != null ? dauer.hashCode() : 0);
        result = 31 * result + (mdate != null ? mdate.hashCode() : 0);
        return result;
    }
}
