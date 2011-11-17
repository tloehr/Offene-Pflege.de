package entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
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
public class Mitarbeiter {
    private String maKennung;

    @javax.persistence.Column(name = "MAKennung", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public String getMaKennung() {
        return maKennung;
    }

    public void setMaKennung(String maKennung) {
        this.maKennung = maKennung;
    }

    private String nachname;

    @javax.persistence.Column(name = "Nachname", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
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

    private Date gebDatum;

    @javax.persistence.Column(name = "GebDatum", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Date getGebDatum() {
        return gebDatum;
    }

    public void setGebDatum(Date gebDatum) {
        this.gebDatum = gebDatum;
    }

    private Timestamp version;

    @javax.persistence.Column(name = "_version", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getVersion() {
        return version;
    }

    public void setVersion(Timestamp version) {
        this.version = version;
    }

    private String editor;

    @javax.persistence.Column(name = "_editor", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mitarbeiter that = (Mitarbeiter) o;

        if (editor != null ? !editor.equals(that.editor) : that.editor != null) return false;
        if (gebDatum != null ? !gebDatum.equals(that.gebDatum) : that.gebDatum != null) return false;
        if (maKennung != null ? !maKennung.equals(that.maKennung) : that.maKennung != null) return false;
        if (nachname != null ? !nachname.equals(that.nachname) : that.nachname != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        if (vorname != null ? !vorname.equals(that.vorname) : that.vorname != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = maKennung != null ? maKennung.hashCode() : 0;
        result = 31 * result + (nachname != null ? nachname.hashCode() : 0);
        result = 31 * result + (vorname != null ? vorname.hashCode() : 0);
        result = 31 * result + (gebDatum != null ? gebDatum.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (editor != null ? editor.hashCode() : 0);
        return result;
    }
}
