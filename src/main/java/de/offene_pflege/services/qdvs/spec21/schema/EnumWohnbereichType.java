//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.1-b171012.0423 generiert 
// Siehe <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// xc4nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.01.04 um 04:00:08 PM CET 
//


package de.offene_pflege.services.qdvs.spec21.schema;

import de.offene_pflege.entity.building.Station;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java-Klasse fxFCr enum_wohnbereich_type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="enum_wohnbereich_type"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="WB_1"/&gt;
 *     &lt;enumeration value="WB_2"/&gt;
 *     &lt;enumeration value="WB_3"/&gt;
 *     &lt;enumeration value="WB_4"/&gt;
 *     &lt;enumeration value="WB_5"/&gt;
 *     &lt;enumeration value="WB_6"/&gt;
 *     &lt;enumeration value="WB_7"/&gt;
 *     &lt;enumeration value="WB_8"/&gt;
 *     &lt;enumeration value="WB_9"/&gt;
 *     &lt;enumeration value="WB_10"/&gt;
 *     &lt;enumeration value="WB_11"/&gt;
 *     &lt;enumeration value="WB_12"/&gt;
 *     &lt;enumeration value="WB_13"/&gt;
 *     &lt;enumeration value="WB_14"/&gt;
 *     &lt;enumeration value="WB_15"/&gt;
 *     &lt;enumeration value="WB_16"/&gt;
 *     &lt;enumeration value="WB_17"/&gt;
 *     &lt;enumeration value="WB_18"/&gt;
 *     &lt;enumeration value="WB_19"/&gt;
 *     &lt;enumeration value="WB_20"/&gt;
 *     &lt;enumeration value="WB_21"/&gt;
 *     &lt;enumeration value="WB_22"/&gt;
 *     &lt;enumeration value="WB_23"/&gt;
 *     &lt;enumeration value="WB_24"/&gt;
 *     &lt;enumeration value="WB_25"/&gt;
 *     &lt;enumeration value="WB_26"/&gt;
 *     &lt;enumeration value="WB_27"/&gt;
 *     &lt;enumeration value="WB_28"/&gt;
 *     &lt;enumeration value="WB_29"/&gt;
 *     &lt;enumeration value="WB_30"/&gt;
 *     &lt;enumeration value="WB_31"/&gt;
 *     &lt;enumeration value="WB_32"/&gt;
 *     &lt;enumeration value="WB_33"/&gt;
 *     &lt;enumeration value="WB_34"/&gt;
 *     &lt;enumeration value="WB_35"/&gt;
 *     &lt;enumeration value="WB_36"/&gt;
 *     &lt;enumeration value="WB_37"/&gt;
 *     &lt;enumeration value="WB_38"/&gt;
 *     &lt;enumeration value="WB_39"/&gt;
 *     &lt;enumeration value="WB_40"/&gt;
 *     &lt;enumeration value="WB_41"/&gt;
 *     &lt;enumeration value="WB_42"/&gt;
 *     &lt;enumeration value="WB_43"/&gt;
 *     &lt;enumeration value="WB_44"/&gt;
 *     &lt;enumeration value="WB_45"/&gt;
 *     &lt;enumeration value="WB_46"/&gt;
 *     &lt;enumeration value="WB_47"/&gt;
 *     &lt;enumeration value="WB_48"/&gt;
 *     &lt;enumeration value="WB_49"/&gt;
 *     &lt;enumeration value="WB_50"/&gt;
 *     &lt;enumeration value="WB_51"/&gt;
 *     &lt;enumeration value="WB_52"/&gt;
 *     &lt;enumeration value="WB_53"/&gt;
 *     &lt;enumeration value="WB_54"/&gt;
 *     &lt;enumeration value="WB_55"/&gt;
 *     &lt;enumeration value="WB_56"/&gt;
 *     &lt;enumeration value="WB_57"/&gt;
 *     &lt;enumeration value="WB_58"/&gt;
 *     &lt;enumeration value="WB_59"/&gt;
 *     &lt;enumeration value="WB_60"/&gt;
 *     &lt;enumeration value="WB_61"/&gt;
 *     &lt;enumeration value="WB_62"/&gt;
 *     &lt;enumeration value="WB_63"/&gt;
 *     &lt;enumeration value="WB_64"/&gt;
 *     &lt;enumeration value="WB_65"/&gt;
 *     &lt;enumeration value="WB_66"/&gt;
 *     &lt;enumeration value="WB_67"/&gt;
 *     &lt;enumeration value="WB_68"/&gt;
 *     &lt;enumeration value="WB_69"/&gt;
 *     &lt;enumeration value="WB_70"/&gt;
 *     &lt;enumeration value="WB_71"/&gt;
 *     &lt;enumeration value="WB_72"/&gt;
 *     &lt;enumeration value="WB_73"/&gt;
 *     &lt;enumeration value="WB_74"/&gt;
 *     &lt;enumeration value="WB_75"/&gt;
 *     &lt;enumeration value="WB_76"/&gt;
 *     &lt;enumeration value="WB_77"/&gt;
 *     &lt;enumeration value="WB_78"/&gt;
 *     &lt;enumeration value="WB_79"/&gt;
 *     &lt;enumeration value="WB_80"/&gt;
 *     &lt;enumeration value="WB_81"/&gt;
 *     &lt;enumeration value="WB_82"/&gt;
 *     &lt;enumeration value="WB_83"/&gt;
 *     &lt;enumeration value="WB_84"/&gt;
 *     &lt;enumeration value="WB_85"/&gt;
 *     &lt;enumeration value="WB_86"/&gt;
 *     &lt;enumeration value="WB_87"/&gt;
 *     &lt;enumeration value="WB_88"/&gt;
 *     &lt;enumeration value="WB_89"/&gt;
 *     &lt;enumeration value="WB_90"/&gt;
 *     &lt;enumeration value="WB_91"/&gt;
 *     &lt;enumeration value="WB_92"/&gt;
 *     &lt;enumeration value="WB_93"/&gt;
 *     &lt;enumeration value="WB_94"/&gt;
 *     &lt;enumeration value="WB_95"/&gt;
 *     &lt;enumeration value="WB_96"/&gt;
 *     &lt;enumeration value="WB_97"/&gt;
 *     &lt;enumeration value="WB_98"/&gt;
 *     &lt;enumeration value="WB_99"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 */
