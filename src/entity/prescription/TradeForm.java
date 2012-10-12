package entity.prescription;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "MPDarreichung")
//@NamedQueries({
//        @NamedQuery(name = "Darreichung.findAll", query = "SELECT m FROM TradeForm m"),
//        @NamedQuery(name = "Darreichung.findByDafID", query = "SELECT m FROM TradeForm m WHERE m.id = :dafID"),
//        @NamedQuery(name = "Darreichung.findByZusatz", query = "SELECT m FROM TradeForm m WHERE m.subtext = :zusatz"),
//        @NamedQuery(name = "Darreichung.findByMedProdukt", query = "SELECT m FROM TradeForm m WHERE m.medProduct = :medProdukt ORDER BY m.dosageForm.preparation")
//})
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
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof TradeForm)) {
            return false;
        }
        TradeForm other = (TradeForm) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.TradeForm[ID=" + id + "]";
    }

}


