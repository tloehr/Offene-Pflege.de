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
@Table(name = "MPStoffe")
public class MedStoffe {
    private long stoffId;

    @javax.persistence.Column(name = "StoffID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getStoffId() {
        return stoffId;
    }

    public void setStoffId(long stoffId) {
        this.stoffId = stoffId;
    }

    private String bezeichnung;

    @javax.persistence.Column(name = "Bezeichnung", nullable = true, insertable = true, updatable = true, length = 500, precision = 0)
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

        MedStoffe medStoffe = (MedStoffe) o;

        if (stoffId != medStoffe.stoffId) return false;
        if (bezeichnung != null ? !bezeichnung.equals(medStoffe.bezeichnung) : medStoffe.bezeichnung != null)
            return false;
        if (uKennung != null ? !uKennung.equals(medStoffe.uKennung) : medStoffe.uKennung != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (stoffId ^ (stoffId >>> 32));
        result = 31 * result + (bezeichnung != null ? bezeichnung.hashCode() : 0);
        result = 31 * result + (uKennung != null ? uKennung.hashCode() : 0);
        return result;
    }
}
