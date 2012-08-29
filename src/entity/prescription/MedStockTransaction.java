package entity.prescription;

import entity.system.Users;
import op.OPDE;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "MPBuchung")
@NamedQueries({
        @NamedQuery(name = "MedStockTransaction.findAll", query = "SELECT m FROM MedStockTransaction m"),
        @NamedQuery(name = "MedStockTransaction.findByBuchID", query = "SELECT m FROM MedStockTransaction m WHERE m.buchID = :buchID"),
        @NamedQuery(name = "MedStockTransaction.findByBestand", query = "SELECT m FROM MedStockTransaction m WHERE m.bestand = :bestand ORDER BY m.pit"),
        @NamedQuery(name = "MedStockTransaction.findByMenge", query = "SELECT m FROM MedStockTransaction m WHERE m.menge = :menge"),
        @NamedQuery(name = "MedStockTransaction.findByText", query = "SELECT m FROM MedStockTransaction m WHERE m.text = :text"),
        @NamedQuery(name = "MedStockTransaction.findByStatus", query = "SELECT m FROM MedStockTransaction m WHERE m.status = :status"),
        @NamedQuery(name = "MedStockTransaction.findByPit", query = "SELECT m FROM MedStockTransaction m WHERE m.pit = :pit")})
public class MedStockTransaction implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BuchID")
    private Long buchID;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "Menge")
    private BigDecimal menge;
    @Column(name = "Text")
    private String text;
    @Basic(optional = false)
    @Column(name = "Status")
    private short status;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;

    public MedStockTransaction() {
    }

    public MedStockTransaction(MedStock bestand, BigDecimal menge) {
        this.pit = new Date();
        this.bestand = bestand;
        this.menge = menge;
        this.bhp = null;
        this.status = MedStockTransactionTools.STATUS_EINBUCHEN_ANFANGSBESTAND;
        this.user = OPDE.getLogin().getUser();
    }

    public MedStockTransaction(MedStock bestand, BigDecimal menge, BHP bhp) {
        this.bestand = bestand;
        this.menge = menge;
        this.pit = new Date();
        this.bhp = bhp;
        this.status = MedStockTransactionTools.STATUS_AUSBUCHEN_NORMAL;
        this.user = OPDE.getLogin().getUser();
//        bestand.getStockTransaction().add(this);
//        bhp.getStockTransaction().add(this);
    }

//    public MedStockTransaction(MedBestand bestand, BigDecimal menge, BHP bhp, short status) {
//        this.bestand = bestand;
//        this.menge = menge;
//        this.pit = new Date();
//        this.bhp = bhp;
//        this.status = status;
//        this.user = OPDE.getLogin().getUser();
//        bestand.getStockTransaction().add(this);
//        bhp.getStockTransaction().add(this);
//    }

    public MedStockTransaction(MedStock bestand, BigDecimal menge, short status) {
        this.bestand = bestand;
        this.menge = menge;
        this.pit = new Date();
        this.bhp = null;
        this.status = status;
        this.user = OPDE.getLogin().getUser();
    }

    public Long getBuchID() {
        return buchID;
    }

    public void setBuchID(Long buchID) {
        this.buchID = buchID;
    }

    public BigDecimal getMenge() {
        return menge;
    }

    public void setMenge(BigDecimal menge) {
        this.menge = menge;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

    public BHP getBhp() {
        return bhp;
    }

//    public void setBhp(BHP bhp) {
//        this.bhp = bhp;
//    }

    public MedStock getBestand() {
        return bestand;
    }

//    public void setBestand(MedBestand bestand) {
//        this.bestand = bestand;
//    }

    public Users getUser() {
        return user;
    }

    // N:1 Relationen
    @JoinColumn(name = "BestID", referencedColumnName = "BestID")
    @ManyToOne
    //OWNER
    private MedStock bestand;

    @JoinColumn(name = "BHPID", referencedColumnName = "BHPID")
    @ManyToOne
    //OWNER
    private BHP bhp;

    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    //OWNER
    private Users user;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (buchID != null ? buchID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof MedStockTransaction)) {
            return false;
        }
        MedStockTransaction other = (MedStockTransaction) object;
        if ((this.buchID == null && other.buchID != null) || (this.buchID != null && !this.buchID.equals(other.buchID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MedStockTransaction{" +
                "buchID=" + buchID +
                '}';
    }
}
