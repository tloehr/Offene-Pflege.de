/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.backend.entity.prescription;

import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.backend.entity.Ownable;
import de.offene_pflege.backend.entity.done.*;
import de.offene_pflege.backend.services.*;
import de.offene_pflege.backend.entity.process.QProcess;
import de.offene_pflege.backend.entity.process.QElement;
import de.offene_pflege.backend.entity.process.SYSPRE2PROCESS;
import de.offene_pflege.backend.entity.system.Commontags;
import de.offene_pflege.backend.entity.system.OPUsers;
import de.offene_pflege.interfaces.Attachable;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSCalendar;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.OptimisticLockingType;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
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
@OptimisticLocking(cascade = false, type = OptimisticLockingType.VERSION_COLUMN)
public class Prescription extends DefaultEntity implements Ownable, QElement, Comparable<Prescription>, Attachable {


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

    @JoinColumn(name = "AnUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private OPUsers userON;
    @JoinColumn(name = "AbUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private OPUsers userOFF;
    @JoinColumn(name = "BWKennung", referencedColumnName = "id")
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
    private GP docON;
    @JoinColumn(name = "AbArztID", referencedColumnName = "ArztID")
    @ManyToOne
    private GP docOFF;


    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "prescription2tags", joinColumns =
    @JoinColumn(name = "prescid"), inverseJoinColumns =
    @JoinColumn(name = "ctagid"))
    private Collection<Commontags> commontags;


    public Prescription() {
    }


    public String getText() {
        return SYSTools.catchNull(text);
    }

    public void setText(String bemerkung) {
        this.text = SYSTools.tidy(bemerkung);
    }

    public boolean hasMed() {
        return tradeform != null;
    }

    public boolean shouldBeCalculated() {
        // TODO: distinction between the several UPR modes
        return hasMed() && resident.getCalcMediUPR1();
    }

    public boolean isWeightControlled() {
        return hasMed() && tradeform.isWeightControlled();
    }

    @Override
    public OPUsers getOwner() {
        return userON;
    }

    @Override
    public String titleAsString() {
        return SYSTools.xx("nursingrecords.prescription") + ": " + PrescriptionService.getShortDescriptionAsCompactText(this);
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public boolean isClosed() {
        return new DateTime(to).isBeforeNow();
    }

    public boolean isActiveOn(LocalDate day) {
        return new Interval(new DateTime(from), new DateTime(to)).overlaps(new Interval(day.toDateTimeAtStartOfDay(), SYSCalendar.eod(day)));
    }

    public boolean isLimited() {
        return to.before(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
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
    public OPUsers findOwner() {
        return userON;
    }

    @Override
    public long pitInMillis() {
        return from.getTime();
    }

    @Override
    public ArrayList<QProcess> findAttachedProcesses() {
        ArrayList<QProcess> list = new ArrayList<QProcess>();
        for (SYSPRE2PROCESS att : attachedProcessConnections) {
            list.add(att.getQProcess());
        }
        return list;
    }


    @Override
    public String contentAsHTML() {
        return PrescriptionService.getPrescriptionAsHTML(this, false, false, true, false);
    }

    @Override
    public String pitAsHTML() {
        String result = "";
        DateFormat df = DateFormat.getDateInstance();

        if (isClosed()) {

            result += "<table id=\"fonttext\" border=\"0\" cellspacing=\"0\">";
            result += "<tr>";
            result += "<td valign=\"top\"><b>" + df.format(from) + "</b></td>";
            result += "<td valign=\"top\">&raquo;</td>";
            result += "<td valign=\"top\"><b>" + df.format(to) + "</b></td>";
            result += "</tr>\n";
            result += "<tr>";
            result += "<td valign=\"top\">" + userON.getFullname() + "</td>";
            result += "<td valign=\"top\">&raquo;</td>";
            result += "<td valign=\"top\">" + userOFF.getFullname() + "</td>";
            result += "</tr>\n";
            if (docON != null || docOFF != null) {
                result += "<tr>";
                result += "<td valign=\"top\">" + GPService.getFullName(docON) + "</td>";
                result += "<td valign=\"top\">&raquo;</td>";
                result += "<td valign=\"top\">" + GPService.getFullName(docOFF) + "</td>";
                result += "</tr>\n";
            }
            if (hospitalON != null || hospitalOFF != null) {
                result += "<tr>";
                result += "<td valign=\"top\">" + HospitalService.getFullName(hospitalON) + "</td>";
                result += "<td valign=\"top\">&raquo;</td>";
                result += "<td valign=\"top\">" + HospitalService.getFullName(hospitalOFF) + "</td>";
                result += "</tr>\n";
            }
            result += "</table>\n";

        } else {

            if (to.before(SYSConst.DATE_UNTIL_FURTHER_NOTICE)) {
                result += SYSConst.html_bold(df.format(from)) + "&nbsp;&raquo;&nbsp;" + SYSConst.html_bold(df.format(to));
            } else {
                result += SYSConst.html_bold(df.format(from)) + "&nbsp;&raquo;&raquo;";
            }

            result += "<br/>" + userON.getFullname();
            if (docON != null) {
                result += "<br/>";
                result += GPService.getFullName(docON);
            }
            if (hospitalON != null) {
                result += "<br/>";
                result += HospitalService.getFullName(hospitalON);
            }
        }

        result += "<br>" + "[" + id + "]";

        return result;
    }







    @Override
    public boolean isActive() {
        return ResidentTools.isActive(resident) && !isClosed();
    }
}
