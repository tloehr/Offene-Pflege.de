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
package de.offene_pflege.backend.entity.system;

import de.offene_pflege.backend.entity.DefaultStringIDEntity;
import de.offene_pflege.backend.entity.done.User2File;
import de.offene_pflege.backend.services.OPUsersService;
import de.offene_pflege.interfaces.Attachable;

import javax.persistence.*;
import java.util.Collection;

/**
 * @author tloehr
 */
@Entity
@Table(name = "opusers")
public class OPUsers extends DefaultStringIDEntity implements Comparable<OPUsers>, Attachable {
    private String vorname;
    private String nachname;
    private Short userstatus;
    private String md5pw;
    private String email;
    private int mailconfirmed;
    private int cipherid; // zum chiffrierten ausdrucken der Pflegedoku. Datenschutz für die MitarbeiterInnen
    private Collection<OPGroups> opGroups;
    private Collection<User2File> attachedFilesConnections;

    @Basic(optional = false)
    @Column(name = "Vorname")
    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }


    @Basic(optional = false)
    @Column(name = "Nachname")
    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    @Column(name = "userstatus")
    public Short getUserstatus() {
        return userstatus;
    }

    public void setUserstatus(Short userstatus) {
        this.userstatus = userstatus;
    }

    @Basic(optional = false)
    @Column(name = "MD5PW")
    public String getMd5pw() {
        return md5pw;
    }

    public void setMd5pw(String md5pw) {
        this.md5pw = md5pw;
    }

    @Column(name = "EMail")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic(optional = false)
    @Column(name = "mailconfirmed")
    public int getMailconfirmed() {
        return mailconfirmed;
    }

    public void setMailconfirmed(int mailconfirmed) {
        this.mailconfirmed = mailconfirmed;
    }

    @Basic(optional = false)
    @Column(name = "cipherid")
    public int getCipherid() {
        return cipherid;
    }

    public void setCipherid(int cipherid) {
        this.cipherid = cipherid;
    }

    @ManyToMany
    @JoinTable(name = "member", joinColumns =
    @JoinColumn(name = "UKennung"), inverseJoinColumns =
    @JoinColumn(name = "GKennung"))
    public Collection<OPGroups> getOpGroups() {
        return opGroups;
    }

    public void setOpGroups(Collection<OPGroups> opGroups) {
        this.opGroups = opGroups;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "opUsers")
    public Collection<User2File> getAttachedFilesConnections() {
        return attachedFilesConnections;
    }

    public void setAttachedFilesConnections(Collection<User2File> attachedFilesConnections) {
        this.attachedFilesConnections = attachedFilesConnections;
    }


    public OPUsers() {
    }

    @Override
    public int compareTo(OPUsers o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public boolean active() {
        return OPUsersService.isActive(this);
    }
}
