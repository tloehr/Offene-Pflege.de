package de.offene_pflege.entity.prescription;

import de.offene_pflege.entity.DefaultEntity;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.system.OPUsers;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medorder")
@Getter
@Setter
public class MedOrder extends DefaultEntity {

    @JoinColumn(name = "mosid", referencedColumnName = "id")
    @ManyToOne
    private MedOrders medOrders;

    @JoinColumn(name = "resid", referencedColumnName = "id")
    @ManyToOne
    private Resident resident;

    @JoinColumn(name = "dafid", referencedColumnName = "DafID")
    @ManyToOne
    private TradeForm tradeForm;

    @JoinColumn(name = "arztid", referencedColumnName = "ArztID")
    @ManyToOne
    private GP gp;

    @JoinColumn(name = "khid", referencedColumnName = "KHID")
    @ManyToOne
    private Hospital hospital;

    @Basic
    @Column(nullable = false)
    private LocalDateTime opened_on;


    @ManyToOne
    @JoinColumn(name = "opened_by", referencedColumnName = "UKennung")
    private OPUsers opened_by;

    @Basic
    @Column(nullable = false)
    private LocalDateTime closed_on;


    @ManyToOne
    @JoinColumn(name = "closed_by", referencedColumnName = "UKennung")
    private OPUsers closed_by;

}
