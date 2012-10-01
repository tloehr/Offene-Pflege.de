/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.prescription;

import entity.files.SYSPRE2FILE;
import entity.info.Resident;
import entity.nursingprocess.Intervention;
import entity.process.QProcess;
import entity.process.QProcessElement;
import entity.process.SYSPRE2PROCESS;
import entity.system.Users;
import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * OPDE kann verschiedene Arten von wiederkehrenden Terminen für die Anwendung einer ärztlichen Verordnung
 * speichern. Dazu sind zwei Entity Classes nötig. Verordnungen und VerordnungPlanung.
 * <ul>
 * <li><b>Verordnungen</b> enthält die Angaben über die Medikamente und Maßnahmen. Ärzte und Krankenhäuser, sowie Situations bei
 * Bedarfsmedikattion.</li>
 * <li><b>VerordnungPlanung</b> sind die Termine und die Dosis bzw. Häufigkeit in der ein Medikament oder eine Maßnahme angewendet werden soll.
 * Ebenso stehen hier drin, die maximale Tagesdosis einer Bedarfsmedikation. Zu jeder Verordnung können unterschiedliche Termin-Muster und Dosen
 * eingegeben werden. Für jede dieser Einträge gibt es ein Objekt aus dieser Klasse.</li>
 * </ul>
 * Folgende Terminarten können in OPDE formuliert werden:
 * <ul>
 * <li><b>Regelverordnungen</b>, die einem bestimmten chronologischen Muster folgen
 * <ul>
 * <li><b>täglich</b>, bzw. alle <i>n</i> Tage.</li>
 * <li><b>wöchentlich</b>, bzw. alle <i>n</i> Wochen, an unterschiedlichen Wochentagen.</li>
 * <li><b>monatlich</b>, bzw. alle <i>n</i> Monate, jeweils am <i>m.</i> Tag des Monats oder am <i>o.</i> Wochentag des Monats.</li>
 * <li>es kann ein Datum festgelegt werden, ab dem <b>die erste Anwendung</b> dieser Vergabe erfolgen soll. Dies steht normalerweise auf dem aktuellen Tagesdatum.</li>
 * <li>Die einzelnen Dosisangaben können für vordefinierte Tageszeiten oder für eine Uhrzeit eingegeben werden.
 * <ul>
 * <li>Nachts, früh morgens</li>
 * <li>Morgens</li>
 * <li>Mittags</li>
 * <li>Nachmittags</li>
 * <li>Abends</li>
 * <li>Nachts, spät abends</li>
 * <li>Uhrzeit</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * <li><b>Bedarfsverordnungen</b>, die nur für die Anwendung in <b>bestimmten Situations</b> gedacht sind.</li>
 * </ul>
 * <h2>Beispiele</h2>
 * <h3>Bedarfsmedikation</h3>
 * <img src="http://www.offene-pflege.de/images/stories/opde/medi/prescription-bedarf1.png" />
 * <ul>
 * <li><code><b>Verordnung</b>{verid=4658, anDatum=Thu Dec 22 15:54:14 CET 2011, abDatum=Fri Dec 31 23:59:59 CET 9999, bisPackEnde=false, verKennung=3580, bemerkung='', stellplan=false, attachedFiles=[], attachedVorgaenge=[], angesetztDurch=Löhr, Torsten [tloehr], abgesetztDurch=null, bewohner=[JH1], massnahme=entity.rest.Massnahmen[massID=140], darreichung=entity.rest.Darreichung[dafID=1336], situation=entity.rest.Situations[sitID=10], anKH=entity.rest.Hospital[khid=16], abKH=null, anArzt=entity.rest.Doc[arztID=21], abArzt=null}</code></li>
 * <li><code><b>VerordnungPlanung</b>{bhppid=7403, nachtMo=0, morgens=0, mittags=0, nachmittags=0, abends=0, nachtAb=0, uhrzeitDosis=0, uhrzeit=null, maxAnzahl=1, maxEDosis=2, taeglich=1, woechentlich=0, monatlich=0, tagNum=0, mon=0, die=0, mit=0, don=0, fre=0, sam=0, son=0, lDatum=Thu Dec 22 15:55:05 CET 2011, uKennung='tloehr', prescription=Verordnung{verid=4658, ...}}</code></li>
 * </ul>
 * <p/>
 * <h3>Regelverordnung mit sehr unterschiedlichen Dosierungen</h3>
 * <ul>
 * <li><img src="http://www.offene-pflege.de/images/stories/opde/medi/prescription-regel123.png" /><p/><code><b>Verordnung</b>{verid=4659, anDatum=Thu Dec 22 16:09:09 CET 2011, abDatum=Fri Dec 31 23:59:59 CET 9999, bisPackEnde=false, verKennung=3581, bemerkung='', stellplan=false, attachedFiles=[], attachedVorgaenge=[], angesetztDurch=Löhr, Torsten [tloehr], abgesetztDurch=null, bewohner=[JH1], massnahme=entity.rest.Massnahmen[massID=140], darreichung=entity.rest.Darreichung[dafID=1336], situation=null, anKH=null, abKH=null, anArzt=entity.rest.Doc[arztID=1], abArzt=null}</code></li>
 * <li><img src="http://www.offene-pflege.de/images/stories/opde/medi/prescription-regel1.png" /><p/><code><b>VerordnungPlanung</b>{bhppid=7406, nachtMo=0, morgens=1, mittags=1, nachmittags=0, abends=1, nachtAb=0, uhrzeitDosis=0, uhrzeit=null, maxAnzahl=0, maxEDosis=0, taeglich=1, woechentlich=0, monatlich=0, tagNum=0, mon=0, die=0, mit=0, don=0, fre=0, sam=0, son=0, lDatum=Thu Dec 22 16:12:49 CET 2011, uKennung='tloehr', prescription=Verordnung{verid=4659, ...}}</code></li>
 * <li><img src="http://www.offene-pflege.de/images/stories/opde/medi/prescription-regel2.png" /><p/><code><b>VerordnungPlanung</b>{bhppid=7404, nachtMo=0, morgens=0, mittags=0, nachmittags=0, abends=0, nachtAb=0, uhrzeitDosis=2.5, uhrzeit=Thu Dec 22 22:00:00 CET 2011, maxAnzahl=0, maxEDosis=0, taeglich=0, woechentlich=1, monatlich=0, tagNum=0, mon=0, die=1, mit=0, don=0, fre=0, sam=1, son=0, lDatum=Thu Dec 22 16:10:52 CET 2011, uKennung='tloehr', prescription=Verordnung{verid=4659, ...}}</code></li>
 * <li><img src="http://www.offene-pflege.de/images/stories/opde/medi/prescription-regel3.png" /><p/><code><b>VerordnungPlanung</b>{bhppid=7405, nachtMo=0, morgens=0, mittags=0, nachmittags=0, abends=3, nachtAb=0, uhrzeitDosis=0, uhrzeit=null, maxAnzahl=0, maxEDosis=0, taeglich=0, woechentlich=0, monatlich=2, tagNum=0, mon=0, die=0, mit=0, don=0, fre=0, sam=1, son=0, lDatum=Thu Dec 22 16:11:49 CET 2011, uKennung='tloehr', prescription=Verordnung{verid=4659, ...}}</code></li>
 * </ul>
 *
 * @author tloehr
 */
