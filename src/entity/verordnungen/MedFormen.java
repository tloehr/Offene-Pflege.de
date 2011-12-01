package entity.verordnungen;

import op.tools.SYSTools;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "MPFormen")
@NamedQueries({
    @NamedQuery(name = "MedFormen.findAll", query = "SELECT m FROM MedFormen m"),
    @NamedQuery(name = "MedFormen.findByFormID", query = "SELECT m FROM MedFormen m WHERE m.formID = :formID"),
    @NamedQuery(name = "MedFormen.findByZubereitung", query = "SELECT m FROM MedFormen m WHERE m.zubereitung = :zubereitung"),
    @NamedQuery(name = "MedFormen.findByAnwText", query = "SELECT m FROM MedFormen m WHERE m.anwText = :anwText"),
    @NamedQuery(name = "MedFormen.findByAnwEinheit", query = "SELECT m FROM MedFormen m WHERE m.anwEinheit = :anwEinheit"),
    @NamedQuery(name = "MedFormen.findByPackEinheit", query = "SELECT m FROM MedFormen m WHERE m.packEinheit = :packEinheit"),
    @NamedQuery(name = "MedFormen.findByMassID", query = "SELECT m FROM MedFormen m WHERE m.massID = :massID"),
    @NamedQuery(name = "MedFormen.findByStellplan", query = "SELECT m FROM MedFormen m WHERE m.stellplan = :stellplan"),
    @NamedQuery(name = "MedFormen.findByStatus", query = "SELECT m FROM MedFormen m WHERE m.status = :status"),
    @NamedQuery(name = "MedFormen.findByEquiv", query = "SELECT m FROM MedFormen m WHERE m.equiv = :equiv")})
public class MedFormen implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "FormID")
    private Long formID;
    @Basic(optional = false)
    @Column(name = "Zubereitung")
    private String zubereitung;
    @Basic(optional = false)
    @Column(name = "AnwText")
    private String anwText;
    @Basic(optional = false)
    @Column(name = "AnwEinheit")
    private short anwEinheit;
    @Basic(optional = false)
    @Column(name = "PackEinheit")
    private short packEinheit;
    @Basic(optional = false)
    @Column(name = "MassID")
    private long massID;
    @Basic(optional = false)
    @Column(name = "Stellplan")
    private short stellplan;
    @Basic(optional = false)
    @Column(name = "Status")
    private short status;
    @Basic(optional = false)
    @Column(name = "Equiv")
    private int equiv;

    public MedFormen() {
    }



    public Long getFormID() {
        return formID;
    }

    public void setFormID(Long formID) {
        this.formID = formID;
    }

    public String getZubereitung() {
        return zubereitung;
    }

    public void setZubereitung(String zubereitung) {
        this.zubereitung = zubereitung;
    }

    public String getAnwText() {
        return anwText;
    }

    public void setAnwText(String anwText) {
        this.anwText = anwText;
    }

    public short getAnwEinheit() {
        return anwEinheit;
    }

    public void setAnwEinheit(short anwEinheit) {
        this.anwEinheit = anwEinheit;
    }

    public short getPackEinheit() {
        return packEinheit;
    }

    public void setPackEinheit(short packEinheit) {
        this.packEinheit = packEinheit;
    }

    public long getMassID() {
        return massID;
    }

    public void setMassID(long massID) {
        this.massID = massID;
    }

    public short getStellplan() {
        return stellplan;
    }

    public void setStellplan(short stellplan) {
        this.stellplan = stellplan;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public int getEquiv() {
        return equiv;
    }

    public void setEquiv(int equiv) {
        this.equiv = equiv;
    }

    public boolean anwUndPackEinheitenGleich(){
        return anwEinheit == packEinheit;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (formID != null ? formID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MedFormen)) {
            return false;
        }
        MedFormen other = (MedFormen) object;
        if ((this.formID == null && other.formID != null) || (this.formID != null && !this.formID.equals(other.formID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.MedFormen[formID=" + formID + "]";
    }

}