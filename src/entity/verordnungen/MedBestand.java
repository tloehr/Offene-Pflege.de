package entity.verordnungen;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "MPBestand")
@NamedQueries({
        @NamedQuery(name = "MedBestand.findAll", query = "SELECT m FROM MedBestand m"),
        @NamedQuery(name = "MedBestand.findByBestID", query = "SELECT m FROM MedBestand m WHERE m.bestID = :bestID"),
        @NamedQuery(name = "MedBestand.findByUKennung", query = "SELECT m FROM MedBestand m WHERE m.uKennung = :uKennung"),
        @NamedQuery(name = "MedBestand.findByEin", query = "SELECT m FROM MedBestand m WHERE m.ein = :ein"),
        @NamedQuery(name = "MedBestand.findByAnbruch", query = "SELECT m FROM MedBestand m WHERE m.anbruch = :anbruch"),
        @NamedQuery(name = "MedBestand.findByAus", query = "SELECT m FROM MedBestand m WHERE m.aus = :aus"),
        @NamedQuery(name = "MedBestand.findByText", query = "SELECT m FROM MedBestand m WHERE m.text = :text"),
        @NamedQuery(name = "MedBestand.findByApv", query = "SELECT m FROM MedBestand m WHERE m.apv = :apv"),
        @NamedQuery(name = "MedBestand.findByDarreichungAndBewohnerImAnbruch", query = " " +
                " SELECT b FROM MedBestand b WHERE b.vorrat.bewohner = :bewohner AND b.darreichung = :darreichung " +
                " AND b.anbruch < '9999-12-31 23:59:59' AND b.aus = '9999-12-31 23:59:59'"),
        @NamedQuery(name = "MedBestand.getSumme", query = " " +
                " SELECT SUM(buch.menge) FROM MedBestand best JOIN best.buchungen buch WHERE best = :bestand")
})
public class MedBestand implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BestID")
    private Long bestID;

    @Basic(optional = false)
    @Column(name = "UKennung")
    private String uKennung;
    @Basic(optional = false)
    @Column(name = "Ein")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ein;
    @Basic(optional = false)
    @Column(name = "Anbruch")
    @Temporal(TemporalType.TIMESTAMP)
    private Date anbruch;
    @Basic(optional = false)
    @Column(name = "Aus")
    @Temporal(TemporalType.TIMESTAMP)
    private Date aus;
    @Column(name = "Text")
    private String text;
    @Basic(optional = false)
    @Column(name = "APV")
    private BigDecimal apv;

    public MedBestand() {
    }

    public MedBestand(Long bestID) {
        this.bestID = bestID;
    }


    public Long getBestID() {
        return bestID;
    }

    public void setBestID(Long bestID) {
        this.bestID = bestID;
    }

    public String getUKennung() {
        return uKennung;
    }

    public void setUKennung(String uKennung) {
        this.uKennung = uKennung;
    }

    public Date getEin() {
        return ein;
    }

    public void setEin(Date ein) {
        this.ein = ein;
    }

    public Date getAnbruch() {
        return anbruch;
    }

    public void setAnbruch(Date anbruch) {
        this.anbruch = anbruch;
    }

    public Date getAus() {
        return aus;
    }

    public void setAus(Date aus) {
        this.aus = aus;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

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

    public Collection<MedBuchungen> getBuchungen() {
        return buchungen;
    }

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
    public int hashCode() {
        int hash = 0;
        hash += (bestID != null ? bestID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MedBestand)) {
            return false;
        }
        MedBestand other = (MedBestand) object;
        if ((this.bestID == null && other.bestID != null) || (this.bestID != null && !this.bestID.equals(other.bestID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.MedBestand[bestID=" + bestID + "]";
    }

}
