package entity.medis;

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
public class MpBestellung {
    private long bestellId;

    @javax.persistence.Column(name = "BestellID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getBestellId() {
        return bestellId;
    }

    public void setBestellId(long bestellId) {
        this.bestellId = bestellId;
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

    private long arztId;

    @javax.persistence.Column(name = "ArztID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getArztId() {
        return arztId;
    }

    public void setArztId(long arztId) {
        this.arztId = arztId;
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

    private String text;

    @javax.persistence.Column(name = "Text", nullable = true, insertable = true, updatable = true, length = 16777215, precision = 0)
    @Basic
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    private Timestamp abschluss;

    @javax.persistence.Column(name = "Abschluss", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getAbschluss() {
        return abschluss;
    }

    public void setAbschluss(Timestamp abschluss) {
        this.abschluss = abschluss;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MpBestellung that = (MpBestellung) o;

        if (arztId != that.arztId) return false;
        if (bestellId != that.bestellId) return false;
        if (vorId != that.vorId) return false;
        if (abschluss != null ? !abschluss.equals(that.abschluss) : that.abschluss != null) return false;
        if (datum != null ? !datum.equals(that.datum) : that.datum != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (uKennung != null ? !uKennung.equals(that.uKennung) : that.uKennung != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (bestellId ^ (bestellId >>> 32));
        result = 31 * result + (int) (vorId ^ (vorId >>> 32));
        result = 31 * result + (int) (arztId ^ (arztId >>> 32));
        result = 31 * result + (uKennung != null ? uKennung.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (datum != null ? datum.hashCode() : 0);
        result = 31 * result + (abschluss != null ? abschluss.hashCode() : 0);
        return result;
    }
}
