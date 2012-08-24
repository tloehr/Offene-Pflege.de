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
package entity.files;

import entity.info.Resident;
import entity.Users;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "SYSFiles")
@NamedQueries({
        @NamedQuery(name = "SYSFiles.findAll", query = "SELECT s FROM SYSFiles s"),
        @NamedQuery(name = "SYSFiles.findByOcfid", query = "SELECT s FROM SYSFiles s WHERE s.ocfid = :ocfid"),
        @NamedQuery(name = "SYSFiles.findByBWKennung", query = "SELECT s FROM SYSFiles s WHERE s.resident = :bewohner"),
        @NamedQuery(name = "SYSFiles.findByFilename", query = "SELECT s FROM SYSFiles s WHERE s.filename = :filename"),
        @NamedQuery(name = "SYSFiles.findByMd5", query = "SELECT s FROM SYSFiles s WHERE s.md5 = :md5"),
        @NamedQuery(name = "SYSFiles.findByFiledate", query = "SELECT s FROM SYSFiles s WHERE s.filedate = :filedate"),
        @NamedQuery(name = "SYSFiles.findByFilesize", query = "SELECT s FROM SYSFiles s WHERE s.filesize = :filesize"),
        @NamedQuery(name = "SYSFiles.findByPit", query = "SELECT s FROM SYSFiles s WHERE s.pit = :pit"),
//        @NamedQuery(name = "SYSFiles.findByBWKennung2VER", query = ""
//                + " SELECT s"
//                + " FROM SYSFiles s "
//                + " JOIN s.verAssignCollection sf "
//                + " WHERE sf.prescription = :prescription"),
        @NamedQuery(name = "SYSFiles.findByNReport", query = ""
                + " SELECT s "
                + " FROM SYSFiles s "
                + " JOIN s.pbAssignCollection sf "
                + " WHERE sf.nReport = :nReport "),
        @NamedQuery(name = "SYSFiles.findByBWInfo", query = ""
                + " SELECT s "
                + " FROM SYSFiles s "
                + " JOIN s.bwiAssignCollection sf "
                + " WHERE sf.bwinfo = :bwinfo "),
        @NamedQuery(name = "SYSFiles.findByVerordnung", query = ""
                + " SELECT s "
                + " FROM SYSFiles s "
                + " JOIN s.verAssignCollection sf "
                + " WHERE sf.verordnung = :verordnung ")
})
public class SYSFiles implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "OCFID")
    private Long ocfid;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "Filename")
    private String filename;
    @Basic(optional = false)
    @Column(name = "MD5")
    private String md5;
    @Basic(optional = false)
    @Column(name = "Filedate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date filedate;
    @Basic(optional = false)
    @Column(name = "Filesize")
    private long filesize;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;
    @Basic(optional = false)
    @Column(name = "Beschreibung")
    private String beschreibung;
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;
    //    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sysfile")
//    private Collection<Sysbw2file> bwAssignCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sysfile")
    private Collection<SYSNR2FILE> pbAssignCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sysfile")
    private Collection<SYSINF2FILE> bwiAssignCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sysfile")
    private Collection<SYSPRE2FILE> verAssignCollection;

    public SYSFiles() {
    }

    public SYSFiles(String filename, String md5, Date filedate, long filesize, Users user, Resident resident) {
        this.filename = filename;
        this.md5 = md5;
        this.filedate = filedate;
        this.filesize = filesize;
        this.user = user;
        this.pit = new Date();
        this.resident = resident;
    }


//    public Collection<Sysbw2file> getBwAssignCollection() {
//        return bwAssignCollection;
//    }

    public Collection<SYSINF2FILE> getBwiAssignCollection() {
        return bwiAssignCollection;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Collection<SYSNR2FILE> getPbAssignCollection() {
        return pbAssignCollection;
    }

    public Collection<SYSPRE2FILE> getVerAssignCollection() {
        return verAssignCollection;
    }

    public Long getOcfid() {
        return ocfid;
    }

    public void setOcfid(Long ocfid) {
        this.ocfid = ocfid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMd5() {
        return md5;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public String getRemoteFilename() {
        return md5 + ".sysfile";
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public Date getFiledate() {
        return filedate;
    }

    public void setFiledate(Date filedate) {
        this.filedate = filedate;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ocfid != null ? ocfid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof SYSFiles)) {
            return false;
        }
        SYSFiles other = (SYSFiles) object;
        if ((this.ocfid == null && other.ocfid != null) || (this.ocfid != null && !this.ocfid.equals(other.ocfid))) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Object o) {

        return getFilename().compareTo(((SYSFiles) o).getFilename());
    }

    @Override
    public String toString() {
        return "entity.files.SYSFiles[ocfid=" + ocfid + "] " + filename;
    }
}
