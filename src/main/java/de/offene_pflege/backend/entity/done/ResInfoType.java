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

import de.offene_pflege.backend.entity.DefaultStringIDEntity;

import javax.persistence.*;
import java.util.Collection;

/**
 * @author tloehr
 */
@Entity
@Table(name = "resinfotype")
public class ResInfoType extends DefaultStringIDEntity {
    private String xml;
    private String bWInfoKurz;
    private String bWInfoLang;
    private Integer type;
    private Short intervalMode;
    private Integer equiv;
    private Boolean deprecated;
    private ResInfoCategory resInfoCat;
    private Collection<ResInfo> resInfoCollection;

    public ResInfoType() {
    }

    @Basic(optional = false)
    @Lob
    @Column(name = "XML")
    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    @Basic(optional = false)
    @Column(name = "BWInfoKurz")
    public String getbWInfoKurz() {
        return bWInfoKurz;
    }

    public void setbWInfoKurz(String bWInfoKurz) {
        this.bWInfoKurz = bWInfoKurz;
    }

    @Lob
    @Column(name = "BWInfoLang")
    public String getbWInfoLang() {
        return bWInfoLang;
    }

    public void setbWInfoLang(String bWInfoLang) {
        this.bWInfoLang = bWInfoLang;
    }

    @Column(name = "type")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Column(name = "IntervalMode")
    public Short getIntervalMode() {
        return intervalMode;
    }

    public void setIntervalMode(Short intervalMode) {
        this.intervalMode = intervalMode;
    }

    // Wenn Formulare veralten, dann werden sie durch ein neues ersetzt. Dieses hat aber einen anderen BWINFTYP. Damit ich bei Auswertungen (z.B. TXEssen) die Attribute des jeweilig gültigen Nachfolge Formulars noch finde suche ich über
    // die abstrakte Nummerierung in 'type'.
    // Es kann aber sein, dass ein Formular mehrere andere ersetzt, damit ich diese zusammengehörigkeit finde benutze ich equiv.
    // Bei Formularen, die bisher nicht ersetzt wurden oder wo es mir egal ist, steht equiv auf 0. Aber ansonsten fasse ich Gruppen von Formularen zusammen, die sich gegenseitig ersetzt haben.
    // Das spielt nur in PnlInformation eine Rolle. Dort schließt aber die Neuerstellung eines Formulars aus einer Gruppe alle noch offenen ab.
    // Es gibt immer nur ein Ersatz Formular in eine equiv Gruppe. Aber durchaus mehrere ersetzte.
    // Diese Änderungen werden aber nur bei der Entwicklung vorgenommen und nicht während der Laufzeit.
    @Column(name = "equiv")
    public Integer getEquiv() {
        return equiv;
    }

    public void setEquiv(Integer equiv) {
        this.equiv = equiv;
    }

    // steht für eine veraltete Version eines Formulars
    @Column(name = "deprecated")

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    @JoinColumn(name = "BWIKID", referencedColumnName = "id")
    @ManyToOne
    public ResInfoCategory getResInfoCat() {
        return resInfoCat;
    }

    public void setResInfoCat(ResInfoCategory resInfoCat) {
        this.resInfoCat = resInfoCat;
    }

}
