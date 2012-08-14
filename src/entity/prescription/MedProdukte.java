package entity.prescription;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "MProdukte")
@NamedQueries({
        @NamedQuery(name = "MedProdukte.findAll", query = "SELECT m FROM MedProdukte m"),
        @NamedQuery(name = "MedProdukte.findByMedPID", query = "SELECT m FROM MedProdukte m WHERE m.medPID = :medPID"),
        @NamedQuery(name = "MedProdukte.findByBezeichnungLike", query = "SELECT m FROM MedProdukte m WHERE m.bezeichnung LIKE :bezeichnung ORDER BY m.bezeichnung"),
        @NamedQuery(name = "MedProdukte.findByBezeichnung", query = "SELECT m FROM MedProdukte m WHERE m.bezeichnung = :bezeichnung")
})
public class MedProdukte implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "MedPID")
    private Long medPID;
    @Basic(optional = false)
    @Column(name = "Bezeichnung")
    private String bezeichnung;

    public MedProdukte() {
    }

    public MedProdukte(MedHersteller hersteller, String bezeichnung) {
        this.hersteller = hersteller;
        this.bezeichnung = bezeichnung;
        this.darreichungen = new ArrayList<TradeForm>();
    }

    public MedProdukte(String bezeichnung) {
        this.hersteller = null;
        this.bezeichnung = bezeichnung;
        this.darreichungen = new ArrayList<TradeForm>();
    }

    public Long getMedPID() {
        return medPID;
    }

    public void setMedPID(Long medPID) {
        this.medPID = medPID;
    }


    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public MedHersteller getHersteller() {
        return hersteller;
    }

    public void setHersteller(MedHersteller hersteller) {
        this.hersteller = hersteller;
    }

    public Collection<TradeForm> getDarreichungen() {
        return darreichungen;
    }

    @JoinColumn(name = "MPHID", referencedColumnName = "MPHID")
    @ManyToOne
    private MedHersteller hersteller;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (medPID != null ? medPID.hashCode() : 0);
        return hash;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "medProdukt")
    private Collection<TradeForm> darreichungen;

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof MedProdukte)) {
            return false;
        }
        MedProdukte other = (MedProdukte) object;
        if ((this.medPID == null && other.medPID != null) || (this.medPID != null && !this.medPID.equals(other.medPID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.MedProdukte[medPID=" + medPID + "]";
    }

}
