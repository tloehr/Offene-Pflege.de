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

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author tloehr
 */
@Entity
@Table(name = "resinfotype")
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
    @Column(name = "type")
    private Integer type;
    @Column(name = "IntervalMode")
    private Short intervalMode;
    @Version
    @Column(name = "version")
    private Long version;

    // Wenn Formulare veralten, dann werden sie durch ein neues ersetzt. Dieses hat aber einen anderen BWINFTYP. Damit ich bei Auswertungen (z.B. TXEssen) die Attribute des jeweilig gültigen Nachfolge Formulars noch finde suche ich über
    // die abstrakte Nummerierung in 'type'.
    // Es kann aber sein, dass ein Formular mehrere andere ersetzt, damit ich diese zusammengehörigkeit finde benutze ich equiv.
    // Bei Formularen, die bisher nicht ersetzt wurden oder wo es mir egal ist, steht equiv auf 0. Aber ansonsten fasse ich Gruppen von Formularen zusammen, die sich gegenseitig ersetzt haben.
    // Das spielt nur in PnlInformation eine Rolle. Dort schließt aber die Neuerstellung eines Formulars aus einer Gruppe alle noch offenen ab.
    // Es gibt immer nur ein Ersatz Formular in eine equiv Gruppe. Aber durchaus mehrere ersetzte.
    // Diese Änderungen werden aber nur bei der Entwicklung vorgenommen und nicht während der Laufzeit.
    @Column(name = "equiv")
    private Integer equiv;

    // steht für eine veraltete Version eines Formulars
    @Column(name = "deprecated")
    private Boolean deprecated;

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

    public Boolean isDeprecated() {
        return deprecated;
    }

    public Integer getEquiv() {
        return equiv;
    }

    public void setEquiv(Integer equiv) {
        this.equiv = equiv;
    }

    public void setBwinftyp(String bwinftyp) {
        this.bwinftyp = bwinftyp;
    }

//    public Integer getEquiv() {
//        return equiv;
//    }

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Short getIntervalMode() {
        return intervalMode;
    }

    public void setIntervalMode(Short intervalMode) {
        this.intervalMode = intervalMode;
    }

    public boolean isAlertType() {
        return type == ResInfoTypeTools.TYPE_ALLERGY || type == ResInfoTypeTools.TYPE_INFECTION || type == ResInfoTypeTools.TYPE_DIABETES || type == ResInfoTypeTools.TYPE_WARNING || type == ResInfoTypeTools.TYPE_FALLRISK;
    }

//    /**
//     * means, that the underlying form for this infotype is not used anymore. current resinfos are still
//     * available but cannot be changed anymore. Use the replacement instead. If there is any.
//     * @return
//     */
//    public boolean isObsolete() {
//        return type < 0;
//    }

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
//            OPDE.debug(this.bwinftyp + " != " + other.bwinftyp);
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "[" + bwinftyp + "] " + bWInfoKurz;
    }
}
