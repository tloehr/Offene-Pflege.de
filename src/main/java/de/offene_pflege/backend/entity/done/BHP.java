package de.offene_pflege.backend.entity.done;


import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.backend.entity.prescription.Prescription;
import de.offene_pflege.backend.entity.prescription.PrescriptionSchedule;
import de.offene_pflege.backend.entity.prescription.TradeForm;
import de.offene_pflege.backend.entity.system.OPUsers;
import de.offene_pflege.backend.services.BHPService;
import de.offene_pflege.backend.services.TradeFormTools;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

//import org.eclipse.persistence.annotations.OptimisticLocking;
//import org.eclipse.persistence.annotations.OptimisticLockingType;

@Entity
@Table(name = "bhp")
public class BHP extends DefaultEntity implements Comparable<BHP> {
    private Date soll;
    private Date ist;
    private Byte sZeit;
    private Byte iZeit;
    private BigDecimal dosis;
    private Byte state;
    private String text;
    private Date mdate;
    private Boolean needsText;
    private Long nanotime;
    private BHP outcome4;
    private List<MedStockTransaction> stockTransaction;
    private PrescriptionSchedule prescriptionSchedule;
    private Prescription prescription;
    private Resident resident;
    private TradeForm tradeform;
    private OPUsers user;

    @Basic(optional = false)
    @Column(name = "Soll")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getSoll() {
        return soll;
    }

    public void setSoll(Date soll) {
        this.soll = soll;
    }


    @Column(name = "Ist")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getIst() {
        return ist;
    }

    public void setIst(Date ist) {
        this.ist = ist;
    }

    @Column(name = "SZeit")
    public Byte getsZeit() {
        return sZeit;
    }

    public void setsZeit(Byte sZeit) {
        this.sZeit = sZeit;
    }

    @Column(name = "IZeit")
    public Byte getiZeit() {
        return iZeit;
    }

    public void setiZeit(Byte iZeit) {
        this.iZeit = iZeit;
    }

    @Column(name = "Dosis")
    public BigDecimal getDosis() {
        return dosis;
    }

    public void setDosis(BigDecimal dosis) {
        this.dosis = dosis;
    }

    @Column(name = "Status")
    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    @Lob
    @Column(name = "text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Basic(optional = false)
    @Column(name = "MDate")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    @Column(name = "needsText")
    public Boolean getNeedsText() {
        return needsText;
    }

    public void setNeedsText(Boolean needsText) {
        this.needsText = needsText;
    }

    @Basic(optional = false)
    @Column(name = "nanotime")
    public Long getNanotime() {
        return nanotime;
    }

    public void setNanotime(Long nanotime) {
        this.nanotime = nanotime;
    }

    @JoinColumn(name = "outcome4", referencedColumnName = "id")
    @ManyToOne
    public BHP getOutcome4() {
        return outcome4;
    }

    public void setOutcome4(BHP outcome4) {
        this.outcome4 = outcome4;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bhp")
    public List<MedStockTransaction> getStockTransaction() {
        return stockTransaction;
    }

    public void setStockTransaction(List<MedStockTransaction> stockTransaction) {
        this.stockTransaction = stockTransaction;
    }

    @JoinColumn(name = "BHPPID", referencedColumnName = "id")
    @ManyToOne
    public PrescriptionSchedule getPrescriptionSchedule() {
        return prescriptionSchedule;
    }

    public void setPrescriptionSchedule(PrescriptionSchedule prescriptionSchedule) {
        this.prescriptionSchedule = prescriptionSchedule;
    }

    @JoinColumn(name = "VerID", referencedColumnName = "id")
    @ManyToOne
    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }


    @JoinColumn(name = "BWKennung", referencedColumnName = "id")
    @ManyToOne
    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    @JoinColumn(name = "DafID", referencedColumnName = "id")
    @ManyToOne
    public TradeForm getTradeform() {
        return tradeform;
    }

    public void setTradeform(TradeForm tradeform) {
        this.tradeform = tradeform;
    }

    @JoinColumn(name = "UKennung", referencedColumnName = "id")
    @ManyToOne
    public OPUsers getUser() {
        return user;
    }

    public void setUser(OPUsers user) {
        this.user = user;
    }


    public BHP() {
    }


    @Override
    public int compareTo(BHP that) {
        int result = BHPService.getShift(this).compareTo(BHPService.getShift(that));
        if (result == 0) {
            result = sZeit.compareTo(that.getsZeit());
        }
        if (result == 0) {
            result = SYSTools.nullCompare(getTradeform(), that.getTradeform());
        }

        if (result == 0) {
            if (prescription.hasMed()) {
                result = TradeFormTools.toPrettyString(prescription.getTradeForm()).compareTo(TradeFormTools.toPrettyString(that.getPrescription().getTradeForm()));
            } else {
                result = this.prescription.getIntervention().getBezeichnung().compareTo(that.getPrescription().getIntervention().getBezeichnung());
            }
        }

        if (result == 0) {
            getId().compareTo(that.getId());
        }
        return result;
    }


}
