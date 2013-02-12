package entity.prescription;

import javax.persistence.*;
import java.io.Serializable;
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

    public TradeForm() {
    }

    public TradeForm(MedProducts medProduct) {
        this.medProduct = medProduct;
        this.packages = new ArrayList<MedPackage>();
        this.stocks = new ArrayList<MedStock>();
    }

    public TradeForm(MedProducts medProduct, String subtext, DosageForm dosageForm) {
        this.medProduct = medProduct;
        this.subtext = subtext;
        this.dosageForm = dosageForm;
        this.packages = new ArrayList<MedPackage>();
        this.stocks = new ArrayList<MedStock>();
    }

    public Long getID() {
        return id;
    }

    public String getSubtext() {
        return subtext;
    }

    public void setSubtext(String zusatz) {
        this.subtext = zusatz;
    }

    public Collection<MedPackage> getPackages() {
        return packages;
    }

    // ==
    // 1:N Relationen
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tradeForm")
    private Collection<MedPackage> packages;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tradeform")
    private Collection<MedStock> stocks;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeForm tradeForm = (TradeForm) o;

        if (dosageForm != null ? !dosageForm.equals(tradeForm.dosageForm) : tradeForm.dosageForm != null) return false;
        if (id != null ? !id.equals(tradeForm.id) : tradeForm.id != null) return false;
        if (medProduct != null ? !medProduct.equals(tradeForm.medProduct) : tradeForm.medProduct != null) return false;
//        if (packages != null ? !packages.equals(tradeForm.packages) : tradeForm.packages != null) return false;
//        if (stocks != null ? !stocks.equals(tradeForm.stocks) : tradeForm.stocks != null) return false;
        if (subtext != null ? !subtext.equals(tradeForm.subtext) : tradeForm.subtext != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (subtext != null ? subtext.hashCode() : 0);
//        result = 31 * result + (packages != null ? packages.hashCode() : 0);
//        result = 31 * result + (stocks != null ? stocks.hashCode() : 0);
        result = 31 * result + (medProduct != null ? medProduct.hashCode() : 0);
        result = 31 * result + (dosageForm != null ? dosageForm.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "entity.rest.TradeForm[ID=" + id + "]";
    }

}