@Entity
@Table(name = "BHPVerordnung")
@NamedQueries({
        @NamedQuery(name = "Verordnung.findAll", query = "SELECT b FROM Prescription b"),
        @NamedQuery(name = "Verordnung.findByVerID", query = "SELECT b FROM Prescription b WHERE b.id = :verid"),
        @NamedQuery(name = "Verordnung.findByAnDatum", query = "SELECT b FROM Prescription b WHERE b.from = :from"),
        @NamedQuery(name = "Verordnung.findByVorgang", query = " "
                + " SELECT ve FROM Prescription ve "
                + " JOIN ve.attachedProcessConnections av"
                + " JOIN av.qProcess v"
                + " WHERE v = :vorgang "),
        @NamedQuery(name = "Verordnung.findByAbDatum", query = "SELECT b FROM Prescription b WHERE b.to = :to"),
        @NamedQuery(name = "Verordnung.findByBisPackEnde", query = "SELECT b FROM Prescription b WHERE b.toEndOfPackage = :bisPackEnde"),
        @NamedQuery(name = "Verordnung.findByVerKennung", query = "SELECT b FROM Prescription b WHERE b.prescRelation = :verKennung"),
        @NamedQuery(name = "Verordnung.findByStellplan", query = "SELECT b FROM Prescription b WHERE b.showOnDailyPlan = :stellplan")

})

