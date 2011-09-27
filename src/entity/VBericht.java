/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import op.OPDE;
import op.tools.SYSTools;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "vbericht")
@NamedQueries({
        @NamedQuery(name = "VBericht.findAll", query = "SELECT v FROM VBericht v"),
        @NamedQuery(name = "VBericht.findByVbid", query = "SELECT v FROM VBericht v WHERE v.vbid = :vbid"),
        @NamedQuery(name = "VBericht.findByPit", query = "SELECT v FROM VBericht v WHERE v.pit = :pit"),
        @NamedQuery(name = "VBericht.findByArt", query = "SELECT v FROM VBericht v WHERE v.art = :art"),
        @NamedQuery(name = "VBericht.findByVorgang", query = "SELECT v FROM VBericht v WHERE v.vorgang = :vorgang "),
        // 0 heisst nur normale Berichte.
        // VBERICHT_ART_USER = 0;
        @NamedQuery(name = "VBericht.findByVorgangOhneSystem", query = "SELECT v FROM VBericht v WHERE v.vorgang = :vorgang AND v.art = 0"),
        @NamedQuery(name = "VBericht.findByPdca", query = "SELECT v FROM VBericht v WHERE v.pdca = :pdca")})
public class VBericht implements Serializable, VorgangElement {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "VBID")
    private Long vbid;
    @Lob
    @Column(name = "Text")
    private String text;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;
    @Basic(optional = false)
    @Column(name = "Art")
    private short art;
    @Basic(optional = false)
    @Column(name = "PDCA")
    private short pdca;
    @JoinColumn(name = "VorgangID", referencedColumnName = "VorgangID")
    @ManyToOne
    private Vorgaenge vorgang;
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    public VBericht() {
    }

    public VBericht(String text, short art, Vorgaenge vorgang) {
        this.text = text;
        this.art = art;
        this.vorgang = vorgang;
        this.pdca = VorgaengeTools.PDCA_OFF;
        this.pit = new Date();
        this.user = OPDE.getLogin().getUser();
    }

    public Long getVbid() {
        return vbid;
    }

    public void setVbid(Long vbid) {
        this.vbid = vbid;
    }

    public Vorgaenge getVorgang() {
        return vorgang;
    }

    public void setVorgang(Vorgaenge vorgang) {
        this.vorgang = vorgang;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

    public short getArt() {
        return art;
    }

    public void setArt(short art) {
        this.art = art;
    }

    public short getPdca() {
        return pdca;
    }

    public void setPdca(short pdca) {
        this.pdca = pdca;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public boolean isSystem() {
        return art != VBerichtTools.VBERICHT_ART_USER;
    }

    @Override
    public String getContentAsHTML() {
        return VBerichtTools.getBerichtAsHTML(this);
    }

    @Override
    public long getPITInMillis() {
        return pit.getTime();
    }

    @Override
    public String getPITAsHTML() {
        return VBerichtTools.getPITAsHTML(this);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (vbid != null ? vbid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VBericht)) {
            return false;
        }
        VBericht other = (VBericht) object;
        if ((this.vbid == null && other.vbid != null) || (this.vbid != null && !this.vbid.equals(other.vbid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.VBericht[vbid=" + vbid + "]";
    }
}
