//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.1-b171012.0423 generiert 
// Siehe <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// xc4nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.01.04 um 04:00:08 PM CET 
//


package de.offene_pflege.services.qdvs.spec20.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse fxFCr header_type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="header_type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="document" type="{https://www.das-pflege.de}document_type"/&gt;
 *         &lt;element name="care_provider" type="{https://www.das-pflege.de}care_provider_type"/&gt;
 *         &lt;element name="software" type="{https://www.das-pflege.de}software_type"/&gt;
 *         &lt;element name="validation_status" type="{https://www.das-pflege.de}validation_status_type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
