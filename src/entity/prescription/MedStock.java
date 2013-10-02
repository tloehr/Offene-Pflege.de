package entity.prescription;

import entity.system.Users;
import op.OPDE;
import op.tools.SYSConst;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "medstock")

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
    @Column(name = "state")
    private Integer state;
    @Basic(optional = false)
    @Column(name = "UPR")
    private BigDecimal upr;
    @Basic(optional = false)
    @Column(name = "UPReff")
    private BigDecimal uprEffective;
    @Basic(optional = false)
    @Column(name = "DummyUPR")
    private Integer uprDummyMode;
    @Basic(optional = false)
    @Column(name = "expire")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expires;


    public MedStock() {
    }

    public MedStock(MedInventory inventory, TradeForm tradeform, MedPackage aPackage, String text, BigDecimal upr, int uprDummyMode) {
        this.upr = upr == null ? BigDecimal.ONE : upr;
        this.uprEffective = this.upr;
        this.uprDummyMode = uprDummyMode;
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

    public boolean isToBeClosedSoon() {
        return state == MedStockTools.STATE_WILL_BE_CLOSED_SOON;
    }

    public void setOpened(Date anbruch) {
        this.opened = anbruch;
    }

    public Date getExpires() {
        return expires;
    }

    public boolean isExpired() {
//        if (isClosed()) {
//            return false;
//        }
//        boolean expired1 = expires != null && new DateTime(expires).isBeforeNow();
//        boolean expired2 = isOpened() && tradeform.getDaysToExpireAfterOpened() != null && new DateTime(opened).plusDays(tradeform.getDaysToExpireAfterOpened()).isBeforeNow();
//        return expired1 || expired2;
        return expiresIn(0);
    }

    public boolean expiresIn(int days) {
        if (isClosed()) {
            return false;
        }
        boolean expired1 = expires != null && new DateTime(expires).minusDays(days).isBeforeNow();
        boolean expired2 = isOpened() && tradeform.getDaysToExpireAfterOpened() != null && new DateTime(opened).plusDays(tradeform.getDaysToExpireAfterOpened()).minusDays(days).isBeforeNow();
        return expired1 || expired2;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
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
     * this upr is used for calculation if the dosageform is type UPRn and the tradeform has no UPR on it own.
     *
     * @return
     */
    public BigDecimal getUPR() {
        return upr;
    }

    public Integer getUPRDummyMode() {
        return uprDummyMode;
    }

    public void setUPR(BigDecimal upr) {
        this.upr = upr;
    }

    /**
     * the UPREffective value is in fact never used. it is simply stored for debugging reasons.
     * when the user sets a new UPR, then THIS UPR is always stored as UPReffective. The setUPR method
     * then decides if the UPR should be preserved or discarded.
     * if it is discarded the UPR which was used when the new stock was registeres is kept.
     * if not, it replaces the old value.
     * if the UPR is a dummy (because its the first stock for that tradeform) its always used.
     *
     * @return
     */
    public BigDecimal getUPREffective() {
        return uprEffective;
    }

//    public boolean isDummyUPR() {
//        return uprDummy;
//    }

    // ==
    // 1:1 Relationen
    // ==
    @JoinColumn(name = "nextbest", referencedColumnName = "BestID")
    @OneToOne
    private MedStock nextStock;

    // ==
    // 1:N Relations
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
//
//    public List<MedStockTransaction> getStockTransaction() {
//        return stockTransaction;
//    }

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

    public void setUPREffective(BigDecimal uprEffective) {
        this.uprEffective = uprEffective;
    }

    public void setUPRDummyMode(Integer uprDummyMode) {
        this.uprDummyMode = uprDummyMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedStock medStock = (MedStock) o;

        if (aPackage != null ? !aPackage.equals(medStock.aPackage) : medStock.aPackage != null) return false;
        if (id != null ? !id.equals(medStock.id) : medStock.id != null) return false;
        if (in != null ? !in.equals(medStock.in) : medStock.in != null) return false;
        if (inventory != null ? !inventory.equals(medStock.inventory) : medStock.inventory != null) return false;
        if (nextStock != null ? !nextStock.equals(medStock.nextStock) : medStock.nextStock != null) return false;
        if (opened != null ? !opened.equals(medStock.opened) : medStock.opened != null) return false;
        if (out != null ? !out.equals(medStock.out) : medStock.out != null) return false;
        if (state != null ? !state.equals(medStock.state) : medStock.state != null) return false;
//        if (stockTransaction != null ? !stockTransaction.equals(medStock.stockTransaction) : medStock.stockTransaction != null)
//            return false;
        if (text != null ? !text.equals(medStock.text) : medStock.text != null) return false;
        if (tradeform != null ? !tradeform.equals(medStock.tradeform) : medStock.tradeform != null) return false;
        if (upr != null ? !upr.equals(medStock.upr) : medStock.upr != null) return false;
//        if (uprDummy != null ? !uprDummy.equals(medStock.uprDummy) : medStock.uprDummy != null) return false;
        if (uprEffective != null ? !uprEffective.equals(medStock.uprEffective) : medStock.uprEffective != null)
            return false;
        if (user != null ? !user.equals(medStock.user) : medStock.user != null) return false;
        if (version != null ? !version.equals(medStock.version) : medStock.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (in != null ? in.hashCode() : 0);
        result = 31 * result + (opened != null ? opened.hashCode() : 0);
        result = 31 * result + (out != null ? out.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (upr != null ? upr.hashCode() : 0);
        result = 31 * result + (uprEffective != null ? uprEffective.hashCode() : 0);
//        result = 31 * result + (uprDummy != null ? uprDummy.hashCode() : 0);
        result = 31 * result + (nextStock != null ? nextStock.hashCode() : 0);
//        result = 31 * result + (stockTransaction != null ? stockTransaction.hashCode() : 0);
        result = 31 * result + (aPackage != null ? aPackage.hashCode() : 0);
        result = 31 * result + (inventory != null ? inventory.hashCode() : 0);
        result = 31 * result + (tradeform != null ? tradeform.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
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
