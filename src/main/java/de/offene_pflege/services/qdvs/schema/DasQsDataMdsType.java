//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2019.10.30 um 10:38:41 AM CET 
//


package de.offene_pflege.services.qdvs.schema;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Erhebungsbogen zur Erfassung von Versorgungsergebnissen der
 * 				stationären Langzeitpflege sofern ein Ausschlussgrund vorliegt = Minimaldatensatz (MDS)
 * 
 * <p>Java-Klasse für das_qs_data_mds_type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="das_qs_data_mds_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IDBEWOHNER">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;pattern value="[0-9]{6}"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="WOHNBEREICH">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;maxLength value="255"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ERHEBUNGSDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="EINZUGSDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GEBURTSMONAT">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_monat_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GEBURTSJAHR">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}gYear">
 *                       &lt;minInclusive value="1900"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GESCHLECHT">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_geschlecht_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="AUSSCHLUSSGRUND">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_ausschlussgrund_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "das_qs_data_mds_type", propOrder = {
    "idbewohner",
    "wohnbereich",
    "erhebungsdatum",
    "einzugsdatum",
    "geburtsmonat",
    "geburtsjahr",
    "geschlecht",
    "ausschlussgrund"
})
public class DasQsDataMdsType {

    @XmlElement(name = "IDBEWOHNER", required = true)
    protected IDBEWOHNER idbewohner;
    @XmlElement(name = "WOHNBEREICH", required = true)
    protected WOHNBEREICH wohnbereich;
    @XmlElement(name = "ERHEBUNGSDATUM", required = true)
    protected ERHEBUNGSDATUM erhebungsdatum;
    @XmlElement(name = "EINZUGSDATUM", required = true)
    protected EINZUGSDATUM einzugsdatum;
    @XmlElement(name = "GEBURTSMONAT", required = true)
    protected GEBURTSMONAT geburtsmonat;
    @XmlElement(name = "GEBURTSJAHR", required = true)
    protected GEBURTSJAHR geburtsjahr;
    @XmlElement(name = "GESCHLECHT", required = true)
    protected GESCHLECHT geschlecht;
    @XmlElement(name = "AUSSCHLUSSGRUND", required = true)
    protected AUSSCHLUSSGRUND ausschlussgrund;

    /**
     * Ruft den Wert der idbewohner-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IDBEWOHNER }
     *     
     */
    public IDBEWOHNER getIDBEWOHNER() {
        return idbewohner;
    }

    /**
     * Legt den Wert der idbewohner-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IDBEWOHNER }
     *     
     */
    public void setIDBEWOHNER(IDBEWOHNER value) {
        this.idbewohner = value;
    }

    /**
     * Ruft den Wert der wohnbereich-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link WOHNBEREICH }
     *     
     */
    public WOHNBEREICH getWOHNBEREICH() {
        return wohnbereich;
    }

    /**
     * Legt den Wert der wohnbereich-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link WOHNBEREICH }
     *     
     */
    public void setWOHNBEREICH(WOHNBEREICH value) {
        this.wohnbereich = value;
    }

    /**
     * Ruft den Wert der erhebungsdatum-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ERHEBUNGSDATUM }
     *     
     */
    public ERHEBUNGSDATUM getERHEBUNGSDATUM() {
        return erhebungsdatum;
    }

    /**
     * Legt den Wert der erhebungsdatum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ERHEBUNGSDATUM }
     *     
     */
    public void setERHEBUNGSDATUM(ERHEBUNGSDATUM value) {
        this.erhebungsdatum = value;
    }

    /**
     * Ruft den Wert der einzugsdatum-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EINZUGSDATUM }
     *     
     */
    public EINZUGSDATUM getEINZUGSDATUM() {
        return einzugsdatum;
    }

    /**
     * Legt den Wert der einzugsdatum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EINZUGSDATUM }
     *     
     */
    public void setEINZUGSDATUM(EINZUGSDATUM value) {
        this.einzugsdatum = value;
    }

    /**
     * Ruft den Wert der geburtsmonat-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GEBURTSMONAT }
     *     
     */
    public GEBURTSMONAT getGEBURTSMONAT() {
        return geburtsmonat;
    }

    /**
     * Legt den Wert der geburtsmonat-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GEBURTSMONAT }
     *     
     */
    public void setGEBURTSMONAT(GEBURTSMONAT value) {
        this.geburtsmonat = value;
    }

    /**
     * Ruft den Wert der geburtsjahr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GEBURTSJAHR }
     *     
     */
    public GEBURTSJAHR getGEBURTSJAHR() {
        return geburtsjahr;
    }

    /**
     * Legt den Wert der geburtsjahr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GEBURTSJAHR }
     *     
     */
    public void setGEBURTSJAHR(GEBURTSJAHR value) {
        this.geburtsjahr = value;
    }

    /**
     * Ruft den Wert der geschlecht-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GESCHLECHT }
     *     
     */
    public GESCHLECHT getGESCHLECHT() {
        return geschlecht;
    }

    /**
     * Legt den Wert der geschlecht-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GESCHLECHT }
     *     
     */
    public void setGESCHLECHT(GESCHLECHT value) {
        this.geschlecht = value;
    }

    /**
     * Ruft den Wert der ausschlussgrund-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AUSSCHLUSSGRUND }
     *     
     */
    public AUSSCHLUSSGRUND getAUSSCHLUSSGRUND() {
        return ausschlussgrund;
    }

    /**
     * Legt den Wert der ausschlussgrund-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AUSSCHLUSSGRUND }
     *     
     */
    public void setAUSSCHLUSSGRUND(AUSSCHLUSSGRUND value) {
        this.ausschlussgrund = value;
    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_ausschlussgrund_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class AUSSCHLUSSGRUND {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         */
        public void setValue(int value) {
            this.value = value;
        }

    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class EINZUGSDATUM {

        @XmlAttribute(name = "value", required = true)
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setValue(XMLGregorianCalendar value) {
            this.value = value;
        }

    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ERHEBUNGSDATUM {

        @XmlAttribute(name = "value", required = true)
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setValue(XMLGregorianCalendar value) {
            this.value = value;
        }

    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="value" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}gYear">
     *             &lt;minInclusive value="1900"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GEBURTSJAHR {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setValue(int value) {
            this.value = value;
        }

    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_monat_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GEBURTSMONAT {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         */
        public void setValue(int value) {
            this.value = value;
        }

    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_geschlecht_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GESCHLECHT {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         */
        public void setValue(int value) {
            this.value = value;
        }

    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="value" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;pattern value="[0-9]{6}"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class IDBEWOHNER {

        @XmlAttribute(name = "value", required = true)
        protected String value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="value">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;maxLength value="255"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class WOHNBEREICH {

        @XmlAttribute(name = "value")
        protected String value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

    }

}
