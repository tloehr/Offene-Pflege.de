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
package entity.info;

import entity.Users;
import entity.files.SYSBWI2FILE;
import entity.process.QProcessElement;
import entity.process.SYSINF2PROCESS;
import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

/**
 * @author tloehr
 */
@Entity
@Table(name = "BWInfo")
@NamedQueries({
        @NamedQuery(name = "BWInfo.findAll", query = "SELECT b FROM BWInfo b"),
        @NamedQuery(name = "BWInfo.findByBwinfoid", query = "SELECT b FROM BWInfo b WHERE b.bwinfoid = :bwinfoid"),
        @NamedQuery(name = "BWInfo.findByVorgang", query = " "
                + " SELECT bw FROM BWInfo bw "
                + " JOIN bw.attachedVorgaenge av"
                + " JOIN av.vorgang v"
                + " WHERE v = :vorgang "),
        @NamedQuery(name = "BWInfo.findByVon", query = "SELECT b FROM BWInfo b WHERE b.von = :von"),
        @NamedQuery(name = "BWInfo.findByBewohnerByBWINFOTYP_DESC", query = "SELECT b FROM BWInfo b WHERE b.bewohner = :bewohner AND b.bwinfotyp = :bwinfotyp ORDER BY b.von DESC"),
        @NamedQuery(name = "BWInfo.findByBewohnerByBWINFOTYP_ASC", query = "SELECT b FROM BWInfo b WHERE b.bewohner = :bewohner AND b.bwinfotyp = :bwinfotyp ORDER BY b.von ASC"),
        @NamedQuery(name = "BWInfo.findByBis", query = "SELECT b FROM BWInfo b WHERE b.bis = :bis")})
public class BWInfo implements Serializable, QProcessElement, Cloneable, Comparable<BWInfo> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BWINFOID")
    private Long bwinfoid;
    @Version
    @Column(name = "version")
    private Long version;
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
    @Column(name = "HTML")
    private String html;
    @Lob
    @Column(name = "Properties")
    private String properties;
    @Lob
    @Column(name = "Bemerkung")
    private String bemerkung;
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
    private Resident bewohner;
    // ==
    // M:N Relationen
    // ==
    // ==
    // 1:N Relationen
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bwinfo")
    private Collection<SYSBWI2FILE> attachedFiles;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bwinfo")
    private Collection<SYSINF2PROCESS> attachedVorgaenge;


    public BWInfo() {
    }

    public BWInfo(BWInfoTyp bwinfotyp, Resident bewohner) {
        this.properties = "";
        Date now = new Date();

        if (bwinfotyp.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
            this.von = now;
            this.bis = now;
        } else if (bwinfotyp.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_BYDAY) {
            this.von = new DateTime().toDateMidnight().toDate();
            this.bis = SYSConst.DATE_BIS_AUF_WEITERES;
        } else {
            this.von = now;
            this.bis = SYSConst.DATE_BIS_AUF_WEITERES;
        }

        this.bwinfotyp = bwinfotyp;
        this.angesetztDurch = OPDE.getLogin().getUser();
        this.bewohner = bewohner;
        this.attachedFiles = new ArrayList<SYSBWI2FILE>();
        this.attachedVorgaenge = new ArrayList<SYSINF2PROCESS>();
    }

    public BWInfo(Date von, Date bis, String xml, String html, String properties, String bemerkung, BWInfoTyp bwinfotyp, Resident bewohner) {
        this.von = von;
        this.bis = bis;
        this.xml = xml;
        this.html = html;
        this.properties = properties;
        this.bemerkung = bemerkung;
        this.bwinfotyp = bwinfotyp;
        this.angesetztDurch = OPDE.getLogin().getUser();
        this.abgesetztDurch = null;
        this.bewohner = bewohner;
        this.attachedFiles = new ArrayList<SYSBWI2FILE>();
        this.attachedVorgaenge = new ArrayList<SYSINF2PROCESS>();
    }

    public Long getBwinfoid() {
        return bwinfoid;
    }

    public void setBwinfoid(Long bwinfoid) {
        this.bwinfoid = bwinfoid;
    }

    public Resident getBewohner() {
        return bewohner;
    }

    public BWInfoTyp getBwinfotyp() {
        return bwinfotyp;
    }

    public Date getVon() {
        return von;
    }

    public void setVon(Date von) {
        if (bwinfotyp.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_BYDAY) {
            von = new DateTime(von).toDateMidnight().toDate();
        }
        this.von = von;
        if (bwinfotyp.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
            this.bis = von;
        }
    }

    public Date getBis() {
        return bis;
    }

    public void setBis(Date bis) {
        if (bwinfotyp.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_BYDAY) {
            bis = new DateTime(bis).toDateMidnight().plusDays(1).toDateTime().minusMinutes(1).toDate();
        }
        this.bis = bis;
        if (bwinfotyp.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
            this.von = bis;
        }
    }

    public boolean isHeimaufnahme() {
        return bwinfotyp.getBwinftyp().equalsIgnoreCase("hauf");
    }

    public String getXml() {
        return xml;
    }

    public String getHtml() {
        return SYSTools.anonymizeString(html);
    }

    public void setHtml(String html) {
        this.html = html;
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

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
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


    public Collection<SYSBWI2FILE> getAttachedFiles() {
        return attachedFiles;
    }

    public Collection<SYSINF2PROCESS> getAttachedVorgaenge() {
        return attachedVorgaenge;
    }

    @Override
    public long getPITInMillis() {
        return von.getTime();
    }

    /**
     * SingleIncidents können nicht abgesetzt sein. Ansonsten, dann, wenn bis vor dem aktuellen Zeitpunkt liegt.
     *
     * @return
     */
    public boolean isAbgesetzt() {
        return bwinfotyp.getIntervalMode() != BWInfoTypTools.MODE_INTERVAL_SINGLE_INCIDENTS && bis.before(new Date());
    }

    public boolean isSingleIncident() {
        return bwinfotyp.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_SINGLE_INCIDENTS;
    }

    public boolean isNoConstraints() {
        return bwinfotyp.getIntervalMode() == BWInfoTypTools.MODE_INTERVAL_NOCONSTRAINTS;
    }

    public boolean isActiveNoConstraint() {
        return isNoConstraints() && !isAbgesetzt();
    }

    @Override
    public String getContentAsHTML() {
        return html;
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
    public int compareTo(BWInfo bwInfo) {
        if (bwInfo.getBwinfotyp().getStatus() == BWInfoTypTools.STATUS_NORMAL) {
            return 0;
        } else if (getBwinfotyp().getBwinftyp().equalsIgnoreCase(BWInfoTypTools.TYP_DIAGNOSE) || bwInfo.getBwinfotyp().getBwinftyp().equalsIgnoreCase(BWInfoTypTools.TYP_DIAGNOSE)) {
            Properties thisProps = BWInfoTools.getContent(this);
            Properties thatProps = BWInfoTools.getContent(bwInfo);
            String thisICD = thisProps.getProperty("icd");
            String thatICD = thatProps.getProperty("icd");
            return thisICD.compareTo(thatICD);
        } else {
            return 0;
        }
    }

    @Override
    public String getTitle() {
        return bwinfotyp.getBWInfoKurz();
    }

    @Override
    public boolean equals(Object object) {

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
    public BWInfo clone() {
        return new BWInfo(von, bis, xml, html, properties, bemerkung, bwinfotyp, bewohner);
    }

    @Override
    public String toString() {
        return "entity.info.BWInfo[bwinfoid=" + bwinfoid + "]";
    }
}
