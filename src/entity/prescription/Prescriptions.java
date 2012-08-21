/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.prescription;

import entity.*;
import entity.files.Sysver2file;
import entity.info.Resident;
import entity.nursingprocess.Intervention;
import entity.process.SYSPRE2PROCESS;
import entity.process.QProcessElement;
import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * OPDE kann verschiedene Arten von wiederkehrenden Terminen für die Anwendung einer ärztlichen Verordnung
 * speichern. Dazu sind zwei Entity Classes nötig. Verordnungen und VerordnungPlanung.
 * <ul>
 * <li><b>Verordnungen</b> enthält die Angaben über die Medikamente und Maßnahmen. Ärzte und Krankenhäuser, sowie Situationen bei
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
 * <li><b>Bedarfsverordnungen</b>, die nur für die Anwendung in <b>bestimmten Situationen</b> gedacht sind.</li>
 * </ul>
 * <h2>Beispiele</h2>
 * <h3>Bedarfsmedikation</h3>
 * <img src="http://www.offene-pflege.de/images/stories/opde/medi/verordnung-bedarf1.png" />
 * <ul>
 * <li><code><b>Verordnung</b>{verid=4658, anDatum=Thu Dec 22 15:54:14 CET 2011, abDatum=Fri Dec 31 23:59:59 CET 9999, bisPackEnde=false, verKennung=3580, bemerkung='', stellplan=false, attachedFiles=[], attachedVorgaenge=[], angesetztDurch=Löhr, Torsten [tloehr], abgesetztDurch=null, bewohner=[JH1], massnahme=entity.rest.Massnahmen[massID=140], darreichung=entity.rest.Darreichung[dafID=1336], situation=entity.rest.Situationen[sitID=10], anKH=entity.rest.Krankenhaus[khid=16], abKH=null, anArzt=entity.rest.Arzt[arztID=21], abArzt=null}</code></li>
 * <li><code><b>VerordnungPlanung</b>{bhppid=7403, nachtMo=0, morgens=0, mittags=0, nachmittags=0, abends=0, nachtAb=0, uhrzeitDosis=0, uhrzeit=null, maxAnzahl=1, maxEDosis=2, taeglich=1, woechentlich=0, monatlich=0, tagNum=0, mon=0, die=0, mit=0, don=0, fre=0, sam=0, son=0, lDatum=Thu Dec 22 15:55:05 CET 2011, uKennung='tloehr', verordnung=Verordnung{verid=4658, ...}}</code></li>
 * </ul>
 * <p/>
 * <h3>Regelverordnung mit sehr unterschiedlichen Dosierungen</h3>
 * <ul>
 * <li><img src="http://www.offene-pflege.de/images/stories/opde/medi/verordnung-regel123.png" /><p/><code><b>Verordnung</b>{verid=4659, anDatum=Thu Dec 22 16:09:09 CET 2011, abDatum=Fri Dec 31 23:59:59 CET 9999, bisPackEnde=false, verKennung=3581, bemerkung='', stellplan=false, attachedFiles=[], attachedVorgaenge=[], angesetztDurch=Löhr, Torsten [tloehr], abgesetztDurch=null, bewohner=[JH1], massnahme=entity.rest.Massnahmen[massID=140], darreichung=entity.rest.Darreichung[dafID=1336], situation=null, anKH=null, abKH=null, anArzt=entity.rest.Arzt[arztID=1], abArzt=null}</code></li>
 * <li><img src="http://www.offene-pflege.de/images/stories/opde/medi/verordnung-regel1.png" /><p/><code><b>VerordnungPlanung</b>{bhppid=7406, nachtMo=0, morgens=1, mittags=1, nachmittags=0, abends=1, nachtAb=0, uhrzeitDosis=0, uhrzeit=null, maxAnzahl=0, maxEDosis=0, taeglich=1, woechentlich=0, monatlich=0, tagNum=0, mon=0, die=0, mit=0, don=0, fre=0, sam=0, son=0, lDatum=Thu Dec 22 16:12:49 CET 2011, uKennung='tloehr', verordnung=Verordnung{verid=4659, ...}}</code></li>
 * <li><img src="http://www.offene-pflege.de/images/stories/opde/medi/verordnung-regel2.png" /><p/><code><b>VerordnungPlanung</b>{bhppid=7404, nachtMo=0, morgens=0, mittags=0, nachmittags=0, abends=0, nachtAb=0, uhrzeitDosis=2.5, uhrzeit=Thu Dec 22 22:00:00 CET 2011, maxAnzahl=0, maxEDosis=0, taeglich=0, woechentlich=1, monatlich=0, tagNum=0, mon=0, die=1, mit=0, don=0, fre=0, sam=1, son=0, lDatum=Thu Dec 22 16:10:52 CET 2011, uKennung='tloehr', verordnung=Verordnung{verid=4659, ...}}</code></li>
 * <li><img src="http://www.offene-pflege.de/images/stories/opde/medi/verordnung-regel3.png" /><p/><code><b>VerordnungPlanung</b>{bhppid=7405, nachtMo=0, morgens=0, mittags=0, nachmittags=0, abends=3, nachtAb=0, uhrzeitDosis=0, uhrzeit=null, maxAnzahl=0, maxEDosis=0, taeglich=0, woechentlich=0, monatlich=2, tagNum=0, mon=0, die=0, mit=0, don=0, fre=0, sam=1, son=0, lDatum=Thu Dec 22 16:11:49 CET 2011, uKennung='tloehr', verordnung=Verordnung{verid=4659, ...}}</code></li>
 * </ul>
 *
 * @author tloehr
 */
