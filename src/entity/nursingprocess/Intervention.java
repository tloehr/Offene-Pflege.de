/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.nursingprocess;

import entity.info.ResInfoCategory;
import op.tools.SYSTools;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author tloehr
 */
@Entity
@Table(name = "intervention")
public class Intervention implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MassID")
    private Long massID;
    @Basic(optional = false)
    @Column(name = "Bezeichnung")
    private String bezeichnung;
    @Basic(optional = false)
    @Column(name = "Dauer")
    private BigDecimal dauer;
    @Basic(optional = false)
    @Column(name = "MassArt")
    private int interventionType;
    @Column(name = "Aktiv")
    private Boolean active;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "Flag")
    private int flag;

    @JoinColumn(name = "BWIKID", referencedColumnName = "BWIKID")
    @ManyToOne
    private ResInfoCategory category;

    public Intervention() {
    }

    public Intervention(Long massID) {
        this.massID = massID;
    }

    public Intervention(String bezeichnung, BigDecimal dauer, int interventionType, ResInfoCategory category) {
        this.bezeichnung = SYSTools.tidy(bezeichnung);
        this.dauer = dauer;
        this.interventionType = interventionType;
        this.category = category;
        this.active = true;
    }

    public Long getMassID() {
        return massID;
    }

    public void setMassID(Long massID) {
        this.massID = massID;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = SYSTools.tidy(bezeichnung);
    }

    public BigDecimal getDauer() {
        return dauer;
    }

    public void setDauer(BigDecimal dauer) {
        this.dauer = dauer;
    }

    public int getInterventionType() {
        return interventionType;
    }

    public void setInterventionType(int massArt) {
        this.interventionType = massArt;
    }

    public ResInfoCategory getCategory() {
        return category;
    }

    public void setCategory(ResInfoCategory kategorie) {
        this.category = kategorie;
    }

    public boolean isActive() {
        return active;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (massID != null ? massID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Intervention)) {
            return false;
        }
        Intervention other = (Intervention) object;
        if ((this.massID == null && other.massID != null) || (this.massID != null && !this.massID.equals(other.massID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return bezeichnung;
    }

}
