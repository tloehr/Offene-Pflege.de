package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;

import javax.persistence.*;

@Entity
@Table(name = "dosageform")
/**
 *
 */
public class DosageForm extends DefaultEntity {
    private String preparation;
    private String usageText;
    private short usageUnit;
    private short packUnit;
    private short dailyPlan;
    private short uprstate;
    private int sameas;
    private Intervention intervention;

    @Basic(optional = false)
    @Column(name = "Zubereitung")
    public String getPreparation() {
        return preparation;
    }

    public void setPreparation(String preparation) {
        this.preparation = preparation;
    }

    @Basic(optional = false)
    @Column(name = "AnwText")
    public String getUsageText() {
        return usageText;
    }

    public void setUsageText(String usageText) {
        this.usageText = usageText;
    }

    @Basic(optional = false)
    @Column(name = "AnwEinheit")
    public short getUsageUnit() {
        return usageUnit;
    }

    public void setUsageUnit(short usageUnit) {
        this.usageUnit = usageUnit;
    }

    @Basic(optional = false)
    @Column(name = "PackEinheit")
    public short getPackUnit() {
        return packUnit;
    }

    public void setPackUnit(short packUnit) {
        this.packUnit = packUnit;
    }

    /**
     * das hier ist einfach eine Sortierungsmöglichkeit, die bei der Stellplanerzeugung berücksichtigt wird. Da kann man
     * festlegen, was auf dem Plan zusammenstehen soll. Z.B. für Tropfen oder so. 1 sind hier Tropfen, 2 Spritzen. Der
     * Fantasie sind keine Grenzen gesetzt. Die Vorlage ist so eingestellt, dass alles über 0 grau hinterlegt wird.
     *
     * @return
     */
    @Basic(optional = false)
    @Column(name = "Stellplan")
    public short getDailyPlan() {
        return dailyPlan;
    }

    public void setDailyPlan(short dailyPlan) {
        this.dailyPlan = dailyPlan;
    }

    @Basic(optional = false)
    @Column(name = "Status")
    public short getUprstate() {
        return uprstate;
    }

    public void setUprstate(short uprstate) {
        this.uprstate = uprstate;
    }

    @Basic(optional = false)
    @Column(name = "Equiv")
    public int getSameas() {
        return sameas;
    }

    public void setSameas(int sameas) {
        this.sameas = sameas;
    }

    @JoinColumn(name = "MassID", referencedColumnName = "MassID")
    @ManyToOne
    public Intervention getIntervention() {
        return intervention;
    }

    public void setIntervention(Intervention intervention) {
        this.intervention = intervention;
    }


    public DosageForm() {
    }

}