//@SqlResultSetMappings({
//
////        @SqlResultSetMapping(name = "Verordnung.findAllForStellplanResultMapping",
////                entities = {@EntityResult(entityClass = Verordnung.class), @EntityResult(entityClass = Station.class), @EntityResult(entityClass = VerordnungPlanung.class)},
////                columns = {@ColumnResult(name = "BestID"), @ColumnResult(name = "VorID"), @ColumnResult(name = "FormID"), @ColumnResult(name = "MedPID"), @ColumnResult(name = "M.Bezeichnung"), @ColumnResult(name = "Ms.Bezeichnung")
////                }
////        ),
//        @SqlResultSetMapping(name = "Verordnung.findByBewohnerMitVorraetenResultMapping",
//                entities = @EntityResult(entityClass = Prescription.class),
//                columns = {@ColumnResult(name = "VorID"), @ColumnResult(name = "saldo"), @ColumnResult(name = "BestID"), @ColumnResult(name = "summe")}
//        ),
//        @SqlResultSetMapping(name = "Verordnung.findAllBedarfResultMapping",
//                entities = {@EntityResult(entityClass = Prescription.class), @EntityResult(entityClass = Situations.class), @EntityResult(entityClass = PrescriptionSchedule.class)},
//                columns = {@ColumnResult(name = "vor.Saldo"), @ColumnResult(name = "bisher.tagesdosis"), @ColumnResult(name = "bestand.APV"), @ColumnResult(name = "bestand.Summe"),
//                        @ColumnResult(name = "bestand.BestID")
//                }
//        )
//})

//@NamedNativeQueries({
//        // Das hier ist eine Liste aller Verordnungen eines Bewohners.
//        // Durch Joins werden die zugehörigen Vorräte und aktuellen Bestände
//        // beigefügt.
//        @NamedNativeQuery(name = "Verordnung.findByBewohnerMitVorraeten", query = " " +
//                " SELECT v.*, vor.VorID, vor.saldo, bestand.BestID, bestand.summe, M.Bezeichnung mptext, Ms.Bezeichnung mssntext " +
//                " FROM BHPVerordnung v " +
//                // Die drei folgenden Joins brauche ich nur für die Sortierung in der ORDER BY Klause
//                " INNER JOIN Massnahmen Ms ON Ms.MassID = v.MassID " +
//                " LEFT OUTER JOIN MPDarreichung D ON v.DafID = D.DafID " +
//                " LEFT OUTER JOIN MProdukte M ON M.MedPID = D.MedPID" +
//                // Das hier gibt eine Liste aller Vorräte eines Bewohners. Jedem Vorrat
//                // wird mindestens eine DafID zugeordnet. Das können auch mehr sein, die stehen
//                // dann in verschiedenen Zeilen. Das bedeutet ganz einfach, dass einem Vorrat
//                // ja unterschiedliche DAFs mal zugeordnet worden sind. Und hier stehen jetzt einfach
//                // alle gültigen Kombinationen aus DAF und VOR inkl. der Salden, die jemals vorgekommen sind.
//                // Für den entsprechenden Bewohner natürlich. Wenn man das nun über die DAF mit der Verordnung joined,
//                // dann erhält man zwingend den passenden Vorrat, wenn es denn einen gibt.
//                " LEFT OUTER JOIN " +
//                " ( " +
//                "   SELECT DISTINCT a.VorID, b.DafID, a.saldo FROM ( " +
//                "       SELECT best.VorID, sum(buch.Menge) saldo FROM MPBestand best " +
//                "       INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
//                "       INNER JOIN MPVorrat vor1 ON best.VorID = vor1.VorID" +
//                "       WHERE vor1.BWKennung = ? AND vor1.Bis = '9999-12-31 23:59:59'" +
//                "       GROUP BY best.VorID" +
//                "   ) a  " +
//                "   INNER JOIN (" +
//                "       SELECT best.VorID, best.DafID FROM MPBestand best " +
//                "   ) b ON a.VorID = b.VorID " +
//                " ) vor ON vor.DafID = v.DafID " +
//                // Dieses Join fügt diejenigen Bestände hinzu, die zur Zeit im Anbruch sind
//                " LEFT OUTER JOIN " +
//                " ( " +
//                "   SELECT best1.*, SUM(buch1.Menge) summe " +
//                "   FROM MPBestand best1 " +
//                "   INNER JOIN MPBuchung buch1 ON buch1.BestID = best1.BestID " +
//                "   WHERE best1.Aus = '9999-12-31 23:59:59' AND best1.Anbruch < now() " +
//                "   GROUP BY best1.BestID" +
//                " ) bestand ON bestand.VorID = vor.VorID " +
//                " WHERE v.BWKennung = ? " +
//                // Wenn man als 3. Parameter eine 1 übergibt, dann werden alle
//                // Verordungen angezeigt, wenn nicht, dann nur die aktuellen.
//                " AND (1=? OR date(v.AbDatum) >= current_date())" +
//                " ORDER BY v.SitID IS NULL, v.DafID IS NOT NULL, ifnull(mptext, mssntext) ", resultSetMapping = "Verordnung.findByBewohnerMitVorraetenResultMapping"),
//
//})

