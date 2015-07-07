package entity.prescription;

import op.tools.SYSTools;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "tradeform")

public class TradeForm implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DafID")
    private Long id;
    @Column(name = "Zusatz")
    private String subtext;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "UPR")
    private BigDecimal upr;
    @Basic(optional = false)
    @Column(name = "expDaysWhenOpen")
    private Integer daysToExpireAfterOpened;
    @Basic(optional = false)
    @Column(name = "weightcontrol")
    private Boolean weightControlled;

    public TradeForm() {
    }

    public TradeForm(MedProducts medProduct) {
        this.medProduct = medProduct;
        this.packages = new ArrayList<MedPackage>();
        this.stocks = new ArrayList<MedStock>();
        this.upr = null;
        this.weightControlled = false;
    }

    public TradeForm(MedProducts medProduct, String subtext, DosageForm dosageForm) {
        this.medProduct = medProduct;
        this.subtext = SYSTools.tidy(subtext);
        this.dosageForm = dosageForm;
        this.packages = new ArrayList<MedPackage>();
        this.stocks = new ArrayList<MedStock>();
        this.upr = null;
        this.weightControlled = false;
    }

    public Long getID() {
        return id;
    }

    public boolean isWeightControlled() {
        return weightControlled.equals(Boolean.TRUE);
    }

    public Boolean getWeightControlled() {
        return weightControlled;
    }

    public void setWeightControlled(Boolean weightControlled) {
        this.weightControlled = weightControlled;
    }

    public String getSubtext() {
        return subtext;
    }

    public void setSubtext(String zusatz) {
        this.subtext = SYSTools.tidy(zusatz);
    }

    public Collection<MedPackage> getPackages() {
        return packages;
    }

    public Integer getDaysToExpireAfterOpened() {
        return daysToExpireAfterOpened;
    }

    public void setDaysToExpireAfterOpened(Integer daysToExpireAfterOpened) {
        this.daysToExpireAfterOpened = daysToExpireAfterOpened;
    }

    // ==
    // 1:N Relationen
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tradeForm")
    private Collection<MedPackage> packages;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tradeform")
    private Collection<Prescription> prescriptions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tradeform")
    private Collection<MedStock> stocks;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tradeform")
    private Collection<BHP> bhps;

    // N:1 Relationen
    @JoinColumn(name = "MedPID", referencedColumnName = "MedPID")
    @ManyToOne
    private MedProducts medProduct;

    @JoinColumn(name = "FormID", referencedColumnName = "FormID")
    @ManyToOne
    private DosageForm dosageForm;


    public MedProducts getMedProduct() {
        return medProduct;
    }

    public DosageForm getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(DosageForm dosageForm) {
        this.dosageForm = dosageForm;
    }

    public Collection<MedStock> getMedStocks() {
        return stocks;
    }

    public Collection<Prescription> getPrescriptions() {
        return prescriptions;
    }


    /**
     * This is only relevant for DosageFormTypes UPRn. If this UPR is null, then the UPRs from the single MedStocks are
     * used for calculation. If this UPR is NOT null then it is used for calculations instead.
     *
     * @return the upr to be used. NULL if not set.
     */
    public BigDecimal getUPR() {
        return upr;
    }

    public void setUPR(BigDecimal upr) {
        this.upr = upr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeForm tradeForm = (TradeForm) o;

        if (id != null ? !id.equals(tradeForm.id) : tradeForm.id != null) return false;
        if (subtext != null ? !subtext.equals(tradeForm.subtext) : tradeForm.subtext != null) return false;
        if (dosageForm != null ? !dosageForm.equals(tradeForm.dosageForm) : tradeForm.dosageForm != null) return false;
        if (medProduct != null ? !medProduct.equals(tradeForm.medProduct) : tradeForm.medProduct != null) return false;
        if (weightControlled != null ? !weightControlled.equals(tradeForm.weightControlled) : tradeForm.weightControlled != null)
            return false;


//        if (packages != null ? !packages.equals(tradeForm.packages) : tradeForm.packages != null) return false;
//        if (stocks != null ? !stocks.equals(tradeForm.stocks) : tradeForm.stocks != null) return false;


        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (subtext != null ? subtext.hashCode() : 0);
//        result = 31 * result + (packages != null ? packages.hashCode() : 0);
//        result = 31 * result + (stocks != null ? stocks.hashCode() : 0);
        result = 31 * result + (dosageForm != null ? dosageForm.hashCode() : 0);
        result = 31 * result + (medProduct != null ? medProduct.hashCode() : 0);
        result = 31 * result + (weightControlled != null ? weightControlled.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return "entity.rest.TradeForm[ID=" + id + "]";
    }

    public Collection<BHP> getBhps() {
        return bhps;
    }
}


