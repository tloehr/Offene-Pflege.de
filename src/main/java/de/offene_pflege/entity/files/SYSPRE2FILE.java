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
package de.offene_pflege.entity.files;

import de.offene_pflege.entity.prescription.Prescription;
import de.offene_pflege.entity.system.OPUsers;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "syspre2file")

public class SYSPRE2FILE implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;
    @JoinColumn(name = "VERID", referencedColumnName = "VerID")
    @ManyToOne
    private Prescription prescription;
    @JoinColumn(name = "FID", referencedColumnName = "OCFID")
    @ManyToOne
    private SYSFiles sysfile;
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private OPUsers user;

    public SYSPRE2FILE() {
    }

    public SYSPRE2FILE(SYSFiles sysfile, Prescription prescription, OPUsers user, Date pit) {
        this.sysfile = sysfile;
        this.prescription = prescription;
        this.user = user;
        this.pit = pit;
    }

    public OPUsers getUser() {
        return user;
    }

    public void setUser(OPUsers user) {
        this.user = user;
    }


    public Date getPit() {
        return pit;
    }

    public SYSFiles getSysfile() {
        return sysfile;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof SYSPRE2FILE)) {
            return false;
        }
        SYSPRE2FILE other = (SYSPRE2FILE) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.files.SYSPRE2FILE[id=" + id + "]";
    }
}
