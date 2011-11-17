package entity.medis;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Situationen {
    private long sitId;

    @javax.persistence.Column(name = "SitID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getSitId() {
        return sitId;
    }

    public void setSitId(long sitId) {
        this.sitId = sitId;
    }

    private byte kategorie;

    @javax.persistence.Column(name = "Kategorie", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getKategorie() {
        return kategorie;
    }

    public void setKategorie(byte kategorie) {
        this.kategorie = kategorie;
    }

    private byte uKategorie;

    @javax.persistence.Column(name = "UKategorie", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getuKategorie() {
        return uKategorie;
    }

    public void setuKategorie(byte uKategorie) {
        this.uKategorie = uKategorie;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Situationen that = (Situationen) o;

        if (kategorie != that.kategorie) return false;
        if (sitId != that.sitId) return false;
        if (uKategorie != that.uKategorie) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (sitId ^ (sitId >>> 32));
        result = 31 * result + (int) kategorie;
        result = 31 * result + (int) uKategorie;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
}
