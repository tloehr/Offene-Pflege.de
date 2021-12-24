package de.offene_pflege.entity.prescription;

import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSCalendar;
import lombok.extern.log4j.Log4j2;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.OptimisticLockingType;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;

@Entity
@Table(name = "pschedule")
@OptimisticLocking(cascade = false, type = OptimisticLockingType.VERSION_COLUMN)
@Log4j2
public class PrescriptionSchedule implements Serializable, Cloneable, Comparable<PrescriptionSchedule> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BHPPID")
    private Long bhppid;
    @Version
    @Column(name = "version")
    private Long version;
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
    @Column(name = "checkAfterHrs")
    private BigDecimal checkAfterHours;


    public PrescriptionSchedule() {

    }

    public PrescriptionSchedule(Prescription prescription) {

        nachtMo = BigDecimal.ZERO;
        mittags = BigDecimal.ZERO;
        nachmittags = BigDecimal.ZERO;
        abends = BigDecimal.ZERO;
        nachtAb = BigDecimal.ZERO;
        uhrzeitDosis = BigDecimal.ZERO;
        uhrzeit = null;
        taeglich = 1;
        woechentlich = 0;
        monatlich = 0;
        tagNum = 0;
        lDatum = new Date();

        this.prescription = prescription;

        if (prescription.isOnDemand()) {
            morgens = BigDecimal.ZERO;
            maxAnzahl = 1;
            maxEDosis = BigDecimal.ONE;
        } else {
            morgens = BigDecimal.ONE;
            maxAnzahl = 0;
            maxEDosis = BigDecimal.ZERO;
        }

        mon = 0;
        die = 0;
        mit = 0;
        don = 0;
        fre = 0;
        sam = 0;
        son = 0;

    }

    public PrescriptionSchedule(BigDecimal nachtMo, BigDecimal morgens, BigDecimal mittags, BigDecimal nachmittags, BigDecimal abends, BigDecimal nachtAb, BigDecimal uhrzeitDosis, Date uhrzeit, Integer maxAnzahl, BigDecimal maxEDosis, Short taeglich, Short woechentlich, Short monatlich, Short tagNum, Short mon, Short die, Short mit, Short don, Short fre, Short sam, Short son, Date lDatum, Prescription prescription) {
        this.nachtMo = nachtMo;
        this.morgens = morgens;
        this.mittags = mittags;
        this.nachmittags = nachmittags;
        this.abends = abends;
        this.nachtAb = nachtAb;
        this.uhrzeitDosis = uhrzeitDosis;
        this.uhrzeit = uhrzeit;
        this.maxAnzahl = maxAnzahl;
        this.maxEDosis = maxEDosis;
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
        this.lDatum = lDatum;
        this.prescription = prescription;
//        this.prescription.getPlanungen().add(this);
    }

    public Long getBhppid() {
        return bhppid;
    }

    public void setBhppid(Long bhppid) {
        this.bhppid = bhppid;
    }


    public Long getVersion() {
        return version;
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

    public Short getTue() {
        return die;
    }

    public void setTue(Short die) {
        this.die = die;
    }

    public Short getWed() {
        return mit;
    }

    public void setWed(Short mit) {
        this.mit = mit;
    }

    public Short getThu() {
        return don;
    }

    public void setThu(Short don) {
        this.don = don;
    }

    public Short getFri() {
        return fre;
    }

    public void setFri(Short fre) {
        this.fre = fre;
    }

    public Short getSat() {
        return sam;
    }

    public void setSat(Short sam) {
        this.sam = sam;
    }

    public Short getSun() {
        return son;
    }

    public void setSun(Short son) {
        this.son = son;
    }

    /**
     * this is the time in hours after a mandantory description of the outcome of the application of that specific on-demand-prescription needs to be entered by the user.
     *
     * @return
     */
    public BigDecimal getCheckAfterHours() {
        return checkAfterHours;
    }

    public void setCheckAfterHours(BigDecimal checkAfterHours) {
        this.checkAfterHours = checkAfterHours;
    }

    /**
     * sums up the applications in the morning (that means early in the morning, in the morning and everything between 0h and midday)
     *
     * @return
     */
    public BigDecimal getMorningSum() {
        BigDecimal bd = BigDecimal.ZERO;
        if (uhrzeit != null) {
            byte timecode = SYSCalendar.whatTimeIDIs(uhrzeit);
            if (timecode >= SYSCalendar.BYTE_EARLY_IN_THE_MORNING && timecode <= SYSCalendar.BYTE_MORNING) {
                bd = uhrzeitDosis;
            }
        } else {
            bd = nachtMo.add(morgens);
        }

        return bd;
    }

    public BigDecimal getNoonSum() {
        BigDecimal bd = BigDecimal.ZERO;
        if (uhrzeit != null) {
            byte timecode = SYSCalendar.whatTimeIDIs(uhrzeit);
            if (timecode >= SYSCalendar.BYTE_NOON && timecode <= SYSCalendar.BYTE_AFTERNOON) {
                bd = uhrzeitDosis;
            }
        } else {
            bd = mittags.add(nachmittags);
        }

        return bd;
    }


    public BigDecimal getEveningSum() {
        BigDecimal bd = BigDecimal.ZERO;
        if (uhrzeit != null) {
            byte timecode = SYSCalendar.whatTimeIDIs(uhrzeit);
            if (timecode >= SYSCalendar.BYTE_EVENING && timecode <= SYSCalendar.BYTE_LATE_AT_NIGHT) {
                bd = uhrzeitDosis;
            }
        } else {
            bd = abends.add(nachtAb);
        }

        return bd;
    }

    /**
     * LDatum enthält das Datum, an dem diese PrescriptionSchedule zuletzt zu einer BHP geführt hat. Das Datum kann auch in der Zukunft liegen,
     * dann wird der BHPImport solange warten, bis diese prescription "an der Reihe" ist. Das LDatum wird bei DlgVerabreichung ebenfalls gesetzt.
     * Steht dort auf dem Dialog unter "Erste Verabreichung am".
     *
     * @return
     */
    public Date getLDatum() {
        return new Date(SYSCalendar.startOfDay(lDatum));
    }

    /**
     * @param lDatum
     * @see #getLDatum()
     */
    public void setLDatum(Date lDatum) {
        this.lDatum = new Date(SYSCalendar.startOfDay(lDatum));
    }

    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "VerID", referencedColumnName = "VerID")
    @ManyToOne
    private Prescription prescription;

    public Prescription getPrescription() {
        return prescription;
    }

    public void setprescription(Prescription prescription) {
        this.prescription = prescription;
//        if (!this.prescription.getPlanungen().contains(this)) {
//            this.prescription.getPlanungen().add(this);
//        }
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }


    public BigDecimal getOverAllDoseSum() {
        return nachtMo.add(morgens).add(mittags).add(nachmittags).add(abends).add(nachtAb).add(uhrzeitDosis);
    }

    /**
     * gibt an, ob bei der Planungen die festen Zeiten wie Früh, Spät, Nacht usw. verwendet wurden
     *
     * @return
     */
    public boolean usesTimesOfTheDay() {
        return nachtMo.add(morgens).add(mittags).add(nachmittags).add(abends).add(nachtAb).compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * ermittelt ob es sich um die Planung für eine Bedarfsmedikation handelt
     *
     * @return
     */
    public boolean isOnDemand() {
        return prescription.isOnDemand();
    }

    /**
     * ermittelt ob es sich um die Planung per Uhrzeit handelt
     *
     * @return
     */
    public boolean usesTime() {
        return uhrzeit != null;
    }

    public boolean isDaily() {
        return taeglich > 0;
    }

    public boolean isWeekly() {
        return woechentlich > 0;
    }

    public boolean isMonthly() {
        return monatlich > 0;
    }

    /**
     * @param date, zu prüfendes Datum.
     * @return Ist <code>true</code>, wenn diese Planung wöchentlich gilt und das Attribut mit dem aktuellen Wochentagsnamen größer null ist.
     */
    public boolean isPassenderWochentag(Date date) {
        boolean passend = false;

        if (isWeekly()) { // wenn nicht wöchentlich, dann passt gar nix
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
     * <b>oder</b> das Attribut mit dem aktuellen Wochentagsnamen gleich dem Wochentag im Monat entpricht (der erste Mitwwoch im Monat hat 1, der zweite 2 usw...).
     */
    public boolean isPassenderTagImMonat(Date date) {
        boolean passend = false;
        if (isMonthly()) { // wenn nicht monatlich, dann passt gar nix
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrescriptionSchedule that = (PrescriptionSchedule) o;

        if (bhppid != null ? !bhppid.equals(that.bhppid) : that.bhppid != null) return false;
        if (abends != null ? !abends.equals(that.abends) : that.abends != null) return false;
        if (die != null ? !die.equals(that.die) : that.die != null) return false;
        if (don != null ? !don.equals(that.don) : that.don != null) return false;
        if (fre != null ? !fre.equals(that.fre) : that.fre != null) return false;
        if (lDatum != null ? !lDatum.equals(that.lDatum) : that.lDatum != null) return false;
        if (maxAnzahl != null ? !maxAnzahl.equals(that.maxAnzahl) : that.maxAnzahl != null) return false;
        if (maxEDosis != null ? !maxEDosis.equals(that.maxEDosis) : that.maxEDosis != null) return false;
        if (mit != null ? !mit.equals(that.mit) : that.mit != null) return false;
        if (mittags != null ? !mittags.equals(that.mittags) : that.mittags != null) return false;
        if (mon != null ? !mon.equals(that.mon) : that.mon != null) return false;
        if (monatlich != null ? !monatlich.equals(that.monatlich) : that.monatlich != null) return false;
        if (morgens != null ? !morgens.equals(that.morgens) : that.morgens != null) return false;
        if (nachmittags != null ? !nachmittags.equals(that.nachmittags) : that.nachmittags != null) return false;
        if (nachtAb != null ? !nachtAb.equals(that.nachtAb) : that.nachtAb != null) return false;
        if (nachtMo != null ? !nachtMo.equals(that.nachtMo) : that.nachtMo != null) return false;
        if (sam != null ? !sam.equals(that.sam) : that.sam != null) return false;
        if (son != null ? !son.equals(that.son) : that.son != null) return false;
        if (taeglich != null ? !taeglich.equals(that.taeglich) : that.taeglich != null) return false;
        if (tagNum != null ? !tagNum.equals(that.tagNum) : that.tagNum != null) return false;
        if (uhrzeit != null ? !uhrzeit.equals(that.uhrzeit) : that.uhrzeit != null) return false;
        if (uhrzeitDosis != null ? !uhrzeitDosis.equals(that.uhrzeitDosis) : that.uhrzeitDosis != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        if (woechentlich != null ? !woechentlich.equals(that.woechentlich) : that.woechentlich != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bhppid != null ? bhppid.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (nachtMo != null ? nachtMo.hashCode() : 0);
        result = 31 * result + (morgens != null ? morgens.hashCode() : 0);
        result = 31 * result + (mittags != null ? mittags.hashCode() : 0);
        result = 31 * result + (nachmittags != null ? nachmittags.hashCode() : 0);
        result = 31 * result + (abends != null ? abends.hashCode() : 0);
        result = 31 * result + (nachtAb != null ? nachtAb.hashCode() : 0);
        result = 31 * result + (uhrzeitDosis != null ? uhrzeitDosis.hashCode() : 0);
        result = 31 * result + (uhrzeit != null ? uhrzeit.hashCode() : 0);
        result = 31 * result + (maxAnzahl != null ? maxAnzahl.hashCode() : 0);
        result = 31 * result + (maxEDosis != null ? maxEDosis.hashCode() : 0);
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
        result = 31 * result + (lDatum != null ? lDatum.hashCode() : 0);
        return result;
    }

    @Override
    public Object clone() {
        return new PrescriptionSchedule(nachtMo, morgens, mittags, nachmittags, abends, nachtAb, uhrzeitDosis, uhrzeit, maxAnzahl, maxEDosis, taeglich, woechentlich, monatlich, tagNum, mon, die, mit, don, fre, sam, son, lDatum, prescription);
    }

    public PrescriptionSchedule createCopy(Prescription myprescription) {
        return new PrescriptionSchedule(nachtMo, morgens, mittags, nachmittags, abends, nachtAb, uhrzeitDosis, uhrzeit, maxAnzahl, maxEDosis, taeglich, woechentlich, monatlich, tagNum, mon, die, mit, don, fre, sam, son, lDatum, myprescription);
    }

    /**
     * Vergleichsoperator für die Sortierung. Die Sortierung soll in folgender Reihenfolge sein:
     * <ol>
     * <li>Zuerst die prescriptionen mit Zeiten</li>
     * <li>Dann die prescriptionen mit Uhrzeiten</li>
     * <li>Dann der Rest</li>
     * </ol>
     * Innerhalb der Gruppen wird nach dem PK sortiert. Bei den Uhrzeiten wird das compareTo von Date verwendet.
     *
     * @param that
     * @return
     */
    @Override
    public int compareTo(PrescriptionSchedule that) {

        log.debug("sorting");

        if (this.isOnDemand()) return 0;

        int result = 0;


        if (this.usesTimesOfTheDay() && that.usesTimesOfTheDay()) {
            result = this.bhppid.compareTo(that.getBhppid());
        } else if (this.usesTime() && that.usesTime()) {
            result = this.uhrzeit.compareTo(that.getUhrzeit());
        } else if (this.usesTimesOfTheDay()) { // Zeiten zuerst.
            result = 1;
        } else {
            result = -1;
        }

        return result;
    }

    @Override
    public String toString() {
        return "PrescriptionSchedule{" +
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
                ", prescription=" + prescription +
                '}';
    }
}
