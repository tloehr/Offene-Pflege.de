package entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class MassTermin {
    private long termId;

    @javax.persistence.Column(name = "TermID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getTermId() {
        return termId;
    }

    public void setTermId(long termId) {
        this.termId = termId;
    }

    private long massId;

    @javax.persistence.Column(name = "MassID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getMassId() {
        return massId;
    }

    public void setMassId(long massId) {
        this.massId = massId;
    }

    private long planId;

    @javax.persistence.Column(name = "PlanID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    public long getPlanId() {
        return planId;
    }

    public void setPlanId(long planId) {
        this.planId = planId;
    }

    private byte nachtMo;

    @javax.persistence.Column(name = "NachtMo", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getNachtMo() {
        return nachtMo;
    }

    public void setNachtMo(byte nachtMo) {
        this.nachtMo = nachtMo;
    }

    private byte morgens;

    @javax.persistence.Column(name = "Morgens", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getMorgens() {
        return morgens;
    }

    public void setMorgens(byte morgens) {
        this.morgens = morgens;
    }

    private byte mittags;

    @javax.persistence.Column(name = "Mittags", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getMittags() {
        return mittags;
    }

    public void setMittags(byte mittags) {
        this.mittags = mittags;
    }

    private byte nachmittags;

    @javax.persistence.Column(name = "Nachmittags", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getNachmittags() {
        return nachmittags;
    }

    public void setNachmittags(byte nachmittags) {
        this.nachmittags = nachmittags;
    }

    private byte abends;

    @javax.persistence.Column(name = "Abends", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getAbends() {
        return abends;
    }

    public void setAbends(byte abends) {
        this.abends = abends;
    }

    private byte nachtAb;

    @javax.persistence.Column(name = "NachtAb", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getNachtAb() {
        return nachtAb;
    }

    public void setNachtAb(byte nachtAb) {
        this.nachtAb = nachtAb;
    }

    private byte uhrzeitAnzahl;

    @javax.persistence.Column(name = "UhrzeitAnzahl", nullable = true, insertable = true, updatable = true, length = 3, precision = 0)
    @Basic
    public byte getUhrzeitAnzahl() {
        return uhrzeitAnzahl;
    }

    public void setUhrzeitAnzahl(byte uhrzeitAnzahl) {
        this.uhrzeitAnzahl = uhrzeitAnzahl;
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

    private boolean erforderlich;

    @javax.persistence.Column(name = "Erforderlich", nullable = true, insertable = true, updatable = true, length = 0, precision = 0)
    @Basic
    public boolean isErforderlich() {
        return erforderlich;
    }

    public void setErforderlich(boolean erforderlich) {
        this.erforderlich = erforderlich;
    }

    private Timestamp lDatum;

    @javax.persistence.Column(name = "LDatum", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Basic
    public Timestamp getlDatum() {
        return lDatum;
    }

    public void setlDatum(Timestamp lDatum) {
        this.lDatum = lDatum;
    }

    private BigDecimal dauer;

    @javax.persistence.Column(name = "Dauer", nullable = false, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    public BigDecimal getDauer() {
        return dauer;
    }

    public void setDauer(BigDecimal dauer) {
        this.dauer = dauer;
    }

    private String xml;

    @javax.persistence.Column(name = "XML", nullable = true, insertable = true, updatable = true, length = 16777215, precision = 0)
    @Basic
    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    private String bemerkung;

    @javax.persistence.Column(name = "Bemerkung", nullable = true, insertable = true, updatable = true, length = 16777215, precision = 0)
    @Basic
    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MassTermin that = (MassTermin) o;

        if (abends != that.abends) return false;
        if (die != that.die) return false;
        if (don != that.don) return false;
        if (erforderlich != that.erforderlich) return false;
        if (fre != that.fre) return false;
        if (massId != that.massId) return false;
        if (mit != that.mit) return false;
        if (mittags != that.mittags) return false;
        if (mon != that.mon) return false;
        if (monatlich != that.monatlich) return false;
        if (morgens != that.morgens) return false;
        if (nachmittags != that.nachmittags) return false;
        if (nachtAb != that.nachtAb) return false;
        if (nachtMo != that.nachtMo) return false;
        if (planId != that.planId) return false;
        if (sam != that.sam) return false;
        if (son != that.son) return false;
        if (taeglich != that.taeglich) return false;
        if (tagNum != that.tagNum) return false;
        if (termId != that.termId) return false;
        if (tmp != that.tmp) return false;
        if (uhrzeitAnzahl != that.uhrzeitAnzahl) return false;
        if (woechentlich != that.woechentlich) return false;
        if (bemerkung != null ? !bemerkung.equals(that.bemerkung) : that.bemerkung != null) return false;
        if (dauer != null ? !dauer.equals(that.dauer) : that.dauer != null) return false;
        if (lDatum != null ? !lDatum.equals(that.lDatum) : that.lDatum != null) return false;
        if (uhrzeit != null ? !uhrzeit.equals(that.uhrzeit) : that.uhrzeit != null) return false;
        if (xml != null ? !xml.equals(that.xml) : that.xml != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (termId ^ (termId >>> 32));
        result = 31 * result + (int) (massId ^ (massId >>> 32));
        result = 31 * result + (int) (planId ^ (planId >>> 32));
        result = 31 * result + (int) nachtMo;
        result = 31 * result + (int) morgens;
        result = 31 * result + (int) mittags;
        result = 31 * result + (int) nachmittags;
        result = 31 * result + (int) abends;
        result = 31 * result + (int) nachtAb;
        result = 31 * result + (int) uhrzeitAnzahl;
        result = 31 * result + (uhrzeit != null ? uhrzeit.hashCode() : 0);
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
        result = 31 * result + (erforderlich ? 1 : 0);
        result = 31 * result + (lDatum != null ? lDatum.hashCode() : 0);
        result = 31 * result + (dauer != null ? dauer.hashCode() : 0);
        result = 31 * result + (xml != null ? xml.hashCode() : 0);
        result = 31 * result + (bemerkung != null ? bemerkung.hashCode() : 0);
        result = 31 * result + (int) (tmp ^ (tmp >>> 32));
        return result;
    }
}
