/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.info;

import javax.persistence.*;
import java.io.Serializable;
import java.text.Collator;
import java.util.Collection;

/**
 * @author tloehr
 */
@Entity
@Table(name = "BWInfoKat")
public class ResInfoCategory implements Serializable, Comparable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "BWIKID")
    private Long bwikid;
    @Column(name = "Bezeichnung")
    private String bezeichnung;
    @Column(name = "KatArt")
    private Integer katArt;
    @Column(name = "Sortierung")
    private Integer sortierung;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resInfoCat")
    private Collection<ResInfoType> resInfoTypes;

    public ResInfoCategory() {
    }

    public ResInfoCategory(Long bwikid) {
        this.bwikid = bwikid;
    }

    public Long getID() {
        return bwikid;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public Integer getKatArt() {
        return katArt;
    }

    public void setKatArt(Integer katArt) {
        this.katArt = katArt;
    }

    public Integer getSortierung() {
        return sortierung;
    }

    public void setSortierung(Integer sortierung) {
        this.sortierung = sortierung;
    }

    public Collection<ResInfoType> getResInfoTypes() {
        return resInfoTypes;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bwikid != null ? bwikid.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Object o) {
        final Collator collator = Collator.getInstance();
        collator.setStrength(Collator.SECONDARY);// a == A, a < Ä
        return collator.compare(bezeichnung, ((ResInfoCategory) o).getBezeichnung());
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof ResInfoCategory)) {
            return false;
        }
        ResInfoCategory other = (ResInfoCategory) object;
        if ((this.bwikid == null && other.bwikid != null) || (this.bwikid != null && !this.bwikid.equals(other.bwikid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return bezeichnung;
    }

}
