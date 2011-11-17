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
public class MpHersteller {
    private long mphid;

    @javax.persistence.Column(name = "MPHID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getMphid() {
        return mphid;
    }

    public void setMphid(long mphid) {
        this.mphid = mphid;
    }

    private String firma;

    @javax.persistence.Column(name = "Firma", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    private String strasse;

    @javax.persistence.Column(name = "Strasse", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    private String plz;

    @javax.persistence.Column(name = "PLZ", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    private String ort;

    @javax.persistence.Column(name = "Ort", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    private String tel;

    @javax.persistence.Column(name = "Tel", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    private String fax;

    @javax.persistence.Column(name = "Fax", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    private String www;

    @javax.persistence.Column(name = "WWW", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getWww() {
        return www;
    }

    public void setWww(String www) {
        this.www = www;
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

        MpHersteller that = (MpHersteller) o;

        if (mphid != that.mphid) return false;
        if (fax != null ? !fax.equals(that.fax) : that.fax != null) return false;
        if (firma != null ? !firma.equals(that.firma) : that.firma != null) return false;
        if (ort != null ? !ort.equals(that.ort) : that.ort != null) return false;
        if (plz != null ? !plz.equals(that.plz) : that.plz != null) return false;
        if (strasse != null ? !strasse.equals(that.strasse) : that.strasse != null) return false;
        if (tel != null ? !tel.equals(that.tel) : that.tel != null) return false;
        if (uKennung != null ? !uKennung.equals(that.uKennung) : that.uKennung != null) return false;
        if (www != null ? !www.equals(that.www) : that.www != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (mphid ^ (mphid >>> 32));
        result = 31 * result + (firma != null ? firma.hashCode() : 0);
        result = 31 * result + (strasse != null ? strasse.hashCode() : 0);
        result = 31 * result + (plz != null ? plz.hashCode() : 0);
        result = 31 * result + (ort != null ? ort.hashCode() : 0);
        result = 31 * result + (tel != null ? tel.hashCode() : 0);
        result = 31 * result + (fax != null ? fax.hashCode() : 0);
        result = 31 * result + (www != null ? www.hashCode() : 0);
        result = 31 * result + (uKennung != null ? uKennung.hashCode() : 0);
        return result;
    }
}
