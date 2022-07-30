package de.offene_pflege.entity.values;

import de.offene_pflege.entity.DefaultEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "resvaluetypes")
@Getter
@Setter
public class Resvaluetypes extends DefaultEntity {
    @Basic
    @Column(name = "Text", nullable = false, length = 100)
    private String text;
    @Basic
    @Column(name = "Label1", nullable = true, length = 100)
    private String label1;
    @Basic
    @Column(name = "Label2", nullable = true, length = 100)
    private String label2;
    @Basic
    @Column(name = "Label3", nullable = true, length = 100)
    private String label3;
    @Basic
    @Column(name = "Unit1", nullable = true, length = 100)
    private String unit1;
    @Basic
    @Column(name = "Unit2", nullable = true, length = 100)
    private String unit2;
    @Basic
    @Column(name = "Unit3", nullable = true, length = 100)
    private String unit3;
    @Basic
    @Column(name = "Default1", nullable = true, precision = 2)
    private BigDecimal default1;
    @Basic
    @Column(name = "Default2", nullable = true, precision = 2)
    private BigDecimal default2;
    @Basic
    @Column(name = "Default3", nullable = true, precision = 2)
    private BigDecimal default3;
    @Basic
    @Column(name = "ValType", nullable = false)
    private short valType;
    @Basic
    @Column(name = "format1", nullable = true, length = 100)
    private String format1;
    @Basic
    @Column(name = "format2", nullable = true, length = 100)
    private String format2;
    @Basic
    @Column(name = "format3", nullable = true, length = 100)
    private String format3;
    @Basic
    @Column(name = "active", nullable = false)
    private boolean active;
    @Basic
    @Column(name = "min1", nullable = true, precision = 2)
    private BigDecimal min1;
    @Basic
    @Column(name = "min2", nullable = true, precision = 2)
    private BigDecimal min2;
    @Basic
    @Column(name = "min3", nullable = true, precision = 2)
    private BigDecimal min3;
    @Basic
    @Column(name = "max1", nullable = true, precision = 2)
    private BigDecimal max1;
    @Basic
    @Column(name = "max2", nullable = true, precision = 2)
    private BigDecimal max2;
    @Basic
    @Column(name = "max3", nullable = true, precision = 2)
    private BigDecimal max3;
}
