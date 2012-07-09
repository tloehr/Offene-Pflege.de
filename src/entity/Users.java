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

import entity.files.*;
import entity.system.SYSLogin;
import op.tools.SYSTools;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author tloehr
 */
@Entity
@Table(name = "OCUsers")
@NamedQueries({
        @NamedQuery(name = "Users.findAll", query = "SELECT o FROM Users o"),
        @NamedQuery(name = "Users.findAllSorted", query = "SELECT o FROM Users o ORDER BY o.nachname, o.vorname "),
        @NamedQuery(name = "Users.findByUKennung", query = "SELECT o FROM Users o WHERE o.uKennung = :uKennung"),
        @NamedQuery(name = "Users.findForLogin", query = "SELECT o FROM Users o WHERE o.uKennung = :uKennung AND o.md5pw = :md5pw"),
        @NamedQuery(name = "Users.findByVorname", query = "SELECT o FROM Users o WHERE o.vorname = :vorname"),
        @NamedQuery(name = "Users.findByNachname", query = "SELECT o FROM Users o WHERE o.nachname = :nachname"),
        @NamedQuery(name = "Users.findByStatusSorted", query = "SELECT o FROM Users o WHERE o.status = :status ORDER BY o.nachname, o.vorname"),
        @NamedQuery(name = "Users.findAllMembers", query = "SELECT o FROM Users o "
                + " WHERE :group MEMBER OF o.groups ORDER BY o.nachname, o.vorname "),
        @NamedQuery(name = "Users.findAllNonMembers", query = "SELECT o FROM Users o "
                + " WHERE :group NOT MEMBER OF o.groups ORDER BY o.nachname, o.vorname "),
        @NamedQuery(name = "Users.findByEMail", query = "SELECT o FROM Users o WHERE o.eMail = :eMail")})
public class Users implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "UKennung")
    private String uKennung;
    @Basic(optional = false)
    @Column(name = "Vorname")
    private String vorname;
    @Basic(optional = false)
    @Column(name = "Nachname")
    private String nachname;
    @Column(name = "Status")
    private Short status;
    @Basic(optional = false)
    @Column(name = "MD5PW")
    private String md5pw;
    @Column(name = "EMail")
    private String eMail;
    @ManyToMany
    @JoinTable(name = "OCMember", joinColumns =
    @JoinColumn(name = "UKennung"), inverseJoinColumns =
    @JoinColumn(name = "GKennung"))
    private Collection<Groups> groups;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<SYSFiles> sysfilesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Sysbwi2file> sysbwi2fileCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Syspb2file> syspb2fileCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Sysver2file> sysver2fileCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Pflegeberichte> pflegeberichte;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "editedBy")
    private Collection<Pflegeberichte> korrigiertePflegeberichte;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<SYSLogin> logins;

    public Users() {
        groups = new ArrayList<Groups>();
        status = UsersTools.STATUS_ACTIVE;
    }
//
//    public Users(String uKennung) {
//        this.uKennung = uKennung;
//    }
//
//    public Users(String uKennung, String vorname, String nachname, String md5pw) {
//        this.uKennung = uKennung;
//        this.vorname = vorname;
//        this.nachname = nachname;
//        this.md5pw = md5pw;
//    }

    public String getUKennung() {
        return uKennung;
    }

    public void setUKennung(String uKennung) {
        this.uKennung = uKennung;
    }

    public String getVorname() {
        return SYSTools.anonymizeUser(vorname);
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return SYSTools.anonymizeUser(nachname);
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public String getMd5pw() {
        return md5pw;
    }

    public void setMd5pw(String md5pw) {
        this.md5pw = md5pw;
    }

    public String getEMail() {
        return eMail;
    }

    public void setEMail(String eMail) {
        this.eMail = eMail;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uKennung != null ? uKennung.hashCode() : 0);
        return hash;
    }

    public Collection<Groups> getGroups() {
        return groups;
    }

    public void setGroups(Collection<Groups> groups) {
        this.groups = groups;
    }

    /**
     * gibt an, ob der betreffende User sich anmelden darf.
     *
     * @return true, wenn ja; false, sonst.
     */
    public boolean isActive() {
        return status == UsersTools.STATUS_ACTIVE;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Users)) {
            return false;
        }
        Users other = (Users) object;
        if ((this.uKennung == null && other.uKennung != null) || (this.uKennung != null && !this.uKennung.equals(other.uKennung))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getNameUndVorname() + " [" + uKennung + "]";
    }

    public String getNameUndVorname() {
        return getNachname() + ", " + getVorname();
    }
}
