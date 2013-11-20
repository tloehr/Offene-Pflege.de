package entity.prescription;

import entity.nursingprocess.Intervention;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "dosageform")
/**
 *
 */
public class DosageForm implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FormID")
    private Long id;
    @Basic(optional = false)
    @Column(name = "Zubereitung")
    private String preparation;
    @Basic(optional = false)
    @Column(name = "AnwText")
    private String usageText;
    @Basic(optional = false)
    @Column(name = "AnwEinheit")
    private short usageUnit;
    @Basic(optional = false)
    @Column(name = "PackEinheit")
    private short packUnit;
    @Basic(optional = false)
    @Column(name = "Stellplan")
    private short dailyPlan;
    @Basic(optional = false)
    @Column(name = "Status")
    private short uprstate;
    @Basic(optional = false)
    @Column(name = "Equiv")
    private int sameas;
    @Version
    @Column(name = "version")
    private Long version;

    public DosageForm() {
    }

    public DosageForm(int sameas) {
        this.preparation = "";
        this.usageText = "";
        this.usageUnit = 0;
        this.packUnit = 0;
        this.dailyPlan = 0;
        this.uprstate = DosageFormTools.STATE_UPR1;
        this.sameas = sameas;
        this.intervention = null;
    }

    public String getPreparation() {
        return preparation;
    }

    public String getUsageText() {
        return usageText;
    }

    public short getUsageUnit() {
        return usageUnit;
    }

    public short getPackUnit() {
        return packUnit;
    }

    /**
     *
     * das hier ist einfach eine Sortierungsmöglichkeit, die bei der Stellplanerzeugung berücksichtigt wird.
     * Da kann man festlegen, was auf dem Plan zusammenstehen soll.
     * Z.B. für Tropfen oder so. 1 sind hier Tropfen, 2 Spritzen. Der Fantasie sind keine Grenzen gesetzt.
     * Die Vorlage ist so eingestellt, dass alles über 0 grau hinterlegt wird.
     *
     * @return
     */
    public short getDailyPlan() {
        return dailyPlan;
    }

    public short getUPRState() {
        return uprstate;
    }

    public void setUPRState(short state) {
        this.uprstate = state;
    }

    public int getSameAs() {
        return sameas;
    }

    public void setPreparation(String preparation) {
        this.preparation = preparation;
    }

    public void setUsageText(String usageText) {
        this.usageText = usageText;
    }

    public void setUsageUnit(short usageUnit) {
        this.usageUnit = usageUnit;
    }

    public void setPackUnit(short packUnit) {
        this.packUnit = packUnit;
    }

    public void setDailyPlan(short dailyPlan) {
        this.dailyPlan = dailyPlan;
    }

    public void setSameas(int sameas) {
        this.sameas = sameas;
    }

    public void setIntervention(Intervention intervention) {
        this.intervention = intervention;
    }

    public boolean isUPR1() {
        return uprstate == DosageFormTools.STATE_UPR1;
    }

    public boolean isDontCALC() {
        return uprstate == DosageFormTools.STATE_DONT_CALC;
    }

    public boolean isUPRn() {
        return uprstate == DosageFormTools.STATE_UPRn;
    }

    public Long getId() {
        return id;
    }

    public Intervention getIntervention() {
        return intervention;
    }

    // N:1 Relationen
    @JoinColumn(name = "MassID", referencedColumnName = "MassID")
    @ManyToOne
    private Intervention intervention;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DosageForm that = (DosageForm) o;

        if (dailyPlan != that.dailyPlan) return false;
        if (sameas != that.sameas) return false;
        if (packUnit != that.packUnit) return false;
        if (uprstate != that.uprstate) return false;
        if (usageUnit != that.usageUnit) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (intervention != null ? !intervention.equals(that.intervention) : that.intervention != null) return false;
        if (preparation != null ? !preparation.equals(that.preparation) : that.preparation != null) return false;
        if (usageText != null ? !usageText.equals(that.usageText) : that.usageText != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (preparation != null ? preparation.hashCode() : 0);
        result = 31 * result + (usageText != null ? usageText.hashCode() : 0);
        result = 31 * result + (int) usageUnit;
        result = 31 * result + (int) packUnit;
        result = 31 * result + (int) dailyPlan;
        result = 31 * result + (int) uprstate;
        result = 31 * result + sameas;
        result = 31 * result + (intervention != null ? intervention.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DosageForm{" +
                "id=" + id +
                ", preparation='" + preparation + '\'' +
                ", usageText='" + usageText + '\'' +
                ", usageUnit=" + usageUnit +
                ", packUnit=" + packUnit +
                ", dailyPlan=" + dailyPlan +
                ", state=" + uprstate +
                ", sameAs=" + sameas +
                ", intervention=" + intervention +
                '}';
    }
}