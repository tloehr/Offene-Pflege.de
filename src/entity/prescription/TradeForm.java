package entity.prescription;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "MPDarreichung")
@NamedQueries({
        @NamedQuery(name = "Darreichung.findAll", query = "SELECT m FROM TradeForm m"),
        @NamedQuery(name = "Darreichung.findByDafID", query = "SELECT m FROM TradeForm m WHERE m.dafID = :dafID"),
        @NamedQuery(name = "Darreichung.findByZusatz", query = "SELECT m FROM TradeForm m WHERE m.zusatz = :zusatz"),
        @NamedQuery(name = "Darreichung.findByMedProdukt", query = "SELECT m FROM TradeForm m WHERE m.medProdukt = :medProdukt ORDER BY m.dosageForm.zubereitung")
})
public class TradeForm implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "DafID")
    private Long dafID;
    @Column(name = "Zusatz")
    private String zusatz;

    public TradeForm() {
    }

    public TradeForm(MedProdukte medProdukt) {
        this.medProdukt = medProdukt;
        this.packungen = new ArrayList<MedPackung>();
        this.bestaende = new ArrayList<MedStock>();
    }

    public TradeForm(MedProdukte medProdukt, String zusatz, DosageForm dosageForm) {
        this.medProdukt = medProdukt;
        this.zusatz = zusatz;
        this.dosageForm = dosageForm;
        this.packungen = new ArrayList<MedPackung>();
        this.bestaende = new ArrayList<MedStock>();
    }

    public Long getDafID() {
        return dafID;
    }

    public void setDafID(Long dafID) {
        this.dafID = dafID;
    }

    public String getZusatz() {
        return zusatz;
    }

    public void setZusatz(String zusatz) {
        this.zusatz = zusatz;
    }

    public Collection<MedPackung> getPackungen() {
        return packungen;
    }

    // ==
    // 1:N Relationen
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "darreichung")
    private Collection<MedPackung> packungen;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "darreichung")
    private Collection<MedStock> bestaende;

    // N:1 Relationen
    @JoinColumn(name = "MedPID", referencedColumnName = "MedPID")
    @ManyToOne
    private MedProdukte medProdukt;

    @JoinColumn(name = "FormID", referencedColumnName = "FormID")
    @ManyToOne
    private DosageForm dosageForm;

    public MedProdukte getMedProdukt() {
        return medProdukt;
    }

    public void setMedProdukt(MedProdukte medProdukt) {
        this.medProdukt = medProdukt;
    }

    public DosageForm getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(DosageForm dosageForm) {
        this.dosageForm = dosageForm;
    }

    public Collection<MedStock> getMedStocks() {
        return bestaende;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dafID != null ? dafID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof TradeForm)) {
            return false;
        }
        TradeForm other = (TradeForm) object;
        if ((this.dafID == null && other.dafID != null) || (this.dafID != null && !this.dafID.equals(other.dafID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.Darreichung[dafID=" + dafID + "]";
    }

}


