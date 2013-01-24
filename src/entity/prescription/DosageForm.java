package entity.prescription;

import entity.nursingprocess.Intervention;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "MPFormen")
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
    private short state;
    @Basic(optional = false)
    @Column(name = "Equiv")
    private int equivalent;

    public DosageForm() {
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

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public int getSameAs() {
        return equivalent;
    }

    public boolean isUPR1() {
        return state == DosageFormTools.UPR1;
    }

    public boolean isUPRbyResident() {
        return state == DosageFormTools.UPR_BY_RESIDENT;
    }

    public boolean isUPRbyTradeForm() {
        return state == DosageFormTools.UPR_BY_TRADEFORM;
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
        if (equivalent != that.equivalent) return false;
        if (packUnit != that.packUnit) return false;
        if (state != that.state) return false;
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
        result = 31 * result + (int) state;
        result = 31 * result + equivalent;
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
                ", state=" + state +
                ", sameAs=" + equivalent +
                ", intervention=" + intervention +
                '}';
    }
}