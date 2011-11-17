package entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class QsQuery {
    private long qsqid;

    @javax.persistence.Column(name = "QSQID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getQsqid() {
        return qsqid;
    }

    public void setQsqid(long qsqid) {
        this.qsqid = qsqid;
    }

    private String text;

    @javax.persistence.Column(name = "Text", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String sequel;

    @javax.persistence.Column(name = "SEQUEL", nullable = false, insertable = true, updatable = true, length = 16777215, precision = 0)
    @Basic
    public String getSequel() {
        return sequel;
    }

    public void setSequel(String sequel) {
        this.sequel = sequel;
    }

    private String params;

    @javax.persistence.Column(name = "PARAMS", nullable = false, insertable = true, updatable = true, length = 16777215, precision = 0)
    @Basic
    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    private String format;

    @javax.persistence.Column(name = "FORMAT", nullable = false, insertable = true, updatable = true, length = 16777215, precision = 0)
    @Basic
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    private long kategorie;

    @javax.persistence.Column(name = "Kategorie", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getKategorie() {
        return kategorie;
    }

    public void setKategorie(long kategorie) {
        this.kategorie = kategorie;
    }

    private Timestamp cdate;

    @javax.persistence.Column(name = "_cdate", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getCdate() {
        return cdate;
    }

    public void setCdate(Timestamp cdate) {
        this.cdate = cdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QsQuery qsQuery = (QsQuery) o;

        if (kategorie != qsQuery.kategorie) return false;
        if (qsqid != qsQuery.qsqid) return false;
        if (cdate != null ? !cdate.equals(qsQuery.cdate) : qsQuery.cdate != null) return false;
        if (format != null ? !format.equals(qsQuery.format) : qsQuery.format != null) return false;
        if (params != null ? !params.equals(qsQuery.params) : qsQuery.params != null) return false;
        if (sequel != null ? !sequel.equals(qsQuery.sequel) : qsQuery.sequel != null) return false;
        if (text != null ? !text.equals(qsQuery.text) : qsQuery.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (qsqid ^ (qsqid >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (sequel != null ? sequel.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (int) (kategorie ^ (kategorie >>> 32));
        result = 31 * result + (cdate != null ? cdate.hashCode() : 0);
        return result;
    }
}
