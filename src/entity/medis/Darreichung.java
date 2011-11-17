package entity.medis;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "MPDarreichung")
public class Darreichung {
    private long dafId;

    @javax.persistence.Column(name = "DafID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getDafId() {
        return dafId;
    }

    public void setDafId(long dafId) {
        this.dafId = dafId;
    }

    private String zusatz;

    @javax.persistence.Column(name = "Zusatz", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getZusatz() {
        return zusatz;
    }

    public void setZusatz(String zusatz) {
        this.zusatz = zusatz;
    }

    private long formId;

    @javax.persistence.Column(name = "FormID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getFormId() {
        return formId;
    }

    public void setFormId(long formId) {
        this.formId = formId;
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

    // N:1 Relationen
    @JoinColumn(name = "MedPID", referencedColumnName = "MedPID")
    @ManyToOne
    private MedProdukte medProdukt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Darreichung that = (Darreichung) o;

        if (dafId != that.dafId) return false;
        if (formId != that.formId) return false;
        if (!medProdukt.equals(that)) return false;
        if (uKennung != null ? !uKennung.equals(that.uKennung) : that.uKennung != null) return false;
        if (zusatz != null ? !zusatz.equals(that.zusatz) : that.zusatz != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (dafId ^ (dafId >>> 32));
        result = 31 * result + (zusatz != null ? zusatz.hashCode() : 0);
        result = 31 * result + (int) (medProdukt.getMedPid() ^ (medProdukt.getMedPid() >>> 32));
        result = 31 * result + (int) (formId ^ (formId >>> 32));
        result = 31 * result + (uKennung != null ? uKennung.hashCode() : 0);
        return result;
    }
}
