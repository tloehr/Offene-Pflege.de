package entity.verordnungen;

import entity.Users;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "BHPPlanung")
@NamedQueries({
        @NamedQuery(name = "VerordnungPlanung.findAll", query = "SELECT b FROM VerordnungPlanung b"),
        @NamedQuery(name = "VerordnungPlanung.findByBhppid", query = "SELECT b FROM VerordnungPlanung b WHERE b.bhppid = :bhppid"),
        @NamedQuery(name = "VerordnungPlanung.findByNachtMo", query = "SELECT b FROM VerordnungPlanung b WHERE b.nachtMo = :nachtMo"),
        @NamedQuery(name = "VerordnungPlanung.findByMorgens", query = "SELECT b FROM VerordnungPlanung b WHERE b.morgens = :morgens"),
        @NamedQuery(name = "VerordnungPlanung.findByMittags", query = "SELECT b FROM VerordnungPlanung b WHERE b.mittags = :mittags"),
        @NamedQuery(name = "VerordnungPlanung.findByNachmittags", query = "SELECT b FROM VerordnungPlanung b WHERE b.nachmittags = :nachmittags"),
        @NamedQuery(name = "VerordnungPlanung.findByAbends", query = "SELECT b FROM VerordnungPlanung b WHERE b.abends = :abends"),
        @NamedQuery(name = "VerordnungPlanung.findByNachtAb", query = "SELECT b FROM VerordnungPlanung b WHERE b.nachtAb = :nachtAb"),
        @NamedQuery(name = "VerordnungPlanung.findByUhrzeitDosis", query = "SELECT b FROM VerordnungPlanung b WHERE b.uhrzeitDosis = :uhrzeitDosis"),
        @NamedQuery(name = "VerordnungPlanung.findByUhrzeit", query = "SELECT b FROM VerordnungPlanung b WHERE b.uhrzeit = :uhrzeit"),
        @NamedQuery(name = "VerordnungPlanung.findByMaxAnzahl", query = "SELECT b FROM VerordnungPlanung b WHERE b.maxAnzahl = :maxAnzahl"),
        @NamedQuery(name = "VerordnungPlanung.findByMaxEDosis", query = "SELECT b FROM VerordnungPlanung b WHERE b.maxEDosis = :maxEDosis"),
        @NamedQuery(name = "VerordnungPlanung.findByTaeglich", query = "SELECT b FROM VerordnungPlanung b WHERE b.taeglich = :taeglich"),
        @NamedQuery(name = "VerordnungPlanung.findByWoechentlich", query = "SELECT b FROM VerordnungPlanung b WHERE b.woechentlich = :woechentlich"),
        @NamedQuery(name = "VerordnungPlanung.findByMonatlich", query = "SELECT b FROM VerordnungPlanung b WHERE b.monatlich = :monatlich"),
        @NamedQuery(name = "VerordnungPlanung.findByTagNum", query = "SELECT b FROM VerordnungPlanung b WHERE b.tagNum = :tagNum"),
        @NamedQuery(name = "VerordnungPlanung.findByMon", query = "SELECT b FROM VerordnungPlanung b WHERE b.mon = :mon"),
        @NamedQuery(name = "VerordnungPlanung.findByDie", query = "SELECT b FROM VerordnungPlanung b WHERE b.die = :die"),
        @NamedQuery(name = "VerordnungPlanung.findByMit", query = "SELECT b FROM VerordnungPlanung b WHERE b.mit = :mit"),
        @NamedQuery(name = "VerordnungPlanung.findByDon", query = "SELECT b FROM VerordnungPlanung b WHERE b.don = :don"),
        @NamedQuery(name = "VerordnungPlanung.findByFre", query = "SELECT b FROM VerordnungPlanung b WHERE b.fre = :fre"),
        @NamedQuery(name = "VerordnungPlanung.findBySam", query = "SELECT b FROM VerordnungPlanung b WHERE b.sam = :sam"),
        @NamedQuery(name = "VerordnungPlanung.findBySon", query = "SELECT b FROM VerordnungPlanung b WHERE b.son = :son"),
        @NamedQuery(name = "VerordnungPlanung.findByLDatum", query = "SELECT b FROM VerordnungPlanung b WHERE b.lDatum = :lDatum"),
        @NamedQuery(name = "VerordnungPlanung.findByVerordnungSorted", query = " " +
                " SELECT vp FROM VerordnungPlanung vp WHERE vp.verordnung = :verordnung " +
                " ORDER BY vp.uhrzeit, vp.nachtMo, vp.morgens, vp.mittags, vp.nachmittags, vp.abends, vp.nachtAb ")
})
public class VerordnungPlanung implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BHPPID")
    private Long bhppid;
    @Column(name = "NachtMo")
    private BigDecimal nachtMo;
    @Column(name = "Morgens")
    private BigDecimal morgens;
    @Column(name = "Mittags")
    private BigDecimal mittags;
    @Column(name = "Nachmittags")
    private BigDecimal nachmittags;
    @Column(name = "Abends")
    private BigDecimal abends;
    @Column(name = "NachtAb")
    private BigDecimal nachtAb;
    @Column(name = "UhrzeitDosis")
    private BigDecimal uhrzeitDosis;
    @Column(name = "Uhrzeit")
    @Temporal(TemporalType.TIME)
    private Date uhrzeit;
    @Column(name = "MaxAnzahl")
    private Integer maxAnzahl;
    @Column(name = "MaxEDosis")
    private BigDecimal maxEDosis;
    @Column(name = "Taeglich")
    private Short taeglich;
    @Column(name = "Woechentlich")
    private Short woechentlich;
    @Column(name = "Monatlich")
    private Short monatlich;
    @Column(name = "TagNum")
    private Short tagNum;
    @Column(name = "Mon")
    private Short mon;
    @Column(name = "Die")
    private Short die;
    @Column(name = "Mit")
    private Short mit;
    @Column(name = "Don")
    private Short don;
    @Column(name = "Fre")
    private Short fre;
    @Column(name = "Sam")
    private Short sam;
    @Column(name = "Son")
    private Short son;
    @Basic(optional = false)
    @Column(name = "LDatum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lDatum;

    public VerordnungPlanung() {
    }

    public VerordnungPlanung(Verordnung verordnung) {
        nachtMo = BigDecimal.ZERO;
        mittags = BigDecimal.ZERO;
        abends = BigDecimal.ZERO;
        nachtAb = BigDecimal.ZERO;
        uhrzeitDosis = BigDecimal.ZERO;
        uhrzeit = null;
        taeglich = 1;
        woechentlich = 0;
        monatlich = 0;
        tagNum = 0;
        lDatum = new Date();

        this.verordnung = verordnung;

        if (verordnung.isBedarf()) {
            morgens = BigDecimal.ZERO;
            maxAnzahl = 1;
            maxEDosis = BigDecimal.ONE;
        } else {
            morgens = BigDecimal.ONE;
            maxAnzahl = 0;
            maxEDosis = BigDecimal.ZERO;
        }

    }


    public Long getBhppid() {
        return bhppid;
    }

    public void setBhppid(Long bhppid) {
        this.bhppid = bhppid;
    }


    public BigDecimal getNachtMo() {
        return nachtMo;
    }

    public void setNachtMo(BigDecimal nachtMo) {
        this.nachtMo = nachtMo;
    }

    public BigDecimal getMorgens() {
        return morgens;
    }

    public void setMorgens(BigDecimal morgens) {
        this.morgens = morgens;
    }

    public BigDecimal getMittags() {
        return mittags;
    }

    public void setMittags(BigDecimal mittags) {
        this.mittags = mittags;
    }

    public BigDecimal getNachmittags() {
        return nachmittags;
    }

    public void setNachmittags(BigDecimal nachmittags) {
        this.nachmittags = nachmittags;
    }

    public BigDecimal getAbends() {
        return abends;
    }

    public void setAbends(BigDecimal abends) {
        this.abends = abends;
    }

    public BigDecimal getNachtAb() {
        return nachtAb;
    }

    public void setNachtAb(BigDecimal nachtAb) {
        this.nachtAb = nachtAb;
    }

    public BigDecimal getUhrzeitDosis() {
        return uhrzeitDosis;
    }

    public void setUhrzeitDosis(BigDecimal uhrzeitDosis) {
        this.uhrzeitDosis = uhrzeitDosis;
    }

    public Date getUhrzeit() {
        return uhrzeit;
    }

    public void setUhrzeit(Date uhrzeit) {
        this.uhrzeit = uhrzeit;
    }

    public Integer getMaxAnzahl() {
        return maxAnzahl;
    }

    public void setMaxAnzahl(Integer maxAnzahl) {
        this.maxAnzahl = maxAnzahl;
    }

    public BigDecimal getMaxEDosis() {
        return maxEDosis;
    }

    public void setMaxEDosis(BigDecimal maxEDosis) {
        this.maxEDosis = maxEDosis;
    }

    public Short getTaeglich() {
        return taeglich;
    }

    public void setTaeglich(Short taeglich) {
        this.taeglich = taeglich;
    }

    public Short getWoechentlich() {
        return woechentlich;
    }

    public void setWoechentlich(Short woechentlich) {
        this.woechentlich = woechentlich;
    }

    public Short getMonatlich() {
        return monatlich;
    }

    public void setMonatlich(Short monatlich) {
        this.monatlich = monatlich;
    }

    public Short getTagNum() {
        return tagNum;
    }

    public void setTagNum(Short tagNum) {
        this.tagNum = tagNum;
    }

    public Short getMon() {
        return mon;
    }

    public void setMon(Short mon) {
        this.mon = mon;
    }

    public Short getDie() {
        return die;
    }

    public void setDie(Short die) {
        this.die = die;
    }

    public Short getMit() {
        return mit;
    }

    public void setMit(Short mit) {
        this.mit = mit;
    }

    public Short getDon() {
        return don;
    }

    public void setDon(Short don) {
        this.don = don;
    }

    public Short getFre() {
        return fre;
    }

    public void setFre(Short fre) {
        this.fre = fre;
    }

    public Short getSam() {
        return sam;
    }

    public void setSam(Short sam) {
        this.sam = sam;
    }

    public Short getSon() {
        return son;
    }

    public void setSon(Short son) {
        this.son = son;
    }

    public Date getLDatum() {
        return lDatum;
    }

    public void setLDatum(Date lDatum) {
        this.lDatum = lDatum;
    }

    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "VerID", referencedColumnName = "VerID")
    @ManyToOne
    private Verordnung verordnung;

    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;


    public Verordnung getVerordnung() {
        return verordnung;
    }

    public void setVerordnung(Verordnung verordnung) {
        this.verordnung = verordnung;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    /**
     * gibt an, ob bei der Planungen die festen Zeiten wie Früh, Spät, Nacht usw. verwendet wurden
     *
     * @return
     */
    public boolean verwendetZeiten() {
        return nachtMo.doubleValue() + morgens.doubleValue() + mittags.doubleValue() + nachmittags.doubleValue() + abends.doubleValue() + nachtAb.doubleValue() > 0;
    }

    /**
     * ermittelt ob es sich um die Planung für eine Bedarfsmedikation handelt
     *
     * @return
     */
    public boolean verwendetMaximalDosis() {
        return maxAnzahl > 0;
    }

    /**
     * ermittelt ob es sich um die Planung per Uhrzeit handelt
     *
     * @return
     */
    public boolean verwendetUhrzeit() {
        return uhrzeit != null;
    }

    public boolean isTaeglich() {
        return taeglich > 0;
    }

    public boolean isWoechentlich() {
        return woechentlich > 0;
    }

    public boolean isMonatlich() {
        return monatlich > 0;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bhppid != null ? bhppid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VerordnungPlanung)) {
            return false;
        }
        VerordnungPlanung other = (VerordnungPlanung) object;
        if ((this.bhppid == null && other.bhppid != null) || (this.bhppid != null && !this.bhppid.equals(other.bhppid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "VerordnungPlanung{" +
                "bhppid=" + bhppid +
                ", nachtMo=" + nachtMo +
                ", morgens=" + morgens +
                ", mittags=" + mittags +
                ", nachmittags=" + nachmittags +
                ", abends=" + abends +
                ", nachtAb=" + nachtAb +
                ", uhrzeitDosis=" + uhrzeitDosis +
                ", uhrzeit=" + uhrzeit +
                ", maxAnzahl=" + maxAnzahl +
                ", maxEDosis=" + maxEDosis +
                ", taeglich=" + taeglich +
                ", woechentlich=" + woechentlich +
                ", monatlich=" + monatlich +
                ", tagNum=" + tagNum +
                ", mon=" + mon +
                ", die=" + die +
                ", mit=" + mit +
                ", don=" + don +
                ", fre=" + fre +
                ", sam=" + sam +
                ", son=" + son +
                ", lDatum=" + lDatum +
                ", uKennung='" + user.getUKennung() + '\'' +
                ", verordnung=" + verordnung +
                '}';
    }
}
