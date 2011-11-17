package entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Arzt {
    private long arztId;

    @javax.persistence.Column(name = "ArztID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getArztId() {
        return arztId;
    }

    public void setArztId(long arztId) {
        this.arztId = arztId;
    }

    private String anrede;

    @javax.persistence.Column(name = "Anrede", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public String getAnrede() {
        return anrede;
    }

    public void setAnrede(String anrede) {
        this.anrede = anrede;
    }

    private String titel;

    @javax.persistence.Column(name = "Titel", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    private String name;

    @javax.persistence.Column(name = "Name", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String vorname;

    @javax.persistence.Column(name = "Vorname", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    private String strasse;

    @javax.persistence.Column(name = "Strasse", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    private String plz;

    @javax.persistence.Column(name = "PLZ", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    private String ort;

    @javax.persistence.Column(name = "Ort", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    private String tel;

    @javax.persistence.Column(name = "Tel", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    private String fax;

    @javax.persistence.Column(name = "Fax", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    private String mobil;

    @javax.persistence.Column(name = "Mobil", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getMobil() {
        return mobil;
    }

    public void setMobil(String mobil) {
        this.mobil = mobil;
    }

    private String eMail;

    @javax.persistence.Column(name = "EMail", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    private String fach;

    @javax.persistence.Column(name = "Fach", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getFach() {
        return fach;
    }

    public void setFach(String fach) {
        this.fach = fach;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Arzt arzt = (Arzt) o;

        if (arztId != arzt.arztId) return false;
        if (anrede != null ? !anrede.equals(arzt.anrede) : arzt.anrede != null) return false;
        if (eMail != null ? !eMail.equals(arzt.eMail) : arzt.eMail != null) return false;
        if (fach != null ? !fach.equals(arzt.fach) : arzt.fach != null) return false;
        if (fax != null ? !fax.equals(arzt.fax) : arzt.fax != null) return false;
        if (mobil != null ? !mobil.equals(arzt.mobil) : arzt.mobil != null) return false;
        if (name != null ? !name.equals(arzt.name) : arzt.name != null) return false;
        if (ort != null ? !ort.equals(arzt.ort) : arzt.ort != null) return false;
        if (plz != null ? !plz.equals(arzt.plz) : arzt.plz != null) return false;
        if (strasse != null ? !strasse.equals(arzt.strasse) : arzt.strasse != null) return false;
        if (tel != null ? !tel.equals(arzt.tel) : arzt.tel != null) return false;
        if (titel != null ? !titel.equals(arzt.titel) : arzt.titel != null) return false;
        if (vorname != null ? !vorname.equals(arzt.vorname) : arzt.vorname != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (arztId ^ (arztId >>> 32));
        result = 31 * result + (anrede != null ? anrede.hashCode() : 0);
        result = 31 * result + (titel != null ? titel.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (vorname != null ? vorname.hashCode() : 0);
        result = 31 * result + (strasse != null ? strasse.hashCode() : 0);
        result = 31 * result + (plz != null ? plz.hashCode() : 0);
        result = 31 * result + (ort != null ? ort.hashCode() : 0);
        result = 31 * result + (tel != null ? tel.hashCode() : 0);
        result = 31 * result + (fax != null ? fax.hashCode() : 0);
        result = 31 * result + (mobil != null ? mobil.hashCode() : 0);
        result = 31 * result + (eMail != null ? eMail.hashCode() : 0);
        result = 31 * result + (fach != null ? fach.hashCode() : 0);
        return result;
    }
}
