package entity.verordnungen;

import entity.Bewohner;
import entity.Users;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "BHP")

@SqlResultSetMappings({
        @SqlResultSetMapping(name = "BHP.findByBewohnerDatumSchichtResultMapping",
                entities = @EntityResult(entityClass = BHP.class),
                columns = {@ColumnResult(name = "BestID"), @ColumnResult(name = "NextBest")}
        )
})

@NamedNativeQueries({
        @NamedNativeQuery(name = "BHP.findByBewohnerDatumSchichtKeineMedis", query = " " +
                " SELECT bhp.*, NULL BestID, NULL NextBest " +
                " FROM BHP bhp " +
//                " INNER JOIN BHPPlanung bhpp ON bhp.BHPPID = bhpp.BHPPID" +
//                " INNER JOIN BHPVerordnung v ON bhpp.VerID = v.VerID" +
//                " INNER JOIN Massnahmen mass ON v.MassID = mass.MassID" +
                " WHERE Date(Soll)=Date(?) AND BWKennung=? AND DafID IS NULL" +
                // Durch den Kniff mit ? = 1 kann man den ganzen Ausdruck anhängen oder abhängen.
                " AND ( ? = TRUE OR (SZeit >= ? AND SZeit <= ?) OR (SZeit = 0 AND TIME(Soll) >= ? AND TIME(Soll) <= ?)) ", resultSetMapping = "BHP.findByBewohnerDatumSchichtResultMapping"),

        @NamedNativeQuery(name = "BHP.findByBewohnerDatumSchichtMitMedis", query = " " +
                " SELECT bhp.*, bestand.BestID, bestand.NextBest" +
                " FROM BHP bhp " +
//                " INNER JOIN BHPPlanung bhpp ON bhp.BHPPID = bhpp.BHPPID" +
//                " INNER JOIN BHPVerordnung v ON bhpp.VerID = v.VerID" +
                // Das hier gibt eine Liste aller Vorräte eines Bewohners. Jedem Vorrat
                // wird mindestens eine DafID zugeordnet. Das können auch mehr sein, die stehen
                // dann in verschiedenen Zeilen. Das bedeutet ganz einfach, dass einem Vorrat
                // ja unterschiedliche DAFs mal zugeordnet worden sind. Und hier stehen jetzt einfach
                // alle gültigen Kombinationen aus DAF und VOR inkl. der Salden, die jemals vorgekommen sind.
                // Für den entsprechenden Bewohner natürlich. Wenn man das nun über die DAF mit der Verordnung joined,
                // dann erhält man zwingend den passenden Vorrat, wenn es denn einen gibt.
                " LEFT OUTER JOIN " +
                " (" +
                "     SELECT DISTINCT a.VorID, b.DafID FROM (" +
                "       SELECT best.VorID, best.DafID FROM MPBestand best" +
                "       INNER JOIN MPVorrat vor1 ON best.VorID = vor1.VorID" +
                "       WHERE vor1.BWKennung=? AND vor1.Bis = '9999-12-31 23:59:59'" +
                "       GROUP BY VorID" +
                "     ) a " +
                "     INNER JOIN (" +
                "       SELECT best.VorID, best.DafID FROM MPBestand best " +
                "     ) b ON a.VorID = b.VorID " +
                " ) vor ON vor.DafID = bhp.DafID " +
                // Das hier sucht passende Bestände im Anbruch raus
                " LEFT OUTER JOIN( " +
                "       SELECT best1.NextBest, best1.VorID, best1.BestID, best1.DafID, best1.APV " +
                "       FROM MPBestand best1" +
                "       WHERE best1.Aus = '9999-12-31 23:59:59' AND best1.Anbruch < now() " +
                "       GROUP BY best1.BestID" +
                " ) bestand ON bestand.VorID = vor.VorID " +
                " WHERE bhp.DafID IS NOT NULL AND Date(bhp.Soll)=Date(?) AND bhp.BWKennung=?" +
                // Durch den Kniff mit ? = 1 kann man den ganzen Ausdruck anhängen oder abhängen.
                " AND (? = TRUE OR (SZeit >= ? AND SZeit <= ?) OR (SZeit = 0 AND TIME(Soll) >= ? AND TIME(Soll) <= ?))", resultSetMapping = "BHP.findByBewohnerDatumSchichtResultMapping")
})


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
                " SELECT COUNT(bhp) FROM BHP bhp WHERE bhp.verordnung = :verordnung AND bhp.status <> :status ")})

