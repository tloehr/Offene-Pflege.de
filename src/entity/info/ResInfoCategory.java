/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.info;

import op.tools.SYSTools;

import javax.persistence.*;
import java.awt.*;
import java.io.Serializable;
import java.text.Collator;

/**
 * @author tloehr
 */
@Entity
@Table(name = "BWInfoKat")
//@NamedQueries({
//        @NamedQuery(name = "BWInfoKat.findAll", query = "SELECT b FROM ResInfoCategory b"),
//        @NamedQuery(name = "BWInfoKat.findByBwikid", query = "SELECT b FROM ResInfoCategory b WHERE b.bwikid = :bwikid"),
//        @NamedQuery(name = "BWInfoKat.findByBezeichnung", query = "SELECT b FROM ResInfoCategory b WHERE b.bezeichnung = :bezeichnung"),
//        @NamedQuery(name = "BWInfoKat.findByKatArt", query = "SELECT b FROM ResInfoCategory b WHERE b.katArt = :katArt"),
//        @NamedQuery(name = "BWInfoKat.findBySortierung", query = "SELECT b FROM ResInfoCategory b WHERE b.sortierung = :sortierung")})
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
    @Column(name = "BGHEADER")
    private String bgheader;
    @Column(name = "BGCONTENT")
    private String bgcontent;
    @Column(name = "FGHEADER")
    private String fgheader;
    @Column(name = "FGCONTENT")
    private String fgcontent;


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

    public Color getForegroundHeader() {
        return SYSTools.getColor(fgheader);
    }

    public Color getForegroundContent() {
        int red = Integer.parseInt(fgcontent.substring(0, 2), 16);
        int green = Integer.parseInt(fgcontent.substring(2, 4), 16);
        int blue = Integer.parseInt(fgcontent.substring(4), 16);

        return new Color(red, green, blue);
    }

    public Color getBackgroundHeader() {
        int red = Integer.parseInt(bgheader.substring(0, 2), 16);
        int green = Integer.parseInt(bgheader.substring(2, 4), 16);
        int blue = Integer.parseInt(bgheader.substring(4), 16);

        return new Color(red, green, blue);
    }

    public Color getBackgroundContent() {
        int red = Integer.parseInt(bgcontent.substring(0, 2), 16);
        int green = Integer.parseInt(bgcontent.substring(2, 4), 16);
        int blue = Integer.parseInt(bgcontent.substring(4), 16);

        return new Color(red, green, blue);
    }

    public String getBgheader() {
        return bgheader;
    }

    public String getBgcontent() {
        return bgcontent;
    }

    public String getFgheader() {
        return fgheader;
    }

    public String getFgcontent() {
        return fgcontent;
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
