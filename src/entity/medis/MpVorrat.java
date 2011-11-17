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
public class MpVorrat {
    private long vorId;

    @javax.persistence.Column(name = "VorID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getVorId() {
        return vorId;
    }

    public void setVorId(long vorId) {
        this.vorId = vorId;
    }

    private String text;

    @javax.persistence.Column(name = "Text", nullable = false, insertable = true, updatable = true, length = 500, precision = 0)
    @Basic
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    private String uKennung;

    @javax.persistence.Column(name = "UKennung", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getuKennung() {
        return uKennung;
    }

    public void setuKennung(String uKennung) {
        this.uKennung = uKennung;
    }

    private Timestamp von;

    @javax.persistence.Column(name = "Von", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getVon() {
        return von;
    }

    public void setVon(Timestamp von) {
        this.von = von;
    }

    private Timestamp bis;

    @javax.persistence.Column(name = "Bis", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getBis() {
        return bis;
    }

    public void setBis(Timestamp bis) {
        this.bis = bis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MpVorrat mpVorrat = (MpVorrat) o;

        if (vorId != mpVorrat.vorId) return false;
        if (bis != null ? !bis.equals(mpVorrat.bis) : mpVorrat.bis != null) return false;
        if (bwKennung != null ? !bwKennung.equals(mpVorrat.bwKennung) : mpVorrat.bwKennung != null) return false;
        if (text != null ? !text.equals(mpVorrat.text) : mpVorrat.text != null) return false;
        if (uKennung != null ? !uKennung.equals(mpVorrat.uKennung) : mpVorrat.uKennung != null) return false;
        if (von != null ? !von.equals(mpVorrat.von) : mpVorrat.von != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (vorId ^ (vorId >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (bwKennung != null ? bwKennung.hashCode() : 0);
        result = 31 * result + (uKennung != null ? uKennung.hashCode() : 0);
        result = 31 * result + (von != null ? von.hashCode() : 0);
        result = 31 * result + (bis != null ? bis.hashCode() : 0);
        return result;
    }
}
