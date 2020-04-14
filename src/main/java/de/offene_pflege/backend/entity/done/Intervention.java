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
@Table(name = "intervention")
public class Intervention extends DefaultEntity {
    private String bezeichnung;
    private int interventionType;
    private Boolean active;
    private int flag;
    private ResInfoCategory category;

    @Basic(optional = false)
    @Column(name = "Bezeichnung")
    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    @Basic(optional = false)
    @Column(name = "MassArt")
    public int getInterventionType() {
        return interventionType;
    }

    public void setInterventionType(int interventionType) {
        this.interventionType = interventionType;
    }

    @Column(name = "Aktiv")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Basic(optional = false)
    @Column(name = "Flag")
    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @JoinColumn(name = "BWIKID", referencedColumnName = "BWIKID")
    @ManyToOne
    public ResInfoCategory getCategory() {
        return category;
    }

    public void setCategory(ResInfoCategory category) {
        this.category = category;
    }

    public Intervention() {
    }

    @Override
    public String toString() {
        return bezeichnung;
    }

}
