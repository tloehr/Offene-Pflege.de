package entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Icd {
    private long icdid;

    @javax.persistence.Column(name = "ICDID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getIcdid() {
        return icdid;
    }

    public void setIcdid(long icdid) {
        this.icdid = icdid;
    }

    private String icd10;

    @javax.persistence.Column(name = "ICD10", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getIcd10() {
        return icd10;
    }

    public void setIcd10(String icd10) {
        this.icd10 = icd10;
    }

    private String text;

    @javax.persistence.Column(name = "Text", nullable = false, insertable = true, updatable = true, length = 16777215, precision = 0)
    @Basic
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Icd icd = (Icd) o;

        if (icdid != icd.icdid) return false;
        if (icd10 != null ? !icd10.equals(icd.icd10) : icd.icd10 != null) return false;
        if (text != null ? !text.equals(icd.text) : icd.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (icdid ^ (icdid >>> 32));
        result = 31 * result + (icd10 != null ? icd10.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
}
