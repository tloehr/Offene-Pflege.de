package de.offene_pflege.entity.values;

import de.offene_pflege.entity.DefaultEntity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "resvaluetypes")
public class Resvaluetypes extends DefaultEntity {
    private String text;
    private String label1;
    private String label2;
    private String label3;
    private String unit1;
    private String unit2;
    private String unit3;
    private BigDecimal default1;
    private BigDecimal default2;
    private BigDecimal default3;
    private short valType;
    private String format1;
    private String format2;
    private String format3;
    private boolean active;

    @Basic
    @Column(name = "Text", nullable = false, length = 100)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Basic
    @Column(name = "Label1", nullable = true, length = 100)
    public String getLabel1() {
        return label1;
    }

    public void setLabel1(String label1) {
        this.label1 = label1;
    }

    @Basic
    @Column(name = "Label2", nullable = true, length = 100)
    public String getLabel2() {
        return label2;
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }

    @Basic
    @Column(name = "Label3", nullable = true, length = 100)
    public String getLabel3() {
        return label3;
    }

    public void setLabel3(String label3) {
        this.label3 = label3;
    }

    @Basic
    @Column(name = "Unit1", nullable = true, length = 100)
    public String getUnit1() {
        return unit1;
    }

    public void setUnit1(String unit1) {
        this.unit1 = unit1;
    }

    @Basic
    @Column(name = "Unit2", nullable = true, length = 100)
    public String getUnit2() {
        return unit2;
    }

    public void setUnit2(String unit2) {
        this.unit2 = unit2;
    }

    @Basic
    @Column(name = "Unit3", nullable = true, length = 100)
    public String getUnit3() {
        return unit3;
    }

    public void setUnit3(String unit3) {
        this.unit3 = unit3;
    }

    @Basic
    @Column(name = "Default1", nullable = true, precision = 2)
    public BigDecimal getDefault1() {
        return default1;
    }

    public void setDefault1(BigDecimal default1) {
        this.default1 = default1;
    }

    @Basic
    @Column(name = "Default2", nullable = true, precision = 2)
    public BigDecimal getDefault2() {
        return default2;
    }

    public void setDefault2(BigDecimal default2) {
        this.default2 = default2;
    }

    @Basic
    @Column(name = "Default3", nullable = true, precision = 2)
    public BigDecimal getDefault3() {
        return default3;
    }

    public void setDefault3(BigDecimal default3) {
        this.default3 = default3;
    }

    @Basic
    @Column(name = "ValType", nullable = false)
    public short getValType() {
        return valType;
    }

    public void setValType(short valType) {
        this.valType = valType;
    }

    @Basic
    @Column(name = "format1", nullable = true, length = 100)
    public String getFormat1() {
        return format1;
    }

    public void setFormat1(String format1) {
        this.format1 = format1;
    }

    @Basic
    @Column(name = "format2", nullable = true, length = 100)
    public String getFormat2() {
        return format2;
    }

    public void setFormat2(String format2) {
        this.format2 = format2;
    }

    @Basic
    @Column(name = "format3", nullable = true, length = 100)
    public String getFormat3() {
        return format3;
    }

    public void setFormat3(String format3) {
        this.format3 = format3;
    }

    @Basic
    @Column(name = "active", nullable = false)
    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
