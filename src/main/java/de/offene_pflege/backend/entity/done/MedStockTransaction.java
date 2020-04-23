package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.backend.entity.system.OPUsers;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "medstocktx")

public class MedStockTransaction extends DefaultEntity implements Comparable<MedStockTransaction> {
    private BigDecimal amount;
    private BigDecimal weight;
    private String text;
    private short state;
    private Date pit;
    private MedStock stock;
    private BHP bhp;
    private OPUsers user;

    @Basic(optional = false)
    @Column(name = "Menge")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Basic(optional = false)
    @Column(name = "weight")
    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    @Column(name = "Text", length = 100)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Basic(optional = false)
    @Column(name = "Status")
    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

    @JoinColumn(name = "BestID", referencedColumnName = "BestID")
    @ManyToOne
    public MedStock getStock() {
        return stock;
    }

    public void setStock(MedStock stock) {
        this.stock = stock;
    }

    @JoinColumn(name = "BHPID", referencedColumnName = "BHPID")
    @ManyToOne

    public BHP getBhp() {
        return bhp;
    }

    public void setBhp(BHP bhp) {
        this.bhp = bhp;
    }

    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    public OPUsers getUser() {
        return user;
    }

    public void setUser(OPUsers user) {
        this.user = user;
    }


    public MedStockTransaction() {
    }


    @Override
    public int compareTo(MedStockTransaction that) {
        return this.getPit().compareTo(that.getPit()) * -1;
    }


}
