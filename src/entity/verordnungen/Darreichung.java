package entity.verordnungen;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "MPDarreichung")
@NamedQueries({
        @NamedQuery(name = "Darreichung.findAll", query = "SELECT m FROM Darreichung m"),
        @NamedQuery(name = "Darreichung.findByDafID", query = "SELECT m FROM Darreichung m WHERE m.dafID = :dafID"),
        @NamedQuery(name = "Darreichung.findByZusatz", query = "SELECT m FROM Darreichung m WHERE m.zusatz = :zusatz"),
        @NamedQuery(name = "Darreichung.findByMedProdukt", query = "SELECT m FROM Darreichung m WHERE m.medProdukt = :medProdukt ORDER BY m.medForm.zubereitung")
})
public class Darreichung implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "DafID")
    private Long dafID;
    @Column(name = "Zusatz")
    private String zusatz;

    public Darreichung() {
    }

    public Darreichung(MedProdukte medProdukt) {
        this.medProdukt = medProdukt;
        this.packungen = new ArrayList<MedPackung>();
        this.bestaende = new ArrayList<MedBestand>();
    }

    public Darreichung(MedProdukte medProdukt, String zusatz, MedFormen medForm) {
        this.medProdukt = medProdukt;
        this.zusatz = zusatz;
        this.medForm = medForm;
        this.packungen = new ArrayList<MedPackung>();
        this.bestaende = new ArrayList<MedBestand>();
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
    private Collection<MedBestand> bestaende;

    // N:1 Relationen
    @JoinColumn(name = "MedPID", referencedColumnName = "MedPID")
    @ManyToOne
    private MedProdukte medProdukt;

    @JoinColumn(name = "FormID", referencedColumnName = "FormID")
    @ManyToOne
    private MedFormen medForm;

    public MedProdukte getMedProdukt() {
        return medProdukt;
    }

    public void setMedProdukt(MedProdukte medProdukt) {
        this.medProdukt = medProdukt;
    }

    public MedFormen getMedForm() {
        return medForm;
    }

    public void setMedForm(MedFormen medForm) {
        this.medForm = medForm;
    }

    public Collection<MedBestand> getBestaende() {
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Darreichung)) {
            return false;
        }
        Darreichung other = (Darreichung) object;
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


