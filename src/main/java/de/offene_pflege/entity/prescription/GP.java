package de.offene_pflege.entity.prescription;

import de.offene_pflege.entity.DefaultEntity;
import lombok.*;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.OptimisticLockingType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "gp")
@ToString
@NoArgsConstructor
@Getter
@Setter
public class GP extends DefaultEntity implements Serializable, HasName {
    @Basic(optional = false)
    @Column(name = "Anrede")
    private String anrede;
    @Basic(optional = false)
    @Column(name = "Titel")
    private String titel;
    @Basic(optional = false)
    @Column(name = "Name")
    private String name;
    @Basic(optional = false)
    @Column(name = "Vorname")
    private String vorname;
    @Basic(optional = false)
    @Column(name = "Strasse")
    private String strasse;
    @Basic(optional = false)
    @Column(name = "PLZ")
    private String plz;
    @Basic(optional = false)
    @Column(name = "Ort")
    private String ort;
    @Basic(optional = false)
    @Column(name = "Tel")
    private String tel;
    @Basic(optional = false)
    @Column(name = "Fax")
    private String fax;
    @Column(name = "Mobil")
    private String mobil;
    @Column(name = "EMail")
    private String eMail;
    @Column(name = "Status")
    private Integer status;
    @Basic(optional = false)
    @Column(name = "neurologist")
    private boolean neurologist;
    @Basic(optional = false)
    @Column(name = "skin")
    private boolean dermatology;
    @Basic(optional = false)
    @Column(name = "medorder_period")
    private Integer medorder_period;



}
