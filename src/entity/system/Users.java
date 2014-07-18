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
package entity.system;

import entity.files.*;
import entity.qms.Qmsplan;
import entity.reports.NReport;
import entity.staff.Training;
import entity.staff.Training2Users;
import op.tools.SYSTools;
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
@Table(name = "users")
@OptimisticLocking(cascade = false, type = OptimisticLockingType.VERSION_COLUMN)
public class Users implements Serializable, Comparable<Users> {
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
    @Column(name = "Status")
    private Short status;
    @Basic(optional = false)
    @Column(name = "MD5PW")
    private String md5pw;
    @Column(name = "EMail")
    private String eMail;
    @Basic(optional = false)
    @Column(name = "mailconfirmed")
    private int mailstatus;

    @ManyToMany
    @JoinTable(name = "member", joinColumns =
    @JoinColumn(name = "UKennung"), inverseJoinColumns =
    @JoinColumn(name = "GKennung"))
    private Collection<Groups> groups;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<SYSFiles> sysfilesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<SYSINF2FILE> SYSINF2FILECollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<SYSNR2FILE> SYSNR2FILECollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<SYSPRE2FILE> SYSPRE2FILECollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<NReport> NReport;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "editedBy")
    private Collection<NReport> korrigierteNReport;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<SYSLogin> logins;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "")
    private Collection<Training> trainings;

    @ManyToMany(mappedBy = "notification")
    private Collection<Qmsplan> notifiedAboutQmsplan;

    public Collection<User2File> getAttachedFilesConnections() {
        return attachedFilesConnections;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<User2File> attachedFilesConnections;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "attendee")
    private Collection<Training2Users> attendedTrainings;

    public Users() {
        uid = null;
        groups = new ArrayList<Groups>();
        status = UsersTools.STATUS_ACTIVE;
        mailstatus = UsersTools.MAIL_UNCONFIRMED;
    }

    public int getMailStatus() {
        return mailstatus;
    }

    public void setMailStatus(int mailconfirmed) {
        this.mailstatus = mailconfirmed;
    }

    public Collection<Training> getTrainings() {
        return trainings;
    }

    public String getUID() {
        return uid;
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

//    @Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (uid != null ? uid.hashCode() : 0);
//        return hash;
//    }

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
    public int compareTo(Users o) {
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

        Users users = (Users) o;

        if (eMail != null ? !eMail.equals(users.eMail) : users.eMail != null) return false;
        if (logins != null ? !logins.equals(users.logins) : users.logins != null) return false;
        if (md5pw != null ? !md5pw.equals(users.md5pw) : users.md5pw != null) return false;
        if (nachname != null ? !nachname.equals(users.nachname) : users.nachname != null) return false;
        if (status != null ? !status.equals(users.status) : users.status != null) return false;
        if (version != null ? !version.equals(users.version) : users.version != null) return false;
        if (vorname != null ? !vorname.equals(users.vorname) : users.vorname != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uid != null ? uid.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (vorname != null ? vorname.hashCode() : 0);
        result = 31 * result + (nachname != null ? nachname.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (md5pw != null ? md5pw.hashCode() : 0);
        result = 31 * result + (eMail != null ? eMail.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return getFullname() + " [" + uid + "]";
    }

    public String getFullname() {
        return getName() + ", " + getVorname();
    }
}
