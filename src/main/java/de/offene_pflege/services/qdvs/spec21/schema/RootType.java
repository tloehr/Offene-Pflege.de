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
 * Root-Element
 * 
 * <p>Java-Klasse fxFCr root_type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="root_type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="header" type="{https://www.das-pflege.de}header_type"/&gt;
 *         &lt;element name="body" type="{https://www.das-pflege.de}body_type"/&gt;
 *         &lt;element name="delivery_status" type="{https://www.das-pflege.de}delivery_status_type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
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

    @XmlElement(required = true)
    protected HeaderType header;
    @XmlElement(required = true)
    protected BodyType body;
    @XmlElement(name = "delivery_status")
    protected DeliveryStatusType deliveryStatus;

    /**
     * Ruft den Wert der header-Eigenschaft ab.
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
     */
    public void setHeader(HeaderType value) {
        this.header = value;
    }

    /**
     * Ruft den Wert der body-Eigenschaft ab.
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
     */
    public void setBody(BodyType value) {
        this.body = value;
    }

    /**
     * Ruft den Wert der deliveryStatus-Eigenschaft ab.
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
     */
    public void setDeliveryStatus(DeliveryStatusType value) {
        this.deliveryStatus = value;
    }

}