@Entity
@Table(name = "BHPVerordnung")
@NamedQueries({
        @NamedQuery(name = "Verordnung.findAll", query = "SELECT b FROM Prescriptions b"),
        @NamedQuery(name = "Verordnung.findByVerID", query = "SELECT b FROM Prescriptions b WHERE b.verid = :verid"),
        @NamedQuery(name = "Verordnung.findByAnDatum", query = "SELECT b FROM Prescriptions b WHERE b.anDatum = :anDatum"),
        @NamedQuery(name = "Verordnung.findByVorgang", query = " "
                + " SELECT ve FROM Prescriptions ve "
                + " JOIN ve.attachedVorgaenge av"
                + " JOIN av.vorgang v"
                + " WHERE v = :vorgang "),
        @NamedQuery(name = "Verordnung.findByAbDatum", query = "SELECT b FROM Prescriptions b WHERE b.abDatum = :abDatum"),
        @NamedQuery(name = "Verordnung.findByBisPackEnde", query = "SELECT b FROM Prescriptions b WHERE b.bisPackEnde = :bisPackEnde"),
        @NamedQuery(name = "Verordnung.findByVerKennung", query = "SELECT b FROM Prescriptions b WHERE b.verKennung = :verKennung"),
        @NamedQuery(name = "Verordnung.findByStellplan", query = "SELECT b FROM Prescriptions b WHERE b.stellplan = :stellplan")

})

@SqlResultSetMappings({

//        @SqlResultSetMapping(name = "Verordnung.findAllForStellplanResultMapping",
//                entities = {@EntityResult(entityClass = Verordnung.class), @EntityResult(entityClass = Stationen.class), @EntityResult(entityClass = VerordnungPlanung.class)},
//                columns = {@ColumnResult(name = "BestID"), @ColumnResult(name = "VorID"), @ColumnResult(name = "FormID"), @ColumnResult(name = "MedPID"), @ColumnResult(name = "M.Bezeichnung"), @ColumnResult(name = "Ms.Bezeichnung")
//                }
//        ),
        @SqlResultSetMapping(name = "Verordnung.findByBewohnerMitVorraetenResultMapping",
                entities = @EntityResult(entityClass = Prescriptions.class),
                columns = {@ColumnResult(name = "VorID"), @ColumnResult(name = "saldo"), @ColumnResult(name = "BestID"), @ColumnResult(name = "summe")}
        ),
        @SqlResultSetMapping(name = "Verordnung.findAllBedarfResultMapping",
                entities = {@EntityResult(entityClass = Prescriptions.class), @EntityResult(entityClass = Situationen.class), @EntityResult(entityClass = PrescriptionSchedule.class)},
                columns = {@ColumnResult(name = "vor.Saldo"), @ColumnResult(name = "bisher.tagesdosis"), @ColumnResult(name = "bestand.APV"), @ColumnResult(name = "bestand.Summe"),
                        @ColumnResult(name = "bestand.BestID")
                }
        )
})

