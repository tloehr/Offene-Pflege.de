package entity.prescription;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author tloehr
 */
@Entity
@Table(name = "MPackung")
@NamedQueries({
        @NamedQuery(name = "MedPackung.findAll", query = "SELECT m FROM MedPackung m"),
        @NamedQuery(name = "MedPackung.findByMpid", query = "SELECT m FROM MedPackung m WHERE m.mpid = :mpid"),
        @NamedQuery(name = "MedPackung.findByPzn", query = "SELECT m FROM MedPackung m WHERE m.pzn = :pzn"),
        @NamedQuery(name = "MedPackung.findByGroesse", query = "SELECT m FROM MedPackung m WHERE m.groesse = :groesse"),
        @NamedQuery(name = "MedPackung.findByInhalt", query = "SELECT m FROM MedPackung m WHERE m.inhalt = :inhalt")
})
public class MedPackung implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "MPID")
    private Long mpid;
    @Column(name = "PZN")
    private String pzn;
    @Column(name = "Groesse")
    private Short groesse;
    @Basic(optional = false)
    @Column(name = "Inhalt")
    private BigDecimal inhalt;

    @JoinColumn(name = "DafID", referencedColumnName = "DafID")
    @ManyToOne
    private TradeForm darreichung;

    public MedPackung() {
    }

    public MedPackung(TradeForm darreichung) {
        this.darreichung = darreichung;
//        this.darreichung.getPackungen().add(this);
    }

    public TradeForm getDarreichung() {
        return darreichung;
    }

    public void setDarreichung(TradeForm darreichung) {
        this.darreichung = darreichung;
    }

    public Long getMpid() {
        return mpid;
    }

    public void setMpid(Long mpid) {
        this.mpid = mpid;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (mpid != null ? mpid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

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
                                                                              