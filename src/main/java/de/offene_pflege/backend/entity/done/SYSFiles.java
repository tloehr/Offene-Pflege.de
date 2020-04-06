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
import de.offene_pflege.backend.entity.system.OPUsers;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "sysfiles")
public class SYSFiles extends DefaultEntity implements Comparable {
    private String filename;
    private String md5;
    private Date filedate;
    private long filesize;
    private Date pit;
    private String beschreibung;
    private OPUsers opUsers;
    private Collection<SYSNR2FILE> nrAssignCollection;
    private Collection<SYSINF2FILE> bwiAssignCollection;
    private Collection<SYSPRE2FILE> preAssignCollection;
    private Collection<SYSVAL2FILE> valAssignCollection;
    private Collection<SYSNP2FILE> npAssignCollection;
    private Collection<User2File> usersAssignCollection;
    private Collection<Resident2File> residentAssignCollection;

    public SYSFiles() {
    }

    @Basic(optional = false)
    @Column(name = "Filename", length = 500)
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Basic(optional = false)
    @Column(name = "MD5", unique = true)
    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Basic(optional = false)
    @Column(name = "Filedate")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getFiledate() {
        return filedate;
    }

    public void setFiledate(Date filedate) {
        this.filedate = filedate;
    }

    @Basic(optional = false)
    @Column(name = "Filesize")
    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
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

    @Basic(optional = false)
    @Column(name = "Beschreibung")
    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    @JoinColumn(name = "UID", referencedColumnName = "id")
    @ManyToOne
    public OPUsers getOpUsers() {
        return opUsers;
    }

    public void setOpUsers(OPUsers opUsers) {
        this.opUsers = opUsers;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sysfile")
    public Collection<SYSNR2FILE> getNrAssignCollection() {
        return nrAssignCollection;
    }

    public void setNrAssignCollection(Collection<SYSNR2FILE> nrAssignCollection) {
        this.nrAssignCollection = nrAssignCollection;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sysfile")
    public Collection<SYSINF2FILE> getBwiAssignCollection() {
        return bwiAssignCollection;
    }

    public void setBwiAssignCollection(Collection<SYSINF2FILE> bwiAssignCollection) {
        this.bwiAssignCollection = bwiAssignCollection;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sysfile")
    public Collection<SYSPRE2FILE> getPreAssignCollection() {
        return preAssignCollection;
    }

    public void setPreAssignCollection(Collection<SYSPRE2FILE> preAssignCollection) {
        this.preAssignCollection = preAssignCollection;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sysfile")
    public Collection<SYSVAL2FILE> getValAssignCollection() {
        return valAssignCollection;
    }

    public void setValAssignCollection(Collection<SYSVAL2FILE> valAssignCollection) {
        this.valAssignCollection = valAssignCollection;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sysfile")
    public Collection<SYSNP2FILE> getNpAssignCollection() {
        return npAssignCollection;
    }

    public void setNpAssignCollection(Collection<SYSNP2FILE> npAssignCollection) {
        this.npAssignCollection = npAssignCollection;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sysfile")
    public Collection<User2File> getUsersAssignCollection() {
        return usersAssignCollection;
    }

    public void setUsersAssignCollection(Collection<User2File> usersAssignCollection) {
        this.usersAssignCollection = usersAssignCollection;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sysfile")
    public Collection<Resident2File> getResidentAssignCollection() {
        return residentAssignCollection;
    }

    public void setResidentAssignCollection(Collection<Resident2File> residentAssignCollection) {
        this.residentAssignCollection = residentAssignCollection;
    }

    @Override
    public int compareTo(Object o) {
        return getFilename().compareTo(((SYSFiles) o).getFilename());
    }

}
