package entity.prescription;

import entity.system.Users;
import op.OPDE;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "MPBuchung")

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
    }

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

    public boolean isBHP() {
        return bhp != null;
    }

    public boolean isPartOfCancelPair() {
        return state == MedStockTransactionTools.STATE_CANCEL_REC || state == MedStockTransactionTools.STATE_CANCELLED;
    }

    public MedStock getStock() {
        return stock;
    }

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

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        MedStockTransaction that = (MedStockTransaction) o;
//
//        if (id != null ? !id.equals(that.id) : that.id != null) return false;
//        if (state != that.state) return false;
//        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
//        if (bhp != null ? !bhp.equals(that.bhp) : that.bhp != null) return false;
//        if (pit != null ? !pit.equals(that.pit) : that.pit != null) return false;
//        if (stock != null ? !stock.equals(that.stock) : that.stock != null) return false;
//        if (text != null ? !text.equals(that.text) : that.text != null) return false;
//        if (user != null ? !user.equals(that.user) : that.user != null) return false;
//        if (version != null ? !version.equals(that.version) : that.version != null) return false;
//
//        return true;
//    }
//
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (int) state;
        result = 31 * result + (pit != null ? pit.hashCode() : 0);
        result = 31 * result + (stock != null ? stock.hashCode() : 0);
        result = 31 * result + (bhp != null ? bhp.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

//    @Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (id != null ? id.hashCode() : 0);
//        return hash;
//    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

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
