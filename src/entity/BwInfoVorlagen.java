package entity;

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
public class BwInfoVorlagen {
    private long bwivid;

    @javax.persistence.Column(name = "BWIVID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getBwivid() {
        return bwivid;
    }

    public void setBwivid(long bwivid) {
        this.bwivid = bwivid;
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

    private String bwinftyp;

    @javax.persistence.Column(name = "BWINFTYP", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getBwinftyp() {
        return bwinftyp;
    }

    public void setBwinftyp(String bwinftyp) {
        this.bwinftyp = bwinftyp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BwInfoVorlagen that = (BwInfoVorlagen) o;

        if (bwivid != that.bwivid) return false;
        if (bezeichnung != null ? !bezeichnung.equals(that.bezeichnung) : that.bezeichnung != null) return false;
        if (bwinftyp != null ? !bwinftyp.equals(that.bwinftyp) : that.bwinftyp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (bwivid ^ (bwivid >>> 32));
        result = 31 * result + (bezeichnung != null ? bezeichnung.hashCode() : 0);
        result = 31 * result + (bwinftyp != null ? bwinftyp.hashCode() : 0);
        return result;
    }
}
