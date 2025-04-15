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
 * Datenlieferantspezifische Informationen
 * 
 * <p>Java-Klasse für care_provider_type complex type.</p>
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
 * 
 * <pre>{@code
 * <complexType name="care_provider_type">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="registration" type="{https://www.das-pflege.de}registration_type"/>
 *         <element name="target_date" type="{https://www.das-pflege.de}date_type"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
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
