//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2019.10.30 um 10:38:41 AM CET 
//


package de.offene_pflege.services.qdvs.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für header_type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="header_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="document" type="{https://www.das-pflege.de}document_type"/>
 *         &lt;element name="care_provider" type="{https://www.das-pflege.de}care_provider_type"/>
 *         &lt;element name="software" type="{https://www.das-pflege.de}software_type"/>
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
@XmlType(name = "header_type", propOrder = {
    "document",
    "careProvider",
    "software",
    "validationStatus"
})
public class HeaderType {

    @XmlElement(required = true)
    protected DocumentType document;
    @XmlElement(name = "care_provider", required = true)
    protected CareProviderType careProvider;
    @XmlElement(required = true)
    protected SoftwareType software;
    @XmlElement(name = "validation_status")
    protected ValidationStatusType validationStatus;

    public HeaderType() {
    }


    /**
     * Ruft den Wert der document-Eigenschaft ab.
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
     */
    public void setDocument(DocumentType value) {
        this.document = value;
    }

    /**
     * Ruft den Wert der careProvider-Eigenschaft ab.
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
     */
    public void setCareProvider(CareProviderType value) {
        this.careProvider = value;
    }

    /**
     * Ruft den Wert der software-Eigenschaft ab.
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
     */
    public void setSoftware(SoftwareType value) {
        this.software = value;
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
