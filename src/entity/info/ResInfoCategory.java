/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.info;

import op.OPDE;

import javax.persistence.*;
import java.io.Serializable;
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
    private Long id;
    @Column(name = "Bezeichnung")
    private String text;
    @Column(name = "KatArt")
    private Integer catType;
    @Column(name = "Sortierung")
    private Integer sort;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resInfoCat")
    private Collection<ResInfoType> resInfoTypes;

    public ResInfoCategory() {
    }

    public ResInfoCategory(Long id) {
        this.id = id;
    }

    public Long getID() {
        return id;
    }

    public String getText() {
        return text;
    }

//    @Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (id != null ? id.hashCode() : 0);
//        return hash;
//    }

    @Override
    public int compareTo(Object o) {
        return id.compareTo(((ResInfoCategory) o).getID());
//        OPDE.debug("START");
//        OPDE.debug(text + " <> "+((ResInfoCategory) o).getText());
//        OPDE.debug(text.compareTo(((ResInfoCategory) o).getText()));
//        OPDE.debug("READY");
//        return text.compareTo(((ResInfoCategory) o).getText());
//        final Collator collator = Collator.getInstance();
//        collator.setStrength(Collator.SECONDARY);// a == A, a < Ä
//        return collator.compare(bezeichnung, ((ResInfoCategory) o).getText());
    }

//    @Override
//    public boolean equals(Object object) {
//        if (!(object instanceof ResInfoCategory)) {
//            return false;
//        }
//        ResInfoCategory other = (ResInfoCategory) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResInfoCategory that = (ResInfoCategory) o;

        if (catType != null ? !catType.equals(that.catType) : that.catType != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (resInfoTypes != null ? !resInfoTypes.equals(that.resInfoTypes) : that.resInfoTypes != null) return false;
        if (sort != null ? !sort.equals(that.sort) : that.sort != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (catType != null ? catType.hashCode() : 0);
        result = 31 * result + (sort != null ? sort.hashCode() : 0);
        result = 31 * result + (resInfoTypes != null ? resInfoTypes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return text;
    }

}
