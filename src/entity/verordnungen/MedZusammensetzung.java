package entity.verordnungen;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "MPZusammensetzung")
@NamedQueries({
    @NamedQuery(name = "MedZusammensetzung.findAll", query = "SELECT m FROM MedZusammensetzung m"),
    @NamedQuery(name = "MedZusammensetzung.findByZusID", query = "SELECT m FROM MedZusammensetzung m WHERE m.zusID = :zusID"),
    @NamedQuery(name = "MedZusammensetzung.findByStoffID", query = "SELECT m FROM MedZusammensetzung m WHERE m.stoffID = :stoffID"),
    @NamedQuery(name = "MedZusammensetzung.findByDafID", query = "SELECT m FROM MedZusammensetzung m WHERE m.dafID = :dafID"),
    @NamedQuery(name = "MedZusammensetzung.findByStaerke", query = "SELECT m FROM MedZusammensetzung m WHERE m.staerke = :staerke"),
    @NamedQuery(name = "MedZusammensetzung.findByDimension", query = "SELECT m FROM MedZusammensetzung m WHERE m.dimension = :dimension"),
    @NamedQuery(name = "MedZusammensetzung.findByStofftyp", query = "SELECT m FROM MedZusammensetzung m WHERE m.stofftyp = :stofftyp"),
    @NamedQuery(name = "MedZusammensetzung.findByUKennung", query = "SELECT m FROM MedZusammensetzung m WHERE m.uKennung = :uKennung")})
public class MedZusammensetzung implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ZusID")
    private Long zusID;
    @Basic(optional = false)
    @Column(name = "StoffID")
    private long stoffID;
    @Basic(optional = false)
    @Column(name = "DafID")
    private long dafID;
    @Basic(optional = false)
    @Column(name = "Staerke")
    private BigDecimal staerke;
    @Column(name = "Dimension")
    private Short dimension;
    @Column(name = "Stofftyp")
    private Short stofftyp;
    @Basic(optional = false)
    @Column(name = "UKennung")
    private String uKennung;

    public MedZusammensetzung() {
    }

    public MedZusammensetzung(Long zusID) {
        this.zusID = zusID;
    }

    public MedZusammensetzung(Long zusID, long stoffID, long dafID, BigDecimal staerke, String uKennung) {
        this.zusID = zusID;
        this.stoffID = stoffID;
        this.dafID = dafID;
        this.staerke = staerke;
        this.uKennung = uKennung;
    }

    public Long getZusID() {
        return zusID;
    }

    public void setZusID(Long zusID) {
        this.zusID = zusID;
    }

    public long getStoffID() {
        return stoffID;
    }

    public void setStoffID(long stoffID) {
        this.stoffID = stoffID;
    }

    public long getDafID() {
        return dafID;
    }

    public void setDafID(long dafID) {
        this.dafID = dafID;
    }

    public BigDecimal getStaerke() {
        return staerke;
    }

    public void setStaerke(BigDecimal staerke) {
        this.staerke = staerke;
    }

    public Short getDimension() {
        return dimension;
    }

    public void setDimension(Short dimension) {
        this.dimension = dimension;
    }

    public Short getStofftyp() {
        return stofftyp;
    }

    public void setStofftyp(Short stofftyp) {
        this.stofftyp = stofftyp;
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
        hash += (zusID != null ? zusID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MedZusammensetzung)) {
            return false;
        }
        MedZusammensetzung other = (MedZusammensetzung) object;
        if ((this.zusID == null && other.zusID != null) || (this.zusID != null && !this.zusID.equals(other.zusID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.MedZusammensetzung[zusID=" + zusID + "]";
    }

}

