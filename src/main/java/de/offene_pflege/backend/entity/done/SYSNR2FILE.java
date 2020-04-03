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

import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.backend.entity.reports.NReport;
import de.offene_pflege.backend.entity.system.OPUsers;

import javax.persistence.*;
import java.util.Date;


/**
 * @author tloehr
 */
@Entity
@Table(name = "sysnr2file")
public class SYSNR2FILE extends DefaultEntity {
    private Date pit;
    private SYSFiles sysFiles;
    private NReport nReport;
    private OPUsers opusers;

    public SYSNR2FILE() {
    }

    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

    @JoinColumn(name = "FID", referencedColumnName = "id")
    @ManyToOne
    public SYSFiles getSysFiles() {
        return sysFiles;
    }

    public void setSysFiles(SYSFiles sysFiles) {
        this.sysFiles = sysFiles;
    }

    @JoinColumn(name = "PBID", referencedColumnName = "id")
    @ManyToOne
    public NReport getnReport() {
        return nReport;
    }

    public void setnReport(NReport nReport) {
        this.nReport = nReport;
    }

    @JoinColumn(name = "UKennung", referencedColumnName = "id")
    @ManyToOne
    public OPUsers getOpusers() {
        return opusers;
    }

    public void setOpusers(OPUsers opusers) {
        this.opusers = opusers;
    }
}
