//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.1-b171012.0423 generiert 
// Siehe <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// xc4nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.01.04 um 04:00:08 PM CET 
//


package de.offene_pflege.services.qdvs.spec21.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Datenlieferantspezifische Informationen
 * 
 * <p>Java-Klasse fxFCr care_provider_type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="care_provider_type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="registration" type="{https://www.das-pflege.de}registration_type"/&gt;
 *         &lt;element name="target_date" type="{https://www.das-pflege.de}date_type"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "care_provider_type", propOrder = {
    "registration",
    "targetDate"
})
public class CareProviderType {

    @XmlElement(required = true)
    protected RegistrationType registration;
    @XmlElement(name = "target_date", required = true)
    protected DateType targetDate;

    /**
     * Ruft den Wert der registration-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RegistrationType }
     *     
     */
    public RegistrationType getRegistration() {
        return registration;
    }

    /**
     * Legt den Wert der registration-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RegistrationType }
     *     
     */
    public void setRegistration(RegistrationType value) {
        this.registration = value;
    }

    /**
     * Ruft den Wert der targetDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateType }
     *     
     */
    public DateType getTargetDate() {
        return targetDate;
    }

    /**
     * Legt den Wert der targetDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateType }
     *     
     */
    public void setTargetDate(DateType value) {
        this.targetDate = value;
    }

}
