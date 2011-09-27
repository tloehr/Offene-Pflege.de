/*
 * OffenePflege
 * Copyright (C) 2011 Torsten L�hr
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
 * Auf deutsch (freie �bersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie k�nnen es unter den Bedingungen der GNU General Public License,
 * wie von der Free Software Foundation ver�ffentlicht, weitergeben und/oder modifizieren, gem�� Version 2 der Lizenz.
 *
 * Die Ver�ffentlichung dieses Programms erfolgt in der Hoffnung, da� es Ihnen von Nutzen sein wird, aber
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT F�R EINEN
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht,
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 */
package entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "BWInfoTyp")
@NamedQueries({
    @NamedQuery(name = "BWInfoTyp.findAll", query = "SELECT b FROM BWInfoTyp b"),
    @NamedQuery(name = "BWInfoTyp.findByBwinftyp", query = "SELECT b FROM BWInfoTyp b WHERE b.bwinftyp = :bwinftyp"),
    @NamedQuery(name = "BWInfoTyp.findByBWInfoKurz", query = "SELECT b FROM BWInfoTyp b WHERE b.bWInfoKurz = :bWInfoKurz"),
    @NamedQuery(name = "BWInfoTyp.findByBwikid", query = "SELECT b FROM BWInfoTyp b WHERE b.bwikid = :bwikid"),
    @NamedQuery(name = "BWInfoTyp.findBySortierung", query = "SELECT b FROM BWInfoTyp b WHERE b.sortierung = :sortierung"),
    @NamedQuery(name = "BWInfoTyp.findByIntervalMode", query = "SELECT b FROM BWInfoTyp b WHERE b.intervalMode = :intervalMode")})
public class BWInfoTyp implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
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
    @Basic(optional = false)
    @Column(name = "BWIKID")
    private long bwikid;
    @Column(name = "Sortierung")
    private Integer sortierung;
    @Column(name = "IntervalMode")
    private Short intervalMode;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bwinfotyp")
    private Collection<BWInfo> bwInfoCollection;

    public BWInfoTyp() {
    }

    public BWInfoTyp(String bwinftyp) {
        this.bwinftyp = bwinftyp;
    }

    public BWInfoTyp(String bwinftyp, String xml, String bWInfoKurz, long bwikid) {
        this.bwinftyp = bwinftyp;
        this.xml = xml;
        this.bWInfoKurz = bWInfoKurz;
        this.bwikid = bwikid;
    }

    public String getBwinftyp() {
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

    public String getBWInfoKurz() {
        return bWInfoKurz;
    }

    public void setBWInfoKurz(String bWInfoKurz) {
        this.bWInfoKurz = bWInfoKurz;
    }

    public String getBWInfoLang() {
        return bWInfoLang;
    }

    public void setBWInfoLang(String bWInfoLang) {
        this.bWInfoLang = bWInfoLang;
    }

    public long getBwikid() {
        return bwikid;
    }

    public void setBwikid(long bwikid) {
        this.bwikid = bwikid;
    }

    public Integer getSortierung() {
        return sortierung;
    }

    public void setSortierung(Integer sortierung) {
        this.sortierung = sortierung;
    }

    public Short getIntervalMode() {
        return intervalMode;
    }

    public void setIntervalMode(Short intervalMode) {
        this.intervalMode = intervalMode;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bwinftyp != null ? bwinftyp.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BWInfoTyp)) {
            return false;
        }
        BWInfoTyp other = (BWInfoTyp) object;
        if ((this.bwinftyp == null && other.bwinftyp != null) || (this.bwinftyp != null && !this.bwinftyp.equals(other.bwinftyp))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.BWInfoTyp[bwinftyp=" + bwinftyp + "]";
    }
}
