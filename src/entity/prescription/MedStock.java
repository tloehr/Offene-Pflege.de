package entity.prescription;

import entity.system.Users;
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
        @NamedQuery(name = "MedStock.findAll", query = "SELECT m FROM MedStock m"),
        @NamedQuery(name = "MedStock.findByBestID", query = "SELECT m FROM MedStock m WHERE m.bestID = :bestID"),
        @NamedQuery(name = "MedStock.findByEin", query = "SELECT m FROM MedStock m WHERE m.ein = :ein"),
        @NamedQuery(name = "MedStock.findByAnbruch", query = "SELECT m FROM MedStock m WHERE m.anbruch = :anbruch"),
        @NamedQuery(name = "MedStock.findByAus", query = "SELECT m FROM MedStock m WHERE m.aus = :aus"),
        @NamedQuery(name = "MedStock.findByText", query = "SELECT m FROM MedStock m WHERE m.text = :text"),
        @NamedQuery(name = "MedStock.findByApv", query = "SELECT m FROM MedStock m WHERE m.apv = :apv"),
        @NamedQuery(name = "MedStock.findByDarreichungAndBewohnerImAnbruch", query = " " +
                " SELECT b FROM MedStock b WHERE b.inventory.resident = :bewohner AND b.tradeform = :darreichung " +
                " AND b.anbruch < '9999-12-31 23:59:59' AND b.aus = '9999-12-31 23:59:59'"),
        @NamedQuery(name = "MedStock.findByVorratImAnbruch", query = " " +
                " SELECT b FROM MedStock b WHERE b.inventory = :vorrat " +
                " AND b.anbruch < '9999-12-31 23:59:59' AND b.aus = '9999-12-31 23:59:59'"),
        @NamedQuery(name = "MedStock.findByBewohnerImAnbruchMitSalden", query = " " +
                " SELECT best, SUM(buch.menge) FROM MedStock best" +
                " JOIN best.stockTransaction buch" +
                " WHERE best.inventory.resident = :bewohner AND best.aus = '9999-12-31 23:59:59' " +
                " AND best.anbruch < '9999-12-31 23:59:59' " +
                " GROUP BY best ")
})

@NamedNativeQueries({
        // Das hier ist eine Liste aller Verordnungen eines Bewohners.
        // Durch Joins werden die zugehörigen Vorräte und aktuellen Bestände
        // beigefügt.
        @NamedNativeQuery(name = "MedStock.findByVorratMitRestsumme", query = " " +
                " SELECT best.BestID, sum.saldo " +
                " FROM MPBestand best " +
                " LEFT OUTER JOIN " +
                "      ( " +
                "        SELECT best.BestID, sum(buch.Menge) saldo FROM MPBestand best " +
                "        INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
                "        WHERE best.VorID=? " + // Diese Zeile ist eigentlich nicht nötig. Beschleunigt aber ungemein.
                "        GROUP BY best.BestID " +
                "      ) sum ON sum.BestID = best.BestID " +
                " WHERE best.VorID = ? " +
                " AND ( ? = 1 OR best.Aus = '9999-12-31 23:59:59' ) " +
                " ORDER BY best.ein, best.anbruch ")
})

public class MedStock implements Serializable, Comparable<MedStock> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BestID")
    private Long bestID;
    @Version
    @Column(name = "version")
    private Long version;
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

    public MedStock() {
    }

    public MedStock(MedInventory inventory, TradeForm darreichung, MedPackage aPackage, String text) {
        this.apv = BigDecimal.ONE;

        this.inventory = inventory;
        this.tradeform = tradeform;
        this.aPackage = aPackage;
        this.text = text;
        this.ein = new Date();
        this.anbruch = SYSConst.DATE_BIS_AUF_WEITERES;
        this.aus = SYSConst.DATE_BIS_AUF_WEITERES;
        this.user = OPDE.getLogin().getUser();
        this.stockTransaction = new ArrayList<MedStockTransaction>();

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
    private MedStock naechsterBestand;

    // ==
    // 1:N Relationen
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bestand")
    private Collection<MedStockTransaction> stockTransaction;

    // N:1 Relationen
    @JoinColumn(name = "MPID", referencedColumnName = "MPID")
    @ManyToOne
    private MedPackage aPackage;

    @JoinColumn(name = "VorID", referencedColumnName = "VorID")
    @ManyToOne
    private MedInventory inventory;

    @JoinColumn(name = "DafID", referencedColumnName = "DafID")
    @ManyToOne
    private TradeForm tradeform;

    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;


    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Collection<MedStockTransaction> getStockTransaction() {
        return stockTransaction;
    }

    public MedPackage getaPackage() {

        return aPackage;
    }

    public void setaPackage(MedPackage aPackage) {
        this.aPackage = aPackage;
    }

    public MedInventory getInventory() {
        return inventory;
    }

    public boolean hasNextBestand() {
        return naechsterBestand != null;
    }

//    public void setVorrat(MedInventory inventory) {
//        this.inventory = inventory;
//    }

    public TradeForm getTradeForm() {
        return tradeform;
    }

//    public void setTradeForm(Darreichung darreichung) {
//        this.darreichung = darreichung;
//    }

    public MedStock getNaechsterBestand() {
        return naechsterBestand;
    }

    public void setNaechsterBestand(MedStock naechsterBestand) {
        this.naechsterBestand = naechsterBestand;
    }

    public boolean isAngebrochen() {
        return anbruch.before(SYSConst.DATE_BIS_AUF_WEITERES);
    }

    /*
    * <b>tested:</b> Test0002
    */
    public boolean isAbgeschlossen() {
        return aus.before(SYSConst.DATE_BIS_AUF_WEITERES);
    }

    public boolean hasPackung() {
        return aPackage != null;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bestID != null ? bestID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof MedStock)) {
            return false;
        }
        MedStock other = (MedStock) object;
        if ((this.bestID == null && other.bestID != null) || (this.bestID != null && !this.bestID.equals(other.bestID))) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(MedStock o) {
        int result = this.ein.compareTo(o.getEin());
        if (result == 0) {
            result = this.bestID.compareTo(o.getBestID());
        }
        ;
        return result;
    }

    @Override
    public String toString() {
        return "MedStock{bestID=" + bestID + '}';
    }
}
