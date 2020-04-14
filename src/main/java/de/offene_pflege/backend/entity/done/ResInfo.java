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
package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.backend.entity.prescription.Prescription;
import de.offene_pflege.backend.entity.process.QElement;
import de.offene_pflege.backend.entity.process.QProcess;
import de.offene_pflege.backend.entity.process.SYSINF2PROCESS;
import de.offene_pflege.backend.entity.system.Commontags;
import de.offene_pflege.backend.entity.system.OPUsers;
import de.offene_pflege.backend.entity.values.ResValue;
import de.offene_pflege.backend.services.ResInfoService;
import de.offene_pflege.backend.services.ResidentTools;
import de.offene_pflege.interfaces.Attachable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "resinfo")
public class ResInfo extends DefaultEntity implements QElement, Comparable<ResInfo>, Attachable {
    private Date from;
    private Date to;
    private String properties;
    private String bemerkung;
    private Long connectionid;
    private ResInfoType resInfoType;
    private OPUsers userON;
    private OPUsers userOFF;
    private Resident resident;
    private ResValue resValue;
    private Prescription prescription;
    private Collection<Commontags> commontags;
    private Collection<SYSINF2FILE> attachedFilesConnections;
    private Collection<SYSINF2PROCESS> attachedProcessConnections;


    public ResInfo() {
    }

    @Basic(optional = false)
    @Column(name = "Von")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    @Basic(optional = false)
    @Column(name = "Bis", length = 6)
    @Temporal(TemporalType.TIMESTAMP)

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    @Lob
    @Column(name = "Properties")

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    @Lob
    @Column(name = "Bemerkung")
    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    /**
     * damit gruppen von ResInfos (immer vom selben ResInfoType), die gemeinsam einen Verlauf beschreiben als
     * zusammengehörig erkannt werden können, erhalten diese gruppen immer eine eindeutige nummer, die sich aus der
     * UNIQUE Tabelle errechnet. conncetionid == 0 heisst, dass ein Zusammenhang nicht benötigt wird. Wird bisher nur
     * bei den Wunden benutzt, und da auch nur bei der QDVS Auswertung.
     */
    @Basic(optional = false)
    @Column(name = "connectionid")
    public Long getConnectionid() {
        return connectionid;
    }

    public void setConnectionid(Long connectionid) {
        this.connectionid = connectionid;
    }

    @JoinColumn(name = "BWINFTYP", referencedColumnName = "id")
    @ManyToOne
    public ResInfoType getResInfoType() {
        return resInfoType;
    }

    public void setResInfoType(ResInfoType resInfoType) {
        this.resInfoType = resInfoType;
    }

    @JoinColumn(name = "AnUKennung", referencedColumnName = "id")
    @ManyToOne
    public OPUsers getUserON() {
        return userON;
    }

    public void setUserON(OPUsers userON) {
        this.userON = userON;
    }

    @JoinColumn(name = "AbUKennung", referencedColumnName = "id")
    @ManyToOne
    public OPUsers getUserOFF() {
        return userOFF;
    }

    public void setUserOFF(OPUsers userOFF) {
        this.userOFF = userOFF;
    }

    @JoinColumn(name = "BWKennung", referencedColumnName = "id")
    @ManyToOne
    @Override
    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    @JoinColumn(name = "resvalueid", referencedColumnName = "id")
    @ManyToOne
    public ResValue getResValue() {
        return resValue;
    }

    public void setResValue(ResValue resValue) {
        this.resValue = resValue;
    }

    @JoinColumn(name = "prescriptionid", referencedColumnName = "id")
    @ManyToOne
    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "resinfo2tags", joinColumns =
    @JoinColumn(name = "resinfoid"), inverseJoinColumns =
    @JoinColumn(name = "ctagid"))
    public Collection<Commontags> getCommontags() {
        return commontags;
    }

    public void setCommontags(Collection<Commontags> commontags) {
        this.commontags = commontags;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resInfo")
    public Collection<SYSINF2FILE> getAttachedFilesConnections() {
        return attachedFilesConnections;
    }

    public void setAttachedFilesConnections(Collection<SYSINF2FILE> attachedFilesConnections) {
        this.attachedFilesConnections = attachedFilesConnections;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bwinfo")
    public Collection<SYSINF2PROCESS> getAttachedProcessConnections() {
        return attachedProcessConnections;
    }

    public void setAttachedProcessConnections(Collection<SYSINF2PROCESS> attachedProcessConnections) {
        this.attachedProcessConnections = attachedProcessConnections;
    }

    @Override
    public int compareTo(ResInfo resInfo) {
        int compare = this.getResInfoType().isDeprecated().compareTo(resInfo.getResInfoType().isDeprecated()) * -1;
        if (compare == 0) {
            compare = to.compareTo(resInfo.getTo());
        }
        if (compare == 0) {
            compare = from.compareTo(resInfo.getFrom());
        }
        return compare * -1;
    }

    @Override
    public long pitInMillis() {
        return from.getTime();
    }

    @Override
    public ArrayList<QProcess> findAttachedProcesses() {
        ArrayList<QProcess> list = new ArrayList<>();
        for (SYSINF2PROCESS att : attachedProcessConnections) {
            list.add(att.getQProcess());
        }
        return list;
    }

    @Override
    public String contentAsHTML() {
        return ResInfoService.getContentAsHTML(this);
    }

    @Override
    public String titleAsString() {
        return null;
    }

    @Override
    public String pitAsHTML() {
        return ResInfoService.getPITAsHTML(this);
    }

    @Override
    public OPUsers findOwner() {
        return null;
    }


    @Override
    public boolean active() {
        return ResidentTools.isActive(resident) && !ResInfoService.isClosed(this);
    }


    @Override
    public long findPrimaryKey() {
        return getId();
    }
}
