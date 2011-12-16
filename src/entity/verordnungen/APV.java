package entity.verordnungen;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import entity.Bewohner;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author tloehr
 */
@Entity
@Table(name = "MPAPV")
@NamedQueries({
        @NamedQuery(name = "APV.findAll", query = "SELECT m FROM APV m"),
        @NamedQuery(name = "APV.findByApvid", query = "SELECT m FROM APV m WHERE m.apvid = :apvid"),
        @NamedQuery(name = "APV.findByBewohnerAndDarreichung", query = "SELECT m FROM APV m WHERE m.bewohner = :bewohner AND m.darreichung = :darreichung"),
        @NamedQuery(name = "APV.findByDarreichungOnly", query = "SELECT m FROM APV m WHERE m.bewohner IS NULL AND m.darreichung = :darreichung"),
        @NamedQuery(name = "APV.findByApv", query = "SELECT m FROM APV m WHERE m.apv = :apv"),
        @NamedQuery(name = "APV.findByTauschen", query = "SELECT m FROM APV m WHERE m.tauschen = :tauschen")})
public class APV implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "APVID")
    private Long apvid;
    @Basic(optional = false)
    @Column(name = "APV")
    private BigDecimal apv;
    @Basic(optional = false)
    @Column(name = "Tauschen")
    private boolean tauschen;

    public APV() {
    }

    public APV(BigDecimal apv, boolean tauschen, Bewohner bewohner, Darreichung darreichung) {
        this.apv = apv;
        this.tauschen = tauschen;
        this.bewohner = bewohner;
        this.darreichung = darreichung;
    }

    public Long getApvid() {
        return apvid;
    }

    public void setApvid(Long apvid) {
        this.apvid = apvid;
    }

    public BigDecimal getApv() {
        return apv;
    }

    public void setApv(BigDecimal apv) {
        this.apv = apv;
    }

    public boolean isTauschen() {
        return tauschen;
    }

    public void setTauschen(boolean tauschen) {
        this.tauschen = tauschen;
    }

    //
    // N:1 Relationen
    //

    public Bewohner getBewohner() {
        return bewohner;
    }

    public void setBewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
    }

    public Darreichung getDarreichung() {
        return darreichung;
    }

    public void setDarreichung(Darreichung darreichung) {
        this.darreichung = darreichung;
    }

    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Bewohner bewohner;

    @JoinColumn(name = "DafID", referencedColumnName = "DafID")
    @ManyToOne
    private Darreichung darreichung;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (apvid != null ? apvid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof APV)) {
            return false;
        }
        APV other = (APV) object;
        if ((this.apvid == null && other.apvid != null) || (this.apvid != null && !this.apvid.equals(other.apvid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.APV[apvid=" + apvid + "]";
    }

}
