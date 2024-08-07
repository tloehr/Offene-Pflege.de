//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2023.06.07 um 04:02:29 PM CEST 
//


package de.offene_pflege.services.qdvs.spec30.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Qualitätssicherungsdaten können entweder aus einem kompletten
 * 				Dokumentationsbogen oder einen Minimaldatensatz (MDS) bestehen
 * 
 * <p>Java-Klasse für resident_type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="resident_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="qs_data" type="{https://www.das-pflege.de}das_qs_data_type"/>
 *           &lt;element name="qs_data_mds" type="{https://www.das-pflege.de}das_qs_data_mds_type"/>
 *         &lt;/choice>
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
@XmlType(name = "resident_type", propOrder = {
    "qsData",
    "qsDataMds",
    "validationStatus"
})
public class ResidentType {

    @XmlElement(name = "qs_data")
    protected DasQsDataType qsData;
    @XmlElement(name = "qs_data_mds")
    protected DasQsDataMdsType qsDataMds;
    @XmlElement(name = "validation_status")
    protected ValidationStatusType validationStatus;

    /**
     * Ruft den Wert der qsData-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DasQsDataType }
     *     
     */
    public DasQsDataType getQsData() {
        return qsData;
    }

    /**
     * Legt den Wert der qsData-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DasQsDataType }
     *     
     */
    public void setQsData(DasQsDataType value) {
        this.qsData = value;
    }

    /**
     * Ruft den Wert der qsDataMds-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DasQsDataMdsType }
     *     
     */
    public DasQsDataMdsType getQsDataMds() {
        return qsDataMds;
    }

    /**
     * Legt den Wert der qsDataMds-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DasQsDataMdsType }
     *     
     */
    public void setQsDataMds(DasQsDataMdsType value) {
        this.qsDataMds = value;
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

}
