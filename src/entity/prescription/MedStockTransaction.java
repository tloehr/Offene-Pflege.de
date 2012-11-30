package entity.prescription;

import entity.system.Users;
import op.OPDE;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "MPBuchung")
//@NamedQueries({
//        @NamedQuery(name = "MedStockTransaction.findAll", query = "SELECT m FROM MedStockTransaction m"),
//        @NamedQuery(name = "MedStockTransaction.findByBuchID", query = "SELECT m FROM MedStockTransaction m WHERE m.ID = :buchID"),
//        @NamedQuery(name = "MedStockTransaction.findByBestand", query = "SELECT m FROM MedStockTransaction m WHERE m.stock = :bestand ORDER BY m.pit"),
//        @NamedQuery(name = "MedStockTransaction.findByMenge", query = "SELECT m FROM MedStockTransaction m WHERE m.amount = :menge"),
//        @NamedQuery(name = "MedStockTransaction.findByText", query = "SELECT m FROM MedStockTransaction m WHERE m.text = :text"),
//        @NamedQuery(name = "MedStockTransaction.findByStatus", query = "SELECT m FROM MedStockTransaction m WHERE m.state = :status"),
//        @NamedQuery(name = "MedStockTransaction.findByPit", query = "SELECT m FROM MedStockTransaction m WHERE m.pit = :pit")})
public class MedStockTransaction implements Serializable, Comparable<MedStockTransaction> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BuchID")
    private Long id;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "Menge")
    private BigDecimal amount;
    @Column(name = "Text")
    private String text;
    @Basic(optional = false)
    @Column(name = "Status")
    private short state;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;

    public MedStockTransaction() {
    }

    public MedStockTransaction(MedStock stock, BigDecimal amount) {
        this.pit = new Date();
        this.stock = stock;
        this.amount = amount;
        this.bhp = null;
        this.state = MedStockTransactionTools.STATE_CREDIT;
        this.user = OPDE.getLogin().getUser();
    }

    public MedStockTransaction(MedStock stock, BigDecimal amount, BHP bhp) {
        this.stock = stock;
        this.amount = amount;
        this.pit = new Date();
        this.bhp = bhp;
        this.state = MedStockTransactionTools.STATE_DEBIT;
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

    public MedStockTransaction(MedStock stock, BigDecimal amount, short state) {
        this.stock = stock;
        this.amount = amount;
        this.pit = new Date();
        this.bhp = null;
        this.state = state;
        this.user = OPDE.getLogin().getUser();
    }

    public Long getID() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal menge) {
        this.amount = menge;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public short getState() {
        return state;
    }

    public void setState(short status) {
        this.state = status;
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

    public boolean isBHP(){
        return bhp != null;
    }

    public boolean isPartOfCancelPair() {
        return state == MedStockTransactionTools.STATE_CANCEL_REC || state == MedStockTransactionTools.STATE_CANCELLED;
    }

//    public void setBhp(BHP bhp) {
//        this.bhp = bhp;
//    }

    public MedStock getStock() {
        return stock;
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
    private MedStock stock;

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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MedStockTransaction)) {
            return false;
        }
        MedStockTransaction other = (MedStockTransaction) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(MedStockTransaction that) {
        return this.getPit().compareTo(that.getPit()) * -1;
    }

    @Override
    public String toString() {
        return "MedStockTransaction{" +
                "buchID=" + id +
                '}';
    }
}
