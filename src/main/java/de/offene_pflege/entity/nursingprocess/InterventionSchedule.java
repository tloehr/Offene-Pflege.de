/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.entity.nursingprocess;

import de.offene_pflege.op.tools.SYSCalendar;
import de.offene_pflege.op.tools.SYSTools;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * @author tloehr
 */
@Entity
@Table(name = "ischedule")

public class InterventionSchedule implements Serializable, Cloneable, Comparable<InterventionSchedule> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TermID")
    private Long termID;
    @Column(name = "NachtMo")
    private Short nachtMo;
    @Column(name = "Morgens")
    private Short morgens;
    @Column(name = "Mittags")
    private Short mittags;
    @Column(name = "Nachmittags")
    private Short nachmittags;
    @Column(name = "Abends")
    private Short abends;
    @Column(name = "NachtAb")
    private Short nachtAb;
    @Column(name = "UhrzeitAnzahl")
    private Short uhrzeitAnzahl;
    @Column(name = "Uhrzeit")
    @Temporal(TemporalType.TIME)
    private Date uhrzeit;
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
    @Column(name = "Erforderlich")
    private Boolean erforderlich;
    @Basic(optional = false)
    @Column(name = "LDatum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lDatum;
    @Basic(optional = false)
    @Lob
    @Column(name = "Bemerkung")
    private String bemerkung;
    @Version
    @Column(name = "version")
    private Long version;

    @Transient
    private String uuid; // only for comparison between un-persisted entities

    @JoinColumn(name = "PlanID", referencedColumnName = "PlanID")
    @ManyToOne
    private NursingProcess nursingProcess;
    @JoinColumn(name = "MassID", referencedColumnName = "MassID")
    @ManyToOne
    private Intervention intervention;

    public InterventionSchedule() {
    }

    public InterventionSchedule(NursingProcess planung, Intervention intervention) {
        this.morgens = 1;
        this.taeglich = 1;
        this.lDatum = new LocalDate().toDate();
        this.nursingProcess = planung;
        this.intervention = intervention;

        this.nachtMo = 0;
        this.mittags = 0;
        this.nachmittags = 0;
        this.abends = 0;
        this.nachtAb = 0;
        this.uhrzeitAnzahl = 0;
        this.uhrzeit = null;
        this.woechentlich = 0;
        this.monatlich = 0;
        this.tagNum = 0;
        this.mon = 0;
        this.die = 0;
        this.mit = 0;
        this.don = 0;
        this.fre = 0;
        this.sam = 0;
        this.son = 0;
        this.erforderlich = false;
        this.bemerkung = null;
        this.uuid = UUID.randomUUID().toString();
    }

    public InterventionSchedule(Short nachtMo, Short morgens, Short mittags, Short nachmittags, Short abends, Short nachtAb, Short uhrzeitAnzahl, Date uhrzeit, Short taeglich, Short woechentlich, Short monatlich, Short tagNum, Short mon, Short die, Short mit, Short don, Short fre, Short sam, Short son, Boolean erforderlich, Date lDatum, String bemerkung, NursingProcess nursingProcess, Intervention intervention) {
        this.nachtMo = nachtMo;
        this.morgens = morgens;
        this.mittags = mittags;
        this.nachmittags = nachmittags;
        this.abends = abends;
        this.nachtAb = nachtAb;
        this.uhrzeitAnzahl = uhrzeitAnzahl;
        this.uhrzeit = uhrzeit;
        this.taeglich = taeglich;
        this.woechentlich = woechentlich;
        this.monatlich = monatlich;
        this.tagNum = tagNum;
        this.mon = mon;
        this.die = die;
        this.mit = mit;
        this.don = don;
        this.fre = fre;
        this.sam = sam;
        this.son = son;
        this.erforderlich = erforderlich;
        this.lDatum = lDatum;
        this.bemerkung = bemerkung;
        this.nursingProcess = nursingProcess;
        this.intervention = intervention;
        this.uuid = UUID.randomUUID().toString();
    }

    public Long getTermID() {
        return termID;
    }

    public void setTermID(Long termID) {
        this.termID = termID;
    }

    public Short getNachtMo() {
        return nachtMo;
    }

    public void setNachtMo(Short nachtMo) {
        this.nachtMo = nachtMo;
    }

    public Short getMorgens() {
        return morgens;
    }

    public void setMorgens(Short morgens) {
        this.morgens = morgens;
    }

    public Short getMittags() {
        return mittags;
    }

    public void setMittags(Short mittags) {
        this.mittags = mittags;
    }

    public Short getNachmittags() {
        return nachmittags;
    }

    public void setNachmittags(Short nachmittags) {
        this.nachmittags = nachmittags;
    }

    public Short getAbends() {
        return abends;
    }

    public void setAbends(Short abends) {
        this.abends = abends;
    }

    public Short getNachtAb() {
        return nachtAb;
    }

    public void setNachtAb(Short nachtAb) {
        this.nachtAb = nachtAb;
    }

    public Short getUhrzeitAnzahl() {
        return uhrzeitAnzahl;
    }

    public void setUhrzeitAnzahl(Short uhrzeitAnzahl) {
        this.uhrzeitAnzahl = uhrzeitAnzahl;
    }

    public Date getUhrzeit() {
        return uhrzeit;
    }

    public void setUhrzeit(Date uhrzeit) {
        this.uhrzeit = uhrzeit;
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

    public Boolean isFloating() {
        return erforderlich;
    }

    public void setFloating(Boolean floating) {
        this.erforderlich = floating;
    }

    public Date getLDatum() {
        return lDatum;
    }

    public void setLDatum(Date lDatum) {
        this.lDatum = lDatum;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = SYSTools.tidy(bemerkung);
    }

    public Intervention getIntervention() {
        return intervention;
    }

    public void setIntervention(Intervention intervention) {
        this.intervention = intervention;
    }

    public Date getlDatum() {
        return lDatum;
    }

    public void setlDatum(Date lDatum) {
        this.lDatum = lDatum;
    }

    public NursingProcess getNursingProcess() {
        return nursingProcess;
    }

    public void setNursingProcess(NursingProcess nursingProcess) {
        this.nursingProcess = nursingProcess;
    }

    public boolean verwendetUhrzeit() {
        return uhrzeit != null;
    }

    public boolean isTaeglich() {
        return taeglich > 0;
    }

    public boolean isWoechentlich() {
        return woechentlich > 0;
    }

    public boolean isValid() {
        return (uhrzeit == null && nachtMo + morgens + mittags + nachmittags + abends + nachtAb > 0) || (uhrzeit != null && uhrzeitAnzahl > 0);
    }

    /**
     * @param date, zu prüfendes Datum.
     * @return Ist <code>true</code>, wenn diese Planung wöchentlich gilt und das Attribut mit dem aktuellen Wochentagsnamen größer null ist.
     */
    public boolean isPassenderWochentag(Date date) {
        boolean passend = false;

        if (isWoechentlich()) { // wenn nicht wöchentlich, dann passt gar nix
            GregorianCalendar gcDate = SYSCalendar.toGC(date);
            switch (gcDate.get(GregorianCalendar.DAY_OF_WEEK)) {
                case GregorianCalendar.MONDAY: {
                    passend = mon > 0;
                    break;
                }
                case GregorianCalendar.TUESDAY: {
                    passend = die > 0;
                    break;
                }
                case GregorianCalendar.WEDNESDAY: {
                    passend = mit > 0;
                    break;
                }
                case GregorianCalendar.THURSDAY: {
                    passend = don > 0;
                    break;
                }
                case GregorianCalendar.FRIDAY: {
                    passend = fre > 0;
                    break;
                }
                case GregorianCalendar.SATURDAY: {
                    passend = sam > 0;
                    break;
                }
                case GregorianCalendar.SUNDAY: {
                    passend = son > 0;
                    break;
                }
                default: {
                    passend = false;
                    break;
                }
            }
        }
        return passend;
    }

    /**
     * @param date, zu prüfendes Datum.
     * @return Ist <code>true</code>, wenn diese Planung monatlich gilt und das Attribut <code>tagnum</code> dem aktuellen Tag im Monat entspricht
     *         <b>oder</b> das Attribut mit dem aktuellen Wochentagsnamen gleich dem Wochentag im Monat entpricht (der erste Mitwwoch im Monat hat 1, der zweite 2 usw...).
     */
    public boolean isPassenderTagImMonat(Date date) {
        boolean passend = false;
        if (isMonatlich()) { // wenn nicht monatlich, dann passt gar nix
            GregorianCalendar gcDate = SYSCalendar.toGC(date);

            passend = tagNum == gcDate.get(GregorianCalendar.DAY_OF_MONTH);

            if (!passend) {
                switch (gcDate.get(GregorianCalendar.DAY_OF_WEEK)) {
                    case GregorianCalendar.MONDAY: {
                        passend = mon == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.TUESDAY: {
                        passend = die == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.WEDNESDAY: {
                        passend = mit == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.THURSDAY: {
                        passend = don == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.FRIDAY: {
                        passend = fre == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.SATURDAY: {
                        passend = sam == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.SUNDAY: {
                        passend = son == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    default: {
                        passend = false;
                        break;
                    }
                }
            }
        }
        return passend;
    }

    public boolean isMonatlich() {
        return monatlich > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InterventionSchedule that = (InterventionSchedule) o;
        if (termID != null ? !termID.equals(that.termID) : that.termID != null) return false;
        if (abends != null ? !abends.equals(that.abends) : that.abends != null) return false;
        if (bemerkung != null ? !bemerkung.equals(that.bemerkung) : that.bemerkung != null) return false;
        if (die != null ? !die.equals(that.die) : that.die != null) return false;
        if (don != null ? !don.equals(that.don) : that.don != null) return false;
        if (erforderlich != null ? !erforderlich.equals(that.erforderlich) : that.erforderlich != null) return false;
        if (fre != null ? !fre.equals(that.fre) : that.fre != null) return false;
        if (intervention != null ? !intervention.equals(that.intervention) : that.intervention != null) return false;
        if (lDatum != null ? !lDatum.equals(that.lDatum) : that.lDatum != null) return false;
        if (mit != null ? !mit.equals(that.mit) : that.mit != null) return false;
        if (mittags != null ? !mittags.equals(that.mittags) : that.mittags != null) return false;
        if (mon != null ? !mon.equals(that.mon) : that.mon != null) return false;
        if (monatlich != null ? !monatlich.equals(that.monatlich) : that.monatlich != null) return false;
        if (morgens != null ? !morgens.equals(that.morgens) : that.morgens != null) return false;
        if (nachmittags != null ? !nachmittags.equals(that.nachmittags) : that.nachmittags != null) return false;
        if (nachtAb != null ? !nachtAb.equals(that.nachtAb) : that.nachtAb != null) return false;
        if (nachtMo != null ? !nachtMo.equals(that.nachtMo) : that.nachtMo != null) return false;
        if (nursingProcess != null ? !nursingProcess.equals(that.nursingProcess) : that.nursingProcess != null)
            return false;
        if (sam != null ? !sam.equals(that.sam) : that.sam != null) return false;
        if (son != null ? !son.equals(that.son) : that.son != null) return false;
        if (taeglich != null ? !taeglich.equals(that.taeglich) : that.taeglich != null) return false;
        if (tagNum != null ? !tagNum.equals(that.tagNum) : that.tagNum != null) return false;
        if (uhrzeit != null ? !uhrzeit.equals(that.uhrzeit) : that.uhrzeit != null) return false;
        if (uhrzeitAnzahl != null ? !uhrzeitAnzahl.equals(that.uhrzeitAnzahl) : that.uhrzeitAnzahl != null)
            return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        if (woechentlich != null ? !woechentlich.equals(that.woechentlich) : that.woechentlich != null) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = termID != null ? termID.hashCode() : 0;
        result = 31 * result + (nachtMo != null ? nachtMo.hashCode() : 0);
        result = 31 * result + (morgens != null ? morgens.hashCode() : 0);
        result = 31 * result + (mittags != null ? mittags.hashCode() : 0);
        result = 31 * result + (nachmittags != null ? nachmittags.hashCode() : 0);
        result = 31 * result + (abends != null ? abends.hashCode() : 0);
        result = 31 * result + (nachtAb != null ? nachtAb.hashCode() : 0);
        result = 31 * result + (uhrzeitAnzahl != null ? uhrzeitAnzahl.hashCode() : 0);
        result = 31 * result + (uhrzeit != null ? uhrzeit.hashCode() : 0);
        result = 31 * result + (taeglich != null ? taeglich.hashCode() : 0);
        result = 31 * result + (woechentlich != null ? woechentlich.hashCode() : 0);
        result = 31 * result + (monatlich != null ? monatlich.hashCode() : 0);
        result = 31 * result + (tagNum != null ? tagNum.hashCode() : 0);
        result = 31 * result + (mon != null ? mon.hashCode() : 0);
        result = 31 * result + (die != null ? die.hashCode() : 0);
        result = 31 * result + (mit != null ? mit.hashCode() : 0);
        result = 31 * result + (don != null ? don.hashCode() : 0);
        result = 31 * result + (fre != null ? fre.hashCode() : 0);
        result = 31 * result + (sam != null ? sam.hashCode() : 0);
        result = 31 * result + (son != null ? son.hashCode() : 0);
        result = 31 * result + (erforderlich != null ? erforderlich.hashCode() : 0);
        result = 31 * result + (lDatum != null ? lDatum.hashCode() : 0);
        result = 31 * result + (bemerkung != null ? bemerkung.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (nursingProcess != null ? nursingProcess.hashCode() : 0);
        result = 31 * result + (intervention != null ? intervention.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(InterventionSchedule interventionSchedule) {
        return intervention.getBezeichnung().compareTo(interventionSchedule.getIntervention().getBezeichnung());
    }

    @Override
    public String toString() {
        return "entity.rest.InterventionSchedule[termID=" + termID + "]";
    }

    @Override
    public InterventionSchedule clone() {
        return new InterventionSchedule(nachtMo, morgens, mittags, nachmittags, abends, nachtAb, uhrzeitAnzahl, uhrzeit, taeglich, woechentlich, monatlich, tagNum, mon, die, mit, don, fre, sam, son, erforderlich, lDatum, bemerkung, nursingProcess, intervention);
    }
}
