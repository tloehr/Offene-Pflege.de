/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.info;

import javax.persistence.*;
import java.io.Serializable;
import java.text.Collator;

/**
 * @author tloehr
 */
@Entity
@Table(name = "BWInfoKat")
@NamedQueries({
        @NamedQuery(name = "BWInfoKat.findAll", query = "SELECT b FROM BWInfoKat b"),
        @NamedQuery(name = "BWInfoKat.findByBwikid", query = "SELECT b FROM BWInfoKat b WHERE b.bwikid = :bwikid"),
        @NamedQuery(name = "BWInfoKat.findByBezeichnung", query = "SELECT b FROM BWInfoKat b WHERE b.bezeichnung = :bezeichnung"),
        @NamedQuery(name = "BWInfoKat.findByKatArt", query = "SELECT b FROM BWInfoKat b WHERE b.katArt = :katArt"),
        @NamedQuery(name = "BWInfoKat.findBySortierung", query = "SELECT b FROM BWInfoKat b WHERE b.sortierung = :sortierung")})
public class BWInfoKat implements Serializable, Comparable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "BWIKID")
    private Long bwikid;
    @Column(name = "Bezeichnung")
    private String bezeichnung;
    @Column(name = "KatArt")
    private Integer katArt;
    @Column(name = "Sortierung")
    private Integer sortierung;

    public BWInfoKat() {
    }

    public BWInfoKat(Long bwikid) {
        this.bwikid = bwikid;
    }

    public Long getBwikid() {
        return bwikid;
    }

    public void setBwikid(Long bwikid) {
        this.bwikid = bwikid;
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
        collator.compare("boo", "bär");

        return collator.compare(bezeichnung, ((BWInfoKat) o).getBezeichnung());
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BWInfoKat)) {
            return false;
        }
        BWInfoKat other = (BWInfoKat) object;
        if ((this.bwikid == null && other.bwikid != null) || (this.bwikid != null && !this.bwikid.equals(other.bwikid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.info.BWInfoKat[bwikid=" + bwikid + "]";
    }

}
