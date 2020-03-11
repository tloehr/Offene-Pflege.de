//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2019.10.30 um 10:38:41 AM CET 
//


package de.offene_pflege.services.qdvs.schema;

import javax.xml.bind.annotation.*;


/**
 * Erhebungsbogen zur Erfassung von administrativen Angaben zur
 * 				Vollzähligkeitsanalyse
 * 
 * <p>Java-Klasse für das_qs_data_facility_type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="das_qs_data_facility_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BELEGUNGSKAPAZITAET">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                       &lt;totalDigits value="3"/>
 *                       &lt;minInclusive value="1"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="BELEGUNGAMSTICHTAG">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                       &lt;totalDigits value="3"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="validation_status" type="{https://www.das-pflege.de}validation_status_type" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "das_qs_data_facility_type", propOrder = {
    "belegungskapazitaet",
    "belegungamstichtag",
    "validationStatus"
})
public class DasQsDataFacilityType {

    @XmlElement(name = "BELEGUNGSKAPAZITAET", required = true)
    protected BELEGUNGSKAPAZITAET belegungskapazitaet;
    @XmlElement(name = "BELEGUNGAMSTICHTAG", required = true)
    protected BELEGUNGAMSTICHTAG belegungamstichtag;
    @XmlElement(name = "validation_status")
    protected ValidationStatusType validationStatus;

    public DasQsDataFacilityType(int belegungskapazitaet, int belegungamstichtag) {
        this.belegungskapazitaet = new BELEGUNGSKAPAZITAET(belegungamstichtag);
        this.belegungamstichtag = new BELEGUNGAMSTICHTAG(belegungamstichtag);
    }

    public DasQsDataFacilityType() {
    }

    /**
     * Ruft den Wert der belegungskapazitaet-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BELEGUNGSKAPAZITAET }
     *     
     */
    public BELEGUNGSKAPAZITAET getBELEGUNGSKAPAZITAET() {
        return belegungskapazitaet;
    }

    /**
     * Legt den Wert der belegungskapazitaet-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BELEGUNGSKAPAZITAET }
     *     
     */
    public void setBELEGUNGSKAPAZITAET(BELEGUNGSKAPAZITAET value) {
        this.belegungskapazitaet = value;
    }

    /**
     * Ruft den Wert der belegungamstichtag-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BELEGUNGAMSTICHTAG }
     *     
     */
    public BELEGUNGAMSTICHTAG getBELEGUNGAMSTICHTAG() {
        return belegungamstichtag;
    }

    /**
     * Legt den Wert der belegungamstichtag-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BELEGUNGAMSTICHTAG }
     *     
     */
    public void setBELEGUNGAMSTICHTAG(BELEGUNGAMSTICHTAG value) {
        this.belegungamstichtag = value;
    }

    /**
     * Ruft den Wert der validationStatus-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ValidationStatusType }
     *     
     */
    public ValidationStatusType getValidationStatus() {
        return validationStatus;
    }

    /**
     * Legt den Wert der validationStatus-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ValidationStatusType }
     *     
     */
    public void setValidationStatus(ValidationStatusType value) {
        this.validationStatus = value;
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
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *             &lt;totalDigits value="3"/>
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
    public static class BELEGUNGAMSTICHTAG {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        public BELEGUNGAMSTICHTAG() {
        }

        public BELEGUNGAMSTICHTAG(int value) {
            this.value = value;
        }

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
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *             &lt;totalDigits value="3"/>
     *             &lt;minInclusive value="1"/>
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
    public static class BELEGUNGSKAPAZITAET {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        public BELEGUNGSKAPAZITAET() {
        }

        public BELEGUNGSKAPAZITAET(int value) {
            this.value = value;
        }

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

}
