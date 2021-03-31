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

import de.offene_pflege.entity.Allowance;
import de.offene_pflege.entity.DefaultStringIDEntity;
import de.offene_pflege.entity.building.Station;
import de.offene_pflege.entity.files.Resident2File;
import de.offene_pflege.entity.prescription.GP;
import de.offene_pflege.entity.system.OPUsers;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "resident")
public class Resident extends DefaultStringIDEntity implements Serializable, Comparable<Resident> {

    private String name;
    private String firstname;
    private int gender;
    private Date dob;
    private short adminonly;
    private String controlling;
    private Boolean calcMediUPR1;
    private Station station;
    private GP gp;
    private OPUsers pn1;
    private OPUsers pn2;
    private OPUsers editor;
    private Boolean sterbePhase;
    private long idbewohner;
    private Collection<Allowance> allowance;
    private Collection<ResInfo> resInfoCollection;
    private Collection<Resident2File> attachedFilesConnections;

    @Basic(optional = false)
    @Column(name = "idbewohner", unique = true)
    public long getIdbewohner() {
        return idbewohner;
    }

    public void setIdbewohner(long idbewohner) {
        this.idbewohner = idbewohner;
    }

    @Basic(optional = false)
    @Column(name = "sterbephase")
    public Boolean getSterbePhase() {
        return sterbePhase;
    }

    public void setSterbePhase(Boolean sterbePhase) {
        this.sterbePhase = sterbePhase;
    }

    @Column(name = "Nachname")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic(optional = false)
    @Column(name = "Vorname")
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Basic(optional = false)
    @Column(name = "Geschlecht")
    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Basic(optional = false)
    @Column(name = "GebDatum")
    @Temporal(TemporalType.DATE)
    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    @Basic(optional = false)
    @Column(name = "adminonly")
    public short getAdminonly() {
        return adminonly;
    }

    public void setAdminonly(short adminonly) {
        this.adminonly = adminonly;
    }

    @Basic(optional = false)
    @Column(name = "controlling")
    public String getControlling() {
        return controlling;
    }

    public void setControlling(String controlling) {
        this.controlling = controlling;
    }

    @Basic(optional = false)
    @Column(name = "calcmedi")
    public Boolean getCalcMediUPR1() {
        return calcMediUPR1;
    }

    public void setCalcMediUPR1(Boolean calcMediUPR1) {
        this.calcMediUPR1 = calcMediUPR1;
    }

    @JoinColumn(name = "StatID", referencedColumnName = "id")
    @ManyToOne
    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    @JoinColumn(name = "BV1UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    public OPUsers getPn1() {
        return pn1;
    }

    public void setPn1(OPUsers pn1) {
        this.pn1 = pn1;
    }

    @JoinColumn(name = "BV2UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    public OPUsers getPn2() {
        return pn2;
    }

    public void setPn2(OPUsers pn2) {
        this.pn2 = pn2;
    }

    @JoinColumn(name = "ArztID", referencedColumnName = "ArztID")
    @ManyToOne
    public GP getGp() {
        return gp;
    }

    public void setGp(GP gp) {
        this.gp = gp;
    }

    @JoinColumn(name = "Editor", referencedColumnName = "UKennung")
    @ManyToOne
    public OPUsers getEditor() {
        return editor;
    }

    public void setEditor(OPUsers editor) {
        this.editor = editor;
    }

    @Override
    public int compareTo(Resident o) {
        return toString().compareTo(o.toString());
    }


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "resident")
    public Collection<Allowance> getAllowance() {
        return allowance;
    }

    public void setAllowance(Collection<Allowance> allowance) {
        this.allowance = allowance;
    }


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resident")
    public Collection<ResInfo> getResInfoCollection() {
        return resInfoCollection;
    }

    public void setResInfoCollection(Collection<ResInfo> resInfoCollection) {
        this.resInfoCollection = resInfoCollection;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resident")
    public Collection<Resident2File> getAttachedFilesConnections() {
        return attachedFilesConnections;
    }

    public void setAttachedFilesConnections(Collection<Resident2File> attachedFilesConnections) {
        this.attachedFilesConnections = attachedFilesConnections;
    }

    public Resident() {
    }

    @Override
    public String toString() {
        return getName() + ", " + getFirstname() + " [" + getId() + "]";
    }
}
