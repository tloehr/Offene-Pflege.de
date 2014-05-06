package entity.values;

import op.tools.GUITools;
import op.tools.SYSTools;

import javax.persistence.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 21.09.12
 * Time: 15:28
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "resvaluetypes")
public class ResValueTypes {
    @Column(name = "ID", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    private long id;
    @Column(name = "Text", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    private String text;
    @Column(name = "ValType", nullable = false, insertable = true, updatable = true, length = 5, precision = 0)
    @Basic
    private short valType;
    @Column(name = "Label1", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    private String label1;
    @Column(name = "Label3", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    private String label3;
    @Column(name = "Label2", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    private String label2;
    @Column(name = "Unit1", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    private String unit1;
    @Column(name = "Unit2", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    private String unit2;
    @Column(name = "Unit3", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    private String unit3;
    @Column(name = "Default1", nullable = true, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    private BigDecimal default1;
    @Column(name = "Default2", nullable = true, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    private BigDecimal default2;
    @Column(name = "Default3", nullable = true, insertable = true, updatable = true, length = 9, precision = 2)
    @Basic
    private BigDecimal default3;
    @Column(name = "format1", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    private String format1;
    @Column(name = "format2", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    private String format2;
    @Column(name = "format3", nullable = true, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    private String format3;

    public long getID() {
        return id;
    }

    public void setID(long id) {
        this.id = id;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = SYSTools.tidy(text);
    }


    public short getValType() {
        return valType;
    }

    public void setValType(short valType) {
        this.valType = valType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResValueTypes that = (ResValueTypes) o;

        if (id != that.id) return false;
        if (valType != that.valType) return false;
        if (default1 != null ? !default1.equals(that.default1) : that.default1 != null) return false;
        if (default2 != null ? !default2.equals(that.default2) : that.default2 != null) return false;
        if (default3 != null ? !default3.equals(that.default3) : that.default3 != null) return false;
        if (label1 != null ? !label1.equals(that.label1) : that.label1 != null) return false;
        if (label2 != null ? !label2.equals(that.label2) : that.label2 != null) return false;
        if (label3 != null ? !label3.equals(that.label3) : that.label3 != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (unit1 != null ? !unit1.equals(that.unit1) : that.unit1 != null) return false;
        if (unit2 != null ? !unit2.equals(that.unit2) : that.unit2 != null) return false;
        if (unit3 != null ? !unit3.equals(that.unit3) : that.unit3 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (int) valType;
        result = 31 * result + (label1 != null ? label1.hashCode() : 0);
        result = 31 * result + (label2 != null ? label2.hashCode() : 0);
        result = 31 * result + (label3 != null ? label3.hashCode() : 0);
        result = 31 * result + (unit1 != null ? unit1.hashCode() : 0);
        result = 31 * result + (unit2 != null ? unit2.hashCode() : 0);
        result = 31 * result + (unit3 != null ? unit3.hashCode() : 0);
        result = 31 * result + (default1 != null ? default1.hashCode() : 0);
        result = 31 * result + (default2 != null ? default2.hashCode() : 0);
        result = 31 * result + (default3 != null ? default3.hashCode() : 0);
        return result;
    }


    public String getLabel1() {
        return label1;
    }


    public void setLabel1(String label1) {
        this.label1 = label1;
    }


    public String getLabel2() {
        return label2;
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }


    public String getLabel3() {
        return label3;
    }

    public void setLabel3(String label3) {
        this.label3 = label3;
    }


    public String getUnit1() {
        return unit1;
    }

    public void setUnit1(String unit1) {
        this.unit1 = unit1;
    }


    public String getUnit2() {
        return unit2;
    }

    public void setUnit2(String unit2) {
        this.unit2 = unit2;
    }


    public String getUnit3() {
        return unit3;
    }

    public void setUnit3(String unit3) {
        this.unit3 = unit3;
    }


    public BigDecimal getDefault1() {
        return default1;
    }

    public void setDefault1(BigDecimal default1) {
        this.default1 = default1;
    }


    public BigDecimal getDefault2() {
        return default2;
    }

    public void setDefault2(BigDecimal default2) {
        this.default2 = default2;
    }


    public BigDecimal getDefault3() {
        return default3;
    }

    public void setDefault3(BigDecimal default3) {
        this.default3 = default3;
    }


    public String getFormat1() {
        return format1;
    }

    public void setFormat1(String format1) {
        this.format1 = format1;
    }


    public String getFormat2() {
        return format2;
    }

    public void setFormat2(String format2) {
        this.format2 = format2;
    }


    public String getFormat3() {
        return format3;
    }

    public void setFormat3(String format3) {
        this.format3 = format3;
    }

    public Color getColor() {
        String color = "black";
        switch (valType) {
            case ResValueTypesTools.STOOL: {
                color = "006600";
                break;
            }
            case ResValueTypesTools.LIQUIDBALANCE: {
                color = "00cccc";
                break;
            }
            case ResValueTypesTools.GLUCOSE: {
                color = "3399ff";
                break;
            }
            case ResValueTypesTools.HEIGHT: {
                color = "ff00ff";
                break;
            }
            case ResValueTypesTools.VOMIT: {
                color = "ffcc00";
                break;
            }
            case ResValueTypesTools.QUICK: {
                color = "9900ff";
                break;
            }
            case ResValueTypesTools.RR: {
                color = "ff99cc";
                break;
            }
            case ResValueTypesTools.PULSE: {
                color = "ff0000";
                break;
            }
            case ResValueTypesTools.TEMP: {
                color = "00ff66";
                break;
            }
            case ResValueTypesTools.BREATHING: {
                color = "cccc00";
                break;
            }
            case ResValueTypesTools.WEIGHT: {
                color = "cc99ff";
                break;
            }
            case ResValueTypesTools.O2SATURATION: {
                color = "GRAY";
                break;
            }
            case ResValueTypesTools.ASPIRATION: {
                color = "0000cc";
                break;
            }
            default: {
                color = "black";
                break;
            }
        }

        return GUITools.getColor(color);
    }

}
