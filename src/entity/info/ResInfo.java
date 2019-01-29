/*
 * OffenePflege
 * Copyright (C) 2011 Torsten Löhr
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License V2 as published by the Free Software Foundation
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------
 * Auf deutsch (freie Übersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License,
 * wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, gemäß Version 2 der Lizenz.
 *
 * Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von Nutzen sein wird, aber
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht,
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 */
package entity.info;

import entity.files.SYSINF2FILE;
import entity.prescription.Prescription;
import entity.process.QProcess;
import entity.process.QProcessElement;
import entity.process.SYSINF2PROCESS;
import entity.system.Commontags;
import entity.system.Users;
import entity.values.ResValue;
import interfaces.Attachable;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * @author tloehr
 */
@Entity
@Table(name = "resinfo")
public class ResInfo implements Serializable, QProcessElement, Cloneable, Comparable<ResInfo>, Attachable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BWINFOID")
    private Long bwinfoid;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "Von")
    @Temporal(TemporalType.TIMESTAMP)
    private Date from;
    @Basic(optional = false)
    @Column(name = "Bis")
    @Temporal(TemporalType.TIMESTAMP)
    private Date to;
    @Lob
    @Column(name = "HTML")
    private String html;
    @Lob
    @Column(name = "Properties")
    private String properties;
    @Lob
    @Column(name = "Bemerkung")
    private String bemerkung;
    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "BWINFTYP", referencedColumnName = "BWINFTYP")
    @ManyToOne
    private ResInfoType bwinfotyp;
    @JoinColumn(name = "AnUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users userON;
    @JoinColumn(name = "AbUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users userOFF;
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;
    @JoinColumn(name = "resvalueid", referencedColumnName = "BWID")
    @ManyToOne
    private ResValue resValue;
    @JoinColumn(name = "prescriptionid", referencedColumnName = "verid")
    @ManyToOne
    private Prescription prescription;

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    // ==
    // M:N Relationen
    // ==
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "resinfo2tags", joinColumns =
    @JoinColumn(name = "resinfoid"), inverseJoinColumns =
    @JoinColumn(name = "ctagid"))
    private Collection<Commontags> commontags;

    public Collection<Commontags> getCommontags() {
        return commontags;
    }

    // ==
    // 1:N Relations
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bwinfo")
    private Collection<SYSINF2FILE> attachedFilesConnections;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bwinfo")
    private Collection<SYSINF2PROCESS> attachedProcessConnections;


    public ResInfo() {
    }

    public ResInfo(ResInfoType resInfoType, Resident resident) {

        Date now = new Date();

        if (resInfoType.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
            this.from = now;
            this.to = now;
        } else if (resInfoType.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_BYDAY) {
            this.from = new LocalDate().toDateTimeAtStartOfDay().toDate();
            this.to = SYSConst.DATE_UNTIL_FURTHER_NOTICE;
        } else {
            this.from = now;
            this.to = SYSConst.DATE_UNTIL_FURTHER_NOTICE;
        }

        this.properties = "";
        if (resInfoType.getType() == ResInfoTypeTools.TYPE_STAY) {
            this.properties = ResInfoTypeTools.STAY_KEY + "=";
            this.html = "<ul></ul>";
        }

        this.bwinfotyp = resInfoType;
        this.userON = OPDE.getLogin().getUser();
        this.resident = resident;
        this.attachedFilesConnections = new ArrayList<SYSINF2FILE>();
        this.attachedProcessConnections = new ArrayList<SYSINF2PROCESS>();
        this.resValue = null;
        this.commontags = new HashSet<>();
    }

    private ResInfo(Date from, Date to, String html, String properties, String bemerkung, ResInfoType bwinfotyp, Resident resident, Users userON, Users userOFF, Prescription prescription) {
        this.from = from;
        this.to = to;
        this.html = html;
        this.properties = properties;
        this.bemerkung = bemerkung;
        this.bwinfotyp = bwinfotyp;
        this.userON = userON;
        this.userOFF = userOFF;
        this.resident = resident;
        this.resValue = null;
        this.attachedFilesConnections = new ArrayList<SYSINF2FILE>();
        this.attachedProcessConnections = new ArrayList<SYSINF2PROCESS>();
        this.commontags = new HashSet<>();
        this.prescription = prescription;
    }

    @Override
    public Resident getResident() {
        return resident;
    }

    public ResInfoType getResInfoType() {
        return bwinfotyp;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        if (bwinfotyp.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_BYDAY) {
            from = new LocalDate(from).toDateTimeAtStartOfDay().toDate();
        }
        this.from = from;
        if (bwinfotyp.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
            this.to = from;
        }
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        if (bwinfotyp.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_BYDAY) {
            to = SYSCalendar.eod(new LocalDate(to)).toDate();
        }
        this.to = to;
        if (bwinfotyp.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
            this.from = to;
        }
    }

    public Collection<SYSINF2PROCESS> getAttachedQProcessConnections() {
        return attachedProcessConnections;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getText() {
        return bemerkung;
    }

    public void setText(String text) {
        this.bemerkung = text;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public Users getUserOFF() {
        return userOFF;
    }

    public void setUserOFF(Users user) {
        this.userOFF = user;
    }

    public Users getUserON() {
        return userON;
    }

    public void setUserON(Users user) {
        this.userON = user;
    }

    public boolean isCurrentlyValid() {
        Date now = new Date();
        return from.compareTo(now) <= 0 && to.compareTo(now) >= 0;
    }

    public Collection<SYSINF2FILE> getAttachedFilesConnections() {
        return attachedFilesConnections;
    }

    public Collection<SYSINF2PROCESS> getAttachedVorgaenge() {
        return attachedProcessConnections;
    }

    @Override
    public long getPITInMillis() {
        return from.getTime();
    }

    /**
     * SingleIncidents können nicht abgesetzt sein. Ansonsten, dann, wenn bis vor dem aktuellen Zeitpunkt liegt.
     *
     * @return
     */
    public boolean isClosed() {
        return bwinfotyp.getIntervalMode() != ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS && to.before(new Date());
    }

    public boolean isBySecond() {
        return bwinfotyp.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_BYSECOND;
    }

    public boolean isByDay() {
        return bwinfotyp.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_BYDAY;
    }

    public boolean isSingleIncident() {
        return bwinfotyp.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS;
    }

    public ResValue getResValue() {
        return resValue;
    }

    public void setResValue(ResValue resValue) {
        this.resValue = resValue;
    }

    public boolean isNoConstraints() {
        return bwinfotyp.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_NOCONSTRAINTS;
    }

    public boolean isActiveNoConstraint() {
        return isNoConstraints() && !isClosed();
    }

    @Override
    public Users getUser() {
        return userON;
    }

    @Override
    public String getContentAsHTML() {
        return html;
    }

    @Override
    public String getPITAsHTML() {
        String result = "";
        DateFormat df = isSingleIncident() || isBySecond() ? DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT) : DateFormat.getDateInstance();

        if (isSingleIncident()) {
            result += df.format(from) +
                    "<br/>" +
                    userON.getFullname();
        } else if (isClosed()) {

            result += "<table id=\"fonttext\" border=\"0\" cellspacing=\"0\">";
            result += "<tr>";
            result += "<td valign=\"top\">" + df.format(from) + "</td>";
            result += "<td valign=\"top\">&raquo;</td>";
            result += "<td valign=\"top\">" + df.format(to) + "</td>";
            result += "</tr>\n";
            result += "<tr>";
            result += "<td valign=\"top\">" + userON.getFullname() + "</td>";
            result += "<td valign=\"top\">&raquo;</td>";
            result += "<td valign=\"top\">" + userOFF.getFullname() + "</td>";
            result += "</tr>\n";
            result += "</table>\n";

        } else {
            result += df.format(from) + "&nbsp;&raquo;&raquo;" +
                    "<br/>" +
                    userON.getFullname();
        }

        return result;
    }

    @Override
    public ArrayList<QProcess> getAttachedProcesses() {
        ArrayList<QProcess> list = new ArrayList<QProcess>();
        for (SYSINF2PROCESS att : attachedProcessConnections) {
            list.add(att.getQProcess());
        }
        return list;
    }


    @Override
    public long getID() {
        return bwinfoid == null ? 0 : bwinfoid.longValue();
    }


    @Override
    public int compareTo(ResInfo resInfo) {
        int compare = new Boolean(this.getResInfoType().isObsolete()).compareTo(resInfo.getResInfoType().isObsolete()) * -1;
        if (compare == 0) {
            compare = to.compareTo(resInfo.getTo());
        }
        if (compare == 0) {
            compare = from.compareTo(resInfo.getFrom());
        }
        return compare * -1;
//        if (resInfo.getResInfoType().getType() == ResInfoTypeTools.STATUS_NORMAL) {
//            return 0;
//        } else if (getResInfoType().getID().equalsIgnoreCase(ResInfoTypeTools.TYPE_DIAGNOSIS) || resInfo.getResInfoType().getID().equalsIgnoreCase(ResInfoTypeTools.TYPE_DIAGNOSIS)) {
//            Properties thisProps = ResInfoTools.getData(this);
//            Properties thatProps = ResInfoTools.getData(resInfo);
//            String thisICD = thisProps.getProperty("icd");
//            String thatICD = thatProps.getProperty("icd");
//            return thisICD.compareTo(thatICD);
//        } else {
//            return 0;
//        }
    }

    @Override
    public String getTitle() {
        return SYSTools.xx("nursingrecords.info") + ": " + bwinfotyp.getShortDescription();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResInfo resInfo = (ResInfo) o;

        if (bwinfoid != null && resInfo.bwinfoid != null) return bwinfoid.longValue() == resInfo.bwinfoid.longValue();

        if (bemerkung != null ? !bemerkung.equals(resInfo.bemerkung) : resInfo.bemerkung != null) return false;
        if (bwinfoid != null ? !bwinfoid.equals(resInfo.bwinfoid) : resInfo.bwinfoid != null) return false;
        if (bwinfotyp != null ? !bwinfotyp.equals(resInfo.bwinfotyp) : resInfo.bwinfotyp != null) return false;
        if (from != null ? !from.equals(resInfo.from) : resInfo.from != null) return false;
        if (html != null ? !html.equals(resInfo.html) : resInfo.html != null) return false;
        if (properties != null ? !properties.equals(resInfo.properties) : resInfo.properties != null) return false;
        if (resident != null ? !resident.equals(resInfo.resident) : resInfo.resident != null) return false;
        if (to != null ? !to.equals(resInfo.to) : resInfo.to != null) return false;
        if (userOFF != null ? !userOFF.equals(resInfo.userOFF) : resInfo.userOFF != null) return false;
        if (userON != null ? !userON.equals(resInfo.userON) : resInfo.userON != null) return false;
        if (version != null ? !version.equals(resInfo.version) : resInfo.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bwinfoid != null ? bwinfoid.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (html != null ? html.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        result = 31 * result + (bemerkung != null ? bemerkung.hashCode() : 0);
        result = 31 * result + (bwinfotyp != null ? bwinfotyp.hashCode() : 0);
        result = 31 * result + (userON != null ? userON.hashCode() : 0);
        result = 31 * result + (userOFF != null ? userOFF.hashCode() : 0);
        result = 31 * result + (resident != null ? resident.hashCode() : 0);
        return result;
    }

    @Override
    public ResInfo clone() {
        return new ResInfo(from, to, html, properties, bemerkung, bwinfotyp, resident, userON, userOFF, prescription);
    }

    @Override
    public String toString() {
        return "ResInfo{" +
                "bwinfoid=" + bwinfoid +
                ", version=" + version +
                ", from=" + from +
                ", to=" + to +
                ", bemerkung='" + bemerkung + '\'' +
                ", bwinfotyp=" + bwinfotyp.getID() +
                ", userON=" + userON +
                ", userOFF=" + userOFF +
                ", resident=" + resident +
                ", attachedFilesConnections=" + attachedFilesConnections.size() +
                ", attachedProcessConnections=" + attachedProcessConnections.size() +
                '}';
    }

    @Override
    public boolean isActive() {
        return resident.isActive() && !isClosed();
    }
}
