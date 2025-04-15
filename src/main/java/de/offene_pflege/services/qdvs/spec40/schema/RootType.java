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
 * Root-Element
 * 
 * <p>Java-Klasse für root_type complex type.</p>
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
 * 
 * <pre>{@code
 * <complexType name="root_type">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="header" type="{https://www.das-pflege.de}header_type"/>
 *         <element name="body" type="{https://www.das-pflege.de}body_type"/>
 *         <element name="delivery_status" type="{https://www.das-pflege.de}delivery_status_type" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "root_type", propOrder = {
    "header",
    "body",
    "deliveryStatus"
})
public class RootType {

    /**
     * Umschlagselement für administrative Informationen zur Datenlieferung
     * 
     */
    @XmlElement(required = true)
    protected HeaderType header;
    /**
     * Umschlagselement für einrichtungs- und bewohnerbezogene Qualitätssicherungsdaten und zur Kommentierung
     * 
     */
    @XmlElement(required = true)
    protected BodyType body;
    /**
     * Umschlagselement für Informationen zur technischen Plausibilitätsprüfung und zum globalen Dokumentenstatus
     * 
     */
    @XmlElement(name = "delivery_status")
    protected DeliveryStatusType deliveryStatus;

    /**
     * Umschlagselement für administrative Informationen zur Datenlieferung
     * 
     * @return
     *     possible object is
     *     {@link HeaderType }
     *     
     */
    public HeaderType getHeader() {
        return header;
    }

    /**
     * Legt den Wert der header-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link HeaderType }
     *     
     * @see #getHeader()
     */
    public void setHeader(HeaderType value) {
        this.header = value;
    }

    /**
     * Umschlagselement für einrichtungs- und bewohnerbezogene Qualitätssicherungsdaten und zur Kommentierung
     * 
     * @return
     *     possible object is
     *     {@link BodyType }
     *     
     */
    public BodyType getBody() {
        return body;
    }

    /**
     * Legt den Wert der body-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BodyType }
     *     
     * @see #getBody()
     */
    public void setBody(BodyType value) {
        this.body = value;
    }

    /**
     * Umschlagselement für Informationen zur technischen Plausibilitätsprüfung und zum globalen Dokumentenstatus
     * 
     * @return
     *     possible object is
     *     {@link DeliveryStatusType }
     *     
     */
    public DeliveryStatusType getDeliveryStatus() {
        return deliveryStatus;
    }

    /**
     * Legt den Wert der deliveryStatus-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DeliveryStatusType }
     *     
     * @see #getDeliveryStatus()
     */
    public void setDeliveryStatus(DeliveryStatusType value) {
        this.deliveryStatus = value;
    }

}
