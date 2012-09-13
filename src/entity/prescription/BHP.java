package entity.prescription;

import entity.system.Users;
import entity.info.Resident;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSTools;

import javax.persistence.*;
import java.awt.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "BHP")

//@SqlResultSetMappings({
//        @SqlResultSetMapping(name = "BHP.findByBewohnerDatumSchichtResultMapping",
//                entities = @EntityResult(entityClass = BHP.class),
//                columns = {@ColumnResult(name = "BestID"), @ColumnResult(name = "NextBest")}
//        )
//})
//
//@NamedNativeQueries({
//        @NamedNativeQuery(name = "BHP.findByBewohnerDatumSchichtKeineMedis", query = " " +
//                " SELECT bhp.*, NULL BestID, NULL NextBest " +
//                " FROM BHP bhp " +
////                " INNER JOIN BHPPlanung bhpp ON bhp.BHPPID = bhpp.BHPPID" +
////                " INNER JOIN BHPVerordnung v ON bhpp.VerID = v.VerID" +
////                " INNER JOIN Massnahmen mass ON v.MassID = mass.MassID" +
//                " WHERE Date(Soll)=Date(?) AND BWKennung=? AND DafID IS NULL" +
//                // Durch den Kniff mit ? = 1 kann man den ganzen Ausdruck anhängen oder abhängen.
//                " AND ( ? = TRUE OR (SZeit >= ? AND SZeit <= ?) OR (SZeit = 0 AND TIME(Soll) >= ? AND TIME(Soll) <= ?)) ", resultSetMapping = "BHP.findByBewohnerDatumSchichtResultMapping"),
//
//        @NamedNativeQuery(name = "BHP.findByBewohnerDatumSchichtMitMedis", query = " " +
//                " SELECT bhp.*, bestand.BestID, bestand.NextBest" +
//                " FROM BHP bhp " +
////                " INNER JOIN BHPPlanung bhpp ON bhp.BHPPID = bhpp.BHPPID" +
////                " INNER JOIN BHPVerordnung v ON bhpp.VerID = v.VerID" +
//                // Das hier gibt eine Liste aller Vorräte eines Bewohners. Jedem Vorrat
//                // wird mindestens eine DafID zugeordnet. Das können auch mehr sein, die stehen
//                // dann in verschiedenen Zeilen. Das bedeutet ganz einfach, dass einem Vorrat
//                // ja unterschiedliche DAFs mal zugeordnet worden sind. Und hier stehen jetzt einfach
//                // alle gültigen Kombinationen aus DAF und VOR inkl. der Salden, die jemals vorgekommen sind.
//                // Für den entsprechenden Bewohner natürlich. Wenn man das nun über die DAF mit der Verordnung joined,
//                // dann erhält man zwingend den passenden Vorrat, wenn es denn einen gibt.
//                " LEFT OUTER JOIN " +
//                " (" +
//                "     SELECT DISTINCT a.VorID, b.DafID FROM (" +
//                "       SELECT best.VorID, best.DafID FROM MPBestand best" +
//                "       INNER JOIN MPVorrat vor1 ON best.VorID = vor1.VorID" +
//                "       WHERE vor1.BWKennung=? AND vor1.Bis = '9999-12-31 23:59:59'" +
//                "       GROUP BY VorID" +
//                "     ) a " +
//                "     INNER JOIN (" +
//                "       SELECT best.VorID, best.DafID FROM MPBestand best " +
//                "     ) b ON a.VorID = b.VorID " +
//                " ) vor ON vor.DafID = bhp.DafID " +
//                // Das hier sucht passende Bestände im Anbruch raus
//                " LEFT OUTER JOIN( " +
//                "       SELECT best1.NextBest, best1.VorID, best1.BestID, best1.DafID, best1.APV " +
//                "       FROM MPBestand best1" +
//                "       WHERE best1.Aus = '9999-12-31 23:59:59' AND best1.Anbruch < now() " +
//                "       GROUP BY best1.BestID" +
//                " ) bestand ON bestand.VorID = vor.VorID " +
//                " WHERE bhp.DafID IS NOT NULL AND Date(bhp.Soll)=Date(?) AND bhp.BWKennung=?" +
//                // Durch den Kniff mit ? = 1 kann man den ganzen Ausdruck anhängen oder abhängen.
//                " AND (? = TRUE OR (SZeit >= ? AND SZeit <= ?) OR (SZeit = 0 AND TIME(Soll) >= ? AND TIME(Soll) <= ?))", resultSetMapping = "BHP.findByBewohnerDatumSchichtResultMapping")
//})
//

