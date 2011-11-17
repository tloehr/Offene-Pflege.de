package entity.medis;

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
@Table(name = "MProdukte")
public class MedProdukte {
    private long medPid;

    @javax.persistence.Column(name = "MedPID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getMedPid() {
        return medPid;
    }

    public void setMedPid(long medPid) {
        this.medPid = medPid;
    }

    private long mphid;

    @javax.persistence.Column(name = "MPHID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getMphid() {
        return mphid;
    }

    public void setMphid(long mphid) {
        this.mphid = mphid;
    }

    private String bezeichnung;

    @javax.persistence.Column(name = "Bezeichnung", nullable = false, insertable = true, updatable = true, length = 200, precision = 0)
    @Basic
    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
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

        MedProdukte mProdukte = (MedProdukte) o;

        if (medPid != mProdukte.medPid) return false;
        if (mphid != mProdukte.mphid) return false;
        if (bezeichnung != null ? !bezeichnung.equals(mProdukte.bezeichnung) : mProdukte.bezeichnung != null)
            return false;
        if (uKennung != null ? !uKennung.equals(mProdukte.uKennung) : mProdukte.uKennung != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (medPid ^ (medPid >>> 32));
        result = 31 * result + (int) (mphid ^ (mphid >>> 32));
        result = 31 * result + (bezeichnung != null ? bezeichnung.hashCode() : 0);
        result = 31 * result + (uKennung != null ? uKennung.hashCode() : 0);
        return result;
    }
}
