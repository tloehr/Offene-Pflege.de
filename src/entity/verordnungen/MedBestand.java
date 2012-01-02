package entity.verordnungen;

import entity.Users;
import op.OPDE;
import op.tools.SYSConst;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "MPBestand")
@NamedQueries({
        @NamedQuery(name = "MedBestand.findAll", query = "SELECT m FROM MedBestand m"),
        @NamedQuery(name = "MedBestand.findByBestID", query = "SELECT m FROM MedBestand m WHERE m.bestID = :bestID"),
        @NamedQuery(name = "MedBestand.findByEin", query = "SELECT m FROM MedBestand m WHERE m.ein = :ein"),
        @NamedQuery(name = "MedBestand.findByAnbruch", query = "SELECT m FROM MedBestand m WHERE m.anbruch = :anbruch"),
        @NamedQuery(name = "MedBestand.findByAus", query = "SELECT m FROM MedBestand m WHERE m.aus = :aus"),
        @NamedQuery(name = "MedBestand.findByText", query = "SELECT m FROM MedBestand m WHERE m.text = :text"),
        @NamedQuery(name = "MedBestand.findByApv", query = "SELECT m FROM MedBestand m WHERE m.apv = :apv"),
        @NamedQuery(name = "MedBestand.findByDarreichungAndBewohnerImAnbruch", query = " " +
                " SELECT b FROM MedBestand b WHERE b.vorrat.bewohner = :bewohner AND b.darreichung = :darreichung " +
                " AND b.anbruch < '9999-12-31 23:59:59' AND b.aus = '9999-12-31 23:59:59'"),
        @NamedQuery(name = "MedBestand.findByVorratImAnbruch", query = " " +
                " SELECT b FROM MedBestand b WHERE b.vorrat = :vorrat " +
                " AND b.anbruch < '9999-12-31 23:59:59' AND b.aus = '9999-12-31 23:59:59'"),
        @NamedQuery(name = "MedBestand.findByBewohnerImAnbruchMitSalden", query = " " +
                " SELECT best, SUM(buch.menge) FROM MedBestand best" +
                " JOIN best.buchungen buch" +
                " WHERE best.vorrat.bewohner = :bewohner AND best.aus = '9999-12-31 23:59:59' " +
                " AND best.anbruch < '9999-12-31 23:59:59' " +
                " GROUP BY best ")
})

public class MedBestand implements Serializable, Comparable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BestID")
    private Long bestID;
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

    public MedBestand(BigDecimal apv, MedVorrat vorrat, Darreichung darreichung, MedPackung packung, String text) {
        this.apv = apv;
        this.vorrat = vorrat;
        this.darreichung = darreichung;
        this.packung = packung;
        this.text = text;
        this.ein = new Date();
        this.anbruch = SYSConst.DATE_BIS_AUF_WEITERES;
        this.aus = SYSConst.DATE_BIS_AUF_WEITERES;
        this.user = OPDE.getLogin().getUser();
        this.buchungen = new ArrayList<MedBuchungen>();
        this.naechsterBestand = null;

    }

    public Long getBestID() {
        return bestID;
    }

    public void setBestID(Long bestID) {
        this.bestID = bestID;
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
    // 1:1 Relationen
    // ==
    @JoinColumn(name = "nextbest", referencedColumnName = "BestID")
    @OneToOne
    private MedBestand naechsterBestand;

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

    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;


    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

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

    public boolean hasNextBestand() {
        return naechsterBestand != null;
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

    public boolean isAngebrochen() {
        return anbruch.before(SYSConst.DATE_BIS_AUF_WEITERES);
    }

    public boolean isAbgeschlossen() {
        return aus.before(SYSConst.DATE_BIS_AUF_WEITERES);
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
    public int compareTo(Object o) {
        if (!(o instanceof MedBestand)) {
            return -1;
        }
        MedBestand other = (MedBestand) o;
        return this.ein.compareTo(other.getEin());
    }

    @Override
    public String toString() {
        return "MedBestand{" +
                "bestID=" + bestID +
                ", ein=" + ein +
                ", anbruch=" + anbruch +
                ", aus=" + aus +
                ", text='" + text + '\'' +
                ", apv=" + apv +
                ", buchungen=" + buchungen +
                ", packung=" + packung +
                ", vorrat=" + vorrat +
                ", darreichung=" + darreichung +
                ", naechsterBestand=" + naechsterBestand +
                ", user=" + user +
                '}';
    }
}
