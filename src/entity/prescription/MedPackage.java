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
        @NamedQuery(name = "MedPackung.findAll", query = "SELECT m FROM MedPackage m"),
        @NamedQuery(name = "MedPackung.findByMpid", query = "SELECT m FROM MedPackage m WHERE m.id = :mpid"),
        @NamedQuery(name = "MedPackung.findByPzn", query = "SELECT m FROM MedPackage m WHERE m.pzn = :pzn"),
        @NamedQuery(name = "MedPackung.findByGroesse", query = "SELECT m FROM MedPackage m WHERE m.groesse = :groesse"),
        @NamedQuery(name = "MedPackung.findByInhalt", query = "SELECT m FROM MedPackage m WHERE m.inhalt = :inhalt")
})
public class MedPackage implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MPID")
    private Long id;
    @Column(name = "PZN")
    private String pzn;
    @Column(name = "Groesse")
    private Short groesse;
    @Basic(optional = false)
    @Column(name = "Inhalt")
    private BigDecimal inhalt;

    @JoinColumn(name = "DafID", referencedColumnName = "DafID")
    @ManyToOne
    private TradeForm tradeForm;

    public MedPackage() {
    }

    public MedPackage(TradeForm tf) {
        this.tradeForm = tf;
//        this.darreichung.getPackages().add(this);
    }

    public TradeForm getTradeForm() {
        return tradeForm;
    }

    public Long getID() {
        return id;
    }

    public String getPzn() {
        return pzn;
    }

    public void setPzn(String pzn) {
        this.pzn = pzn;
    }

    public Short getSize() {
        return groesse;
    }

    public void setSize(Short groesse) {
        this.groesse = groesse;
    }

    public BigDecimal getContent() {
        return inhalt;
    }

    public void setContent(BigDecimal inhalt) {
        this.inhalt = inhalt;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof MedPackage)) {
            return false;
        }
        MedPackage other = (MedPackage) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.MedPackage[mpid=" + id + "]";
    }

}
                                                                              