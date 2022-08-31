package de.offene_pflege.entity.prescription;

import de.offene_pflege.entity.DefaultEntity;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.system.OPUsers;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medorder")
@Getter
@Setter
public class MedOrder extends DefaultEntity {

    @JoinColumn(name = "resid", referencedColumnName = "id")
    @ManyToOne
    private Resident resident;

    @JoinColumn(name = "dafid", referencedColumnName = "DafID")
    @ManyToOne
    private TradeForm tradeForm;

    @Lob
    @Column(name = "note")
    private String note;

    @JoinColumn(name = "arztid", referencedColumnName = "ArztID")
    @ManyToOne
    private GP gp;

    @JoinColumn(name = "khid", referencedColumnName = "KHID")
    @ManyToOne
    private Hospital hospital;

    @Basic
    @Column(nullable = false)
    private LocalDateTime created_on;

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "UKennung")
    private OPUsers created_by;

    @Basic
    @Column(nullable = false)
    private Boolean auto_created;

    @Basic
    @Column(nullable = false)
    private LocalDateTime closed_on;

    @ManyToOne
    @JoinColumn(name = "closed_by", referencedColumnName = "UKennung")
    private OPUsers closed_by;

    @ManyToOne
    @JoinColumn(name = "closing_stockid", referencedColumnName = "BestID")
    private MedStock closing_med_stock;


}
