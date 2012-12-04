package entity.prescription;

import entity.system.Users;
import op.OPDE;
import op.tools.SYSConst;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "MPBestand")
//@NamedQueries({
//        @NamedQuery(name = "MedStock.findAll", query = "SELECT m FROM MedStock m"),
//        @NamedQuery(name = "MedStock.findByBestID", query = "SELECT m FROM MedStock m WHERE m.id = :bestID"),
//        @NamedQuery(name = "MedStock.findByEin", query = "SELECT m FROM MedStock m WHERE m.in = :ein"),
//        @NamedQuery(name = "MedStock.findByAnbruch", query = "SELECT m FROM MedStock m WHERE m.opened = :anbruch"),
//        @NamedQuery(name = "MedStock.findByAus", query = "SELECT m FROM MedStock m WHERE m.out = :aus"),
//        @NamedQuery(name = "MedStock.findByText", query = "SELECT m FROM MedStock m WHERE m.text = :text"),
//        @NamedQuery(name = "MedStock.findByApv", query = "SELECT m FROM MedStock m WHERE m.apv = :apv"),
//        @NamedQuery(name = "MedStock.findByDarreichungAndBewohnerImAnbruch", query = " " +
//                " SELECT b FROM MedStock b WHERE b.inventory.resident = :bewohner AND b.tradeform = :darreichung " +
//                " AND b.opened < '9999-12-31 23:59:59' AND b.out = '9999-12-31 23:59:59'"),
//        @NamedQuery(name = "MedStock.findByVorratImAnbruch", query = " " +
//                " SELECT b FROM MedStock b WHERE b.inventory = :vorrat " +
//                " AND b.opened < '9999-12-31 23:59:59' AND b.out = '9999-12-31 23:59:59'"),
//        @NamedQuery(name = "MedStock.findByBewohnerImAnbruchMitSalden", query = " " +
//                " SELECT best, SUM(buch.amount) FROM MedStock best" +
//                " JOIN best.stockTransaction buch" +
//                " WHERE best.inventory.resident = :bewohner AND best.out = '9999-12-31 23:59:59' " +
//                " AND best.opened < '9999-12-31 23:59:59' " +
//                " GROUP BY best ")
//})
//
//@NamedNativeQueries({
//        // Das hier ist eine Liste aller Verordnungen eines Bewohners.
//        // Durch Joins werden die zugehörigen Vorräte und aktuellen Bestände
//        // beigefügt.
//        @NamedNativeQuery(name = "MedStock.findByVorratMitRestsumme", query = " " +
//                " SELECT best.BestID, sum.saldo " +
//                " FROM MPBestand best " +
//                " LEFT OUTER JOIN " +
//                "      ( " +
//                "        SELECT best.BestID, sum(buch.Menge) saldo FROM MPBestand best " +
//                "        INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
//                "        WHERE best.VorID=? " + // Diese Zeile ist eigentlich nicht nötig. Beschleunigt aber ungemein.
//                "        GROUP BY best.BestID " +
//                "      ) sum ON sum.BestID = best.BestID " +
//                " WHERE best.VorID = ? " +
//                " AND ( ? = 1 OR best.Aus = '9999-12-31 23:59:59' ) " +
//                " ORDER BY best.ein, best.anbruch ")
//})

