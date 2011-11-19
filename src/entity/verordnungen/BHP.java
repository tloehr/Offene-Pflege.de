package entity.verordnungen;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "BHP")
@NamedQueries({
    @NamedQuery(name = "BHP.findAll", query = "SELECT b FROM BHP b"),
    @NamedQuery(name = "BHP.findByBHPid", query = "SELECT b FROM BHP b WHERE b.bhpid = :bhpid"),
    @NamedQuery(name = "BHP.findByBHPpid", query = "SELECT b FROM BHP b WHERE b.bhppid = :bhppid"),
    @NamedQuery(name = "BHP.findByUKennung", query = "SELECT b FROM BHP b WHERE b.uKennung = :uKennung"),
    @NamedQuery(name = "BHP.findBySoll", query = "SELECT b FROM BHP b WHERE b.soll = :soll"),
    @NamedQuery(name = "BHP.findByIst", query = "SELECT b FROM BHP b WHERE b.ist = :ist"),
    @NamedQuery(name = "BHP.findBySZeit", query = "SELECT b FROM BHP b WHERE b.sZeit = :sZeit"),
    @NamedQuery(name = "BHP.findByIZeit", query = "SELECT b FROM BHP b WHERE b.iZeit = :iZeit"),
    @NamedQuery(name = "BHP.findByDosis", query = "SELECT b FROM BHP b WHERE b.dosis = :dosis"),
    @NamedQuery(name = "BHP.findByStatus", query = "SELECT b FROM BHP b WHERE b.status = :status"),
    @NamedQuery(name = "BHP.findByMdate", query = "SELECT b FROM BHP b WHERE b.mdate = :mdate"),
    @NamedQuery(name = "BHP.findByDauer", query = "SELECT b FROM BHP b WHERE b.dauer = :dauer")})
public class BHP implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BHPID")
    private Long bhpid;
    @Basic(optional = false)
    @Column(name = "BHPPID")
    private long bhppid;
    @Column(name = "UKennung")
    private String uKennung;
    @Basic(optional = false)
    @Column(name = "Soll")
    @Temporal(TemporalType.TIMESTAMP)
    private Date soll;
    @Column(name = "Ist")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ist;
    @Column(name = "SZeit")
    private Boolean sZeit;
    @Column(name = "IZeit")
    private Boolean iZeit;
    @Column(name = "Dosis")
    private BigDecimal dosis;
    @Column(name = "Status")
    private Boolean status;
    @Lob
    @Column(name = "Bemerkung")
    private String bemerkung;
    @Basic(optional = false)
    @Column(name = "_mdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mdate;
    @Column(name = "Dauer")
    private Short dauer;

    public BHP() {
    }

    public BHP(Long bhpid) {
        this.bhpid = bhpid;
    }

    public BHP(Long bhpid, long bhppid, Date soll, Date mdate) {
        this.bhpid = bhpid;
        this.bhppid = bhppid;
        this.soll = soll;
        this.mdate = mdate;
    }

    public Long getBHPid() {
        return bhpid;
    }

    public void setBHPid(Long bhpid) {
        this.bhpid = bhpid;
    }

    public long getBHPpid() {
        return bhppid;
    }

    public void setBHPpid(long bhppid) {
        this.bhppid = bhppid;
    }

    public String getUKennung() {
        return uKennung;
    }

    public void setUKennung(String uKennung) {
        this.uKennung = uKennung;
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

    public BigDecimal getDosis() {
        return dosis;
    }

    public void setDosis(BigDecimal dosis) {
        this.dosis = dosis;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public Short getDauer() {
        return dauer;
    }

    public void setDauer(Short dauer) {
        this.dauer = dauer;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bhpid != null ? bhpid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BHP)) {
            return false;
        }
        BHP other = (BHP) object;
        if ((this.bhpid == null && other.bhpid != null) || (this.bhpid != null && !this.bhpid.equals(other.bhpid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.BHP[bhpid=" + bhpid + "]";
    }

}
