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
package de.offene_pflege.entity.info;

import de.offene_pflege.entity.files.SYSINF2FILE;
import de.offene_pflege.entity.prescription.Prescription;
import de.offene_pflege.entity.process.QProcess;
import de.offene_pflege.entity.process.QProcessElement;
import de.offene_pflege.entity.process.SYSINF2PROCESS;
import de.offene_pflege.entity.system.Commontags;
import de.offene_pflege.entity.system.OPUsers;
import de.offene_pflege.entity.values.ResValue;
import de.offene_pflege.interfaces.Attachable;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

/**
 * @author tloehr
 */
@Entity
@Table(name = "resinfo")
public class ResInfo implements Serializable, QProcessElement, Comparable<ResInfo>, Attachable {
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
    @Column(name = "Bis",length = 6)
    @Temporal(TemporalType.TIMESTAMP)
    private Date to;
//    @Lob
//    @Column(name = "HTML")
//    private String html;
    @Lob
    @Column(name = "Properties")
    private String properties;
    @Lob
    @Column(name = "Bemerkung")
    private String bemerkung;
    /**
     * damit gruppen von ResInfos (immer vom selben ResInfoType), die gemeinsam einen Verlauf beschreiben als
     * zusammengehörig erkannt werden können, erhalten diese gruppen immer eine eindeutige nummer, die sich aus der
     * UNIQUE Tabelle errechnet. conncetionid == 0 heisst, dass ein Zusammenhang nicht benötigt wird. Wird bisher nur
     * bei den Wunden benutzt, und da auch nur bei der QDVS Auswertung.
     */
    @Basic(optional = false)
    @Column(name = "connectionid")
    private Long connectionid;
    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "BWINFTYP", referencedColumnName = "BWINFTYP")
    @ManyToOne
    private ResInfoType bwinfotyp;
    @JoinColumn(name = "AnUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private OPUsers userON;
    @JoinColumn(name = "AbUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private OPUsers userOFF;
    @JoinColumn(name = "BWKennung", referencedColumnName = "id")
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
        connectionid = 0l;
    }


    @Override
    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public ResInfoType getResInfoType() {
        return bwinfotyp;
    }

    public void setResInfoType(ResInfoType resInfoType) {
        bwinfotyp = resInfoType;
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

    public Collection<SYSINF2PROCESS> getAttachedQProcessConnections() {
        return attachedProcessConnections;
    }

    public Long getConnectionid() {
        return connectionid;
    }

    public void setConnectionid(Long connectionid) {
        this.connectionid = connectionid;
    }

    public void setCommontags(Collection<Commontags> commontags) {
        this.commontags = commontags;
    }

    public void setAttachedFilesConnections(Collection<SYSINF2FILE> attachedFilesConnections) {
        this.attachedFilesConnections = attachedFilesConnections;
    }

    public void setAttachedProcessConnections(Collection<SYSINF2PROCESS> attachedProcessConnections) {
        this.attachedProcessConnections = attachedProcessConnections;
    }

//    public String getHtml() {
//        return html;
//    }

//    public void setHtml(String html) {
//        this.html = html;
//    }

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

    public OPUsers getUserOFF() {
        return userOFF;
    }

    public void setUserOFF(OPUsers user) {
        this.userOFF = user;
    }

    public OPUsers getUserON() {
        return userON;
    }

    public void setUserON(OPUsers user) {
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
    public OPUsers getUser() {
        return userON;
    }

    @Override
    public String getContentAsHTML() {
        return ResInfoTools.getContentAsHTML(this);
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
            result += "<td valign=\"top\">" + userON.getFullname() + getID() + "</td>";
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
        int compare = new Boolean(this.getResInfoType().isDeprecated()).compareTo(resInfo.getResInfoType().isDeprecated()) * -1;
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
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        result = 31 * result + (bemerkung != null ? bemerkung.hashCode() : 0);
        result = 31 * result + (bwinfotyp != null ? bwinfotyp.hashCode() : 0);
        result = 31 * result + (userON != null ? userON.hashCode() : 0);
        result = 31 * result + (userOFF != null ? userOFF.hashCode() : 0);
        result = 31 * result + (resident != null ? resident.hashCode() : 0);
        return result;
    }

//    @Override
//    public ResInfo clone() {
//        return new ResInfo(from, to, html, properties, bemerkung, bwinfotyp, resident, userON, userOFF, prescription);
//    }

    @Override
    public String toString() {
        return "ResInfo{" +
                ", bwinfotyp=" + bwinfotyp.getID() +
                ", from=" + SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.GERMANY).format(from) +
                ", to=" + SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.GERMANY).format(to) +
                '}';
    }

    @Override
    public boolean isActive() {
        return ResidentTools.isActive(resident) && !isClosed();
    }


}
