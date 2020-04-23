package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.backend.entity.prescription.TradeForm;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "medproducts")
public class MedProducts extends DefaultEntity {
    private String text;
    private String sideeffects;
    private ACME acme;
    private Collection<TradeForm> tradeForms;

    @Basic(optional = false)
    @Column(name = "text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Basic(optional = false)
    @Column(name = "sideeffects")
    public String getSideeffects() {
        return sideeffects;
    }

    public void setSideeffects(String sideeffects) {
        this.sideeffects = sideeffects;
    }

    @JoinColumn(name = "acmeid", referencedColumnName = "MPHID")
    @ManyToOne(cascade = CascadeType.PERSIST)
    public ACME getAcme() {
        return acme;
    }

    public void setAcme(ACME acme) {
        this.acme = acme;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "medProduct")
    public Collection<TradeForm> getTradeForms() {
        return tradeForms;
    }

    public void setTradeForms(Collection<TradeForm> tradeForms) {
        this.tradeForms = tradeForms;
    }


    public MedProducts() {
    }


//    public boolean hasSideEffects() {
//        return !SYSTools.catchNull(sideeffects).isEmpty();
//    }


}
