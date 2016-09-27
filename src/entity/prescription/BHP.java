package entity.prescription;

import entity.Ownable;
import entity.info.Resident;
import entity.system.Users;
import op.OPDE;
import gui.GUITools;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
//import org.eclipse.persistence.annotations.OptimisticLocking;
//import org.eclipse.persistence.annotations.OptimisticLockingType;
import org.joda.time.DateTime;

import javax.persistence.*;

import java.awt.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "bhp")
//@OptimisticLocking(cascade = false, type = OptimisticLockingType.VERSION_COLUMN)
public class BHP implements Serializable, Comparable<BHP> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BHPID")
    private Long bhpid;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "Soll")
    @Temporal(TemporalType.TIMESTAMP)
    private Date soll;
    @Column(name = "Ist")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ist;
    @Column(name = "SZeit")
    private Byte sZeit;
    @Column(name = "IZeit")
    private Byte iZeit;
    @Column(name = "Dosis")
    private BigDecimal dosis;
    @Column(name = "Status")
    private Byte state;
    @Lob
    @Column(name = "text")
    private String text;
    @Basic(optional = false)
    @Column(name = "MDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mdate;
    @Column(name = "Dauer")
    private Short dauer;
    @Column(name = "needsText")
    private Boolean needsText;
    @Basic(optional = false)
    @Column(name = "nanotime")
    private Long nanotime;
    @JoinColumn(name = "outcome4", referencedColumnName = "BHPID")
    @ManyToOne
    private BHP outcome4;

    public BHP() {
    }

    public BHP(PrescriptionSchedule prescriptionSchedule) {
        // looks redundant but simplifies enormously
        this.prescriptionSchedule = prescriptionSchedule;
        this.prescription = this.prescriptionSchedule.getPrescription();
        this.resident = this.prescriptionSchedule.getPrescription().getResident();
        this.tradeform = this.prescriptionSchedule.getPrescription().getTradeForm();
        stockTransaction = new ArrayList<MedStockTransaction>();
        this.version = 0l;
        this.nanotime = System.nanoTime();
        this.mdate = new Date();
        this.needsText = false;
        this.dauer = 0;
    }

    /**
     * constructs an outcome text BHP
     *
     * @param bhp
     */
    public BHP(BHP bhp) {
        // looks redundant but simplifies enormously
        this.prescriptionSchedule = bhp.getPrescriptionSchedule();
        this.prescription = this.prescriptionSchedule.getPrescription();
        this.resident = this.prescriptionSchedule.getPrescription().getResident();
        this.tradeform = this.prescriptionSchedule.getPrescription().getTradeForm();

        // the target time depends on the moment when the original OnDemand BHP is clicked.
        // the outcome needs to be checked "check after hours" + this moment
        DateTime targetTime = new DateTime().plusMinutes(this.prescriptionSchedule.getCheckAfterHours().multiply(new BigDecimal(60)).intValue());

        this.soll = targetTime.toDate();
        this.version = 0l;
        this.nanotime = System.nanoTime();
        this.sZeit = SYSCalendar.BYTE_TIMEOFDAY;
        this.dosis = BigDecimal.ONE.negate(); // this is ALWAYS -1. its NOT the negation of the original dose
        this.state = BHPTools.STATE_OPEN;
        this.mdate = new Date();
        stockTransaction = new ArrayList<MedStockTransaction>();
        this.outcome4 = bhp;
        this.needsText = true;
        this.dauer = 0;
    }

    public BHP(PrescriptionSchedule prescriptionSchedule, Date soll, Byte sZeit, BigDecimal dosis) {
        // looks redundant but simplifies enormously
        this.prescriptionSchedule = prescriptionSchedule;
        this.prescription = this.prescriptionSchedule.getPrescription();
        this.resident = this.prescriptionSchedule.getPrescription().getResident();
        this.tradeform = this.prescriptionSchedule.getPrescription().getTradeForm();
        this.soll = soll;
        this.version = 0l;
        this.nanotime = System.nanoTime();
        this.sZeit = sZeit;
        this.dosis = dosis;
        this.state = BHPTools.STATE_OPEN;
        this.mdate = new Date();
        stockTransaction = new ArrayList<MedStockTransaction>();
        this.needsText = false;
        this.dauer = 0;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bhp")
    private List<MedStockTransaction> stockTransaction;

    @JoinColumn(name = "BHPPID", referencedColumnName = "BHPPID")
    @ManyToOne
    private PrescriptionSchedule prescriptionSchedule;

    @JoinColumn(name = "VerID", referencedColumnName = "VerID")
    @ManyToOne
    private Prescription prescription;

    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;

    @JoinColumn(name = "DafID", referencedColumnName = "DafID")
    @ManyToOne
    private TradeForm tradeform;

    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    public PrescriptionSchedule getPrescriptionSchedule() {
        return prescriptionSchedule;
    }

    public void setPrescriptionSchedule(PrescriptionSchedule prescriptionSchedule) {
        this.prescriptionSchedule = prescriptionSchedule;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Long getBHPid() {
        return bhpid;
    }

    public void setBHPid(Long bhpid) {
        this.bhpid = bhpid;
    }

    public Date getSoll() {
        return soll;
    }

    public long getVersion() {
        return version;
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

    public Byte getSollZeit() {
        return sZeit;
    }

    public void setSollZeit(Byte sZeit) {
        this.sZeit = sZeit;
    }

    public Byte getiZeit() {
        return iZeit;
    }

    public void setiZeit(Byte iZeit) {
        this.iZeit = iZeit;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public BigDecimal getDose() {
        return dosis;
    }

    public boolean hasMed() {
        return !isOutcomeText() && prescription.getTradeForm() != null;
    }

    public boolean shouldBeCalculated() {
        return !isOutcomeText() && hasMed() && resident.isCalcMediUPR1();
    }

    public boolean isOpen() {
        return state == BHPTools.STATE_OPEN;
    }


    public void setDosis(BigDecimal dosis) {
        this.dosis = dosis;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = SYSTools.tidy(text);
    }

    public Date getMDate() {
        return mdate;
    }

    public void setMDate(Date mdate) {
        this.mdate = mdate;
    }

    public Short getDauer() {
        return dauer;
    }

    public void setDauer(Short dauer) {
        this.dauer = dauer;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public TradeForm getTradeForm() {
        return tradeform;
    }

    /**
     * true if the underlying prescription is of type "OnDemand".
     *
     * @return
     */
    public boolean isOnDemand() {
        return prescription.isOnDemand() && !isOutcomeText();
    }

    /**
     * determines whether the confirmation of a BHP should trigger a mandantory note or not. (default NOT)
     *
     * @return
     */
    public Boolean getNeedsText() {
        return needsText;
    }

    public void setNeedsText(Boolean needsText) {
        this.needsText = needsText;
    }


    /**
     * BHPs which are "outcome4" another BHP are supposed to have a description text.
     *
     * @return
     */
    public boolean isOutcomeText() {
        return outcome4 != null;
    }


    public BHP getOutcome4() {
        return outcome4;
    }

    public void setOutcome4(BHP outcome4) {
        this.outcome4 = outcome4;
    }

    public Byte getShift() {
        if (isOnDemand()) {
            return SYSCalendar.SHIFT_ON_DEMAND;
        }
        if (isOutcomeText()) {
            return SYSCalendar.SHIFT_OUTCOMES;
        }
        if (sZeit == SYSCalendar.BYTE_TIMEOFDAY) {
            return SYSCalendar.whatShiftIs(this.soll);
        }
        return SYSCalendar.whatShiftIs(this.sZeit);
    }


    public List<MedStockTransaction> getStockTransaction() {
        return stockTransaction;
    }

    /**
     * This method tells, whether there was more than one stock involved in order to provide the necessary medication in the course of the
     * application of this BHP. This can only happen when a stock is closed in advance. After clicking this BHP the first stock is emptied and
     * then the next stock is opened.
     *
     * @return true or false
     */
    public boolean isClosedStockInvolved() {
        boolean yes = false;
        if (stockTransaction != null) {
            for (MedStockTransaction buchung : stockTransaction) {
                yes = buchung.getStock().isClosed();
                if (yes) {
                    break;
                }
            }
        }
        return yes;
    }

    @Override
    public int compareTo(BHP that) {
        int result = this.getShift().compareTo(that.getShift());
        if (result == 0) {
            result = sZeit.compareTo(that.getSollZeit());
        }
        if (result == 0) {
            result = SYSTools.nullCompare(this.getTradeForm(), that.getTradeForm());
        }

        if (result == 0) {
            if (prescription.hasMed()) {
                result = TradeFormTools.toPrettyString(prescription.getTradeForm()).compareTo(TradeFormTools.toPrettyString(that.getPrescription().getTradeForm()));
            } else {
                result = this.prescription.getIntervention().getBezeichnung().compareTo(that.getPrescription().getIntervention().getBezeichnung());
            }
        }

        if (result == 0) {
            bhpid.compareTo(that.bhpid);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {

            return false;
        }

        BHP bhp = (BHP) o;

        if (text != null ? !text.equals(bhp.text) : bhp.text != null) return false;
        if (bhpid != null ? !bhpid.equals(bhp.bhpid) : bhp.bhpid != null) return false;
        if (dauer != null ? !dauer.equals(bhp.dauer) : bhp.dauer != null) return false;
        if (dosis != null ? !dosis.equals(bhp.dosis) : bhp.dosis != null) return false;
        if (iZeit != null ? !iZeit.equals(bhp.iZeit) : bhp.iZeit != null) return false;
        if (ist != null ? !ist.equals(bhp.ist) : bhp.ist != null) return false;
        if (mdate != null ? !mdate.equals(bhp.mdate) : bhp.mdate != null) return false;
        if (nanotime != null ? !nanotime.equals(bhp.nanotime) : bhp.nanotime != null) return false;
        if (prescription != null ? !prescription.equals(bhp.prescription) : bhp.prescription != null) return false;
        if (prescriptionSchedule != null ? !prescriptionSchedule.equals(bhp.prescriptionSchedule) : bhp.prescriptionSchedule != null)
            return false;
        if (resident != null ? !resident.equals(bhp.resident) : bhp.resident != null) return false;
        if (sZeit != null ? !sZeit.equals(bhp.sZeit) : bhp.sZeit != null) return false;
        if (soll != null ? !soll.equals(bhp.soll) : bhp.soll != null) return false;
        if (state != null ? !state.equals(bhp.state) : bhp.state != null) return false;
//        if (stockTransaction != null ? !stockTransaction.equals(bhp.stockTransaction) : bhp.stockTransaction != null)
//            return false;
        if (tradeform != null ? !tradeform.equals(bhp.tradeform) : bhp.tradeform != null) return false;
        if (user != null ? !user.equals(bhp.user) : bhp.user != null) return false;
        if (version != null ? !version.equals(bhp.version) : bhp.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bhpid != null ? bhpid.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (soll != null ? soll.hashCode() : 0);
        result = 31 * result + (ist != null ? ist.hashCode() : 0);
        result = 31 * result + (sZeit != null ? sZeit.hashCode() : 0);
        result = 31 * result + (iZeit != null ? iZeit.hashCode() : 0);
        result = 31 * result + (dosis != null ? dosis.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (mdate != null ? mdate.hashCode() : 0);
        result = 31 * result + (dauer != null ? dauer.hashCode() : 0);
        result = 31 * result + (nanotime != null ? nanotime.hashCode() : 0);
//        result = 31 * result + (stockTransaction != null ? stockTransaction.hashCode() : 0);
        result = 31 * result + (prescriptionSchedule != null ? prescriptionSchedule.hashCode() : 0);
        result = 31 * result + (prescription != null ? prescription.hashCode() : 0);
        result = 31 * result + (resident != null ? resident.hashCode() : 0);
        result = 31 * result + (tradeform != null ? tradeform.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }


//    @Override
//    public boolean equals(Object o) {
//        if (!(o instanceof BHP)){
//            OPDE.info("not instance of BHP " + o.toString());
//            OPDE.info("i am a bhp with id: " + bhpid);
//            return false;
//        }
//        BHP other = (BHP) o;
//        return new Integer(hashCode()).equals(other.hashCode());
//    }
//
//    @Override
//    public int hashCode() {
//        int result = bhpid != null ? bhpid.hashCode() : 0;
//        result = 31 * result + (nanotime != null ? nanotime.hashCode() : 0);
//        return result;
//    }

    @Override
    public String toString() {
        return "BHP{" +
                "bhpid=" + bhpid +
                '}';
    }

}