@XmlType(name = "enum_wohnbereich_type")
@XmlEnum
public enum EnumWohnbereichType {


    /**
     * Wohnbereich 1
     */
    WB_1,

    /**
     * Wohnbereich 2
     */
    WB_2,

    /**
     * Wohnbereich 3
     */
    WB_3,

    /**
     * Wohnbereich 4
     */
    WB_4,

    /**
     * Wohnbereich 5
     */
    WB_5,

    /**
     * Wohnbereich 6
     */
    WB_6,

    /**
     * Wohnbereich 7
     */
    WB_7,

    /**
     * Wohnbereich 8
     */
    WB_8,

    /**
     * Wohnbereich 9
     */
    WB_9,

    /**
     * Wohnbereich 10
     */
    WB_10,

    /**
     * Wohnbereich 11
     */
    WB_11,

    /**
     * Wohnbereich 12
     */
    WB_12,

    /**
     * Wohnbereich 13
     */
    WB_13,

    /**
     * Wohnbereich 14
     */
    WB_14,

    /**
     * Wohnbereich 15
     */
    WB_15,

    /**
     * Wohnbereich 16
     */
    WB_16,

    /**
     * Wohnbereich 17
     */
    WB_17,

    /**
     * Wohnbereich 18
     */
    WB_18,

    /**
     * Wohnbereich 19
     */
    WB_19,

    /**
     * Wohnbereich 20
     */
    WB_20,

    /**
     * Wohnbereich 21
     */
    WB_21,

    /**
     * Wohnbereich 22
     */
    WB_22,

    /**
     * Wohnbereich 23
     */
    WB_23,

    /**
     * Wohnbereich 24
     */
    WB_24,

    /**
     * Wohnbereich 25
     */
    WB_25,

    /**
     * Wohnbereich 26
     */
    WB_26,

    /**
     * Wohnbereich 27
     */
    WB_27,

    /**
     * Wohnbereich 28
     */
    WB_28,

    /**
     * Wohnbereich 29
     */
    WB_29,

    /**
     * Wohnbereich 30
     */
    WB_30,

    /**
     * Wohnbereich 31
     */
    WB_31,

    /**
     * Wohnbereich 32
     */
    WB_32,

    /**
     * Wohnbereich 33
     */
    WB_33,

    /**
     * Wohnbereich 34
     */
    WB_34,

    /**
     * Wohnbereich 35
     */
    WB_35,

    /**
     * Wohnbereich 36
     */
    WB_36,

