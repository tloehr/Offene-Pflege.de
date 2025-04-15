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
 * <p>Java-Klasse für facility_data_type complex type.</p>
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
 * 
 * <pre>{@code
 * <complexType name="facility_data_type">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="qs_data" type="{https://www.das-pflege.de}das_qs_data_facility_type"/>
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
@XmlType(name = "facility_data_type", propOrder = {
    "qsData",
    "validationStatus"
})
public class FacilityDataType {

    @XmlElement(name = "qs_data", required = true)
    protected DasQsDataFacilityType qsData;
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
     *     {@link DasQsDataFacilityType }
     *     
     */
    public DasQsDataFacilityType getQsData() {
        return qsData;
    }

    /**
     * Legt den Wert der qsData-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DasQsDataFacilityType }
     *     
     */
    public void setQsData(DasQsDataFacilityType value) {
        this.qsData = value;
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
