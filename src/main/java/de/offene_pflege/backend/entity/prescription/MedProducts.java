package de.offene_pflege.backend.entity.prescription;

import de.offene_pflege.backend.entity.done.ACME;
import de.offene_pflege.op.tools.SYSTools;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "medproducts")

/**
 *
 */
public class MedProducts implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medpid")
    private Long medPID;
    @Basic(optional = false)
    @Column(name = "text")
    private String text;
    @Basic(optional = false)
    @Column(name = "sideeffects")
    private String sideeffects;
    @Version
    @Column(name = "version")
    private Long version;

    public MedProducts() {
    }

    public MedProducts(ACME acme, String text) {
        this.acme = acme;
        this.text = text;
        this.tradeForms = new ArrayList<TradeForm>();
    }

    public MedProducts(String text) {
        this.acme = null;
        this.text = text;
        this.tradeForms = new ArrayList<TradeForm>();
    }

    public Long getMedPID() {
        return medPID;
    }

    public void setMedPID(Long medPID) {
        this.medPID = medPID;
    }


    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public ACME getACME() {
        return acme;
    }

    public void setACME(ACME acme) {
        this.acme = acme;
    }

    public String getSideEffects() {
        return sideeffects;
    }

    public void setSideEffects(String sideeffects) {
        this.sideeffects = sideeffects;
    }

    public Collection<TradeForm> getTradeforms() {
        return tradeForms;
    }

    public boolean hasSideEffects() {
        return !SYSTools.catchNull(sideeffects).isEmpty();
    }

    @JoinColumn(name = "acmeid", referencedColumnName = "MPHID")
    @ManyToOne(cascade = CascadeType.PERSIST)
    private ACME acme;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "medProduct")
    private Collection<TradeForm> tradeForms;


    @Override
    public String toString() {
        return "entity.rest.MedProducts[medPID=" + medPID + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedProducts that = (MedProducts) o;

        if (medPID != null ? !medPID.equals(that.medPID) : that.medPID != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (acme != null ? !acme.equals(that.acme) : that.acme != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = medPID != null ? medPID.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (acme != null ? acme.hashCode() : 0);
        return result;
    }
}
