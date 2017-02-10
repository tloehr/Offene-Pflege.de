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

import entity.Allowance;
import entity.building.Station;
import entity.files.Resident2File;
import entity.prescription.GP;
import entity.system.Users;
import op.OPDE;
import op.tools.SYSTools;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

/**
 * @author tloehr
 */
@Entity
@Table(name = "resident")
public class Resident implements Serializable, Comparable<Resident> {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "BWKennung")
    private String rid;
    @Basic(optional = false)
    @Column(name = "Nachname")
    private String name;
    @Basic(optional = false)
    @Column(name = "Vorname")
    private String firstname;
    @Basic(optional = false)
    @Column(name = "Geschlecht")
    private int gender;
    @Basic(optional = false)
    @Column(name = "GebDatum")
    @Temporal(TemporalType.DATE)
    private Date dob;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "adminonly")
    private short adminonly;
    @Basic(optional = false)
    @Column(name = "controlling")
    private String controlling;
    @Basic(optional = false)
    @Column(name = "calcmedi")
    private Boolean calcMediUPR1;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "resident")
    private Collection<Allowance> allowance;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resident")
    private Collection<ResInfo> resInfoCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resident")
    private Collection<Resident2File> attachedFilesConnections;

    @JoinColumn(name = "StatID", referencedColumnName = "StatID")
    @ManyToOne
    private Station station;
    @JoinColumn(name = "BV1UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users pn1;
    @JoinColumn(name = "BV2UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users pn2;
    @JoinColumn(name = "ArztID", referencedColumnName = "ArztID")
    @ManyToOne
    private GP gp;
    @JoinColumn(name = "Editor", referencedColumnName = "UKennung")
    @ManyToOne
    private Users editor;


    public Resident() {
    }

    public Resident(String name, String firstname, int gender, Date dob) {
        this.rid = null;
        this.name = name;
        this.firstname = firstname;
        this.gender = gender;
        this.dob = dob;
        this.editor = OPDE.getLogin().getUser();
        this.adminonly = 0;
        this.controlling = null;
        this.calcMediUPR1 = OPDE.isCalcMediUPR1();
    }

    public Collection<Resident2File> getAttachedFilesConnections() {
        return attachedFilesConnections;
    }

    public String getRIDAnonymous() {
        return SYSTools.anonymizeRID(rid);
    }

    public String getRID() {
        return rid;
    }

    public Boolean isCalcMediUPR1() {
        return calcMediUPR1;
    }

    public void setCalcMediUPR1(Boolean calcMedi) {
        this.calcMediUPR1 = calcMedi;
    }

    public void setRID(String rid) {
        if (this.rid == null) {
            this.rid = rid;
        }
    }

    public String getNameNeverAnonymous() {
        return name;
    }

//    public vs getRoom() {
//        return room;
//    }
//
//    public void setRoom(Rooms room) {
//        this.room = room;
//    }

    public String getName() {
        return SYSTools.anonymizeName(name, SYSTools.INDEX_LASTNAME);
    }

    public void setControlling(Properties props) {
        try {
            StringWriter writer = new StringWriter();
            props.store(writer, null);
            controlling = writer.toString();
            writer.close();
        } catch (IOException ex) {
            OPDE.fatal(ex);
        }
    }

    public Properties getControlling() {
        Properties props = new Properties();
        if (controlling != null) {
            try {
                StringReader reader = new StringReader(controlling);
                props.load(reader);
                reader.close();
            } catch (IOException ex) {
                OPDE.fatal(ex);
            }
        }
        return props;
    }

    public void setName(String nachname) {
        this.name = nachname;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    /**
     * Residents that have no assigned stations are considered inactive, hence not present in the home anymore
     *
     * @return
     */
    public boolean isActive() {
        return station != null;
    }

    public String getFirstname() {
        int index = (gender == ResidentTools.MALE ? SYSTools.INDEX_FIRSTNAME_MALE : SYSTools.INDEX_FIRSTNAME_FEMALE);
        return SYSTools.anonymizeName(firstname, index);
    }

    public String getFirstnameNeverAnonymous() {
        return firstname;
    }

    public void setFirstname(String vorname) {
        this.firstname = vorname;
    }

    public Users getEditor() {
        return editor;
    }

    public void setEditor(Users editor) {
        this.editor = editor;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int geschlecht) {
        this.gender = geschlecht;
    }

    public Date getDOB() {
        return SYSTools.anonymizeDate(dob);
    }

    public void setDOB(Date gebDatum) {
        this.dob = gebDatum;
    }

    public short getAdminonly() {
        return adminonly;
    }

    public void setAdminonly(short adminonly) {
        this.adminonly = adminonly;
    }

    public Collection<Allowance> getKonto() {
        return allowance;
    }

    public Users getPN1() {
        return pn1;
    }

    public void setPN1(Users bv1) {
        this.pn1 = bv1;
    }

    public Users getPN2() {
        return pn2;
    }

    public void setPN2(Users bv2) {
        this.pn2 = bv2;
    }

    public GP getGP() {
        return gp;
    }

    public void setGP(GP gp) {
        this.gp = gp;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rid != null ? rid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Resident)) {
            return false;
        }
        Resident other = (Resident) object;
        if ((this.rid == null && other.rid != null) || (this.rid != null && !this.rid.equals(other.rid))) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Resident o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public String toString() {
        return getName() + ", " + getFirstname() + " [" + rid + "]";
    }
}
