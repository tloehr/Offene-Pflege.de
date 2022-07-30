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
package de.offene_pflege.entity.system;

import de.offene_pflege.entity.files.*;
import de.offene_pflege.entity.reports.NReport;
import de.offene_pflege.interfaces.Attachable;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSTools;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.OptimisticLockingType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author tloehr
 */
@Entity
@Table(name = "opusers")
@OptimisticLocking(cascade = false, type = OptimisticLockingType.VERSION_COLUMN)
public class OPUsers implements Serializable, Comparable<OPUsers>, Attachable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "UKennung")
    private String uid;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "Vorname")
    private String vorname;
    @Basic(optional = false)
    @Column(name = "Nachname")
    private String nachname;
    @Column(name = "userstatus")
    private Short userstatus;
    @Basic(optional = false)
    @Column(name = "MD5PW")
    private String md5pw;
    @Column(name = "EMail")
    private String eMail;
    @Basic(optional = false)
    @Column(name = "mailconfirmed")
    private int mailConfirmed;
    @Basic(optional = false)
    @Column(name = "cipherid")
    private int cipherid; // zum chiffrierten ausdrucken der Pflegedoku. Datenschutz für die MitarbeiterInnen

    @ManyToMany
    @JoinTable(name = "member", joinColumns =
    @JoinColumn(name = "UKennung"), inverseJoinColumns =
    @JoinColumn(name = "GKennung"))
    private Collection<OPGroups> groups;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<SYSFiles> sysfilesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<SYSINF2FILE> SYSINF2FILECollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<SYSNR2FILE> SYSNR2FILECollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<SYSPRE2FILE> SYSPRE2FILECollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "newBy")
    private Collection<NReport> NReport;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "editedBy")
    private Collection<NReport> korrigierteNReport;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<SYSLogin> logins;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "")
//    private Collection<MedOrders> medOrders;



    public Collection<User2File> getAttachedFilesConnections() {
        return attachedFilesConnections;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<User2File> attachedFilesConnections;

    public OPUsers() {
        uid = null;
        groups = new ArrayList<>();
        userstatus = UsersTools.STATUS_ACTIVE;
        mailConfirmed = UsersTools.MAIL_UNCONFIRMED;
        cipherid = 12;  //todo: berechnen
    }

    public int getCipherid() {
        return cipherid;
    }

    public void setCipherid(int cipherid) {
        this.cipherid = cipherid;
    }

    public int getMailConfirmed() {
        return mailConfirmed;
    }

    public void setMailConfirmed(int mailConfirmed) {
        this.mailConfirmed = mailConfirmed;
    }


    public String getUID() {
        return uid;
    }

    public String getUIDCiphered() {
            return OPDE.isUserCipher() ? "#"+cipherid : uid;
        }

    public void setUID(String uid) {
        this.uid = uid;
    }

    public String getVorname() {
        return SYSTools.anonymizeName(vorname, SYSTools.INDEX_FIRSTNAME_FEMALE);
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getName() {
        return SYSTools.anonymizeName(nachname, SYSTools.INDEX_LASTNAME);
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public Short getUserstatus() {
        return userstatus;
    }

    public void setUserstatus(Short status) {
        this.userstatus = status;
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

//    @Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (uid != null ? uid.hashCode() : 0);
//        return hash;
//    }

    public Collection<OPGroups> getGroups() {
        return groups;
    }

    public void setGroups(Collection<OPGroups> groups) {
        this.groups = groups;
    }

    /**
     * gibt an, ob der betreffende User sich anmelden darf.
     *
     * @return true, wenn ja; false, sonst.
     */
    @Override
    public boolean isActive() {
        return userstatus == UsersTools.STATUS_ACTIVE;
    }

    @Override
    public int compareTo(OPUsers o) {
        return toString().compareTo(o.toString());
    }

//    @Override
//    public boolean equals(Object object) {
//
//        if (!(object instanceof Users)) {
//            return false;
//        }
//        Users other = (Users) object;
//        if ((this.uid == null && other.uid != null) || (this.uid != null && !this.uid.equals(other.uid))) {
//            return false;
//        }
//        return true;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OPUsers OPUsers = (OPUsers) o;

        if (eMail != null ? !eMail.equals(OPUsers.eMail) : OPUsers.eMail != null) return false;
        if (logins != null ? !logins.equals(OPUsers.logins) : OPUsers.logins != null) return false;
        if (md5pw != null ? !md5pw.equals(OPUsers.md5pw) : OPUsers.md5pw != null) return false;
        if (nachname != null ? !nachname.equals(OPUsers.nachname) : OPUsers.nachname != null) return false;
        if (userstatus != null ? !userstatus.equals(OPUsers.userstatus) : OPUsers.userstatus != null) return false;
        if (version != null ? !version.equals(OPUsers.version) : OPUsers.version != null) return false;
        if (vorname != null ? !vorname.equals(OPUsers.vorname) : OPUsers.vorname != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uid != null ? uid.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (vorname != null ? vorname.hashCode() : 0);
        result = 31 * result + (nachname != null ? nachname.hashCode() : 0);
        result = 31 * result + (userstatus != null ? userstatus.hashCode() : 0);
        result = 31 * result + (md5pw != null ? md5pw.hashCode() : 0);
        result = 31 * result + (eMail != null ? eMail.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return getFullname() + (OPDE.isAnonym() ? "" : " [" + uid + "]");
    }

    public String getFullname() {
        String fullname = "";
        if (OPDE.isUserCipher()){
            fullname = "#"+cipherid;
        } else {
            fullname = getName() + ", " + getVorname();
        }
        return fullname;
    }

}
