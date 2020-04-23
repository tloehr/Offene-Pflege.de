package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.backend.entity.prescription.TradeForm;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author tloehr
 */
@Entity
@Table(name = "medpackage")

public class MedPackage extends DefaultEntity {
    private String pzn;
    private Short groesse;
    private BigDecimal inhalt;
    private TradeForm tradeForm;

    @Column(name = "PZN")
    public String getPzn() {
        return pzn;
    }

    public void setPzn(String pzn) {
        this.pzn = pzn;
    }

    @Column(name = "Groesse")
    public Short getGroesse() {
        return groesse;
    }

    public void setGroesse(Short groesse) {
        this.groesse = groesse;
    }

    @Basic(optional = false)
    @Column(name = "Inhalt")
    public BigDecimal getInhalt() {
        return inhalt;
    }

    public void setInhalt(BigDecimal inhalt) {
        this.inhalt = inhalt;
    }

    @JoinColumn(name = "DafID", referencedColumnName = "id")
    @ManyToOne
    public TradeForm getTradeForm() {
        return tradeForm;
    }

    public void setTradeForm(TradeForm tradeForm) {
        this.tradeForm = tradeForm;
    }


    public MedPackage() {
    }


}
                                                                              