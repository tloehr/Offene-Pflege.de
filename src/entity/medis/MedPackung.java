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
@Table(name = "MPackung")
public class MedPackung {
    private long mpid;

    @javax.persistence.Column(name = "MPID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getMpid() {
        return mpid;
    }

    public void setMpid(long mpid) {
        this.mpid = mpid;
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

    private String pzn;

    @javax.persistence.Column(name = "PZN", nullable = true, insertable = true, updatable = true, length = 7, precision = 0)
    @Basic
    public String getPzn() {
        return pzn;
    }

    public void setPzn(String pzn) {
        this.pzn = pzn;
    }

    private byte groesse;

    @javax.persistence.Column(name = "Groesse", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getGroesse() {
        return groesse;
    }

    public void setGroesse(byte groesse) {
        this.groesse = groesse;
    }

    private BigDecimal inhalt;

    @javax.persistence.Column(name = "Inhalt", nullable = false, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    public BigDecimal getInhalt() {
        return inhalt;
    }

    public void setInhalt(BigDecimal inhalt) {
        this.inhalt = inhalt;
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

        MedPackung medPackung = (MedPackung) o;

        if (dafId != medPackung.dafId) return false;
        if (groesse != medPackung.groesse) return false;
        if (mpid != medPackung.mpid) return false;
        if (inhalt != null ? !inhalt.equals(medPackung.inhalt) : medPackung.inhalt != null) return false;
        if (pzn != null ? !pzn.equals(medPackung.pzn) : medPackung.pzn != null) return false;
        if (uKennung != null ? !uKennung.equals(medPackung.uKennung) : medPackung.uKennung != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (mpid ^ (mpid >>> 32));
        result = 31 * result + (int) (dafId ^ (dafId >>> 32));
        result = 31 * result + (pzn != null ? pzn.hashCode() : 0);
        result = 31 * result + (int) groesse;
        result = 31 * result + (inhalt != null ? inhalt.hashCode() : 0);
        result = 31 * result + (uKennung != null ? uKennung.hashCode() : 0);
        return result;
    }
}
