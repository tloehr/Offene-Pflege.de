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
package de.offene_pflege.backend.entity.info;

import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.backend.entity.done.Resident;
import de.offene_pflege.backend.entity.done.SYSINF2FILE;
import de.offene_pflege.backend.entity.prescription.Prescription;
import de.offene_pflege.backend.entity.process.QProcessElement;
import de.offene_pflege.backend.entity.process.SYSINF2PROCESS;
import de.offene_pflege.backend.entity.system.Commontags;
import de.offene_pflege.backend.entity.system.OPUsers;
import de.offene_pflege.backend.entity.values.ResValue;
import de.offene_pflege.backend.services.ResInfoService;
import de.offene_pflege.backend.services.ResInfoTypeTools;
import de.offene_pflege.backend.services.ResidentTools;
import de.offene_pflege.interfaces.Attachable;

import javax.persistence.*;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "resinfo")
public class ResInfo extends DefaultEntity implements QProcessElement, Comparable<ResInfo>, Attachable {


    @Basic(optional = false)
    @Column(name = "Von")
    @Temporal(TemporalType.TIMESTAMP)
    private Date from;
    @Basic(optional = false)
    @Column(name = "Bis",length = 6)
    @Temporal(TemporalType.TIMESTAMP)
    private Date to;

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
    @JoinColumn(name = "BWINFTYP", referencedColumnName = "id")
    @ManyToOne
    private ResInfoType resInfoType;
    @JoinColumn(name = "AnUKennung", referencedColumnName = "id")
    @ManyToOne
    private OPUsers userON;
    @JoinColumn(name = "AbUKennung", referencedColumnName = "id")
    @ManyToOne
    private OPUsers userOFF;
    @JoinColumn(name = "BWKennung", referencedColumnName = "id")
    @ManyToOne
    private Resident resident;
    @JoinColumn(name = "resvalueid", referencedColumnName = "id")
    @ManyToOne
    private ResValue resValue;
    @JoinColumn(name = "prescriptionid", referencedColumnName = "id")
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "id")
    private Collection<SYSINF2FILE> attachedFilesConnections;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bwinfo")
    private Collection<SYSINF2PROCESS> attachedProcessConnections;


    public ResInfo() {
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

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Long getConnectionid() {
        return connectionid;
    }

    public void setConnectionid(Long connectionid) {
        this.connectionid = connectionid;
    }

    public ResInfoType getResInfoType() {
        return resInfoType;
    }

    public void setResInfoType(ResInfoType resInfoType) {
        this.resInfoType = resInfoType;
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

    @Override
    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public ResValue getResValue() {
        return resValue;
    }

    public void setResValue(ResValue resValue) {
        this.resValue = resValue;
    }

    public void setCommontags(Collection<Commontags> commontags) {
        this.commontags = commontags;
    }

    public Collection<SYSINF2FILE> getAttachedFilesConnections() {
        return attachedFilesConnections;
    }

    public void setAttachedFilesConnections(Collection<SYSINF2FILE> attachedFilesConnections) {
        this.attachedFilesConnections = attachedFilesConnections;
    }

    public Collection<SYSINF2PROCESS> getAttachedProcessConnections() {
        return attachedProcessConnections;
    }

    public void setAttachedProcessConnections(Collection<SYSINF2PROCESS> attachedProcessConnections) {
        this.attachedProcessConnections = attachedProcessConnections;
    }

    @Override
    public int compareTo(ResInfo resInfo) {
        int compare =  this.getResInfoType().isDeprecated().compareTo(resInfo.getResInfoType().isDeprecated()) * -1;
        if (compare == 0) {
            compare = to.compareTo(resInfo.getTo());
        }
        if (compare == 0) {
            compare = from.compareTo(resInfo.getFrom());
        }
        return compare * -1;
    }

    @Override
    public String getContentAsHTML() {
        return ResInfoService.getContentAsHTML(this);
    }

    @Override
    public String getPITAsHTML() {
        return ResInfoService.getPITAsHTML(this);
    } ^



    @Override
    public boolean isActive() {
        return ResidentTools.isActive(resident) && !ResInfoService.isClosed(this);
    }


}
