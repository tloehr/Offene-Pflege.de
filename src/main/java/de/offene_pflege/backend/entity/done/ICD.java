/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;

import javax.persistence.*;

/**
 * @author tloehr
 */
@Entity
@Table(name = "icd")

public class ICD extends DefaultEntity {
    private String icd10;
    private String text;

    public ICD() {
    }

    @Basic(optional = false)
    @Column(name = "icd10")
    public String getIcd10() {
        return icd10;
    }

    public void setIcd10(String icd10) {
        this.icd10 = icd10;
    }

    @Basic(optional = false)
    @Lob
    @Column(name = "Text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
