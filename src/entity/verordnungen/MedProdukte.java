package entity.verordnungen;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "MProdukte")
@NamedQueries({
    @NamedQuery(name = "MedProdukte.findAll", query = "SELECT m FROM MedProdukte m"),
    @NamedQuery(name = "MedProdukte.findByMedPID", query = "SELECT m FROM MedProdukte m WHERE m.medPID = :medPID"),
    @NamedQuery(name = "MedProdukte.findByMphid", query = "SELECT m FROM MedProdukte m WHERE m.mphid = :mphid"),
    @NamedQuery(name = "MedProdukte.findByBezeichnung", query = "SELECT m FROM MedProdukte m WHERE m.bezeichnung = :bezeichnung"),
    @NamedQuery(name = "MedProdukte.findByUKennung", query = "SELECT m FROM MedProdukte m WHERE m.uKennung = :uKennung")})
public class MedProdukte implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "MedPID")
    private Long medPID;
    @Basic(optional = false)
    @Column(name = "MPHID")
    private long mphid;
    @Basic(optional = false)
    @Column(name = "Bezeichnung")
    private String bezeichnung;
    @Basic(optional = false)
    @Column(name = "UKennung")
    private String uKennung;

    public MedProdukte() {
    }

    public MedProdukte(Long medPID) {
        this.medPID = medPID;
    }

    public MedProdukte(Long medPID, long mphid, String bezeichnung, String uKennung) {
        this.medPID = medPID;
        this.mphid = mphid;
        this.bezeichnung = bezeichnung;
        this.uKennung = uKennung;
    }

    public Long getMedPID() {
        return medPID;
    }

    public void setMedPID(Long medPID) {
        this.medPID = medPID;
    }

    public long getMphid() {
        return mphid;
    }

    public void setMphid(long mphid) {
        this.mphid = mphid;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getUKennung() {
        return uKennung;
    }

    public void setUKennung(String uKennung) {
        this.uKennung = uKennung;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (medPID != null ? medPID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
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
