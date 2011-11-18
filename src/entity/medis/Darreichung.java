package entity.medis;

import op.tools.SYSTools;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 17.11.11
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "MPDarreichung")
public class Darreichung {
    private Long dafId;

    @javax.persistence.Column(name = "DafID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public Long getDafId() {
        return dafId;
    }

    public void setDafId(Long dafId) {
        this.dafId = dafId;
    }

    private String zusatz;

    @javax.persistence.Column(name = "Zusatz", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    public String getZusatz() {
        return SYSTools.catchNull(zusatz);
    }

    public void setZusatz(String zusatz) {
        this.zusatz = zusatz;
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

    // N:1 Relationen
    @JoinColumn(name = "MedPID", referencedColumnName = "MedPID")
    @ManyToOne
    private MedProdukte medProdukt;

    @JoinColumn(name = "FormID", referencedColumnName = "FormID")
    @ManyToOne
    private MedFormen medForm;

    public MedProdukte getMedProdukt() {
        return medProdukt;
    }

    public void setMedProdukt(MedProdukte medProdukt) {
        this.medProdukt = medProdukt;
    }

    public MedFormen getMedForm() {
        return medForm;
    }

    public void setMedForm(MedFormen medForm) {
        this.medForm = medForm;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dafId != 0 ? dafId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Darreichung)) {
            return false;
        }
        Darreichung other = (Darreichung) object;
        if ((this.dafId == null && other.dafId != null) || (this.dafId != null && !this.dafId.equals(other.dafId))) {
            return false;
        }
        return true;
    }
}