@NamedQueries({
        @NamedQuery(name = "BHP.findAll", query = "SELECT b FROM BHP b"),
        @NamedQuery(name = "BHP.findByBHPid", query = "SELECT b FROM BHP b WHERE b.bhpid = :bhpid"),
        @NamedQuery(name = "BHP.findBySoll", query = "SELECT b FROM BHP b WHERE b.soll = :soll"),
        @NamedQuery(name = "BHP.findByIst", query = "SELECT b FROM BHP b WHERE b.ist = :ist"),
        @NamedQuery(name = "BHP.findBySZeit", query = "SELECT b FROM BHP b WHERE b.sZeit = :sZeit"),
        @NamedQuery(name = "BHP.findByIZeit", query = "SELECT b FROM BHP b WHERE b.iZeit = :iZeit"),
        @NamedQuery(name = "BHP.findByDosis", query = "SELECT b FROM BHP b WHERE b.dosis = :dosis"),
        @NamedQuery(name = "BHP.findByStatus", query = "SELECT b FROM BHP b WHERE b.status = :status"),
        @NamedQuery(name = "BHP.findByMdate", query = "SELECT b FROM BHP b WHERE b.mdate = :mdate"),
        @NamedQuery(name = "BHP.findByDauer", query = "SELECT b FROM BHP b WHERE b.dauer = :dauer"),
        @NamedQuery(name = "BHP.numByNOTStatusAndVerordnung", query = " " +
                " SELECT COUNT(bhp) FROM BHP bhp WHERE bhp.prescription = :prescription AND bhp.status <> :status ")})

