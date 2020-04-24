/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.backend.entity.prescription;

import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.backend.entity.done.*;
import de.offene_pflege.backend.entity.process.SYSPRE2PROCESS;
import de.offene_pflege.backend.entity.system.Commontags;
import de.offene_pflege.backend.entity.system.OPUsers;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * OPDE kann verschiedene Arten von wiederkehrenden Terminen für die Anwendung einer ärztlichen Verordnung speichern.
 * Dazu sind zwei Entity Classes nötig. Verordnungen und VerordnungPlanung.
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
 * <li><code><b>Verordnung</b>{verid=4658, anDatum=Thu Dec 22 15:54:14 CET 2011, abDatum=Fri Dec 31 23:59:59 CET 9999, bisPackEnde=false, verKennung=3580, bemerkung='', stellplan=false, attachedFiles=[], attachedVorgaenge=[], angesetztDurch=Löhr, Torsten [tloehr], abgesetztDurch=null, bewohner=[JH1], massnahme=entity.rest.Massnahmen[massID=140], darreichung=entity.rest.Darreichung[dafID=1336], situation=entity.rest.Situations[sitID=10], anKH=entity.rest.Hospital[khid=16], abKH=null, anArzt=entity.rest.GP[arztID=21], abArzt=null}</code></li>
 * <li><code><b>VerordnungPlanung</b>{bhppid=7403, nachtMo=0, morgens=0, mittags=0, nachmittags=0, abends=0, nachtAb=0, uhrzeitDosis=0, uhrzeit=null, maxAnzahl=1, maxEDosis=2, taeglich=1, woechentlich=0, monatlich=0, tagNum=0, mon=0, die=0, mit=0, don=0, fre=0, sam=0, son=0, lDatum=Thu Dec 22 15:55:05 CET 2011, uKennung='tloehr', prescription=Verordnung{verid=4658, ...}}</code></li>
 * </ul>
 * <p>
 * <h3>Regelverordnung mit sehr unterschiedlichen Dosierungen</h3>
 * <ul>
 * <li><img src="http://www.offene-pflege.de/images/stories/opde/medi/prescription-regel123.png" /><p/><code><b>Verordnung</b>{verid=4659, anDatum=Thu Dec 22 16:09:09 CET 2011, abDatum=Fri Dec 31 23:59:59 CET 9999, bisPackEnde=false, verKennung=3581, bemerkung='', stellplan=false, attachedFiles=[], attachedVorgaenge=[], angesetztDurch=Löhr, Torsten [tloehr], abgesetztDurch=null, bewohner=[JH1], massnahme=entity.rest.Massnahmen[massID=140], darreichung=entity.rest.Darreichung[dafID=1336], situation=null, anKH=null, abKH=null, anArzt=entity.rest.GP[arztID=1], abArzt=null}</code></li>
 * <li><img src="http://www.offene-pflege.de/images/stories/opde/medi/prescription-regel1.png" /><p/><code><b>VerordnungPlanung</b>{bhppid=7406, nachtMo=0, morgens=1, mittags=1, nachmittags=0, abends=1, nachtAb=0, uhrzeitDosis=0, uhrzeit=null, maxAnzahl=0, maxEDosis=0, taeglich=1, woechentlich=0, monatlich=0, tagNum=0, mon=0, die=0, mit=0, don=0, fre=0, sam=0, son=0, lDatum=Thu Dec 22 16:12:49 CET 2011, uKennung='tloehr', prescription=Verordnung{verid=4659, ...}}</code></li>
 * <li><img src="http://www.offene-pflege.de/images/stories/opde/medi/prescription-regel2.png" /><p/><code><b>VerordnungPlanung</b>{bhppid=7404, nachtMo=0, morgens=0, mittags=0, nachmittags=0, abends=0, nachtAb=0, uhrzeitDosis=2.5, uhrzeit=Thu Dec 22 22:00:00 CET 2011, maxAnzahl=0, maxEDosis=0, taeglich=0, woechentlich=1, monatlich=0, tagNum=0, mon=0, die=1, mit=0, don=0, fre=0, sam=1, son=0, lDatum=Thu Dec 22 16:10:52 CET 2011, uKennung='tloehr', prescription=Verordnung{verid=4659, ...}}</code></li>
 * <li><img src="http://www.offene-pflege.de/images/stories/opde/medi/prescription-regel3.png" /><p/><code><b>VerordnungPlanung</b>{bhppid=7405, nachtMo=0, morgens=0, mittags=0, nachmittags=0, abends=3, nachtAb=0, uhrzeitDosis=0, uhrzeit=null, maxAnzahl=0, maxEDosis=0, taeglich=0, woechentlich=0, monatlich=2, tagNum=0, mon=0, die=0, mit=0, don=0, fre=0, sam=1, son=0, lDatum=Thu Dec 22 16:11:49 CET 2011, uKennung='tloehr', prescription=Verordnung{verid=4659, ...}}</code></li>
 * </ul>
 *
 * @author tloehr
 */
