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
package entity;

import entity.files.Sysbw2file;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "Bewohner")
@NamedQueries({
        @NamedQuery(name = "Bewohner.findAll", query = "SELECT b FROM Bewohner b"),
        @NamedQuery(name = "Bewohner.findAllActiveSorted", query = "SELECT b FROM Bewohner b WHERE b.station IS NOT NULL ORDER BY b.nachname, b.vorname"),
        @NamedQuery(name = "Bewohner.findAllActiveSortedByStationen", query = "SELECT b FROM Bewohner b WHERE b.station IS NOT NULL ORDER BY b.station.bezeichnung, b.nachname, b.vorname"),
        @NamedQuery(name = "Bewohner.findByBWKennung", query = "SELECT b FROM Bewohner b WHERE b.bWKennung = :bWKennung"),
        @NamedQuery(name = "Bewohner.findByNachname", query = "SELECT b FROM Bewohner b WHERE b.nachname like :nachname ORDER BY b.nachname, b.vorname"),
        @NamedQuery(name = "Bewohner.findByVorname", query = "SELECT b FROM Bewohner b WHERE b.vorname = :vorname"),
        @NamedQuery(name = "Bewohner.findByGeschlecht", query = "SELECT b FROM Bewohner b WHERE b.geschlecht = :geschlecht"),
        @NamedQuery(name = "Bewohner.findByGebDatum", query = "SELECT b FROM Bewohner b WHERE b.gebDatum = :gebDatum"),
        @NamedQuery(name = "Bewohner.findByVersion", query = "SELECT b FROM Bewohner b WHERE b.version = :version"),
        @NamedQuery(name = "Bewohner.findByEditor", query = "SELECT b FROM Bewohner b WHERE b.editor = :editor"),
        @NamedQuery(name = "Bewohner.findByAdminonly", query = "SELECT b FROM Bewohner b WHERE b.adminonly = :adminonly")})
public class Bewohner implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "BWKennung")
    private String bWKennung;
    @Basic(optional = false)
    @Column(name = "Nachname")
    private String nachname;
    @Basic(optional = false)
    @Column(name = "Vorname")
    private String vorname;
    @Basic(optional = false)
    @Column(name = "Geschlecht")
    private int geschlecht;
    @Basic(optional = false)
    @Column(name = "GebDatum")
    @Temporal(TemporalType.DATE)
    private Date gebDatum;
    @Basic(optional = false)
    @Column(name = "_version")
    @Temporal(TemporalType.TIMESTAMP)
    private Date version;
    @Basic(optional = false)
    @Column(name = "_editor")
    private String editor;
    @Basic(optional = false)
    @Column(name = "adminonly")
    private short adminonly;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bewohner")
    private Collection<Sysbw2file> bwFilesCollection;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "bewohner")
    private Collection<Barbetrag> konto;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bewohner")
//    private Collection<Pflegeberichte> pflegeberichteCollection;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bewohner")
//    private Collection<Verordnung> verordnungCollection;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bewohner")
//    private Collection<BWInfo> bwinfoCollection;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "bewohner")
    private Collection<Pflegeberichte> pflegberichte;

    // Bewohner, die keiner Pflegestation zugeordnet sind, gelten als inaktiv.
    @JoinColumn(name = "StatID", referencedColumnName = "StatID")
    @ManyToOne
    private Stationen station;
    @JoinColumn(name = "BV1UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users bv1;
    @JoinColumn(name = "BV2UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users bv2;
    @JoinColumn(name = "ArztID", referencedColumnName = "ArztID")
    @ManyToOne
    private Arzt hausarzt;
    @JoinColumn(name = "BetrID1", referencedColumnName = "BetrID")
    @ManyToOne
    private Betreuer betreuer1;

    public Bewohner() {
    }

    public Bewohner(String bWKennung) {
        this.bWKennung = bWKennung;
    }

    public Bewohner(String bWKennung, String nachname, String vorname, int geschlecht, Date gebDatum, Date version, String editor, short adminonly) {
        this.bWKennung = bWKennung;
        this.nachname = nachname;
        this.vorname = vorname;
        this.geschlecht = geschlecht;
        this.gebDatum = gebDatum;
        this.version = version;
        this.editor = editor;
        this.adminonly = adminonly;
    }

    public Collection<Sysbw2file> getBwFilesCollection() {
        return bwFilesCollection;
    }

    public String getBWKennung() {
        return bWKennung;
    }

    public void setBWKennung(String bWKennung) {
        this.bWKennung = bWKennung;
    }

    public String getNachnameNieAnonym() {
        return nachname;
    }

    public String getNachname() {
        return SYSTools.anonymizeName(nachname, SYSTools.INDEX_NACHNAME);
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public Stationen getStation() {
        return station;
    }

    public void setStation(Stationen station) {
        this.station = station;
    }

    /**
     * Bewohner, die einer Station zugeordnet sind gelten als aktive Bewohner. Also nicht ausgezogen, sind zur Zeit aktuell.
     *
     * @return
     */
    public boolean isAktiv() {
        return station != null;
    }

    public String getVorname() {
        int index = (geschlecht == SYSConst.GESCHLECHT_MÄNNLICH ? SYSTools.INDEX_VORNAME_MANN : SYSTools.INDEX_VORNAME_FRAU);
        return SYSTools.anonymizeName(vorname, index);
    }

    public String getVornameNieAnonym() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public int getGeschlecht() {
        return geschlecht;
    }

    public void setGeschlecht(int geschlecht) {
        this.geschlecht = geschlecht;
    }

    public Date getGebDatum() {
        return gebDatum;
    }

    public void setGebDatum(Date gebDatum) {
        this.gebDatum = gebDatum;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public short getAdminonly() {
        return adminonly;
    }

    public void setAdminonly(short adminonly) {
        this.adminonly = adminonly;
    }

    public Collection<Barbetrag> getKonto() {
        return konto;
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

    public Arzt getHausarzt() {
        return hausarzt;
    }

    public void setHausarzt(Arzt hausarzt) {
        this.hausarzt = hausarzt;
    }

    public Betreuer getBetreuer1() {
        return betreuer1;
    }

    public void setBetreuer1(Betreuer betreuer1) {
        this.betreuer1 = betreuer1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bWKennung != null ? bWKennung.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Bewohner)) {
            return false;
        }
        Bewohner other = (Bewohner) object;
        if ((this.bWKennung == null && other.bWKennung != null) || (this.bWKennung != null && !this.bWKennung.equals(other.bWKennung))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getNachname() + ", " + getVorname() + " [" + bWKennung + "]";
    }
}
