//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.1-b171012.0423 generiert 
// Siehe <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// xc4nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.01.04 um 04:00:08 PM CET 
//


package de.offene_pflege.services.qdvs.spec21.schema;

import jakarta.xml.bind.annotation.*;

/**
 * Erhebungsbogen zur Erfassung von administrativen Angaben zur
 * 				Vollzähligkeitsanalyse
 * 
 * <p>Java-Klasse fxFCr das_qs_data_facility_type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="das_qs_data_facility_type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BELEGUNGSKAPAZITAET"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;attribute name="value" use="required"&gt;
 *                   &lt;simpleType&gt;
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *                       &lt;totalDigits value="3"/&gt;
 *                       &lt;minInclusive value="1"/&gt;
 *                     &lt;/restriction&gt;
 *                   &lt;/simpleType&gt;
 *                 &lt;/attribute&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="BELEGUNGAMSTICHTAG"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;attribute name="value" use="required"&gt;
 *                   &lt;simpleType&gt;
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *                       &lt;totalDigits value="3"/&gt;
 *                       &lt;minInclusive value="0"/&gt;
 *                     &lt;/restriction&gt;
 *                   &lt;/simpleType&gt;
 *                 &lt;/attribute&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "das_qs_data_facility_type", propOrder = {
    "belegungskapazitaet",
    "belegungamstichtag"
})
public class DasQsDataFacilityType {

    @XmlElement(name = "BELEGUNGSKAPAZITAET", required = true)
    protected DasQsDataFacilityType.BELEGUNGSKAPAZITAET belegungskapazitaet;
    @XmlElement(name = "BELEGUNGAMSTICHTAG", required = true)
    protected DasQsDataFacilityType.BELEGUNGAMSTICHTAG belegungamstichtag;

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
     * <p>Java-Klasse fxFCr anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;attribute name="value" use="required"&gt;
     *         &lt;simpleType&gt;
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
     *             &lt;totalDigits value="3"/&gt;
     *             &lt;minInclusive value="0"/&gt;
     *           &lt;/restriction&gt;
     *         &lt;/simpleType&gt;
     *       &lt;/attribute&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class BELEGUNGAMSTICHTAG {

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
     * <p>Java-Klasse fxFCr anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;attribute name="value" use="required"&gt;
     *         &lt;simpleType&gt;
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
     *             &lt;totalDigits value="3"/&gt;
     *             &lt;minInclusive value="1"/&gt;
     *           &lt;/restriction&gt;
     *         &lt;/simpleType&gt;
     *       &lt;/attribute&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class BELEGUNGSKAPAZITAET {

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

}
