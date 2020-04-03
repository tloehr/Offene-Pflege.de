package de.offene_pflege.backend.entity.prescription;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author tloehr
 */
@Entity
@Table(name = "medpackage")

public class MedPackage implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MPID")
    private Long id;
    @Column(name = "PZN")
    private String pzn;
    @Column(name = "Groesse")
    private Short groesse;
    @Basic(optional = false)
    @Column(name = "Inhalt")
    private BigDecimal inhalt;
    @Version
    @Column(name = "version")
    private Long version;

    @JoinColumn(name = "DafID", referencedColumnName = "DafID")
    @ManyToOne
    private TradeForm tradeForm;

    public MedPackage() {
    }

    public MedPackage(TradeForm tf) {
        this.tradeForm = tf;
    }

    public MedPackage(TradeForm tradeForm, BigDecimal inhalt, Short groesse, String pzn) {
        this.tradeForm = tradeForm;
        this.inhalt = inhalt;
        this.groesse = groesse;
        this.pzn = pzn;
    }

    public TradeForm getTradeForm() {
        return tradeForm;
    }

    public Long getID() {
        return id;
    }

    public String getPzn() {
        return pzn;
    }

    public void setPzn(String pzn) {
        this.pzn = pzn;
    }

    public Short getSize() {
        return groesse;
    }

    public void setSize(Short groesse) {
        this.groesse = groesse;
    }

    public BigDecimal getContent() {
        return inhalt;
    }

    public void setContent(BigDecimal inhalt) {
        this.inhalt = inhalt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedPackage that = (MedPackage) o;

        if (groesse != null ? !groesse.equals(that.groesse) : that.groesse != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (inhalt != null ? !inhalt.equals(that.inhalt) : that.inhalt != null) return false;
        if (pzn != null ? !pzn.equals(that.pzn) : that.pzn != null) return false;
        if (tradeForm != null ? !tradeForm.equals(that.tradeForm) : that.tradeForm != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (pzn != null ? pzn.hashCode() : 0);
        result = 31 * result + (groesse != null ? groesse.hashCode() : 0);
        result = 31 * result + (inhalt != null ? inhalt.hashCode() : 0);
        result = 31 * result + (tradeForm != null ? tradeForm.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "entity.rest.MedPackage[mpid=" + id + "]";
    }

}
                                                                              