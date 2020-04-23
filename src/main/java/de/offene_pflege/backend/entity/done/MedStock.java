package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.backend.entity.prescription.TradeForm;
import de.offene_pflege.backend.entity.system.OPUsers;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "medstock")
public class MedStock extends DefaultEntity implements Comparable<MedStock> {
    private Date in;
    private Date opened;
    private Date out;
    private String text;
    private Integer state;
    private BigDecimal upr;
    private BigDecimal uprEffective;
    private Integer uprDummyMode;
    private Date expires;
    private MedStock nextStock;
    private List<MedStockTransaction> stockTransaction;
    private MedPackage aPackage;
    private MedInventory inventory;
    private TradeForm tradeform;
    private OPUsers user;

    @Basic(optional = false)
    @Column(name = "Ein")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getIn() {
        return in;
    }

    public void setIn(Date in) {
        this.in = in;
    }

    @Basic(optional = false)
    @Column(name = "Anbruch")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getOpened() {
        return opened;
    }

    public void setOpened(Date opened) {
        this.opened = opened;
    }

    @Basic(optional = false)
    @Column(name = "Aus")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getOut() {
        return out;
    }

    public void setOut(Date out) {
        this.out = out;
    }

    @Column(name = "Text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Basic(optional = false)
    @Column(name = "state")
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Basic(optional = false)
    @Column(name = "UPR")
    public BigDecimal getUpr() {
        return upr;
    }

    public void setUpr(BigDecimal upr) {
        this.upr = upr;
    }

    /**
     * the UPREffective value is in fact never used. it is simply stored for debugging reasons. when the user sets a new
     * UPR, then THIS UPR is always stored as UPReffective. The setUPR method then decides if the UPR should be
     * preserved or discarded. if it is discarded the UPR which was used when the new stock was registered is kept. if
     * not, it replaces the old value. if the UPR is a dummy (because its the first stock for that tradeform) its always
     * used.
     *
     * @return
     */
    @Basic(optional = false)
    @Column(name = "UPReff")
    public BigDecimal getUprEffective() {
        return uprEffective;
    }

    public void setUprEffective(BigDecimal uprEffective) {
        this.uprEffective = uprEffective;
    }

    @Basic(optional = false)
    @Column(name = "DummyUPR")
    public Integer getUprDummyMode() {
        return uprDummyMode;
    }

    public void setUprDummyMode(Integer uprDummyMode) {
        this.uprDummyMode = uprDummyMode;
    }

    @Basic(optional = false)
    @Column(name = "expire")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    @JoinColumn(name = "nextbest", referencedColumnName = "id")
    @OneToOne
    public MedStock getNextStock() {
        return nextStock;
    }

    public void setNextStock(MedStock nextStock) {
        this.nextStock = nextStock;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "stock")
    public List<MedStockTransaction> getStockTransaction() {
        return stockTransaction;
    }

    public void setStockTransaction(List<MedStockTransaction> stockTransaction) {
        this.stockTransaction = stockTransaction;
    }

    @JoinColumn(name = "MPID", referencedColumnName = "id")
    @ManyToOne
    public MedPackage getaPackage() {
        return aPackage;
    }

    public void setaPackage(MedPackage aPackage) {
        this.aPackage = aPackage;
    }

    @JoinColumn(name = "VorID", referencedColumnName = "id")
    @ManyToOne
    public MedInventory getInventory() {
        return inventory;
    }

    public void setInventory(MedInventory inventory) {
        this.inventory = inventory;
    }

    @JoinColumn(name = "DafID", referencedColumnName = "id")
    @ManyToOne
    public TradeForm getTradeform() {
        return tradeform;
    }

    public void setTradeform(TradeForm tradeform) {
        this.tradeform = tradeform;
    }


    @JoinColumn(name = "UKennung", referencedColumnName = "id")
    @ManyToOne
    public OPUsers getUser() {
        return user;
    }

    public void setUser(OPUsers user) {
        this.user = user;
    }


    public MedStock() {
    }

    @Override
    public int compareTo(MedStock o) {
        int result = getIn().compareTo(o.getIn());
        if (result == 0) {
            result = getId().compareTo(o.getId());
        }
        ;
        return result;
    }


}
