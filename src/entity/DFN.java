package entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "DFN")
@NamedQueries({
    @NamedQuery(name = "Dfn.findAll", query = "SELECT d FROM DFN d"),
    @NamedQuery(name = "Dfn.findByDfnid", query = "SELECT d FROM DFN d WHERE d.dfnid = :dfnid"),
    @NamedQuery(name = "Dfn.findByBWKennung", query = "SELECT d FROM DFN d WHERE d.bWKennung = :bWKennung"),
    @NamedQuery(name = "Dfn.findByTermID", query = "SELECT d FROM DFN d WHERE d.termID = :termID"),
    @NamedQuery(name = "Dfn.findByUKennung", query = "SELECT d FROM DFN d WHERE d.uKennung = :uKennung"),
    @NamedQuery(name = "Dfn.findByMassID", query = "SELECT d FROM DFN d WHERE d.massID = :massID"),
    @NamedQuery(name = "Dfn.findBySoll", query = "SELECT d FROM DFN d WHERE d.soll = :soll"),
    @NamedQuery(name = "Dfn.findByIst", query = "SELECT d FROM DFN d WHERE d.ist = :ist"),
    @NamedQuery(name = "Dfn.findByStDatum", query = "SELECT d FROM DFN d WHERE d.stDatum = :stDatum"),
    @NamedQuery(name = "Dfn.findBySZeit", query = "SELECT d FROM DFN d WHERE d.sZeit = :sZeit"),
    @NamedQuery(name = "Dfn.findByIZeit", query = "SELECT d FROM DFN d WHERE d.iZeit = :iZeit"),
    @NamedQuery(name = "Dfn.findByStatus", query = "SELECT d FROM DFN d WHERE d.status = :status"),
    @NamedQuery(name = "Dfn.findByErforderlich", query = "SELECT d FROM DFN d WHERE d.erforderlich = :erforderlich"),
    @NamedQuery(name = "Dfn.findByDauer", query = "SELECT d FROM DFN d WHERE d.dauer = :dauer"),
    @NamedQuery(name = "Dfn.findByMdate", query = "SELECT d FROM DFN d WHERE d.mdate = :mdate")})
public class DFN implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "DFNID")
    private Long dfnid;
    @Basic(optional = false)
    @Column(name = "BWKennung")
    private String bWKennung;
    @Basic(optional = false)
    @Column(name = "TermID")
    private long termID;
    @Column(name = "UKennung")
    private String uKennung;
    @Basic(optional = false)
    @Column(name = "MassID")
    private long massID;
    @Basic(optional = false)
    @Column(name = "Soll")
    @Temporal(TemporalType.TIMESTAMP)
    private Date soll;
    @Column(name = "Ist")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ist;
    @Basic(optional = false)
    @Column(name = "StDatum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date stDatum;
    @Column(name = "SZeit")
    private Boolean sZeit;
    @Column(name = "IZeit")
    private Boolean iZeit;
    @Basic(optional = false)
    @Column(name = "Status")
    private boolean status;
    @Basic(optional = false)
    @Column(name = "Erforderlich")
    private boolean erforderlich;
    @Basic(optional = false)
    @Column(name = "Dauer")
    private BigDecimal dauer;
    @Basic(optional = false)
    @Column(name = "_mdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mdate;

    public DFN() {
    }

    public DFN(Long dfnid) {
        this.dfnid = dfnid;
    }

    public DFN(Long dfnid, String bWKennung, long termID, long massID, Date soll, Date stDatum, boolean status, boolean erforderlich, BigDecimal dauer, Date mdate) {
        this.dfnid = dfnid;
        this.bWKennung = bWKennung;
        this.termID = termID;
        this.massID = massID;
        this.soll = soll;
        this.stDatum = stDatum;
        this.status = status;
        this.erforderlich = erforderlich;
        this.dauer = dauer;
        this.mdate = mdate;
    }

    public Long getDfnid() {
        return dfnid;
    }

    public void setDfnid(Long dfnid) {
        this.dfnid = dfnid;
    }

    public String getBWKennung() {
        return bWKennung;
    }

    public void setBWKennung(String bWKennung) {
        this.bWKennung = bWKennung;
    }

    public long getTermID() {
        return termID;
    }

    public void setTermID(long termID) {
        this.termID = termID;
    }

    public String getUKennung() {
        return uKennung;
    }

    public void setUKennung(String uKennung) {
        this.uKennung = uKennung;
    }

    public long getMassID() {
        return massID;
    }

    public void setMassID(long massID) {
        this.massID = massID;
    }

    public Date getSoll() {
        return soll;
    }

    public void setSoll(Date soll) {
        this.soll = soll;
    }

    public Date getIst() {
        return ist;
    }

    public void setIst(Date ist) {
        this.ist = ist;
    }

    public Date getStDatum() {
        return stDatum;
    }

    public void setStDatum(Date stDatum) {
        this.stDatum = stDatum;
    }

    public Boolean getSZeit() {
        return sZeit;
    }

    public void setSZeit(Boolean sZeit) {
        this.sZeit = sZeit;
    }

    public Boolean getIZeit() {
        return iZeit;
    }

    public void setIZeit(Boolean iZeit) {
        this.iZeit = iZeit;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getErforderlich() {
        return erforderlich;
    }

    public void setErforderlich(boolean erforderlich) {
        this.erforderlich = erforderlich;
    }

    public BigDecimal getDauer() {
        return dauer;
    }

    public void setDauer(BigDecimal dauer) {
        this.dauer = dauer;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dfnid != null ? dfnid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DFN)) {
            return false;
        }
        DFN other = (DFN) object;
        if ((this.dfnid == null && other.dfnid != null) || (this.dfnid != null && !this.dfnid.equals(other.dfnid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.Dfn[dfnid=" + dfnid + "]";
    }

}

