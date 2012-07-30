/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "QSQuery")
@NamedQueries({
    @NamedQuery(name = "QSQuery.findAll", query = "SELECT q FROM QSQuery q"),
    @NamedQuery(name = "QSQuery.findByQsqid", query = "SELECT q FROM QSQuery q WHERE q.qsqid = :qsqid"),
    @NamedQuery(name = "QSQuery.findByText", query = "SELECT q FROM QSQuery q WHERE q.text = :text"),
    @NamedQuery(name = "QSQuery.findByKategorie", query = "SELECT q FROM QSQuery q WHERE q.kategorie = :kategorie"),
    @NamedQuery(name = "QSQuery.findByCdate", query = "SELECT q FROM QSQuery q WHERE q.cdate = :cdate")})
public class QSQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "QSQID")
    private Long qsqid;
    @Basic(optional = false)
    @Column(name = "Text")
    private String text;
    @Basic(optional = false)
    @Lob
    @Column(name = "SEQUEL")
    private String sequel;
    @Basic(optional = false)
    @Lob
    @Column(name = "PARAMS")
    private String params;
    @Basic(optional = false)
    @Lob
    @Column(name = "FORMAT")
    private String format;
    @Basic(optional = false)
    @Column(name = "Kategorie")
    private long kategorie;
    @Basic(optional = false)
    @Column(name = "_cdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cdate;

    public QSQuery() {
    }

    public QSQuery(Long qsqid) {
        this.qsqid = qsqid;
    }

    public QSQuery(Long qsqid, String text, String sequel, String params, String format, long kategorie, Date cdate) {
        this.qsqid = qsqid;
        this.text = text;
        this.sequel = sequel;
        this.params = params;
        this.format = format;
        this.kategorie = kategorie;
        this.cdate = cdate;
    }

    public Long getQsqid() {
        return qsqid;
    }

    public void setQsqid(Long qsqid) {
        this.qsqid = qsqid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSequel() {
        return sequel;
    }

    public void setSequel(String sequel) {
        this.sequel = sequel;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public long getKategorie() {
        return kategorie;
    }

    public void setKategorie(long kategorie) {
        this.kategorie = kategorie;
    }

    public Date getCdate() {
        return cdate;
    }

    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (qsqid != null ? qsqid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof QSQuery)) {
            return false;
        }
        QSQuery other = (QSQuery) object;
        if ((this.qsqid == null && other.qsqid != null) || (this.qsqid != null && !this.qsqid.equals(other.qsqid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.QSQuery[qsqid=" + qsqid + "]";
    }

}
