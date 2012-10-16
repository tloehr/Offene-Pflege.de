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

import op.OPDE;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author tloehr
 */
@Entity
@Table(name = "BWInfoTyp")
//@NamedQueries({
//        @NamedQuery(name = "BWInfoTyp.findAll", query = "SELECT b FROM ResInfoType b"),
//        @NamedQuery(name = "BWInfoTyp.findByBwinftyp", query = "SELECT b FROM ResInfoType b WHERE b.bwinftyp = :bwinftyp"),
//        @NamedQuery(name = "BWInfoTyp.findByBWInfoKurz", query = "SELECT b FROM ResInfoType b WHERE b.bWInfoKurz = :bWInfoKurz"),
//        @NamedQuery(name = "BWInfoTyp.findByKat", query = "SELECT b FROM ResInfoType b WHERE b.resInfokat = :kat AND b.status >= 0 ORDER BY b.bWInfoKurz"),
//        @NamedQuery(name = "BWInfoTyp.findByIntervalMode", query = "SELECT b FROM ResInfoType b WHERE b.intervalMode = :intervalMode")})
public class ResInfoType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "BWINFTYP")
    private String bwinftyp;
    @Basic(optional = false)
    @Lob
    @Column(name = "XML")
    private String xml;
    @Basic(optional = false)
    @Column(name = "BWInfoKurz")
    private String bWInfoKurz;
    @Lob
    @Column(name = "BWInfoLang")
    private String bWInfoLang;
    @Column(name = "Status")
    private Integer status;
    @Column(name = "IntervalMode")
    private Short intervalMode;
    @Version
    @Column(name = "version")
    private Long version;

    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "BWIKID", referencedColumnName = "BWIKID")
    @ManyToOne
    private ResInfoCategory resInfoCat;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bwinfotyp")
    private Collection<ResInfo> resInfoCollection;

    public ResInfoType() {
    }

    public ResInfoType(String bwinftyp) {
        this.bwinftyp = bwinftyp;
    }


    public String getID() {
        return bwinftyp;
    }

    public void setBwinftyp(String bwinftyp) {
        this.bwinftyp = bwinftyp;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getShortDescription() {
        return bWInfoKurz;
    }

    public void setBWInfoKurz(String bWInfoKurz) {
        this.bWInfoKurz = bWInfoKurz;
    }

    public String getLongDescription() {
        return bWInfoLang;
    }

    public void setBWInfoLang(String bWInfoLang) {
        this.bWInfoLang = bWInfoLang;
    }

    public ResInfoCategory getResInfoCat() {
        return resInfoCat;
    }

    public void setResInfoCat(ResInfoCategory resInfoCat) {
        this.resInfoCat = resInfoCat;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Short getIntervalMode() {
        return intervalMode;
    }

    public void setIntervalMode(Short intervalMode) {
        this.intervalMode = intervalMode;
    }

    public boolean isObsolete(){
        return status < 0;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bwinftyp != null ? bwinftyp.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof ResInfoType)) {
            return false;
        }
        ResInfoType other = (ResInfoType) object;
        if ((this.bwinftyp == null && other.bwinftyp != null) || (this.bwinftyp != null && !this.bwinftyp.equals(other.bwinftyp))) {
            OPDE.debug(this.bwinftyp + " != " + other.bwinftyp);
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.info.ResInfoType[bwinftyp=" + bwinftyp + "]";
    }
}