@NamedNativeQueries({
        // Das hier ist eine Liste aller Verordnungen eines Bewohners.
        // Durch Joins werden die zugehörigen Vorräte und aktuellen Bestände
        // beigefügt.
        @NamedNativeQuery(name = "Verordnung.findByBewohnerMitVorraeten", query = " " +
                " SELECT v.*, vor.VorID, vor.saldo, bestand.BestID, bestand.summe, M.Bezeichnung mptext, Ms.Bezeichnung mssntext " +
                " FROM BHPVerordnung v " +
                // Die drei folgenden Joins brauche ich nur für die Sortierung in der ORDER BY Klause
                " INNER JOIN Massnahmen Ms ON Ms.MassID = v.MassID " +
                " LEFT OUTER JOIN MPDarreichung D ON v.DafID = D.DafID " +
                " LEFT OUTER JOIN MProdukte M ON M.MedPID = D.MedPID" +
                // Das hier gibt eine Liste aller Vorräte eines Bewohners. Jedem Vorrat
                // wird mindestens eine DafID zugeordnet. Das können auch mehr sein, die stehen
                // dann in verschiedenen Zeilen. Das bedeutet ganz einfach, dass einem Vorrat
                // ja unterschiedliche DAFs mal zugeordnet worden sind. Und hier stehen jetzt einfach
                // alle gültigen Kombinationen aus DAF und VOR inkl. der Salden, die jemals vorgekommen sind.
                // Für den entsprechenden Bewohner natürlich. Wenn man das nun über die DAF mit der Verordnung joined,
                // dann erhält man zwingend den passenden Vorrat, wenn es denn einen gibt.
                " LEFT OUTER JOIN " +
                " ( " +
                "   SELECT DISTINCT a.VorID, b.DafID, a.saldo FROM ( " +
                "       SELECT best.VorID, sum(buch.Menge) saldo FROM MPBestand best " +
                "       INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
                "       INNER JOIN MPVorrat vor1 ON best.VorID = vor1.VorID" +
                "       WHERE vor1.BWKennung = ? AND vor1.Bis = '9999-12-31 23:59:59'" +
                "       GROUP BY best.VorID" +
                "   ) a  " +
                "   INNER JOIN (" +
                "       SELECT best.VorID, best.DafID FROM MPBestand best " +
                "   ) b ON a.VorID = b.VorID " +
                " ) vor ON vor.DafID = v.DafID " +
                // Dieses Join fügt diejenigen Bestände hinzu, die zur Zeit im Anbruch sind
                " LEFT OUTER JOIN " +
                " ( " +
                "   SELECT best1.*, SUM(buch1.Menge) summe " +
                "   FROM MPBestand best1 " +
                "   INNER JOIN MPBuchung buch1 ON buch1.BestID = best1.BestID " +
                "   WHERE best1.Aus = '9999-12-31 23:59:59' AND best1.Anbruch < now() " +
                "   GROUP BY best1.BestID" +
                " ) bestand ON bestand.VorID = vor.VorID " +
                " WHERE v.BWKennung = ? " +
                // Wenn man als 3. Parameter eine 1 übergibt, dann werden alle
                // Verordungen angezeigt, wenn nicht, dann nur die aktuellen.
                " AND (1=? OR date(v.AbDatum) >= current_date())" +
                " ORDER BY v.SitID IS NULL, v.DafID IS NOT NULL, ifnull(mptext, mssntext) ", resultSetMapping = "Verordnung.findByBewohnerMitVorraetenResultMapping"),

})

