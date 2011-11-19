package entity.verordnungen;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "MPAPV")
@NamedQueries({
    @NamedQuery(name = "APV.findAll", query = "SELECT m FROM APV m"),
    @NamedQuery(name = "APV.findByApvid", query = "SELECT m FROM APV m WHERE m.apvid = :apvid"),
    @NamedQuery(name = "APV.findByDafID", query = "SELECT m FROM APV m WHERE m.dafID = :dafID"),
    @NamedQuery(name = "APV.findByBWKennung", query = "SELECT m FROM APV m WHERE m.bWKennung = :bWKennung"),
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
    @Column(name = "DafID")
    private long dafID;
    @Basic(optional = false)
    @Column(name = "BWKennung")
    private String bWKennung;
    @Basic(optional = false)
    @Column(name = "APV")
    private BigDecimal apv;
    @Basic(optional = false)
    @Column(name = "Tauschen")
    private boolean tauschen;

    public APV() {
    }

    public APV(Long apvid) {
        this.apvid = apvid;
    }

    public APV(Long apvid, long dafID, String bWKennung, BigDecimal apv, boolean tauschen) {
        this.apvid = apvid;
        this.dafID = dafID;
        this.bWKennung = bWKennung;
        this.apv = apv;
        this.tauschen = tauschen;
    }

    public Long getApvid() {
        return apvid;
    }

    public void setApvid(Long apvid) {
        this.apvid = apvid;
    }

    public long getDafID() {
        return dafID;
    }

    public void setDafID(long dafID) {
        this.dafID = dafID;
    }

    public String getBWKennung() {
        return bWKennung;
    }

    public void setBWKennung(String bWKennung) {
        this.bWKennung = bWKennung;
    }

    public BigDecimal getApv() {
        return apv;
    }

    public void setApv(BigDecimal apv) {
        this.apv = apv;
    }

    public boolean getTauschen() {
        return tauschen;
    }

    public void setTauschen(boolean tauschen) {
        this.tauschen = tauschen;
    }

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
