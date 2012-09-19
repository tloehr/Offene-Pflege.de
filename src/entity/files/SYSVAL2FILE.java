package entity.files;
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

import entity.values.ResValue;
import entity.system.Users;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "SYSVAL2FILE")
public class SYSVAL2FILE implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;
    @JoinColumn(name = "ForeignID", referencedColumnName = "BWID")
    @ManyToOne
    private ResValue value;
    @JoinColumn(name = "FID", referencedColumnName = "OCFID")
    @ManyToOne
    private SYSFiles sysfile;
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    public SYSVAL2FILE() {
    }

    public SYSVAL2FILE(SYSFiles sysfile, ResValue value, Users user, Date pit) {
        this.sysfile = sysfile;
        this.value = value;
        this.user = user;
        this.pit = pit;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }


    public Date getPit() {
        return pit;
    }

    public SYSFiles getSysfile() {
        return sysfile;
    }

    public ResValue getValue() {
        return value;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SYSVAL2FILE that = (SYSVAL2FILE) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (pit != null ? !pit.equals(that.pit) : that.pit != null) return false;
        if (sysfile != null ? !sysfile.equals(that.sysfile) : that.sysfile != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public String toString() {
        return "SYSVAL2FILE{" +
                "id=" + id +
                ", pit=" + pit +
                ", value=" + value +
                ", sysfile=" + sysfile +
                ", user=" + user +
                '}';
    }
}
