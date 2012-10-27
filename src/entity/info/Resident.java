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
import entity.Station;
import entity.prescription.Doc;
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
@Table(name = "Bewohner")
//@NamedQueries({
//        @NamedQuery(name = "Resident.findAll", query = "SELECT b FROM Resident b"),
//        @NamedQuery(name = "Resident.findAllActiveSorted", query = " SELECT b FROM Resident b WHERE b.station IS NOT NULL ORDER BY b.nachname, b.vorname "),
//        @NamedQuery(name = "Resident.findAllActiveSortedByStationen", query = "SELECT b FROM Resident b WHERE b.station IS NOT NULL ORDER BY b.station.bezeichnung, b.nachname, b.vorname"),
//        @NamedQuery(name = "Resident.findByBWKennung", query = "SELECT b FROM Resident b WHERE b.bWKennung = :bWKennung"),
//        @NamedQuery(name = "Resident.findByNachname", query = "SELECT b FROM Resident b WHERE b.nachname like :nachname ORDER BY b.nachname, b.vorname"),
//        @NamedQuery(name = "Resident.findByVorname", query = "SELECT b FROM Resident b WHERE b.vorname = :vorname"),
//        @NamedQuery(name = "Resident.findByGeschlecht", query = "SELECT b FROM Resident b WHERE b.geschlecht = :geschlecht"),
//        @NamedQuery(name = "Resident.findByGebDatum", query = "SELECT b FROM Resident b WHERE b.gebDatum = :gebDatum"),
//        @NamedQuery(name = "Resident.findByEditor", query = "SELECT b FROM Resident b WHERE b.editor = :editor"),
//        @NamedQuery(name = "Resident.findByAdminonly", query = "SELECT b FROM Resident b WHERE b.adminonly = :adminonly")})
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
    private Date birthday;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "adminonly")
    private short adminonly;
    @Basic(optional = false)
    @Column(name = "controlling")
    private String controlling;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "resident")
    private Collection<Allowance> allowance;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bewohner")
//    private Collection<NReport> pflegeberichteCollection;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bewohner")
//    private Collection<Verordnung> verordnungCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resident")
    private Collection<ResInfo> resInfoCollection;
//
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "bewohner")
//    private Collection<NReport> pflegberichte;

    // Bewohner, die keiner Pflegestation zugeordnet sind, gelten als inaktiv.
    @JoinColumn(name = "StatID", referencedColumnName = "StatID")
    @ManyToOne
    private Station station;
    @JoinColumn(name = "BV1UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users bv1;
    @JoinColumn(name = "BV2UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users bv2;
    @JoinColumn(name = "ArztID", referencedColumnName = "ArztID")
    @ManyToOne
    private Doc hausarzt;
    @JoinColumn(name = "BetrID1", referencedColumnName = "BetrID")
    @ManyToOne
    private LCustodian LCustodian1;
    @JoinColumn(name = "Editor", referencedColumnName = "UKennung")
    @ManyToOne
    private Users editor;

    public Resident() {
    }

    public Resident(String name, String firstname, int gender, Date birthday) {
        this.rid = null;
        this.name = name;
        this.firstname = firstname;
        this.gender = gender;
        this.birthday = birthday;
        this.editor = OPDE.getLogin().getUser();
        this.adminonly = 0;
        this.controlling = null;
    }

    public String getRID() {
        return rid;
    }

    public void setRID(String bWKennung) {
        if (this.rid == null) {
            this.rid = bWKennung;
        }
    }

    public String getNachnameNieAnonym() {
        return name;
    }

    public String getName() {
        return SYSTools.anonymizeName(name, SYSTools.INDEX_NACHNAME);
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

    public void setNachname(String nachname) {
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

    public String getVorname() {
        int index = (gender == ResidentTools.GESCHLECHT_MAENNLICH ? SYSTools.INDEX_VORNAME_MANN : SYSTools.INDEX_VORNAME_FRAU);
        return SYSTools.anonymizeName(firstname, index);
    }

    public String getVornameNieAnonym() {
        return firstname;
    }

    public void setVorname(String vorname) {
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

    public void setGeschlecht(int geschlecht) {
        this.gender = geschlecht;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setGebDatum(Date gebDatum) {
        this.birthday = gebDatum;
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

    public Users getBv1() {
        return bv1;
    }

    public void setBv1(Users bv1) {
        this.bv1 = bv1;
    }

    public Users getBv2() {
        return bv2;
    }

    public void setBv2(Users bv2) {
        this.bv2 = bv2;
    }

    public Doc getDoc() {
        return hausarzt;
    }

    public void setDoc(Doc hausarzt) {
        this.hausarzt = hausarzt;
    }

    public LCustodian getLCustodian1() {
        return LCustodian1;
    }

    public void setLCustodian1(LCustodian LCustodian1) {
        this.LCustodian1 = LCustodian1;
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
        return getName() + ", " + getVorname() + " [" + rid + "]";
    }
}
