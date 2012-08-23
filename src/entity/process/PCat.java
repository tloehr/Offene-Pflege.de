/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.process;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author tloehr
 */
@Entity
@Table(name = "VKat")
public class PCat implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VKatID")
    private Long id;
    @Basic(optional = false)
    @Column(name = "Text")
    private String text;
    @Basic(optional = false)
    @Column(name = "Art")
    private short art;

    public PCat() {
    }

    public Long getId() {
        return id;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PCat pCat = (PCat) o;

        if (art != pCat.art) return false;
        if (id != null ? !id.equals(pCat.id) : pCat.id != null) return false;
        if (text != null ? !text.equals(pCat.text) : pCat.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (int) art;
        return result;
    }

    @Override
    public String toString() {
        return text;
    }

}
