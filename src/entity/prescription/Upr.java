package entity.prescription;

import entity.info.Resident;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 05.12.12
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class UPR {

    public UPR() {
    }

    public UPR(BigDecimal upr, MedStock medStock) {
        this.upr = upr;
        this.uprEFF = upr;
        this.medStock = medStock;
        this.resident = medStock.getTradeForm().getDosageForm().isUPRbyResident() ? medStock.getInventory().getResident() : null;
        this.dummy = false;
        this.pit = new Date();
        this.tradeform = medStock.getTradeForm();
    }

    @javax.persistence.Column(name = "UPRID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    private long uprid;
    public long getUprid() {
        return uprid;
    }

    public void setUprid(long uprid) {
        this.uprid = uprid;
    }

    private BigDecimal upr;
    @javax.persistence.Column(name = "UPR", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    public BigDecimal getUpr() {
        return upr;
    }

    public void setUpr(BigDecimal upr) {
        this.upr = upr;
    }

    private boolean dummy;

    @javax.persistence.Column(name = "Dummy", nullable = false, insertable = true, updatable = true, length = 0, precision = 0)
    @Basic
    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    @javax.persistence.Column(name = "UPReff", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    private BigDecimal uprEFF;
    public BigDecimal getUprEFF() {
        return uprEFF;
    }
    public void setUprEFF(BigDecimal uprEFF) {
        this.uprEFF = uprEFF;
    }

    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;


//    private Date pit;
//    @javax.persistence.Column(name = "PIT", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
//    @Temporal(TemporalType.TIMESTAMP)
//    @Basic
//    public Date getPit() {
//        return pit;
//    }
//
//    public void setPit(Date pit) {
//        this.pit = pit;
//    }

    @JoinColumn(name = "stockid", referencedColumnName = "BestID")
    @OneToOne
    private MedStock medStock;

    public MedStock getMedStock() {
        return medStock;
    }

    public void setMedStock(MedStock medStock) {
        this.medStock = medStock;
    }

    @JoinColumn(name = "ResID", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    @JoinColumn(name = "TFID", referencedColumnName = "DafID")
    @ManyToOne
    private TradeForm tradeform;

    public TradeForm getTradeForm() {
        return tradeform;
    }

    public void setTradeForm(TradeForm tradeform) {
        this.tradeform = tradeform;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UPR UPR1 = (UPR) o;

        if (dummy != UPR1.dummy) return false;
        if (uprid != UPR1.uprid) return false;
        if (pit != null ? !pit.equals(UPR1.pit) : UPR1.pit != null) return false;
        if (uprEFF != null ? !uprEFF.equals(UPR1.uprEFF) : UPR1.uprEFF != null) return false;
        if (upr != null ? !upr.equals(UPR1.upr) : UPR1.upr != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (uprid ^ (uprid >>> 32));
        result = 31 * result + (upr != null ? upr.hashCode() : 0);
        result = 31 * result + (dummy ? 1 : 0);
        result = 31 * result + (uprEFF != null ? uprEFF.hashCode() : 0);
        result = 31 * result + (pit != null ? pit.hashCode() : 0);
        return result;
    }
}