@Entity
@Table(name = "prescription")
public class Prescription extends DefaultEntity { //  implements Ownable, QElement, Comparable<Prescription>, Attachable
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
    private long relation;
    @Lob
    @Column(name = "Bemerkung")
    private String text;
    @Basic(optional = false)
    @Column(name = "Stellplan")
    private boolean showOnDailyPlan;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "prescription")
    private List<SYSPRE2FILE> attachedFilesConnections;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "prescription")
    private List<SYSPRE2PROCESS> attachedProcessConnections;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "prescription")
    private List<PrescriptionSchedule> pSchedule;
    // these are the annotations for a prescription. currently only used for the MRE studies
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "prescription")
    private Collection<ResInfo> annotations;
    @JoinColumn(name = "AnUKennung", referencedColumnName = "id")
    @ManyToOne
    private OPUsers userON;
    @JoinColumn(name = "AbUKennung", referencedColumnName = "id")
    @ManyToOne
    private OPUsers userOFF;
    @JoinColumn(name = "BWKennung", referencedColumnName = "id")
    @ManyToOne
    private Resident resident;
    @JoinColumn(name = "MassID", referencedColumnName = "id")
    @ManyToOne
    private Intervention intervention;

    @JoinColumn(name = "DafID", referencedColumnName = "id")
    @ManyToOne
    private TradeForm tradeform;
    @JoinColumn(name = "SitID", referencedColumnName = "id")
    @ManyToOne
    private Situations situation;

    @JoinColumn(name = "AnKHID", referencedColumnName = "id")
    @ManyToOne
    private Hospital hospitalON;
    @JoinColumn(name = "AbKHID", referencedColumnName = "id")
    @ManyToOne
    private Hospital hospitalOFF;

    @JoinColumn(name = "AnArztID", referencedColumnName = "id")
    @ManyToOne
    private GP docON;

    @JoinColumn(name = "AbArztID", referencedColumnName = "id")
    @ManyToOne
    private GP docOFF;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "prescription2tags", joinColumns =
    @JoinColumn(name = "prescid"), inverseJoinColumns =
    @JoinColumn(name = "ctagid"))
    private Collection<Commontags> commontags;

    public Prescription() {
    }

    public Prescription(Prescription other) {
        this.from = other.from;
        this.to = other.to;
        this.toEndOfPackage = other.toEndOfPackage;
        this.relation = other.relation;
        this.text = other.text;
        this.showOnDailyPlan = other.showOnDailyPlan;
        this.attachedFilesConnections = other.attachedFilesConnections;
        this.attachedProcessConnections = other.attachedProcessConnections;
        this.pSchedule = other.pSchedule;
        this.annotations = other.annotations;
        this.userON = other.userON;
        this.userOFF = other.userOFF;
        this.resident = other.resident;
        this.intervention = other.intervention;
        this.tradeform = other.tradeform;
        this.situation = other.situation;
        this.hospitalON = other.hospitalON;
        this.hospitalOFF = other.hospitalOFF;
        this.docON = other.docON;
        this.docOFF = other.docOFF;
        this.commontags = other.commontags;
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

    public boolean isToEndOfPackage() {
        return toEndOfPackage;
    }

    public void setToEndOfPackage(boolean toEndOfPackage) {
        this.toEndOfPackage = toEndOfPackage;
    }

    public long getRelation() {
        return relation;
    }

    public void setRelation(long relation) {
        this.relation = relation;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isShowOnDailyPlan() {
        return showOnDailyPlan;
    }

    public void setShowOnDailyPlan(boolean showOnDailyPlan) {
        this.showOnDailyPlan = showOnDailyPlan;
    }

    public List<SYSPRE2FILE> getAttachedFilesConnections() {
        return attachedFilesConnections;
    }

    public void setAttachedFilesConnections(List<SYSPRE2FILE> attachedFilesConnections) {
        this.attachedFilesConnections = attachedFilesConnections;
    }

    public List<SYSPRE2PROCESS> getAttachedProcessConnections() {
        return attachedProcessConnections;
    }

    public void setAttachedProcessConnections(List<SYSPRE2PROCESS> attachedProcessConnections) {
        this.attachedProcessConnections = attachedProcessConnections;
    }

    public List<PrescriptionSchedule> getpSchedule() {
        return pSchedule;
    }

    public void setpSchedule(List<PrescriptionSchedule> pSchedule) {
        this.pSchedule = pSchedule;
    }

    public Collection<ResInfo> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Collection<ResInfo> annotations) {
        this.annotations = annotations;
    }

    public OPUsers getUserON() {
        return userON;
    }

    public void setUserON(OPUsers userON) {
        this.userON = userON;
    }

    public OPUsers getUserOFF() {
        return userOFF;
    }

    public void setUserOFF(OPUsers userOFF) {
        this.userOFF = userOFF;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Intervention getIntervention() {
        return intervention;
    }

    public void setIntervention(Intervention intervention) {
        this.intervention = intervention;
    }

    public TradeForm getTradeform() {
        return tradeform;
    }

    public void setTradeform(TradeForm tradeform) {
        this.tradeform = tradeform;
    }

    public Situations getSituation() {
        return situation;
    }

    public void setSituation(Situations situation) {
        this.situation = situation;
    }

    public Hospital getHospitalON() {
        return hospitalON;
    }

    public void setHospitalON(Hospital hospitalON) {
        this.hospitalON = hospitalON;
    }

    public Hospital getHospitalOFF() {
        return hospitalOFF;
    }

    public void setHospitalOFF(Hospital hospitalOFF) {
        this.hospitalOFF = hospitalOFF;
    }

    public GP getDocON() {
        return docON;
    }

    public void setDocON(GP docON) {
        this.docON = docON;
    }

    public GP getDocOFF() {
        return docOFF;
    }

    public void setDocOFF(GP docOFF) {
        this.docOFF = docOFF;
    }

    public Collection<Commontags> getCommontags() {
        return commontags;
    }

    public void setCommontags(Collection<Commontags> commontags) {
        this.commontags = commontags;
    }
}
