package entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Massnahmen {
    private long massId;

    @javax.persistence.Column(name = "MassID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getMassId() {
        return massId;
    }

    public void setMassId(long massId) {
        this.massId = massId;
    }

    private String bezeichnung;

    @javax.persistence.Column(name = "Bezeichnung", nullable = false, insertable = true, updatable = true, length = 500, precision = 0)
    @Basic
    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    private BigDecimal dauer;

    @javax.persistence.Column(name = "Dauer", nullable = false, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    public BigDecimal getDauer() {
        return dauer;
    }

    public void setDauer(BigDecimal dauer) {
        this.dauer = dauer;
    }

    private int massArt;

    @javax.persistence.Column(name = "MassArt", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public int getMassArt() {
        return massArt;
    }

    public void setMassArt(int massArt) {
        this.massArt = massArt;
    }

    private long bwikid;

    @javax.persistence.Column(name = "BWIKID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getBwikid() {
        return bwikid;
    }

    public void setBwikid(long bwikid) {
        this.bwikid = bwikid;
    }

    private String xmlt;

    @javax.persistence.Column(name = "XMLT", nullable = true, insertable = true, updatable = true, length = 16777215, precision = 0)
    @Basic
    public String getXmlt() {
        return xmlt;
    }

    public void setXmlt(String xmlt) {
        this.xmlt = xmlt;
    }

    private byte psid;

    @javax.persistence.Column(name = "PSID", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getPsid() {
        return psid;
    }

    public void setPsid(byte psid) {
        this.psid = psid;
    }

    private boolean aktiv;

    @javax.persistence.Column(name = "Aktiv", nullable = true, insertable = true, updatable = true, length = 0, precision = 0)
    @Basic
    public boolean isAktiv() {
        return aktiv;
    }

    public void setAktiv(boolean aktiv) {
        this.aktiv = aktiv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Massnahmen that = (Massnahmen) o;

        if (aktiv != that.aktiv) return false;
        if (bwikid != that.bwikid) return false;
        if (massArt != that.massArt) return false;
        if (massId != that.massId) return false;
        if (psid != that.psid) return false;
        if (bezeichnung != null ? !bezeichnung.equals(that.bezeichnung) : that.bezeichnung != null) return false;
        if (dauer != null ? !dauer.equals(that.dauer) : that.dauer != null) return false;
        if (xmlt != null ? !xmlt.equals(that.xmlt) : that.xmlt != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (massId ^ (massId >>> 32));
        result = 31 * result + (bezeichnung != null ? bezeichnung.hashCode() : 0);
        result = 31 * result + (dauer != null ? dauer.hashCode() : 0);
        result = 31 * result + massArt;
        result = 31 * result + (int) (bwikid ^ (bwikid >>> 32));
        result = 31 * result + (xmlt != null ? xmlt.hashCode() : 0);
        result = 31 * result + (int) psid;
        result = 31 * result + (aktiv ? 1 : 0);
        return result;
    }
}
