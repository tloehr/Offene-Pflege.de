package entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class PlanKontrolle {
    private long pKonId;

    @javax.persistence.Column(name = "PKonID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getpKonId() {
        return pKonId;
    }

    public void setpKonId(long pKonId) {
        this.pKonId = pKonId;
    }

    private long planId;

    @javax.persistence.Column(name = "PlanID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getPlanId() {
        return planId;
    }

    public void setPlanId(long planId) {
        this.planId = planId;
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

    private String bemerkung;

    @javax.persistence.Column(name = "Bemerkung", nullable = true, insertable = true, updatable = true, length = 16777215, precision = 0)
    @Basic
    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    private Timestamp datum;

    @javax.persistence.Column(name = "Datum", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getDatum() {
        return datum;
    }

    public void setDatum(Timestamp datum) {
        this.datum = datum;
    }

    private boolean abschluss;

    @javax.persistence.Column(name = "Abschluss", nullable = true, insertable = true, updatable = true, length = 0, precision = 0)
    @Basic
    public boolean isAbschluss() {
        return abschluss;
    }

    public void setAbschluss(boolean abschluss) {
        this.abschluss = abschluss;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlanKontrolle that = (PlanKontrolle) o;

        if (abschluss != that.abschluss) return false;
        if (pKonId != that.pKonId) return false;
        if (planId != that.planId) return false;
        if (bemerkung != null ? !bemerkung.equals(that.bemerkung) : that.bemerkung != null) return false;
        if (datum != null ? !datum.equals(that.datum) : that.datum != null) return false;
        if (uKennung != null ? !uKennung.equals(that.uKennung) : that.uKennung != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (pKonId ^ (pKonId >>> 32));
        result = 31 * result + (int) (planId ^ (planId >>> 32));
        result = 31 * result + (uKennung != null ? uKennung.hashCode() : 0);
        result = 31 * result + (bemerkung != null ? bemerkung.hashCode() : 0);
        result = 31 * result + (datum != null ? datum.hashCode() : 0);
        result = 31 * result + (abschluss ? 1 : 0);
        return result;
    }
}
