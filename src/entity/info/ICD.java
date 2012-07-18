/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.info;

import op.tools.SYSTools;

import java.io.Serializable;
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

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "ICD")
@NamedQueries({
    @NamedQuery(name = "Icd.findAll", query = "SELECT i FROM ICD i"),
    @NamedQuery(name = "Icd.findByIcdid", query = "SELECT i FROM ICD i WHERE i.icdid = :icdid"),
    @NamedQuery(name = "Icd.findByIcd10", query = "SELECT i FROM ICD i WHERE i.icd10 = :icd10")})
public class ICD implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ICDID")
    private Long icdid;
    @Basic(optional = false)
    @Column(name = "ICD10")
    private String icd10;
    @Basic(optional = false)
    @Lob
    @Column(name = "Text")
    private String text;

    public ICD() {
    }

    public ICD(Long icdid) {
        this.icdid = icdid;
    }

    public ICD(Long icdid, String icd10, String text) {
        this.icdid = icdid;
        this.icd10 = icd10;
        this.text = text;
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
        // TODO: Warning - this method won't work in the case the id fields are not set
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
