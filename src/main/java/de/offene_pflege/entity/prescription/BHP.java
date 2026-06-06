package de.offene_pflege.entity.prescription;


import de.offene_pflege.entity.DefaultEntity;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.reports.NReport;
import de.offene_pflege.entity.system.OPUsers;
import de.offene_pflege.op.tools.SYSCalendar;
import de.offene_pflege.op.tools.SYSTools;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "bhp")
@ToString
public class BHP extends DefaultEntity implements Serializable, Comparable<BHP> {
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
    @Basic(optional = false)
    @Column(name = "nanotime")
    private Long nanotime;
    @JoinColumn(name = "outcome_nreport", referencedColumnName = "id")
    @ManyToOne
    private NReport outcome_report;

    public NReport getOutcome_report() {
        return outcome_report;
    }

    public void setOutcome_report(NReport outcome_report) {
        this.outcome_report = outcome_report;
    }

    public BHP() {
    }

    public BHP(PrescriptionSchedule prescriptionSchedule) {
        // looks redundant but simplifies
        this.prescriptionSchedule = prescriptionSchedule;
        this.prescription = this.prescriptionSchedule.getPrescription();
        this.resident = this.prescriptionSchedule.getPrescription().getResident();
        this.tradeform = this.prescriptionSchedule.getPrescription().getTradeForm();
        stockTransaction = new ArrayList<>();
        this.nanotime = System.nanoTime();
        this.mdate = new Date();
        this.outcome_report = null;
    }

    public BHP(PrescriptionSchedule prescriptionSchedule, Date soll, Byte sZeit, BigDecimal dosis) {
        // looks redundant but simplifies
        this.prescriptionSchedule = prescriptionSchedule;
        this.prescription = this.prescriptionSchedule.getPrescription();
        this.resident = this.prescriptionSchedule.getPrescription().getResident();
        this.tradeform = this.prescriptionSchedule.getPrescription().getTradeForm();
        this.soll = soll;
        this.nanotime = System.nanoTime();
        this.sZeit = sZeit;
        this.dosis = dosis;
        this.state = BHPTools.STATE_OPEN;
        this.mdate = new Date();
        stockTransaction = new ArrayList<>();
        this.outcome_report = null;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bhp")
    private List<MedStockTransaction> stockTransaction;

    @JoinColumn(name = "BHPPID", referencedColumnName = "BHPPID")
    @ManyToOne
    private PrescriptionSchedule prescriptionSchedule;

    @JoinColumn(name = "VerID", referencedColumnName = "VerID")
    @ManyToOne
    private Prescription prescription;

    @JoinColumn(name = "BWKennung", referencedColumnName = "id")
    @ManyToOne
    private Resident resident;

    @JoinColumn(name = "DafID", referencedColumnName = "DafID")
    @ManyToOne
    private TradeForm tradeform;

    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private OPUsers user;

    public PrescriptionSchedule getPrescriptionSchedule() {
        return prescriptionSchedule;
    }

    public void setPrescriptionSchedule(PrescriptionSchedule prescriptionSchedule) {
        this.prescriptionSchedule = prescriptionSchedule;
    }

    public OPUsers getUser() {
        return user;
    }

    public void setUser(OPUsers user) {
        this.user = user;
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
        return prescription.getTradeForm() != null;
    }

    public boolean shouldBeCalculated() {
        return hasMed() && resident.getCalcMediUPR1();
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
        return prescription.isOnDemand();
    }

    public Byte getShift() {
        if (isOnDemand()) {
            return SYSCalendar.SHIFT_ON_DEMAND;
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
            id.compareTo(that.id);
        }
        return result;
    }

}