public class Prescription implements Serializable, QProcessElement, Cloneable, Comparable<Prescription> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VerID")
    private Long id;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "AnDatum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date from;
    @Basic(optional = false)
    @Column(name = "AbDatum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date to;
    @Column(name = "BisPackEnde")
    private boolean toEndOfPackage;
    @Basic(optional = false)
    @Column(name = "VerKennung")
    private long prescRelation;
    @Lob
    @Column(name = "Bemerkung")
    private String text;
    @Basic(optional = false)
    @Column(name = "Stellplan")
    private boolean showOnDailyPlan;

    // ==
    // 1:N Relationen
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "verordnung")
    private List<SYSPRE2FILE> attachedFilesConnections;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prescription")
    private List<SYSPRE2PROCESS> attachedProcessConnections;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prescription")
    private List<PrescriptionSchedule> pSchedule;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prescription")
    private List<BHP> bhps;
    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "AnUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users userON;
    @JoinColumn(name = "AbUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users userOFF;
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;
    @JoinColumn(name = "MassID", referencedColumnName = "MassID")
    @ManyToOne
    private Intervention intervention;
    @JoinColumn(name = "DafID", referencedColumnName = "DafID")
    @ManyToOne
    private TradeForm tradeform;
    @JoinColumn(name = "SitID", referencedColumnName = "SitID")
    @ManyToOne
    private Situations situation;
    @JoinColumn(name = "AnKHID", referencedColumnName = "KHID")
    @ManyToOne
    private Hospital hospitalON;
    @JoinColumn(name = "AbKHID", referencedColumnName = "KHID")
    @ManyToOne
    private Hospital hospitalOFF;
    @JoinColumn(name = "AnArztID", referencedColumnName = "ArztID")
    @ManyToOne
    private Doc docON;
    @JoinColumn(name = "AbArztID", referencedColumnName = "ArztID")
    @ManyToOne
    private Doc docOFF;


    public Prescription() {
    }

    public Prescription(Resident resident) {
        this.resident = resident;
        this.attachedFilesConnections = new ArrayList<SYSPRE2FILE>();
        this.attachedProcessConnections = new ArrayList<SYSPRE2PROCESS>();
        this.pSchedule = new ArrayList<PrescriptionSchedule>();
        this.from = new Date();
        this.to = SYSConst.DATE_BIS_AUF_WEITERES;
        this.userON = OPDE.getLogin().getUser();
    }

    public Prescription(Date from, Date to, boolean toEndOfPackage, long prescRelation, String text, boolean showOnDailyPlan, List<SYSPRE2FILE> attachedFilesConnections, List<SYSPRE2PROCESS> attachedProcessConnections, Users userON, Users userOFF, Resident resident, Intervention intervention, TradeForm tradeform, Situations situation, Hospital hospitalON, Hospital hospitalOFF, Doc docON, Doc docOFF) {
        this.from = from;
        this.to = to;
        this.toEndOfPackage = toEndOfPackage;
        this.prescRelation = prescRelation;
        this.text = text;
        this.showOnDailyPlan = showOnDailyPlan;
        this.attachedFilesConnections = attachedFilesConnections;
        this.attachedProcessConnections = attachedProcessConnections;
        this.userON = userON;
        this.userOFF = userOFF;
        this.resident = resident;
        this.intervention = intervention;
        this.tradeform = tradeform;
        this.situation = situation;
        this.hospitalON = hospitalON;
        this.hospitalOFF = hospitalOFF;
        this.docON = docON;
        this.docOFF = docOFF;
        this.pSchedule = new ArrayList<PrescriptionSchedule>();
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Hospital getHospitalON() {
        return hospitalON;
    }

    public void setHospitalON(Hospital anKH) {
        this.hospitalON = anKH;
    }

    public Hospital getHospitalOFF() {
        return hospitalOFF;
    }

    public void setHospitalOFF(Hospital abKH) {
        this.hospitalOFF = abKH;
    }

    public Doc getDocON() {
        return docON;
    }

    public void setDocON(Doc anDoc) {
        this.docON = anDoc;
    }

    public Doc getDocOFF() {
        return docOFF;
    }

    public void setDocOFF(Doc abDoc) {
        this.docOFF = abDoc;
    }

    public boolean isTillEndOfPackage() {
        return toEndOfPackage;
    }

    public void setTillEndOfPackage(boolean tillEndOfPackage) {
        this.toEndOfPackage = tillEndOfPackage;
    }

    public long getRelation() {
        return prescRelation;
    }

    public void setRelation(long verKennung) {
        this.prescRelation = verKennung;
    }

    public String getText() {
        return SYSTools.catchNull(text);
    }

    public void setText(String bemerkung) {
        this.text = bemerkung;
    }

    public boolean isOnDailyPlan() {
        return showOnDailyPlan;
    }

    public void setShowOnDailyPlan(boolean show) {
        this.showOnDailyPlan = show;
    }

    public Users getUserOFF() {
        return userOFF;
    }

    public Situations getSituation() {
        return situation;
    }

    public boolean hasMed() {
        return tradeform != null;
    }

    public void setSituation(Situations situation) {
        this.situation = situation;
    }

    public TradeForm getTradeForm() {
        return tradeform;
    }

    public void setTradeForm(TradeForm tradeform) {
        this.tradeform = tradeform;
    }

    public Intervention getIntervention() {
        return intervention;
    }

    public void setIntervention(Intervention massnahme) {
        this.intervention = massnahme;
    }

    public void setUserOFF(Users userOFF) {
        this.userOFF = userOFF;
    }

    public Users getUserON() {
        return userON;
    }

    public void setUserON(Users userON) {
        this.userON = userON;
    }


    @Override
    public String getTitle() {
        return PrescriptionTools.getPrescriptionAsShortText(this);
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public boolean isDiscontinued() {
        return new DateTime(to).isBeforeNow();
    }

    public boolean isLimited() {
        return to.before(SYSConst.DATE_BIS_AUF_WEITERES);
    }

    public boolean isOnDemand() {
        return situation != null;
    }

    public List<SYSPRE2FILE> getAttachedFilesConnections() {
        return attachedFilesConnections;
    }

    public List<SYSPRE2PROCESS> getAttachedProcessConnections() {
        return attachedProcessConnections;
    }

    public List<PrescriptionSchedule> getPrescriptionSchedule() {
        return pSchedule;
    }

    @Override
    public Users getUser() {
        return userON;
    }

    @Override
    public long getPITInMillis() {
        return from.getTime();
    }

    @Override
    public ArrayList<QProcess> getAttachedProcesses() {
        ArrayList<QProcess> list = new ArrayList<QProcess>();
        for (SYSPRE2PROCESS att : attachedProcessConnections) {
            list.add(att.getQProcess());
        }
        return list;
    }


    @Override
    public String getContentAsHTML() {
        // TODO: fehlt noch
        return "<html>not yet</html>";
    }

    @Override
    public String getPITAsHTML() {
        // TODO: fehlt noch
        return "<html>not yet</html>";
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Prescription that = (Prescription) o;

        if (toEndOfPackage != that.toEndOfPackage) return false;
        if (showOnDailyPlan != that.showOnDailyPlan) return false;
        if (prescRelation != that.prescRelation) return false;
        if (docOFF != null ? !docOFF.equals(that.docOFF) : that.docOFF != null) return false;
        if (to != null ? !to.equals(that.to) : that.to != null) return false;
        if (hospitalOFF != null ? !hospitalOFF.equals(that.hospitalOFF) : that.hospitalOFF != null) return false;
        if (userOFF != null ? !userOFF.equals(that.userOFF) : that.userOFF != null)
            return false;
        if (docON != null ? !docON.equals(that.docON) : that.docON != null) return false;
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (hospitalON != null ? !hospitalON.equals(that.hospitalON) : that.hospitalON != null) return false;
        if (userON != null ? !userON.equals(that.userON) : that.userON != null)
            return false;
        if (attachedFilesConnections != null ? !attachedFilesConnections.equals(that.attachedFilesConnections) : that.attachedFilesConnections != null)
            return false;
        if (attachedProcessConnections != null ? !attachedProcessConnections.equals(that.attachedProcessConnections) : that.attachedProcessConnections != null)
            return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (bhps != null ? !bhps.equals(that.bhps) : that.bhps != null) return false;
        if (intervention != null ? !intervention.equals(that.intervention) : that.intervention != null) return false;

        if (resident != null ? !resident.equals(that.resident) : that.resident != null) return false;
        if (situation != null ? !situation.equals(that.situation) : that.situation != null) return false;
        if (tradeform != null ? !tradeform.equals(that.tradeform) : that.tradeform != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (toEndOfPackage ? 1 : 0);
        result = 31 * result + (int) (prescRelation ^ (prescRelation >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (showOnDailyPlan ? 1 : 0);
        result = 31 * result + (attachedFilesConnections != null ? attachedFilesConnections.hashCode() : 0);
        result = 31 * result + (attachedProcessConnections != null ? attachedProcessConnections.hashCode() : 0);

        result = 31 * result + (bhps != null ? bhps.hashCode() : 0);
        result = 31 * result + (userON != null ? userON.hashCode() : 0);
        result = 31 * result + (userOFF != null ? userOFF.hashCode() : 0);
        result = 31 * result + (resident != null ? resident.hashCode() : 0);
        result = 31 * result + (intervention != null ? intervention.hashCode() : 0);
        result = 31 * result + (tradeform != null ? tradeform.hashCode() : 0);
        result = 31 * result + (situation != null ? situation.hashCode() : 0);
        result = 31 * result + (hospitalON != null ? hospitalON.hashCode() : 0);
        result = 31 * result + (hospitalOFF != null ? hospitalOFF.hashCode() : 0);
        result = 31 * result + (docON != null ? docON.hashCode() : 0);
        result = 31 * result + (docOFF != null ? docOFF.hashCode() : 0);
        return result;
    }

    @Override
    public Prescription clone() {
        final Prescription copy = new Prescription(from, to, toEndOfPackage, prescRelation, text, showOnDailyPlan, attachedFilesConnections, attachedProcessConnections, userON, userOFF, resident, intervention, tradeform, situation, hospitalON, hospitalOFF, docON, docOFF);

        CollectionUtils.forAllDo(pSchedule, new Closure() {
            public void execute(Object o) {
                PrescriptionSchedule scheduleCopy = ((PrescriptionSchedule) o).createCopy(copy);
                copy.getPrescriptionSchedule().add(scheduleCopy);
            }
        });
        return copy;
    }

    @Override
    public int compareTo(Prescription them) {
//        int result = ((Boolean) isDiscontinued()).compareTo(them.isDiscontinued()) * -1;
        int result = ((Boolean) isOnDemand()).compareTo(them.isOnDemand()) * -1;
//        if (result == 0) {
//            result = ((Boolean) isOnDemand()).compareTo(them.isOnDemand()) * -1;
//        }
        if (result == 0) {
            result = ((Boolean) hasMed()).compareTo(them.hasMed());
        }
        if (result == 0) {
            result = PrescriptionTools.getShortDescription(this).compareTo(PrescriptionTools.getShortDescription(them));
        }
        return result;
    }

    @Override
    public String toString() {
        return "Prescription{" +
                "verid=" + id +
                ", version=" + version +
                ", anDatum=" + from +
                ", abDatum=" + to +
                ", bisPackEnde=" + toEndOfPackage +
                ", verKennung=" + prescRelation +
                ", bemerkung='" + text + '\'' +
                ", stellplan=" + showOnDailyPlan +
                ", attachedFiles=" + attachedFilesConnections +
                ", attachedVorgaenge=" + attachedProcessConnections +
                ", pSchedule=" + pSchedule +
                ", bhps=" + bhps +
                ", angesetztDurch=" + userON +
                ", abgesetztDurch=" + userOFF +
                ", resident=" + resident +
                ", massnahme=" + intervention +
                ", tradeform=" + tradeform +
                ", situation=" + situation +
                ", anKH=" + hospitalON +
                ", abKH=" + hospitalOFF +
                ", anArzt=" + docON +
                ", abArzt=" + docOFF +
                '}';
    }
}
