package entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Taschengeld {
    private long tgid;

    @javax.persistence.Column(name = "TGID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getTgid() {
        return tgid;
    }

    public void setTgid(long tgid) {
        this.tgid = tgid;
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

    private Date belegDatum;

    @javax.persistence.Column(name = "BelegDatum", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Date getBelegDatum() {
        return belegDatum;
    }

    public void setBelegDatum(Date belegDatum) {
        this.belegDatum = belegDatum;
    }

    private String belegtext;

    @javax.persistence.Column(name = "Belegtext", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getBelegtext() {
        return belegtext;
    }

    public void setBelegtext(String belegtext) {
        this.belegtext = belegtext;
    }

    private BigDecimal betrag;

    @javax.persistence.Column(name = "Betrag", nullable = false, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    public BigDecimal getBetrag() {
        return betrag;
    }

    public void setBetrag(BigDecimal betrag) {
        this.betrag = betrag;
    }

    private long cancel;

    @javax.persistence.Column(name = "_cancel", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getCancel() {
        return cancel;
    }

    public void setCancel(long cancel) {
        this.cancel = cancel;
    }

    private String creator;

    @javax.persistence.Column(name = "_creator", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    private String editor;

    @javax.persistence.Column(name = "_editor", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    private Timestamp edate;

    @javax.persistence.Column(name = "_edate", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getEdate() {
        return edate;
    }

    public void setEdate(Timestamp edate) {
        this.edate = edate;
    }

    private Timestamp cdate;

    @javax.persistence.Column(name = "_cdate", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getCdate() {
        return cdate;
    }

    public void setCdate(Timestamp cdate) {
        this.cdate = cdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Taschengeld that = (Taschengeld) o;

        if (cancel != that.cancel) return false;
        if (tgid != that.tgid) return false;
        if (belegDatum != null ? !belegDatum.equals(that.belegDatum) : that.belegDatum != null) return false;
        if (belegtext != null ? !belegtext.equals(that.belegtext) : that.belegtext != null) return false;
        if (betrag != null ? !betrag.equals(that.betrag) : that.betrag != null) return false;
        if (bwKennung != null ? !bwKennung.equals(that.bwKennung) : that.bwKennung != null) return false;
        if (cdate != null ? !cdate.equals(that.cdate) : that.cdate != null) return false;
        if (creator != null ? !creator.equals(that.creator) : that.creator != null) return false;
        if (edate != null ? !edate.equals(that.edate) : that.edate != null) return false;
        if (editor != null ? !editor.equals(that.editor) : that.editor != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (tgid ^ (tgid >>> 32));
        result = 31 * result + (bwKennung != null ? bwKennung.hashCode() : 0);
        result = 31 * result + (belegDatum != null ? belegDatum.hashCode() : 0);
        result = 31 * result + (belegtext != null ? belegtext.hashCode() : 0);
        result = 31 * result + (betrag != null ? betrag.hashCode() : 0);
        result = 31 * result + (int) (cancel ^ (cancel >>> 32));
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (editor != null ? editor.hashCode() : 0);
        result = 31 * result + (edate != null ? edate.hashCode() : 0);
        result = 31 * result + (cdate != null ? cdate.hashCode() : 0);
        return result;
    }
}
