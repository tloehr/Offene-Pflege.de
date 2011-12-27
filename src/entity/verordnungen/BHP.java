package entity.verordnungen;

import entity.Stationen;
import entity.Users;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "BHP")

@SqlResultSetMappings({
        @SqlResultSetMapping(name = "BHP.findByBewohnerDatumSchichtKeineMedisResultMapping",
                entities = @EntityResult(entityClass = BHP.class),
                columns = {@ColumnResult(name = "BestID"), @ColumnResult(name = "NextBest")}
        ),

        @SqlResultSetMapping(name = "Verordnung.findAllForStellplanResultMapping",
                entities = {@EntityResult(entityClass = Verordnung.class), @EntityResult(entityClass = Stationen.class), @EntityResult(entityClass = VerordnungPlanung.class)},
                columns = {@ColumnResult(name = "BestID"), @ColumnResult(name = "NextBest"), @ColumnResult(name = "FormID"), @ColumnResult(name = "MedPID"), @ColumnResult(name = "M.Bezeichnung"), @ColumnResult(name = "Ms.Bezeichnung")
                }
        ),
//                        v.*, st.*, bhp.*, best.BestID, vor.VorID, F.FormID, M.MedPID, M.Bezeichnung, Ms.Bezeichnung

        @SqlResultSetMapping(name = "Verordnung.findByBewohnerMitVorraetenResultMapping",
                entities = @EntityResult(entityClass = Verordnung.class),
                columns = {@ColumnResult(name = "VorID"), @ColumnResult(name = "saldo"), @ColumnResult(name = "BestID"), @ColumnResult(name = "summe")}
        )
})

@NamedNativeQueries({

        /**
         * Dieser Query ordnet Verordnungen den Vorräten zu. Dazu ist ein kleiner Trick nötig. Denn über die Zeit können verschiedene Vorräte mit verschiedenen
         * Darreichungen für dieselbe Verordnung verwendet werden. Der Trick ist der Join über zwei Spalten in der Zeile mit "MPBestand"
         */
        @NamedNativeQuery(name = "BHP.findByBewohnerDatumSchichtKeineMedis", query = " " +
                " SELECT bhp.*, 0 BestID, 0 NextBest " +
                " FROM BHP bhp " +
                " INNER JOIN BHPPlanung bhpp ON bhp.BHPPID = bhpp.BHPPID" +
                " INNER JOIN BHPVerordnung v ON bhpp.VerID = v.VerID" +
                " INNER JOIN Massnahmen mass ON v.MassID = mass.MassID" +
                " WHERE Date(Soll)=Date(now()) AND BWKennung=? AND v.DafID IS NULL", resultSetMapping = "BHP.findByBewohnerDatumSchichtKeineMedisResultMapping"),
        /**
         * Dieser Query wird zur Erzeugung eines Stellplans verwendet.
         */
        @NamedNativeQuery(name = "Verordnung.findAllForStellplan", query = " " +
                " SELECT v.*, st.*, bhp.*, best.BestID, vor.VorID, F.FormID, M.MedPID, M.Bezeichnung, Ms.Bezeichnung " +
                " FROM BHPVerordnung v " +
                " INNER JOIN Bewohner bw ON v.BWKennung = bw.BWKennung  " +
                " INNER JOIN Massnahmen Ms ON Ms.MassID = v.MassID " +
                " INNER JOIN Stationen st ON bw.StatID = st.StatID  " +
                " LEFT OUTER JOIN MPDarreichung D ON v.DafID = D.DafID " +
                " LEFT OUTER JOIN BHPPlanung bhp ON bhp.VerID = v.VerID " +
                " LEFT OUTER JOIN MProdukte M ON M.MedPID = D.MedPID " +
                " LEFT OUTER JOIN MPFormen F ON D.FormID = F.FormID " +
                " LEFT OUTER JOIN ( " +
                "      SELECT DISTINCT M.VorID, M.BWKennung, B.DafID FROM MPVorrat M  " +
                "      INNER JOIN MPBestand B ON M.VorID = B.VorID " +
                "      WHERE M.Bis = '9999-12-31 23:59:59' " +
                " ) vorr ON vorr.DafID = v.DafID AND vorr.BWKennung = v.BWKennung" +
                " LEFT OUTER JOIN MPVorrat vor ON vor.VorID = vorr.VorID" +
                " LEFT OUTER JOIN MPBestand best ON best.VorID = vor.VorID" +
                " WHERE v.AnDatum < now() AND v.AbDatum > now() AND v.SitID IS NULL AND (v.DafID IS NOT NULL OR v.Stellplan IS TRUE) " +
                " AND st.EKennung = ? AND ((best.Aus = '9999-12-31 23:59:59' AND best.Anbruch < '9999-12-31 23:59:59') OR (v.DafID IS NULL)) " +
                // TODO: Dieser Ausdruck muss geändert werden. Der filter die duch die best.* Filter die Nahrungsergänzungen raus. Soll er nicht.
                " ORDER BY st.statid, CONCAT(bw.nachname,bw.vorname), bw.BWKennung, v.DafID IS NOT NULL, F.Stellplan, CONCAT( M.Bezeichnung, Ms.Bezeichnung)",
                resultSetMapping = "Verordnung.findAllForStellplanResultMapping")

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
                " SELECT COUNT(bhp) FROM BHP bhp WHERE bhp.verordnungPlanung.verordnung = :verordnung AND bhp.status <> :status ")})
public class BHP implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BHPID")
    private Long bhpid;
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
    @Column(name = "_mdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mdate;
    @Column(name = "Dauer")
    private Short dauer;

    public BHP() {
    }

    public BHP(VerordnungPlanung verordnungPlanung) {
        this.verordnungPlanung = verordnungPlanung;
    }

    public BHP(VerordnungPlanung verordnungPlanung, Date soll, Byte sZeit, BigDecimal dosis) {
        this.verordnungPlanung = verordnungPlanung;
        this.soll = soll;
        this.sZeit = sZeit;
        this.dosis = dosis;
        this.status = BHPTools.STATUS_OFFEN;
        this.mdate = new Date();
    }

    @JoinColumn(name = "bhppid", referencedColumnName = "BHPPID")
    @ManyToOne
    private VerordnungPlanung verordnungPlanung;

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

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public Short getDauer() {
        return dauer;
    }

    public void setDauer(Short dauer) {
        this.dauer = dauer;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bhpid != null ? bhpid.hashCode() : 0);
        return hash;
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
                ", uKennung='" + uKennung + '\'' +
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
