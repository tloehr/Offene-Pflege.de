package entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "BHPPlanung")
@NamedQueries({
        @NamedQuery(name = "VerordnungPlanung.findByVerordnungSorted", query = " " +
                " SELECT vp FROM VerordnungPlanung vp WHERE vp.verordnung = :verordnung AND vp.tmp = 0 " +
                " ORDER BY vp.uhrzeit, vp.nachtMo, vp.morgens, vp.mittags, vp.nachmittags, vp.abends, vp.nachtAb ")
})
public class VerordnungPlanung {
    private long bhppid;

    @javax.persistence.Column(name = "BHPPID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getBhppid() {
        return bhppid;
    }

    public void setBhppid(long bhppid) {
        this.bhppid = bhppid;
    }


    private BigDecimal nachtMo;

    @javax.persistence.Column(name = "NachtMo", nullable = true, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    public BigDecimal getNachtMo() {
        return nachtMo;
    }

    public void setNachtMo(BigDecimal nachtMo) {
        this.nachtMo = nachtMo;
    }

    private BigDecimal morgens;

    @javax.persistence.Column(name = "Morgens", nullable = true, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    public BigDecimal getMorgens() {
        return morgens;
    }

    public void setMorgens(BigDecimal morgens) {
        this.morgens = morgens;
    }

    private BigDecimal mittags;

    @javax.persistence.Column(name = "Mittags", nullable = true, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    public BigDecimal getMittags() {
        return mittags;
    }

    public void setMittags(BigDecimal mittags) {
        this.mittags = mittags;
    }

    private BigDecimal nachmittags;

    @javax.persistence.Column(name = "Nachmittags", nullable = true, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    public BigDecimal getNachmittags() {
        return nachmittags;
    }

    public void setNachmittags(BigDecimal nachmittags) {
        this.nachmittags = nachmittags;
    }

    private BigDecimal abends;

    @javax.persistence.Column(name = "Abends", nullable = true, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    public BigDecimal getAbends() {
        return abends;
    }

    public void setAbends(BigDecimal abends) {
        this.abends = abends;
    }

    private BigDecimal nachtAb;

    @javax.persistence.Column(name = "NachtAb", nullable = true, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    public BigDecimal getNachtAb() {
        return nachtAb;
    }

    public void setNachtAb(BigDecimal nachtAb) {
        this.nachtAb = nachtAb;
    }

    private BigDecimal uhrzeitDosis;

    @javax.persistence.Column(name = "UhrzeitDosis", nullable = true, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    public BigDecimal getUhrzeitDosis() {
        return uhrzeitDosis;
    }

    public void setUhrzeitDosis(BigDecimal uhrzeitDosis) {
        this.uhrzeitDosis = uhrzeitDosis;
    }

    private Time uhrzeit;

    @javax.persistence.Column(name = "Uhrzeit", nullable = true, insertable = true, updatable = true, length = 8, precision = 0)
    @Basic
    public Time getUhrzeit() {
        return uhrzeit;
    }

    public void setUhrzeit(Time uhrzeit) {
        this.uhrzeit = uhrzeit;
    }

    private int maxAnzahl;

    @javax.persistence.Column(name = "MaxAnzahl", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public int getMaxAnzahl() {
        return maxAnzahl;
    }

    public void setMaxAnzahl(int maxAnzahl) {
        this.maxAnzahl = maxAnzahl;
    }

    private BigDecimal maxEDosis;

    @javax.persistence.Column(name = "MaxEDosis", nullable = true, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    public BigDecimal getMaxEDosis() {
        return maxEDosis;
    }

    public void setMaxEDosis(BigDecimal maxEDosis) {
        this.maxEDosis = maxEDosis;
    }

    private byte taeglich;

    @javax.persistence.Column(name = "Taeglich", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getTaeglich() {
        return taeglich;
    }

    public void setTaeglich(byte taeglich) {
        this.taeglich = taeglich;
    }

    private byte woechentlich;

    @javax.persistence.Column(name = "Woechentlich", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getWoechentlich() {
        return woechentlich;
    }

    public void setWoechentlich(byte woechentlich) {
        this.woechentlich = woechentlich;
    }

    private byte monatlich;

    @javax.persistence.Column(name = "Monatlich", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getMonatlich() {
        return monatlich;
    }

    public void setMonatlich(byte monatlich) {
        this.monatlich = monatlich;
    }

    private byte tagNum;

    @javax.persistence.Column(name = "TagNum", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getTagNum() {
        return tagNum;
    }

    public void setTagNum(byte tagNum) {
        this.tagNum = tagNum;
    }

    private byte mon;

    @javax.persistence.Column(name = "Mon", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getMon() {
        return mon;
    }

    public void setMon(byte mon) {
        this.mon = mon;
    }

    private byte die;

    @javax.persistence.Column(name = "Die", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getDie() {
        return die;
    }

    public void setDie(byte die) {
        this.die = die;
    }

    private byte mit;

    @javax.persistence.Column(name = "Mit", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getMit() {
        return mit;
    }

    public void setMit(byte mit) {
        this.mit = mit;
    }

    private byte don;

    @javax.persistence.Column(name = "Don", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getDon() {
        return don;
    }

    public void setDon(byte don) {
        this.don = don;
    }

    private byte fre;

    @javax.persistence.Column(name = "Fre", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getFre() {
        return fre;
    }

    public void setFre(byte fre) {
        this.fre = fre;
    }

    private byte sam;

    @javax.persistence.Column(name = "Sam", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getSam() {
        return sam;
    }

    public void setSam(byte sam) {
        this.sam = sam;
    }

    private byte son;

    @javax.persistence.Column(name = "Son", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getSon() {
        return son;
    }

    public void setSon(byte son) {
        this.son = son;
    }

    private Date lDatum;

    @javax.persistence.Column(name = "LDatum", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Date getlDatum() {
        return lDatum;
    }

    public void setlDatum(Date lDatum) {
        this.lDatum = lDatum;
    }

    private String uKennung;

    @javax.persistence.Column(name = "UKennung", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public String getuKennung() {
        return uKennung;
    }

    public void setuKennung(String uKennung) {
        this.uKennung = uKennung;
    }

    private long tmp;

    @javax.persistence.Column(name = "tmp", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getTmp() {
        return tmp;
    }

    public void setTmp(long tmp) {
        this.tmp = tmp;
    }

    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "VerID", referencedColumnName = "VerID")
    @ManyToOne
    private Verordnung verordnung;


    public Verordnung getVerordnung() {
        return verordnung;
    }

    public void setVerordnung(Verordnung verordnung) {
        this.verordnung = verordnung;
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

    public boolean isTaeglich(){
        return taeglich > 0;
    }

    public boolean isWoechentlich(){
        return woechentlich > 0;
    }

    public boolean isMonatlich(){
        return monatlich > 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VerordnungPlanung that = (VerordnungPlanung) o;

        if (bhppid != that.bhppid) return false;
        if (verordnung != that.verordnung) return false;
        if (die != that.die) return false;
        if (don != that.don) return false;
        if (fre != that.fre) return false;
        if (maxAnzahl != that.maxAnzahl) return false;
        if (mit != that.mit) return false;
        if (mon != that.mon) return false;
        if (monatlich != that.monatlich) return false;
        if (sam != that.sam) return false;
        if (son != that.son) return false;
        if (taeglich != that.taeglich) return false;
        if (tagNum != that.tagNum) return false;
        if (tmp != that.tmp) return false;
        if (woechentlich != that.woechentlich) return false;
        if (abends != null ? !abends.equals(that.abends) : that.abends != null) return false;
        if (lDatum != null ? !lDatum.equals(that.lDatum) : that.lDatum != null) return false;
        if (maxEDosis != null ? !maxEDosis.equals(that.maxEDosis) : that.maxEDosis != null) return false;
        if (mittags != null ? !mittags.equals(that.mittags) : that.mittags != null) return false;
        if (morgens != null ? !morgens.equals(that.morgens) : that.morgens != null) return false;
        if (nachmittags != null ? !nachmittags.equals(that.nachmittags) : that.nachmittags != null) return false;
        if (nachtAb != null ? !nachtAb.equals(that.nachtAb) : that.nachtAb != null) return false;
        if (nachtMo != null ? !nachtMo.equals(that.nachtMo) : that.nachtMo != null) return false;
        if (uKennung != null ? !uKennung.equals(that.uKennung) : that.uKennung != null) return false;
        if (uhrzeit != null ? !uhrzeit.equals(that.uhrzeit) : that.uhrzeit != null) return false;
        if (uhrzeitDosis != null ? !uhrzeitDosis.equals(that.uhrzeitDosis) : that.uhrzeitDosis != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (bhppid ^ (bhppid >>> 32));
        result = 31 * result + (nachtMo != null ? nachtMo.hashCode() : 0);
        result = 31 * result + (morgens != null ? morgens.hashCode() : 0);
        result = 31 * result + (mittags != null ? mittags.hashCode() : 0);
        result = 31 * result + (nachmittags != null ? nachmittags.hashCode() : 0);
        result = 31 * result + (abends != null ? abends.hashCode() : 0);
        result = 31 * result + (nachtAb != null ? nachtAb.hashCode() : 0);
        result = 31 * result + (uhrzeitDosis != null ? uhrzeitDosis.hashCode() : 0);
        result = 31 * result + (uhrzeit != null ? uhrzeit.hashCode() : 0);
        result = 31 * result + maxAnzahl;
        result = 31 * result + (maxEDosis != null ? maxEDosis.hashCode() : 0);
        result = 31 * result + (int) taeglich;
        result = 31 * result + (int) woechentlich;
        result = 31 * result + (int) monatlich;
        result = 31 * result + (int) tagNum;
        result = 31 * result + (int) mon;
        result = 31 * result + (int) die;
        result = 31 * result + (int) mit;
        result = 31 * result + (int) don;
        result = 31 * result + (int) fre;
        result = 31 * result + (int) sam;
        result = 31 * result + (int) son;
        result = 31 * result + (lDatum != null ? lDatum.hashCode() : 0);
        result = 31 * result + (uKennung != null ? uKennung.hashCode() : 0);
        result = 31 * result + (int) (tmp ^ (tmp >>> 32));
        return result;
    }
}
