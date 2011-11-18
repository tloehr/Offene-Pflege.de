package entity.medis;

import javax.persistence.*;
import java.math.BigDecimal;
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
@Table(name = "MPBestand")
@NamedQueries({
        @NamedQuery(name = "MedBestand.findByDarreichungAndBewohnerImAnbruch", query = " " +
                " SELECT b FROM MedBestand b WHERE b.vorrat.bewohner = :bewohner AND b.darreichung = :darreichung " +
                " AND b.anbruch < '9999-12-31 23:59:59' AND b.aus = '9999-12-31 23:59:59'"),
        @NamedQuery(name = "MedBestand.getSumme", query = " " +
                " SELECT SUM(buch.menge) FROM MedBestand best JOIN best.buchungen buch WHERE best = :bestand")
})
public class MedBestand {
    private long bestId;

    @javax.persistence.Column(name = "BestID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getBestId() {
        return bestId;
    }

    public void setBestId(long bestId) {
        this.bestId = bestId;
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

    private Date ein;

    @javax.persistence.Column(name = "Ein", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Date getEin() {
        return ein;
    }

    public void setEin(Date ein) {
        this.ein = ein;
    }

    private Date anbruch;

    @javax.persistence.Column(name = "Anbruch", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Date getAnbruch() {
        return anbruch;
    }

    public void setAnbruch(Date anbruch) {
        this.anbruch = anbruch;
    }

    private Date aus;

    @javax.persistence.Column(name = "Aus", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Date getAus() {
        return aus;
    }

    public void setAus(Date aus) {
        this.aus = aus;
    }

    private String text;

    @javax.persistence.Column(name = "Text", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private BigDecimal apv;

    @javax.persistence.Column(name = "APV", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    public BigDecimal getApv() {
        return apv;
    }

    public void setApv(BigDecimal apv) {
        this.apv = apv;
    }


    // ==
    // 1:N Relationen
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bestand")
    private Collection<MedBuchungen> buchungen;

    // N:1 Relationen
    @JoinColumn(name = "MPID", referencedColumnName = "MPID")
    @ManyToOne
    private MedPackung packung;

    @JoinColumn(name = "VorID", referencedColumnName = "VorID")
    @ManyToOne
    private MedVorrat vorrat;

    @JoinColumn(name = "DafID", referencedColumnName = "DafID")
    @ManyToOne
    private Darreichung darreichung;

    @JoinColumn(name = "nextbest", referencedColumnName = "BestID")
    @ManyToOne
    private MedBestand naechsterBestand;

    public MedPackung getPackung() {
        return packung;
    }

    public void setPackung(MedPackung packung) {
        this.packung = packung;
    }

    public MedVorrat getVorrat() {
        return vorrat;
    }

    public void setVorrat(MedVorrat vorrat) {
        this.vorrat = vorrat;
    }

    public Darreichung getDarreichung() {
        return darreichung;
    }

    public void setDarreichung(Darreichung darreichung) {
        this.darreichung = darreichung;
    }

    public MedBestand getNaechsterBestand() {
        return naechsterBestand;
    }

    public void setNaechsterBestand(MedBestand naechsterBestand) {
        this.naechsterBestand = naechsterBestand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedBestand medBestand = (MedBestand) o;

        if (bestId != medBestand.bestId) return false;
//        if (dafId != medBestand.dafId) return false;
//        if (mpid != medBestand.mpid) return false;
//        if (nextBest != medBestand.nextBest) return false;
//        if (vorId != medBestand.vorId) return false;
        if (anbruch != null ? !anbruch.equals(medBestand.anbruch) : medBestand.anbruch != null) return false;
        if (apv != null ? !apv.equals(medBestand.apv) : medBestand.apv != null) return false;
        if (aus != null ? !aus.equals(medBestand.aus) : medBestand.aus != null) return false;
        if (ein != null ? !ein.equals(medBestand.ein) : medBestand.ein != null) return false;
        if (text != null ? !text.equals(medBestand.text) : medBestand.text != null) return false;
        if (uKennung != null ? !uKennung.equals(medBestand.uKennung) : medBestand.uKennung != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (bestId ^ (bestId >>> 32));
//        result = 31 * result + (int) (dafId ^ (dafId >>> 32));
//        result = 31 * result + (int) (mpid ^ (mpid >>> 32));
//        result = 31 * result + (int) (vorId ^ (vorId >>> 32));
        result = 31 * result + (uKennung != null ? uKennung.hashCode() : 0);
        result = 31 * result + (ein != null ? ein.hashCode() : 0);
        result = 31 * result + (anbruch != null ? anbruch.hashCode() : 0);
//        result = 31 * result + (int) (nextBest ^ (nextBest >>> 32));
        result = 31 * result + (aus != null ? aus.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (apv != null ? apv.hashCode() : 0);
        return result;
    }
}
