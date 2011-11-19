/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.vorgang;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author tloehr
 */
@Entity
@Table(name = "VKat")
@NamedQueries({
        @NamedQuery(name = "VKat.findAll", query = "SELECT v FROM VKat v"),
        @NamedQuery(name = "VKat.findAllSorted", query = "SELECT v FROM VKat v ORDER BY v.text"),
        @NamedQuery(name = "VKat.findByVKatID", query = "SELECT v FROM VKat v WHERE v.vKatID = :vKatID"),
        @NamedQuery(name = "VKat.findByText", query = "SELECT v FROM VKat v WHERE v.text = :text"),
        @NamedQuery(name = "VKat.findByArt", query = "SELECT v FROM VKat v WHERE v.art = :art")})
public class VKat implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "VKatID")
    private Long vKatID;
    @Basic(optional = false)
    @Column(name = "Text")
    private String text;
    @Basic(optional = false)
    @Column(name = "Art")
    private short art;

    public VKat() {
    }

    public VKat(Long vKatID) {
        this.vKatID = vKatID;
    }

    public VKat(String text) {
        this.text = text;
        this.art = VKatTools.VKAT_ART_ALLGEMEIN;
    }

    public Long getVKatID() {
        return vKatID;
    }

    public void setVKatID(Long vKatID) {
        this.vKatID = vKatID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public short getArt() {
        return art;
    }

    public void setArt(short art) {
        this.art = art;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (vKatID != null ? vKatID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VKat)) {
            return false;
        }
        VKat other = (VKat) object;
        if ((this.vKatID == null && other.vKatID != null) || (this.vKatID != null && !this.vKatID.equals(other.vKatID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return text;
    }

}
