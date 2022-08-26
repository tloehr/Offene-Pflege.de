package de.offene_pflege.entity.prescription;


import de.offene_pflege.entity.DefaultEntity;
import de.offene_pflege.entity.system.OPUsers;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "medorders")
@Getter
@Setter
public class MedOrders extends DefaultEntity {

    @Basic
    @Column(nullable = false)
    private LocalDateTime created_on;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by", referencedColumnName = "UKennung")
    private OPUsers created_by;

    @Basic
    @Column(nullable = false)
    private LocalDate order_week;

    @Basic
    @Column(nullable = false)
    private LocalDateTime closed_on;

    @ManyToOne
    @JoinColumn(name = "closed_by", referencedColumnName = "UKennung")
    private OPUsers closed_by;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "medOrders")
    @OrderBy("opened_on")
    private List<MedOrder> orderList;

}

