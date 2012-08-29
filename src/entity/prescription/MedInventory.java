package entity.prescription;

import entity.info.Resident;
import entity.system.Users;
import op.OPDE;
import op.tools.SYSConst;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "MPVorrat")
@NamedQueries({
        @NamedQuery(name = "MedInventory.findAll", query = "SELECT m FROM MedInventory m"),
        @NamedQuery(name = "MedInventory.findByVorID", query = "SELECT m FROM MedInventory m WHERE m.vorID = :vorID"),
        @NamedQuery(name = "MedInventory.findByText", query = "SELECT m FROM MedInventory m WHERE m.text = :text"),
        @NamedQuery(name = "MedInventory.findByVon", query = "SELECT m FROM MedInventory m WHERE m.von = :von"),
        @NamedQuery(name = "MedInventory.findByBis", query = "SELECT m FROM MedInventory m WHERE m.bis = :bis"),
        @NamedQuery(name = "MedInventory.getSumme", query = " " +
                " SELECT SUM(buch.menge) FROM MedInventory vor JOIN vor.medStocks best JOIN best.stockTransaction buch WHERE vor = :vorrat "),
//        @NamedQuery(name = "MedInventory.findActiveByBewohnerAndDarreichung", query = " " +
//                " SELECT DISTINCT inv FROM MedInventory inv " +
//                " JOIN inv.medStocks stock " +
//                " WHERE inv.resident = :resident AND stock.tradeform = :tradeform " +
//                " AND inv.bis = " + SYSConst.MYSQL_DATETIME_BIS_AUF_WEITERES),
        @NamedQuery(name = "MedInventory.findByBewohnerMitBestand", query = " " +
                " SELECT best.inventory, SUM(buch.menge) FROM MedStock best " +
                " JOIN best.stockTransaction buch " +
                " WHERE best.inventory.resident = :bewohner AND best.inventory.bis = '9999-12-31 23:59:59' " +
                " GROUP BY best.inventory ")
})

@NamedNativeQueries({
        // Das hier ist eine Liste aller Verordnungen eines Bewohners.
        // Durch Joins werden die zugehörigen Vorräte und aktuellen Bestände
        // beigefügt.
        @NamedNativeQuery(name = "MedInventory.findVorraeteMitSummen", query = " " +
                " SELECT DISTINCT v.VorID, b.saldo" +
                " FROM MPVorrat v " +
                " LEFT OUTER JOIN (" +
                "       SELECT best.VorID, sum(buch.Menge) saldo FROM MPBestand best " +
                "       INNER JOIN MPVorrat v ON v.VorID = best.VorID " +
                "       INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
                "       WHERE v.BWKennung=? " + // Diese Zeile ist eigentlich nicht nötig. Beschleunigt aber ungemein.
                "       GROUP BY best.VorID" +
                " ) b ON b.VorID = v.VorID" +
                " WHERE v.BWKennung=? " +
                " AND ( ? = 1 OR v.Bis = '9999-12-31 23:59:59' ) " +
                " ORDER BY v.Text ")
})
//
//@SqlResultSetMappings({
//        @SqlResultSetMapping(name = "MedInventory.findVorraeteMitSummenResultMapping",
//                entities = @EntityResult(entityClass = MedInventory.class),
//                columns = @ColumnResult(name = "b.saldo")
//        )
//})

public class MedInventory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "VorID")
    private Long vorID;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "Text")
    private String text;
    @Basic(optional = false)
    @Column(name = "Von")
    @Temporal(TemporalType.TIMESTAMP)
    private Date von;
    @Basic(optional = false)
    @Column(name = "Bis")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bis;

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
        this.von = new Date();
        this.bis = SYSConst.DATE_BIS_AUF_WEITERES;
        this.medStocks = new ArrayList<MedStock>();
    }

    public Long getVorID() {
        return vorID;
    }

    public void setVorID(Long vorID) {
        this.vorID = vorID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getVon() {
        return von;
    }

    public void setVon(Date von) {
        this.von = von;
    }

    public Date getBis() {
        return bis;
    }

    public boolean isAbgeschlossen(){
        return bis.before(SYSConst.DATE_BIS_AUF_WEITERES);
    }

    public void setBis(Date bis) {
        this.bis = bis;
    }

    // ==
    // 1:N Relationen
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "inventory")
    private Collection<MedStock> medStocks;

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
    public int hashCode() {
        int hash = 0;
        hash += (vorID != null ? vorID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof MedInventory)) {
            return false;
        }
        MedInventory other = (MedInventory) object;
        if ((this.vorID == null && other.vorID != null) || (this.vorID != null && !this.vorID.equals(other.vorID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.MedInventory[vorID=" + vorID + "]";
    }

    public Collection<MedStock> getMedStocks() {
        return medStocks;
    }
}
                                       