    /**
     * Wohnbereich 37
     */
    WB_37,

    /**
     * Wohnbereich 38
     */
    WB_38,

    /**
     * Wohnbereich 39
     */
    WB_39,

    /**
     * Wohnbereich 40
     */
    WB_40,

    /**
     * Wohnbereich 41
     */
    WB_41,

    /**
     * Wohnbereich 42
     */
    WB_42,

    /**
     * Wohnbereich 43
     */
    WB_43,

    /**
     * Wohnbereich 44
     */
    WB_44,

    /**
     * Wohnbereich 45
     */
    WB_45,

    /**
     * Wohnbereich 46
     */
    WB_46,

    /**
     * Wohnbereich 47
     */
    WB_47,

    /**
     * Wohnbereich 48
     */
    WB_48,

    /**
     * Wohnbereich 49
     */
    WB_49,

    /**
     * Wohnbereich 50
     */
    WB_50,

    /**
     * Wohnbereich 51
     */
    WB_51,

    /**
     * Wohnbereich 52
     */
    WB_52,

    /**
     * Wohnbereich 53
     */
    WB_53,

    /**
     * Wohnbereich 54
     */
    WB_54,

    /**
     * Wohnbereich 55
     */
    WB_55,

    /**
     * Wohnbereich 56
     */
    WB_56,

    /**
     * Wohnbereich 57
     */
    WB_57,

    /**
     * Wohnbereich 58
     */
    WB_58,

    /**
     * Wohnbereich 59
     */
    WB_59,

    /**
     * Wohnbereich 60
     */
    WB_60,

    /**
     * Wohnbereich 61
     */
    WB_61,

    /**
     * Wohnbereich 62
     */
    WB_62,

    /**
     * Wohnbereich 63
     */
    WB_63,

    /**
     * Wohnbereich 64
     */
    WB_64,

    /**
     * Wohnbereich 65
     */
    WB_65,

    /**
     * Wohnbereich 66
     */
    WB_66,

    /**
     * Wohnbereich 67
     */
    WB_67,

    /**
     * Wohnbereich 68
     */
    WB_68,

    /**
     * Wohnbereich 69
     */
    WB_69,

    /**
     * Wohnbereich 70
     */
    WB_70,

    /**
     * Wohnbereich 71
     */
    WB_71,

    /**
     * Wohnbereich 72
     */
    WB_72,

    /**
     * Wohnbereich 73
     */
    WB_73,

    /**
     * Wohnbereich 74
     */
    WB_74,

    /**
     * Wohnbereich 75
     */
    WB_75,

    /**
     * Wohnbereich 76
     */
    WB_76,

    /**
     * Wohnbereich 77
     */
    WB_77,

    /**
     * Wohnbereich 78
     */
    WB_78,

    /**
     * Wohnbereich 79
     */
    WB_79,

    /**
     * Wohnbereich 80
     */
    WB_80,

    /**
     * Wohnbereich 81
     */
    WB_81,

    /**
     * Wohnbereich 82
     */
    WB_82,

    /**
     * Wohnbereich 83
     */
    WB_83,

    /**
     * Wohnbereich 84
     */
    WB_84,

    /**
     * Wohnbereich 85
     */
    WB_85,

    /**
     * Wohnbereich 86
     */
    WB_86,

    /**
     * Wohnbereich 87
     */
    WB_87,

    /**
     * Wohnbereich 88
     */
    WB_88,

    /**
     * Wohnbereich 89
     */
    WB_89,

    /**
     * Wohnbereich 90
     */
    WB_90,

    /**
     * Wohnbereich 91
     */
    WB_91,

    /**
     * Wohnbereich 92
     */
    WB_92,

    /**
     * Wohnbereich 93
     */
    WB_93,

    /**
     * Wohnbereich 94
     */
    WB_94,

    /**
     * Wohnbereich 95
     */
    WB_95,

    /**
     * Wohnbereich 96
     */
    WB_96,

    /**
     * Wohnbereich 97
     */
    WB_97,

    /**
     * Wohnbereich 98
     */
    WB_98,

    /**
     * Wohnbereich 99
     */
    WB_99;

    public String value() {
        return name();
    }

    public static EnumWohnbereichType fromValue(String v) {
        return valueOf(v);
    }

    public static EnumWohnbereichType fromValue(Station station) {
        if (station == null) return WB_99;
        return valueOf("WB_" + station.getId());
    }

}
