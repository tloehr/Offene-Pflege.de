//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2019.10.30 um 10:38:41 AM CET 
//


package de.offene_pflege.services.qdvs.spec14.schema;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Root-Element
 *
 * <p>Java-Klasse für root_type complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="root_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="header" type="{https://www.das-pflege.de}header_type"/>
 *         &lt;element name="body" type="{https://www.das-pflege.de}body_type"/>
 *         &lt;element name="delivery_status" type="{https://www.das-pflege.de}delivery_status_type" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "root_type", propOrder = {
        "header",
        "body",
        "deliveryStatus"
})
public class RootType {
    @XmlElement(required = true)
    protected HeaderType header;
    @XmlElement(required = true)
    protected BodyType body;
    @XmlElement(name = "delivery_status")
    protected DeliveryStatusType deliveryStatus;

    public RootType() {
    }

    /**
     * Ruft den Wert der header-Eigenschaft ab.
     *
     * @return possible object is {@link HeaderType }
     */
    public HeaderType getHeader() {
        return header;
    }

    /**
     * Legt den Wert der header-Eigenschaft fest.
     *
     * @param value allowed object is {@link HeaderType }
     */
    public void setHeader(HeaderType value) {
        this.header = value;
    }

    /**
     * Ruft den Wert der body-Eigenschaft ab.
     *
     * @return possible object is {@link BodyType }
     */
    public BodyType getBody() {
        return body;
    }

    /**
     * Legt den Wert der body-Eigenschaft fest.
     *
     * @param value allowed object is {@link BodyType }
     */
    public void setBody(BodyType value) {
        this.body = value;
    }

    /**
     * Ruft den Wert der deliveryStatus-Eigenschaft ab.
     *
     * @return possible object is {@link DeliveryStatusType }
     */
    public DeliveryStatusType getDeliveryStatus() {
        return deliveryStatus;
    }

    /**
     * Legt den Wert der deliveryStatus-Eigenschaft fest.
     *
     * @param value allowed object is {@link DeliveryStatusType }
     */
    public void setDeliveryStatus(DeliveryStatusType value) {
        this.deliveryStatus = value;
    }

}
