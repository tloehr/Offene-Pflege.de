package entity.bwinfo;

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

import entity.Bewohner;
import entity.Users;
import entity.files.Sysbwi2file;
import entity.vorgang.SYSBWI2VORGANG;
import entity.vorgang.VorgangElement;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "BWInfos")

public class BWInfos implements Serializable, VorgangElement {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Basic(optional = false)
    @Column(name = "Von")
    @Temporal(TemporalType.TIMESTAMP)
    private Date von;
    @Basic(optional = false)
    @Column(name = "Bis")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bis;
    @Column(name = "FK")
    private Long fk;
    @Column(name = "TypeID")
    private String typeID;
    @Lob
    @Column(name = "XML")
    private String xml;
    @Lob
    @Column(name = "Bemerkung")
    private String bemerkung;

//    // ==
//    // N:1 Relationen
//    // ==
//    @JoinColumn(name = "BWINFTYP", referencedColumnName = "BWINFTYP")
//    @ManyToOne
//    private BWInfoTyp bwinfotyp;
    @JoinColumn(name = "AnUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users angesetztDurch;
    @JoinColumn(name = "AbUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users abgesetztDurch;
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Bewohner bewohner;
//    // ==
//    // M:N Relationen
//    // ==
//    // ==
//    // 1:N Relationen
//    // ==
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bwinfo")
//    private Collection<Sysbwi2file> attachedFiles;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bwinfo")
//    private Collection<SYSBWI2VORGANG> attachedVorgaenge;


    public BWInfos() {
    }


    public Bewohner getBewohner() {
        return bewohner;
    }

    public Date getVon() {
        return von;
    }

    public void setVon(Date von) {
        this.von = von;
    }

    public Date getBis() {
        return bis;
    }

    public void setBis(Date bis) {
        this.bis = bis;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Users getAbgesetztDurch() {
        return abgesetztDurch;
    }

    public void setAbgesetztDurch(Users abgesetztDurch) {
        this.abgesetztDurch = abgesetztDurch;
    }

    public Users getAngesetztDurch() {
        return angesetztDurch;
    }

    public void setAngesetztDurch(Users angesetztDurch) {
        this.angesetztDurch = angesetztDurch;
    }

    public Long getFk() {
        return fk;
    }

    public void setFk(Long fk) {
        this.fk = fk;
    }

    public BWInfos(Long fk, String typeID, String bemerkung, Bewohner bewohner) {
        this.von = new Date();
        this.bis = SYSConst.DATE_BIS_AUF_WEITERES;
        this.xml = null;
        this.angesetztDurch = OPDE.getLogin().getUser();
        this.abgesetztDurch = null;
        this.fk = fk;
        this.typeID = typeID;
        this.bemerkung = bemerkung;
        this.bewohner = bewohner;
    }

    public String getTypeID() {

        return typeID;
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }

    public boolean isActive(){
        Date now = new Date();
        return von.before(now) && bis.after(now);
    }

    //    public Collection<Sysbwi2file> getAttachedFiles() {
//        return attachedFiles;
//    }
//
//    public Collection<SYSBWI2VORGANG> getAttachedVorgaenge() {
//        return attachedVorgaenge;
//    }

    @Override
    public long getPITInMillis() {
        return von.getTime();
    }

    @Override
    public String getContentAsHTML() {
        // TODO: fehlt noch
        return "<html>not yet</html>";
    }

    @Override
    public String getPITAsHTML() {
        // TODO: fehlt noch
        return "<html>not yet</html>";
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BWInfos)) {
            return false;
        }
        BWInfos other = (BWInfos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.BWInfos[id=" + id + "]";
    }
}