public class BHP implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BHPID")
    private Long bhpid;
    @Version
    private long version;
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

    public BHP() {
    }

    public BHP(VerordnungPlanung verordnungPlanung) {
        // Das sieht redundant aus, dient aber der Vereinfachung
        this.verordnungPlanung = verordnungPlanung;
        this.verordnung = verordnungPlanung.getVerordnung();
        this.bewohner = verordnungPlanung.getVerordnung().getBewohner();
        this.darreichung = verordnungPlanung.getVerordnung().getDarreichung();
        buchungen = new ArrayList<MedBuchungen>();
    }

    public BHP(VerordnungPlanung verordnungPlanung, Date soll, Byte sZeit, BigDecimal dosis) {
        // Das sieht redundant aus, dient aber der Vereinfachung
        this.verordnungPlanung = verordnungPlanung;
        this.verordnung = verordnungPlanung.getVerordnung();
        this.bewohner = verordnungPlanung.getVerordnung().getBewohner();
        this.darreichung = verordnungPlanung.getVerordnung().getDarreichung();
        this.soll = soll;
        this.sZeit = sZeit;
        this.dosis = dosis;
        this.status = BHPTools.STATUS_OFFEN;
        this.mdate = new Date();
        buchungen = new ArrayList<MedBuchungen>();
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bhp")
    private Collection<MedBuchungen> buchungen;

    @JoinColumn(name = "BHPPID", referencedColumnName = "BHPPID")
    @ManyToOne
    private VerordnungPlanung verordnungPlanung;

    @JoinColumn(name = "VerID", referencedColumnName = "VerID")
    @ManyToOne
    private Verordnung verordnung;

    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Bewohner bewohner;

    @JoinColumn(name = "DafID", referencedColumnName = "DafID")
    @ManyToOne
    private Darreichung darreichung;

    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;


    public VerordnungPlanung getVerordnungPlanung() {
        return verordnungPlanung;
    }

    public void setVerordnungPlanung(VerordnungPlanung verordnungPlanung) {
        this.verordnungPlanung = verordnungPlanung;
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

    public Verordnung getVerordnung() {
        return verordnung;
    }

    public void setVerordnung(Verordnung verordnung) {
        this.verordnung = verordnung;
    }

    public Bewohner getBewohner() {
        return bewohner;
    }

    public void setBewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
    }

    public Darreichung getDarreichung() {
        return darreichung;
    }

    public void setDarreichung(Darreichung darreichung) {
        this.darreichung = darreichung;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bhpid != null ? bhpid.hashCode() : 0);
        return hash;
    }

    public Collection<MedBuchungen> getBuchungen() {
        return buchungen;
    }

    public boolean hasAbgesetzteBestand() {
        boolean yes = false;
        if (buchungen != null) {
            for (MedBuchungen buchung : buchungen) {
                yes = buchung.getBestand().isAbgeschlossen();
                if (yes) {
                    break;
                }
            }
        }
        return yes;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BHP)) {
            return false;
        }
        BHP other = (BHP) object;
        if ((this.bhpid == null && other.bhpid != null) || (this.bhpid != null && !this.bhpid.equals(other.bhpid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BHP{" +
                "bhpid=" + bhpid +
                ", soll=" + soll +
                ", ist=" + ist +
                ", sZeit=" + sZeit +
                ", iZeit=" + iZeit +
                ", dosis=" + dosis +
                ", status=" + status +
                ", bemerkung='" + bemerkung + '\'' +
                ", mdate=" + mdate +
                ", dauer=" + dauer +
                ", verordnungPlanung=" + verordnungPlanung +
                '}';
    }
}
