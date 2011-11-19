package entity.verordnungen;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "MPStoffe")
@NamedQueries({
    @NamedQuery(name = "MedStoffe.findAll", query = "SELECT m FROM MedStoffe m"),
    @NamedQuery(name = "MedStoffe.findByStoffID", query = "SELECT m FROM MedStoffe m WHERE m.stoffID = :stoffID"),
    @NamedQuery(name = "MedStoffe.findByBezeichnung", query = "SELECT m FROM MedStoffe m WHERE m.bezeichnung = :bezeichnung"),
    @NamedQuery(name = "MedStoffe.findByUKennung", query = "SELECT m FROM MedStoffe m WHERE m.uKennung = :uKennung")})
public class MedStoffe implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "StoffID")
    private Long stoffID;
    @Column(name = "Bezeichnung")
    private String bezeichnung;
    @Basic(optional = false)
    @Column(name = "UKennung")
    private String uKennung;

    public MedStoffe() {
    }

    public MedStoffe(Long stoffID) {
        this.stoffID = stoffID;
    }

    public MedStoffe(Long stoffID, String uKennung) {
        this.stoffID = stoffID;
        this.uKennung = uKennung;
    }

    public Long getStoffID() {
        return stoffID;
    }

    public void setStoffID(Long stoffID) {
        this.stoffID = stoffID;
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
        hash += (stoffID != null ? stoffID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MedStoffe)) {
            return false;
        }
        MedStoffe other = (MedStoffe) object;
        if ((this.stoffID == null && other.stoffID != null) || (this.stoffID != null && !this.stoffID.equals(other.stoffID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.MedStoffe[stoffID=" + stoffID + "]";
    }

}