public class BHP implements Serializable, Comparable<BHP> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BHPID")
    private Long bhpid;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "Soll")
    @Temporal(TemporalType.TIMESTAMP)
    private Date soll;
    @Column(name = "Ist")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ist;
    @Column(name = "SZeit")
    private Byte sZeit;
    @Column(name = "IZeit")
    private Byte iZeit;
    @Column(name = "Dosis")
    private BigDecimal dosis;
    @Column(name = "Status")
    private Byte status;
    @Lob
    @Column(name = "Bemerkung")
    private String bemerkung;
    @Basic(optional = false)
    @Column(name = "MDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mdate;
    @Column(name = "Dauer")
    private Short dauer;
    @Basic(optional = false)
    @Column(name = "nanotime")
    private Long nanotime;

    public BHP() {
    }

    public BHP(PrescriptionSchedule prescriptionSchedule) {
        // looks redundant but simplifies enormously
        this.prescriptionSchedule = prescriptionSchedule;
        this.prescription = this.prescriptionSchedule.getPrescription();
        this.resident = this.prescriptionSchedule.getPrescription().getResident();
        this.tradeform = this.prescriptionSchedule.getPrescription().getTradeForm();
        stockTransaction = new ArrayList<MedStockTransaction>();
        this.version = 0l;
        this.nanotime = System.nanoTime();
        this.mdate = new Date();
    }

    public BHP(PrescriptionSchedule prescriptionSchedule, Date soll, Byte sZeit, BigDecimal dosis) {
        // looks redundant but simplifies enormously
        this.prescriptionSchedule = prescriptionSchedule;
        this.prescription = this.prescriptionSchedule.getPrescription();
        this.resident = this.prescriptionSchedule.getPrescription().getResident();
        this.tradeform = this.prescriptionSchedule.getPrescription().getTradeForm();
        this.soll = soll;
        this.version = 0l;
        this.nanotime = System.nanoTime();
        this.sZeit = sZeit;
        this.dosis = dosis;
        this.status = BHPTools.STATE_OPEN;
        this.mdate = new Date();
        stockTransaction = new ArrayList<MedStockTransaction>();
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bhp")
    private Collection<MedStockTransaction> stockTransaction;

    @JoinColumn(name = "BHPPID", referencedColumnName = "BHPPID")
    @ManyToOne
    private PrescriptionSchedule prescriptionSchedule;

    @JoinColumn(name = "VerID", referencedColumnName = "VerID")
    @ManyToOne
    private Prescriptions prescription;

    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;

    @JoinColumn(name = "DafID", referencedColumnName = "DafID")
    @ManyToOne
    private TradeForm tradeform;

    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;


    public PrescriptionSchedule getPrescriptionSchedule() {
        return prescriptionSchedule;
    }

    public void setPrescriptionSchedule(PrescriptionSchedule prescriptionSchedule) {
        this.prescriptionSchedule = prescriptionSchedule;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Long getBHPid() {
        return bhpid;
    }

    public void setBHPid(Long bhpid) {
        this.bhpid = bhpid;
    }

    public Date getSoll() {
        return soll;
    }

    public long getVersion() {
        return version;
    }

    public void setSoll(Date soll) {
        this.soll = soll;
    }

    public Date getIst() {
        return ist;
    }

    public void setIst(Date ist) {
        this.ist = ist;
    }

    public Byte getSollZeit() {
        return sZeit;
    }

    public void setSollZeit(Byte sZeit) {
        this.sZeit = sZeit;
    }

    public Byte getiZeit() {
        return iZeit;
    }

    public void setiZeit(Byte iZeit) {
        this.iZeit = iZeit;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public BigDecimal getDosis() {
        return dosis;
    }

    public boolean hasMed() {
        return prescription.getTradeForm() != null;
    }

    public String getFGHTML() {
        if (isOnDemand()) {
            return "#" + OPDE.getProps().getProperty("ON_DEMAND_FGBHP");
        }
        return "#" + OPDE.getProps().getProperty(BHPTools.SHIFT_KEY_TEXT[getShift()] + "_FGBHP");
    }

    public Color getFG() {
        if (isOnDemand()) {
            return SYSTools.getColor(OPDE.getProps().getProperty("ON_DEMAND_FGBHP"));
        }
        return SYSTools.getColor(OPDE.getProps().getProperty(BHPTools.SHIFT_KEY_TEXT[getShift()] + "_FGBHP"));
    }

    public Color getBG() {
        if (isOnDemand()) {
            return SYSTools.getColor(OPDE.getProps().getProperty("ON_DEMAND_BGBHP"));
        }
        return SYSTools.getColor(OPDE.getProps().getProperty(BHPTools.SHIFT_KEY_TEXT[getShift()] + "_BGBHP"));
    }

    public void setDosis(BigDecimal dosis) {
        this.dosis = dosis;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Date getMDate() {
        return mdate;
    }

    public void setMDate(Date mdate) {
        this.mdate = mdate;
    }

    public Short getDauer() {
        return dauer;
    }

    public void setDauer(Short dauer) {
        this.dauer = dauer;
    }

    public Prescriptions getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescriptions prescription) {
        this.prescription = prescription;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public TradeForm getTradeForm() {
        return tradeform;
    }

    public boolean isOnDemand() {
        return prescription.isOnDemand();
    }


    public Byte getShift() {
        if (isOnDemand()) {
            return BHPTools.SHIFT_ON_DEMAND;
        }
        if (sZeit == BHPTools.BYTE_TIMEOFDAY) {
            return SYSCalendar.whatShiftIs(this.soll);
        }
        return SYSCalendar.whatShiftIs(this.sZeit);
    }

//    @Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (bhpid != null ? bhpid.hashCode() : 0);
//        return hash;
//    }

    public Collection<MedStockTransaction> getStockTransaction() {
        return stockTransaction;
    }

    /**
     * This method tells, whether there was more than one stock involved in order to provide the necessary medication in the cause of the
     * application of this BHP. This can only happen when a stock is closed in advance. After clicking this BHP the first stock is emptied and
     * then the next stock is opened.
     *
     * @return true or false
     */
    public boolean isClosedStockInvolved() {
        boolean yes = false;
        if (stockTransaction != null) {
            for (MedStockTransaction buchung : stockTransaction) {
                yes = buchung.getStock().isClosed();
                if (yes) {
                    break;
                }
            }
        }
        return yes;
    }

    @Override
    public int compareTo(BHP that) {
        int result = this.getShift().compareTo(that.getShift());
        if (result == 0) {
            result = SYSTools.nullCompare(this.getTradeForm(), that.getTradeForm());
        }
        if (result == 0) {
            result = sZeit.compareTo(that.getSollZeit());
        }
        if (result == 0) {
            if (prescription.hasMed()) {
                result = TradeFormTools.toPrettyString(prescription.getTradeForm()).compareTo(TradeFormTools.toPrettyString(that.getPrescription().getTradeForm()));
            } else {
                result = this.prescription.getIntervention().getBezeichnung().compareTo(that.getPrescription().getIntervention().getBezeichnung());
            }
        }
        if (result == 0) {
            result = nanotime.compareTo(that.nanotime);
        }
        if (result == 0) {
            bhpid.compareTo(that.bhpid);
        }
        return result;
    }


//    @Override
//    public boolean equals(Object object) {
//        if (!(object instanceof BHP)) {
//            return false;
//        }
//        BHP other = (BHP) object;
//        if ((this.bhpid == null && other.bhpid != null) || (this.bhpid != null && !this.bhpid.equals(other.bhpid))) {
//            return false;
//        }
//        return true;
//    }


    @Override
    public boolean equals(Object o) {

        BHP other = (BHP) o;
        return new Integer(hashCode()).equals(other.hashCode());
    }

    @Override
    public int hashCode() {
        int result = bhpid != null ? bhpid.hashCode() : 0;
        result = 31 * result + (nanotime != null ? nanotime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BHP{" +
                "bhpid=" + bhpid +
                '}';
    }
}