public class MedStock implements Serializable, Comparable<MedStock> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BestID")
    private Long id;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "Ein")
    @Temporal(TemporalType.TIMESTAMP)
    private Date in;
    @Basic(optional = false)
    @Column(name = "Anbruch")
    @Temporal(TemporalType.TIMESTAMP)
    private Date opened;
    @Basic(optional = false)
    @Column(name = "Aus")
    @Temporal(TemporalType.TIMESTAMP)
    private Date out;
    @Column(name = "Text")
    private String text;
    @Basic(optional = false)
    @Column(name = "APV")
    private BigDecimal upr;
    @Basic(optional = false)
    @Column(name = "state")
    private Integer state;

    public MedStock() {
    }

    public MedStock(MedInventory inventory, TradeForm tradeform, MedPackage aPackage, String text) {
        this.upr = null;

        this.inventory = inventory;
        this.tradeform = tradeform;
        this.aPackage = aPackage;
        this.text = text;
        this.in = new Date();
        this.opened = SYSConst.DATE_UNTIL_FURTHER_NOTICE;
        this.out = SYSConst.DATE_UNTIL_FURTHER_NOTICE;
        this.user = OPDE.getLogin().getUser();
        this.stockTransaction = new ArrayList<MedStockTransaction>();
        this.state = MedStockTools.STATE_NOTHING;
        this.nextStock = null;
    }

    public Long getID() {
        return id;
    }

    public Date getIN() {
        return in;
    }

    public void setIN(Date ein) {
        this.in = ein;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getOpened() {
        return opened;
    }

    public boolean isToBeClosedSoon(){
        return state == MedStockTools.STATE_WILL_BE_CLOSED_SOON;
    }

    public void setOpened(Date anbruch) {
        this.opened = anbruch;
    }

    public Date getOut() {
        return out;
    }

    public void setOut(Date aus) {
        this.out = aus;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * UPR stands for usage-package-ratio. It denotes the ratio between the unit of the packaging and the unit of the usage.
     * For instance: a liquid drug is usually delivered in bottles. lets say this bottle contains 20 ml. But the prescription
     * by the GP is: 20 drops in the evening. Therefore we need to know which ratio to use in order to calculate the amount
     * of liquid used, when somebody applies 20 drops. How much ml is that ?
     * As a rule of thumb some of You may have learned, that a watery solution can make 20 drops out of 1 ml. But this
     * is only a rough approximation. In OPDE the system calculates the average amount of the usage time of a certain
     * drug. In our example a watery solution would have an UPR of 20.
     *
     * Depending on the DosageForm there are three possible ways of handling the UPR.
     * <ol>
     *     <li>UPR1 meaning the ratio is exactly ONE. That is the most common case. One entity taken out of the box is calculated exactly as one entity applied to the resident. This ratio is constant.</li>
     *     <li>UPR_BY_TRADEFORM. DosageForms like drop applied liquids have the same UPR throughout all members of the same tradeform set. The size of a drop does not vary between the several dispensers for the product.</li>
     *     <li>UPR_BY_RESIDENT is mainly used when we are dealing with ointments. The amount of ointment used per treatment varies greatly according to the specific diagnose or body measures.</li>
     * </ol>
     *
     * The former list is respected during the calculation of the UPRs.
     *
     * @return
     */
    public BigDecimal getUPR() {
        if (upr == null){
            return BigDecimal.ONE;
        }
        return upr;
    }

    public boolean isReplaceUPR(){
        return upr == null;
    }

    public void setUPR(BigDecimal upr) {
        this.upr = upr;
    }

    // ==
    // 1:1 Relationen
    // ==
    @JoinColumn(name = "nextbest", referencedColumnName = "BestID")
    @OneToOne
    private MedStock nextStock;

    // ==
    // 1:N Relationen
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "stock")
    private List<MedStockTransaction> stockTransaction;

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

    public List<MedStockTransaction> getStockTransaction() {
        return stockTransaction;
    }

    public MedPackage getPackage() {

        return aPackage;
    }

    public void setPackage(MedPackage aPackage) {
        this.aPackage = aPackage;
    }

    public MedInventory getInventory() {
        return inventory;
    }

    public boolean hasNext2Open() {
        return nextStock != null;
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

    public MedStock getNextStock() {
        return nextStock;
    }

    public void setNextStock(MedStock naechsterBestand) {
        this.nextStock = naechsterBestand;
    }

    public boolean isNew() {
        return opened.equals(SYSConst.DATE_UNTIL_FURTHER_NOTICE) && out.equals(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
    }

    public boolean isOpened() {
        return opened.before(SYSConst.DATE_UNTIL_FURTHER_NOTICE) && out.equals(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
    }

    public boolean isClosed() {
        return out.before(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
    }

    public boolean hasPackage() {
        return aPackage != null;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof MedStock)) {
            return false;
        }
        MedStock other = (MedStock) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(MedStock o) {
        int result = this.in.compareTo(o.getIN());
        if (result == 0) {
            result = this.id.compareTo(o.getID());
        }
        ;
        return result;
    }

    @Override
    public String toString() {
        return "MedStock{bestID=" + id + '}';
    }
}
