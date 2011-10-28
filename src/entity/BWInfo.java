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

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "BWInfo")
@NamedQueries({
        @NamedQuery(name = "BWInfo.findAll", query = "SELECT b FROM BWInfo b"),
        @NamedQuery(name = "BWInfo.findByBwinfoid", query = "SELECT b FROM BWInfo b WHERE b.bwinfoid = :bwinfoid"),
        @NamedQuery(name = "BWInfo.findByVon", query = "SELECT b FROM BWInfo b WHERE b.von = :von"),
        @NamedQuery(name = "BWInfo.findByBewohnerByBWINFOTYP_DESC", query = "SELECT b FROM BWInfo b WHERE b.bewohner = :bewohner AND b.bwinfotyp = :bwinfotyp ORDER BY b.von DESC"),
        @NamedQuery(name = "BWInfo.findByBis", query = "SELECT b FROM BWInfo b WHERE b.bis = :bis"),
        @NamedQuery(name = "BWInfo.findByReiter", query = "SELECT b FROM BWInfo b WHERE b.reiter = :reiter")})
public class BWInfo implements Serializable, VorgangElement {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BWINFOID")
    private Long bwinfoid;
    @Basic(optional = false)
    @Column(name = "Von")
    @Temporal(TemporalType.TIMESTAMP)
    private Date von;
    @Basic(optional = false)
    @Column(name = "Bis")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bis;
    @Lob
    @Column(name = "XML")
    private String xml;
    @Lob
    @Column(name = "Bemerkung")
    private String bemerkung;
    @Column(name = "Reiter")
    private BigInteger reiter;
    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "BWINFTYP", referencedColumnName = "BWINFTYP")
    @ManyToOne
    private BWInfoTyp bwinfotyp;
    @JoinColumn(name = "AnUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users angesetztDurch;
    @JoinColumn(name = "AbUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users abgesetztDurch;
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Bewohner bewohner;
    // ==
    // M:N Relationen
    // ==
//    @ManyToMany
//    @JoinTable(name = "SYSBWINFO2VORGANG", joinColumns =
//    @JoinColumn(name = "BWInfoID"), inverseJoinColumns =
//    @JoinColumn(name = "VorgangID"))
//    private Collection<Vorgaenge> vorgaenge;


    public BWInfo() {
    }

    public BWInfo(Long bwinfoid) {
        this.bwinfoid = bwinfoid;
    }

    public Long getBwinfoid() {
        return bwinfoid;
    }

    public void setBwinfoid(Long bwinfoid) {
        this.bwinfoid = bwinfoid;
    }

    public Bewohner getBewohner() {
        return bewohner;
    }

    public BWInfoTyp getBwinfotyp() {
        return bwinfotyp;
    }

    public Date getVon() {
        return von;
    }

    public void setVon(Date von) {
        this.von = von;
    }

    public Date getBis() {
        return bis;
    }

    public void setBis(Date bis) {
        this.bis = bis;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public BigInteger getReiter() {
        return reiter;
    }

    public void setReiter(BigInteger reiter) {
        this.reiter = reiter;
    }

    public Users getAbgesetztDurch() {
        return abgesetztDurch;
    }

    public void setAbgesetztDurch(Users abgesetztDurch) {
        this.abgesetztDurch = abgesetztDurch;
    }

    public Users getAngesetztDurch() {
        return angesetztDurch;
    }

    public void setAngesetztDurch(Users angesetztDurch) {
        this.angesetztDurch = angesetztDurch;
    }

    @Override
    public long getPITInMillis() {
        return von.getTime();
    }

    @Override
    public String getContentAsHTML() {
        // TODO: fehlt noch
        return "<html>not yet</html>";
    }

    @Override
    public String getPITAsHTML() {
        // TODO: fehlt noch
        return "<html>not yet</html>";
    }

    @Override
    public long getID() {
        return bwinfoid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bwinfoid != null ? bwinfoid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BWInfo)) {
            return false;
        }
        BWInfo other = (BWInfo) object;
        if ((this.bwinfoid == null && other.bwinfoid != null) || (this.bwinfoid != null && !this.bwinfoid.equals(other.bwinfoid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.BWInfo[bwinfoid=" + bwinfoid + "]";
    }
}
