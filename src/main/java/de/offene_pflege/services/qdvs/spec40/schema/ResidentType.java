//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v4.0.5 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
//


package de.offene_pflege.services.qdvs.spec40.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Qualitätssicherungsdaten können entweder aus einem kompletten
 * 				Dokumentationsbogen oder einen Minimaldatensatz (MDS) bestehen
 * 
 * <p>Java-Klasse für resident_type complex type.</p>
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
 * 
 * <pre>{@code
 * <complexType name="resident_type">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <choice>
 *           <element name="qs_data" type="{https://www.das-pflege.de}das_qs_data_type"/>
 *           <element name="qs_data_mds" type="{https://www.das-pflege.de}das_qs_data_mds_type"/>
 *         </choice>
 *         <element name="validation_status" type="{https://www.das-pflege.de}validation_status_type" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
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
    /**
     * Umschlagselement für Informationen zur Plausibilitätsprüfung
     * 
     */
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
     * Umschlagselement für Informationen zur Plausibilitätsprüfung
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
     * @see #getValidationStatus()
     */
    public void setValidationStatus(ValidationStatusType value) {
        this.validationStatus = value;
    }

}
