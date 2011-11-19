package entity.verordnungen;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "MPackung")
@NamedQueries({
    @NamedQuery(name = "MedPackung.findAll", query = "SELECT m FROM MedPackung m"),
    @NamedQuery(name = "MedPackung.findByMpid", query = "SELECT m FROM MedPackung m WHERE m.mpid = :mpid"),
    @NamedQuery(name = "MedPackung.findByDafID", query = "SELECT m FROM MedPackung m WHERE m.dafID = :dafID"),
    @NamedQuery(name = "MedPackung.findByPzn", query = "SELECT m FROM MedPackung m WHERE m.pzn = :pzn"),
    @NamedQuery(name = "MedPackung.findByGroesse", query = "SELECT m FROM MedPackung m WHERE m.groesse = :groesse"),
    @NamedQuery(name = "MedPackung.findByInhalt", query = "SELECT m FROM MedPackung m WHERE m.inhalt = :inhalt"),
    @NamedQuery(name = "MedPackung.findByUKennung", query = "SELECT m FROM MedPackung m WHERE m.uKennung = :uKennung")})
public class MedPackung implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "MPID")
    private Long mpid;
    @Basic(optional = false)
    @Column(name = "DafID")
    private long dafID;
    @Column(name = "PZN")
    private String pzn;
    @Column(name = "Groesse")
    private Short groesse;
    @Basic(optional = false)
    @Column(name = "Inhalt")
    private BigDecimal inhalt;
    @Basic(optional = false)
    @Column(name = "UKennung")
    private String uKennung;

    public MedPackung() {
    }

    public MedPackung(Long mpid) {
        this.mpid = mpid;
    }

    public MedPackung(Long mpid, long dafID, BigDecimal inhalt, String uKennung) {
        this.mpid = mpid;
        this.dafID = dafID;
        this.inhalt = inhalt;
        this.uKennung = uKennung;
    }

    public Long getMpid() {
        return mpid;
    }

    public void setMpid(Long mpid) {
        this.mpid = mpid;
    }

    public long getDafID() {
        return dafID;
    }

    public void setDafID(long dafID) {
        this.dafID = dafID;
    }

    public String getPzn() {
        return pzn;
    }

    public void setPzn(String pzn) {
        this.pzn = pzn;
    }

    public Short getGroesse() {
        return groesse;
    }

    public void setGroesse(Short groesse) {
        this.groesse = groesse;
    }

    public BigDecimal getInhalt() {
        return inhalt;
    }

    public void setInhalt(BigDecimal inhalt) {
        this.inhalt = inhalt;
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
        hash += (mpid != null ? mpid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MedPackung)) {
            return false;
        }
        MedPackung other = (MedPackung) object;
        if ((this.mpid == null && other.mpid != null) || (this.mpid != null && !this.mpid.equals(other.mpid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.MedPackung[mpid=" + mpid + "]";
    }

}
                                                                              