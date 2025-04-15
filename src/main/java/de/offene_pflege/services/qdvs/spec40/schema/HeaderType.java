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
 * <p>Java-Klasse für header_type complex type.</p>
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
 * 
 * <pre>{@code
 * <complexType name="header_type">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="document" type="{https://www.das-pflege.de}document_type"/>
 *         <element name="care_provider" type="{https://www.das-pflege.de}care_provider_type"/>
 *         <element name="software" type="{https://www.das-pflege.de}software_type"/>
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
@XmlType(name = "header_type", propOrder = {
    "document",
    "careProvider",
    "software",
    "validationStatus"
})
public class HeaderType {

    /**
     * Umschlagselement für dokumentspezifische Informationen
     * 
     */
    @XmlElement(required = true)
    protected DocumentType document;
    /**
     * Umschlagselement für Informationen zum Datenlieferanten
     * 
     */
    @XmlElement(name = "care_provider", required = true)
    protected CareProviderType careProvider;
    /**
     * Umschlagselement für Informationen zu der verwendeten Dokumentationssoftware
     * 
     */
    @XmlElement(required = true)
    protected SoftwareType software;
    /**
     * Umschlagselement für Informationen zur Plausibilitätsprüfung
     * 
     */
    @XmlElement(name = "validation_status")
    protected ValidationStatusType validationStatus;

    /**
     * Umschlagselement für dokumentspezifische Informationen
     * 
     * @return
     *     possible object is
     *     {@link DocumentType }
     *     
     */
    public DocumentType getDocument() {
        return document;
    }

    /**
     * Legt den Wert der document-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentType }
     *     
     * @see #getDocument()
     */
    public void setDocument(DocumentType value) {
        this.document = value;
    }

    /**
     * Umschlagselement für Informationen zum Datenlieferanten
     * 
     * @return
     *     possible object is
     *     {@link CareProviderType }
     *     
     */
    public CareProviderType getCareProvider() {
        return careProvider;
    }

    /**
     * Legt den Wert der careProvider-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CareProviderType }
     *     
     * @see #getCareProvider()
     */
    public void setCareProvider(CareProviderType value) {
        this.careProvider = value;
    }

    /**
     * Umschlagselement für Informationen zu der verwendeten Dokumentationssoftware
     * 
     * @return
     *     possible object is
     *     {@link SoftwareType }
     *     
     */
    public SoftwareType getSoftware() {
        return software;
    }

    /**
     * Legt den Wert der software-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SoftwareType }
     *     
     * @see #getSoftware()
     */
    public void setSoftware(SoftwareType value) {
        this.software = value;
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
