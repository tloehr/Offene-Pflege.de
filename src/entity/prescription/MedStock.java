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
    private Boolean uprDummy;

    public MedStock() {
    }

    public MedStock(MedInventory inventory, TradeForm tradeform, MedPackage aPackage, String text, BigDecimal upr) {
        this.upr = upr == null ? BigDecimal.ONE : upr;
        this.uprEffective = this.upr;
        this.uprDummy = upr == null;
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

    public BigDecimal getUPR() {
        return upr;
    }

    public void setUPR(BigDecimal upr) {
        this.uprEffective = upr;

        if (uprDummy) {
            this.upr = upr;
            this.uprDummy = false;
            return;
        }

        // if the deviation was too high (usually more than 20%), then the new UPR is discarded
        BigDecimal maxDeviation = new BigDecimal(Double.parseDouble(OPDE.getProps().getProperty("apv_korridor")));
        BigDecimal deviation = getUPR().divide(upr, 4, BigDecimal.ROUND_HALF_UP).subtract(new BigDecimal(100)).abs();
        OPDE.debug("the deviation was: " + deviation + "%");

        // if the deviation is below the limit, then the new UPR will be accepted.
        // it must also be greater than 0
        if (deviation.compareTo(maxDeviation) <= 0 && upr.compareTo(BigDecimal.ZERO) > 0) {
            this.upr = upr;
            this.uprDummy = false;
        }
    }

    public BigDecimal getUPREffective() {
        return uprEffective;
    }

    public boolean isDummyUPR() {
        return uprDummy;
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
