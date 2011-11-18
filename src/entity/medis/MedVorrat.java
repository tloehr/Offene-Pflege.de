package entity.medis;

import entity.Bewohner;
import op.tools.SYSConst;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "MPVorrat")
@NamedQueries({
        @NamedQuery(name = "MedVorrat.getSumme", query = " " +
                " SELECT SUM(buch.menge) FROM MedVorrat vor JOIN vor.bestaende best JOIN best.buchungen buch WHERE vor = :vorrat "),
        @NamedQuery(name = "MedVorrat.findByBewohnerAndDarreichung", query = " " +
                " SELECT vor FROM MedVorrat vor " +
                " JOIN vor.bestaende best " +
                " WHERE vor.bewohner = :bewohner AND best.darreichung = :darreichung " +
                " AND vor.bis = " + SYSConst.MYSQL_DATETIME_BIS_AUF_WEITERES),
        @NamedQuery(name = "MedVorrat.findByBewohnerAndDarreichung", query = " " +
                " SELECT vor FROM MedVorrat vor " +
                " JOIN vor.bestaende best " +
                " WHERE vor.bewohner = :bewohner AND best.darreichung = :darreichung " +
                " AND vor.bis = " + SYSConst.MYSQL_DATETIME_BIS_AUF_WEITERES)
})
public class MedVorrat {
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

    private String uKennung;

    @javax.persistence.Column(name = "UKennung", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getuKennung() {
        return uKennung;
    }

    public void setuKennung(String uKennung) {
        this.uKennung = uKennung;
    }

    private Date von;

    @javax.persistence.Column(name = "Von", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Date getVon() {
        return von;
    }

    public void setVon(Date von) {
        this.von = von;
    }

    private Date bis;

    @javax.persistence.Column(name = "Bis", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Date getBis() {
        return bis;
    }

    public void setBis(Date bis) {
        this.bis = bis;
    }

    // ==
    // 1:N Relationen
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vorrat")
    private Collection<MedBestand> bestaende;

    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Bewohner bewohner;

    public Collection<MedBestand> getBestaende() {
        return bestaende;
    }

    public Bewohner getBewohner() {
        return bewohner;
    }

    public void setBewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedVorrat medVorrat = (MedVorrat) o;

        if (vorId != medVorrat.vorId) return false;
        if (bis != null ? !bis.equals(medVorrat.bis) : medVorrat.bis != null) return false;
        if (text != null ? !text.equals(medVorrat.text) : medVorrat.text != null) return false;
        if (uKennung != null ? !uKennung.equals(medVorrat.uKennung) : medVorrat.uKennung != null) return false;
        if (von != null ? !von.equals(medVorrat.von) : medVorrat.von != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (vorId ^ (vorId >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (uKennung != null ? uKennung.hashCode() : 0);
        result = 31 * result + (von != null ? von.hashCode() : 0);
        result = 31 * result + (bis != null ? bis.hashCode() : 0);
        return result;
    }
}
