package entity.prescription;

import entity.info.Resident;
import entity.system.Users;
import op.OPDE;
import op.tools.SYSConst;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "MPVorrat")
//@NamedQueries({
//        @NamedQuery(name = "MedInventory.findAll", query = "SELECT m FROM MedInventory m"),
//        @NamedQuery(name = "MedInventory.findByVorID", query = "SELECT m FROM MedInventory m WHERE m.id = :vorID"),
//        @NamedQuery(name = "MedInventory.findByText", query = "SELECT m FROM MedInventory m WHERE m.text = :text"),
//        @NamedQuery(name = "MedInventory.findByVon", query = "SELECT m FROM MedInventory m WHERE m.from = :von"),
//        @NamedQuery(name = "MedInventory.findByBis", query = "SELECT m FROM MedInventory m WHERE m.to = :bis"),
//        @NamedQuery(name = "MedInventory.getSumme", query = " " +
//                " SELECT SUM(buch.amount) FROM MedInventory vor JOIN vor.medStocks best JOIN best.stockTransaction buch WHERE vor = :vorrat "),
////        @NamedQuery(name = "MedInventory.findActiveByBewohnerAndDarreichung", query = " " +
////                " SELECT DISTINCT inv FROM MedInventory inv " +
////                " JOIN inv.medStocks stock " +
////                " WHERE inv.resident = :resident AND stock.tradeform = :tradeform " +
////                " AND inv.bis = " + SYSConst.MYSQL_DATETIME_BIS_AUF_WEITERES),
//        @NamedQuery(name = "MedInventory.findByBewohnerMitBestand", query = " " +
//                " SELECT best.inventory, SUM(buch.amount) FROM MedStock best " +
//                " JOIN best.stockTransaction buch " +
//                " WHERE best.inventory.resident = :bewohner AND best.inventory.to = '9999-12-31 23:59:59' " +
//                " GROUP BY best.inventory ")
//})
//
//@NamedNativeQueries({
//        // Das hier ist eine Liste aller Verordnungen eines Bewohners.
//        // Durch Joins werden die zugehörigen Vorräte und aktuellen Bestände
//        // beigefügt.
//        @NamedNativeQuery(name = "MedInventory.findVorraeteMitSummen", query = " " +
//                " SELECT DISTINCT v.VorID, b.saldo" +
//                " FROM MPVorrat v " +
//                " LEFT OUTER JOIN (" +
//                "       SELECT best.VorID, sum(buch.Menge) saldo FROM MPBestand best " +
//                "       INNER JOIN MPVorrat v ON v.VorID = best.VorID " +
//                "       INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
//                "       WHERE v.BWKennung=? " + // Diese Zeile ist eigentlich nicht nötig. Beschleunigt aber ungemein.
//                "       GROUP BY best.VorID" +
//                " ) b ON b.VorID = v.VorID" +
//                " WHERE v.BWKennung=? " +
//                " AND ( ? = 1 OR v.Bis = '9999-12-31 23:59:59' ) " +
//                " ORDER BY v.Text ")
//})
//
//@SqlResultSetMappings({
//        @SqlResultSetMapping(name = "MedInventory.findVorraeteMitSummenResultMapping",
//                entities = @EntityResult(entityClass = MedInventory.class),
//                columns = @ColumnResult(name = "b.saldo")
//        )
//})

public class MedInventory implements Serializable, Comparable<MedInventory> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VorID")
    private Long id;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "Text")
    private String text;
    @Basic(optional = false)
    @Column(name = "Von")
    @Temporal(TemporalType.TIMESTAMP)
    private Date from;
    @Basic(optional = false)
    @Column(name = "Bis")
    @Temporal(TemporalType.TIMESTAMP)
    private Date to;

    public MedInventory() {


    }

    public Resident getResident() {
        return resident;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public MedInventory(Resident resident, String text) {
        this.resident = resident;
        this.text = text;
        this.user = OPDE.getLogin().getUser();
        this.from = new Date();
        this.to = SYSConst.DATE_UNTIL_FURTHER_NOTICE;
        this.medStocks = new ArrayList<MedStock>();
    }

    public Long getID() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public boolean isClosed(){
        return to.before(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
    }


    // ==
    // 1:N Relationen
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "inventory")
    private List<MedStock> medStocks;

    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;
    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedInventory that = (MedInventory) o;

        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
//        if (medStocks != null ? !medStocks.equals(that.medStocks) : that.medStocks != null) return false;
        if (resident != null ? !resident.equals(that.resident) : that.resident != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (to != null ? !to.equals(that.to) : that.to != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
//        result = 31 * result + (medStocks != null ? medStocks.hashCode() : 0);
        result = 31 * result + (resident != null ? resident.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "entity.rest.MedInventory[vorID=" + id + "]";
    }

    @Override
    public int compareTo(MedInventory o) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<MedStock> getMedStocks() {
        return medStocks;
    }
}
                                       