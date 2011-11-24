package entity.verordnungen;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "MPDarreichung")
@NamedQueries({
        @NamedQuery(name = "Darreichung.findAll", query = "SELECT m FROM Darreichung m"),
        @NamedQuery(name = "Darreichung.findByDafID", query = "SELECT m FROM Darreichung m WHERE m.dafID = :dafID"),
        @NamedQuery(name = "Darreichung.findByZusatz", query = "SELECT m FROM Darreichung m WHERE m.zusatz = :zusatz"),
        @NamedQuery(name = "Darreichung.findByUKennung", query = "SELECT m FROM Darreichung m WHERE m.uKennung = :uKennung")})
public class Darreichung implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "DafID")
    private Long dafID;
    @Column(name = "Zusatz")
    private String zusatz;
    @Basic(optional = false)
    @Column(name = "UKennung")
    private String uKennung;

    public Darreichung() {
    }

    public Darreichung(Long dafID) {
        this.dafID = dafID;
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

    public String getUKennung() {
        return uKennung;
    }

    public void setUKennung(String uKennung) {
        this.uKennung = uKennung;
    }

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


