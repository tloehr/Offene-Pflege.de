/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.info;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author tloehr
 */
@Entity
@Table(name = "icd")

public class ICD implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "icdid")
    private Long icdid;
    @Basic(optional = false)
    @Column(name = "icd10")
    private String icd10;
    @Basic(optional = false)
    @Lob
    @Column(name = "Text")
    private String text;

    public ICD() {
    }

    public ICD(String code, String content) {
        this.icd10 = code;
        this.text = content;
    }

    public Long getIcdid() {
        return icdid;
    }

    public void setIcdid(Long icdid) {
        this.icdid = icdid;
    }

    public String getICD10() {
        return icd10;
    }

    public void setICD10(String icd10) {
        this.icd10 = icd10;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (icdid != null ? icdid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof ICD)) {
            return false;
        }
        ICD other = (ICD) object;
        if ((this.icdid == null && other.icdid != null) || (this.icdid != null && !this.icdid.equals(other.icdid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return icd10 + " " + text;
    }

}