public class Prescriptions implements Serializable, QProcessElement, Cloneable, Comparable<Prescriptions> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "VerID")
    private Long verid;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "AnDatum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date anDatum;
    @Basic(optional = false)
    @Column(name = "AbDatum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date abDatum;
    @Column(name = "BisPackEnde")
    private boolean bisPackEnde;
    @Basic(optional = false)
    @Column(name = "VerKennung")
    private long verKennung;
    @Lob
    @Column(name = "Bemerkung")
    private String bemerkung;
    @Basic(optional = false)
    @Column(name = "Stellplan")
    private boolean stellplan;

    // ==
    // 1:N Relationen
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "verordnung")
    private List<Sysver2file> attachedFiles;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "verordnung")
    private List<SYSPRE2PROCESS> attachedVorgaenge;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prescription")
    private List<PrescriptionSchedule> pSchedule;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prescription")
    private List<BHP> bhps;
    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "AnUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users angesetztDurch;
    @JoinColumn(name = "AbUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users abgesetztDurch;
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;
    @JoinColumn(name = "MassID", referencedColumnName = "MassID")
    @ManyToOne
    private Intervention massnahme;
    @JoinColumn(name = "DafID", referencedColumnName = "DafID")
    @ManyToOne
    private TradeForm tradeform;
    @JoinColumn(name = "SitID", referencedColumnName = "SitID")
    @ManyToOne
    private Situationen situation;
    @JoinColumn(name = "AnKHID", referencedColumnName = "KHID")
    @ManyToOne
    private Krankenhaus anKH;
    @JoinColumn(name = "AbKHID", referencedColumnName = "KHID")
    @ManyToOne
    private Krankenhaus abKH;
    @JoinColumn(name = "AnArztID", referencedColumnName = "ArztID")
    @ManyToOne
    private Arzt anArzt;
    @JoinColumn(name = "AbArztID", referencedColumnName = "ArztID")
    @ManyToOne
    private Arzt abArzt;


    public Prescriptions() {
    }

    public Prescriptions(Resident resident) {
        this.resident = resident;
        this.attachedFiles = new ArrayList<Sysver2file>();
        this.attachedVorgaenge = new ArrayList<SYSPRE2PROCESS>();
        this.pSchedule = new ArrayList<PrescriptionSchedule>();
        this.anDatum = new Date();
        this.abDatum = SYSConst.DATE_BIS_AUF_WEITERES;
        this.angesetztDurch = OPDE.getLogin().getUser();
    }

    public Prescriptions(Date anDatum, Date abDatum, boolean bisPackEnde, long verKennung, String bemerkung, boolean stellplan, List<Sysver2file> attachedFiles, List<SYSPRE2PROCESS> attachedVorgaenge, Users angesetztDurch, Users abgesetztDurch, Resident resident, Intervention massnahme, TradeForm tradeform, Situationen situation, Krankenhaus anKH, Krankenhaus abKH, Arzt anArzt, Arzt abArzt) {
        this.anDatum = anDatum;
        this.abDatum = abDatum;
        this.bisPackEnde = bisPackEnde;
        this.verKennung = verKennung;
        this.bemerkung = bemerkung;
        this.stellplan = stellplan;
        this.attachedFiles = attachedFiles;
        this.attachedVorgaenge = attachedVorgaenge;
        this.angesetztDurch = angesetztDurch;
        this.abgesetztDurch = abgesetztDurch;
        this.resident = resident;
        this.massnahme = massnahme;
        this.tradeform = tradeform;
        this.situation = situation;
        this.anKH = anKH;
        this.abKH = abKH;
        this.anArzt = anArzt;
        this.abArzt = abArzt;
        this.pSchedule = new ArrayList<PrescriptionSchedule>();
    }

    public Long getVerid() {
        return verid;
    }

    public Long getVersion() {
        return version;
    }

    public void setVerid(Long verid) {
        this.verid = verid;
    }

    public Date getAnDatum() {
        return anDatum;
    }

    public void setAnDatum(Date anDatum) {
        this.anDatum = anDatum;
    }

    public Date getAbDatum() {
        return abDatum;
    }

    public void setAbDatum(Date abDatum) {
        this.abDatum = abDatum;
    }

    public Krankenhaus getAnKH() {
        return anKH;
    }

    public void setAnKH(Krankenhaus anKH) {
        this.anKH = anKH;
    }

    public Krankenhaus getAbKH() {
        return abKH;
    }

    public void setAbKH(Krankenhaus abKH) {
        this.abKH = abKH;
    }

    public Arzt getAnArzt() {
        return anArzt;
    }

    public void setAnArzt(Arzt anArzt) {
        this.anArzt = anArzt;
    }

    public Arzt getAbArzt() {
        return abArzt;
    }

    public void setAbArzt(Arzt abArzt) {
        this.abArzt = abArzt;
    }

    public boolean isTillEndOfPackage() {
        return bisPackEnde;
    }

    public void setBisPackEnde(boolean bisPackEnde) {
        this.bisPackEnde = bisPackEnde;
    }

    public long getVerKennung() {
        return verKennung;
    }

    public void setVerKennung(long verKennung) {
        this.verKennung = verKennung;
    }

    public String getBemerkung() {
        return SYSTools.catchNull(bemerkung);
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public boolean isStellplan() {
        return stellplan;
    }

    public void setStellplan(boolean stellplan) {
        this.stellplan = stellplan;
    }

    public Users getAbgesetztDurch() {
        return abgesetztDurch;
    }

    public Situationen getSituation() {
        return situation;
    }

    public boolean hasMed() {
        return tradeform != null;
    }

    public void setSituation(Situationen situation) {
        this.situation = situation;
    }

    public TradeForm getTradeForm() {
        return tradeform;
    }

    public void setTradeForm(TradeForm tradeform) {
        this.tradeform = tradeform;
    }

    public Intervention getMassnahme() {
        return massnahme;
    }

    public void setMassnahme(Intervention massnahme) {
        this.massnahme = massnahme;
    }

    public void setAbgesetztDurch(Users abgesetztDurch) {
        this.abgesetztDurch = abgesetztDurch;
    }

    public Users getAngesetztDurch() {
        return angesetztDurch;
    }

    public void setAngesetztDurch(Users angesetztDurch) {
        this.angesetztDurch = angesetztDurch;
    }


    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public boolean isDiscontinued() {
        return abDatum.before(new Date());
    }

    public boolean isBegrenzt() {
        return abDatum.before(SYSConst.DATE_BIS_AUF_WEITERES);
    }

    public boolean isOnDemand() {
        return situation != null;
    }

    public List<Sysver2file> getAttachedFiles() {
        return attachedFiles;
    }

    public List<SYSPRE2PROCESS> getAttachedVorgaenge() {
        return attachedVorgaenge;
    }

    public List<PrescriptionSchedule> getPrescriptionSchedule() {
        return pSchedule;
    }

    @Override
    public long getPITInMillis() {
        return anDatum.getTime();
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
        return verid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Prescriptions that = (Prescriptions) o;

        if (bisPackEnde != that.bisPackEnde) return false;
        if (stellplan != that.stellplan) return false;
        if (verKennung != that.verKennung) return false;
        if (abArzt != null ? !abArzt.equals(that.abArzt) : that.abArzt != null) return false;
        if (abDatum != null ? !abDatum.equals(that.abDatum) : that.abDatum != null) return false;
        if (abKH != null ? !abKH.equals(that.abKH) : that.abKH != null) return false;
        if (abgesetztDurch != null ? !abgesetztDurch.equals(that.abgesetztDurch) : that.abgesetztDurch != null)
            return false;
        if (anArzt != null ? !anArzt.equals(that.anArzt) : that.anArzt != null) return false;
        if (anDatum != null ? !anDatum.equals(that.anDatum) : that.anDatum != null) return false;
        if (anKH != null ? !anKH.equals(that.anKH) : that.anKH != null) return false;
        if (angesetztDurch != null ? !angesetztDurch.equals(that.angesetztDurch) : that.angesetztDurch != null)
            return false;
        if (attachedFiles != null ? !attachedFiles.equals(that.attachedFiles) : that.attachedFiles != null)
            return false;
        if (attachedVorgaenge != null ? !attachedVorgaenge.equals(that.attachedVorgaenge) : that.attachedVorgaenge != null)
            return false;
        if (bemerkung != null ? !bemerkung.equals(that.bemerkung) : that.bemerkung != null) return false;
        if (bhps != null ? !bhps.equals(that.bhps) : that.bhps != null) return false;
        if (massnahme != null ? !massnahme.equals(that.massnahme) : that.massnahme != null) return false;

        if (resident != null ? !resident.equals(that.resident) : that.resident != null) return false;
        if (situation != null ? !situation.equals(that.situation) : that.situation != null) return false;
        if (tradeform != null ? !tradeform.equals(that.tradeform) : that.tradeform != null) return false;
        if (verid != null ? !verid.equals(that.verid) : that.verid != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = verid != null ? verid.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (anDatum != null ? anDatum.hashCode() : 0);
        result = 31 * result + (abDatum != null ? abDatum.hashCode() : 0);
        result = 31 * result + (bisPackEnde ? 1 : 0);
        result = 31 * result + (int) (verKennung ^ (verKennung >>> 32));
        result = 31 * result + (bemerkung != null ? bemerkung.hashCode() : 0);
        result = 31 * result + (stellplan ? 1 : 0);
        result = 31 * result + (attachedFiles != null ? attachedFiles.hashCode() : 0);
        result = 31 * result + (attachedVorgaenge != null ? attachedVorgaenge.hashCode() : 0);

        result = 31 * result + (bhps != null ? bhps.hashCode() : 0);
        result = 31 * result + (angesetztDurch != null ? angesetztDurch.hashCode() : 0);
        result = 31 * result + (abgesetztDurch != null ? abgesetztDurch.hashCode() : 0);
        result = 31 * result + (resident != null ? resident.hashCode() : 0);
        result = 31 * result + (massnahme != null ? massnahme.hashCode() : 0);
        result = 31 * result + (tradeform != null ? tradeform.hashCode() : 0);
        result = 31 * result + (situation != null ? situation.hashCode() : 0);
        result = 31 * result + (anKH != null ? anKH.hashCode() : 0);
        result = 31 * result + (abKH != null ? abKH.hashCode() : 0);
        result = 31 * result + (anArzt != null ? anArzt.hashCode() : 0);
        result = 31 * result + (abArzt != null ? abArzt.hashCode() : 0);
        return result;
    }

    @Override
    public Object clone() {
        final Prescriptions copy = new Prescriptions(anDatum, abDatum, bisPackEnde, verKennung, bemerkung, stellplan, attachedFiles, attachedVorgaenge, angesetztDurch, abgesetztDurch, resident, massnahme, tradeform, situation, anKH, abKH, anArzt, abArzt);

        CollectionUtils.forAllDo(pSchedule, new Closure() {
            public void execute(Object o) {
                PrescriptionSchedule scheduleCopy = ((PrescriptionSchedule) o).createCopy(copy);
                copy.getPrescriptionSchedule().add(scheduleCopy);
            }
        });
        return copy;
    }

    @Override
    public int compareTo(Prescriptions them) {
        int result = ((Boolean) isDiscontinued()).compareTo(them.isDiscontinued()) * -1;
        if (result == 0) {
            result = ((Boolean) isOnDemand()).compareTo(them.isOnDemand()) * -1;
        }
        if (result == 0) {
            result = ((Boolean) hasMed()).compareTo(them.hasMed());
        }
        if (result == 0) {
            result = PrescriptionsTools.getPrescriptionAsText(this).compareTo(PrescriptionsTools.getPrescriptionAsText(them));
        }
        return result;
    }

    @Override
    public String toString() {
        return "Prescriptions{" +
                "verid=" + verid +
                ", version=" + version +
                ", anDatum=" + anDatum +
                ", abDatum=" + abDatum +
                ", bisPackEnde=" + bisPackEnde +
                ", verKennung=" + verKennung +
                ", bemerkung='" + bemerkung + '\'' +
                ", stellplan=" + stellplan +
                ", attachedFiles=" + attachedFiles +
                ", attachedVorgaenge=" + attachedVorgaenge +
                ", pSchedule=" + pSchedule +
                ", bhps=" + bhps +
                ", angesetztDurch=" + angesetztDurch +
                ", abgesetztDurch=" + abgesetztDurch +
                ", resident=" + resident +
                ", massnahme=" + massnahme +
                ", tradeform=" + tradeform +
                ", situation=" + situation +
                ", anKH=" + anKH +
                ", abKH=" + abKH +
                ", anArzt=" + anArzt +
                ", abArzt=" + abArzt +
                '}';
    }
}
