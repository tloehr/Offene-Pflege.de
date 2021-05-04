//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2019.10.30 um 10:38:41 AM CET 
//


package de.offene_pflege.services.qdvs.schema;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java-Klasse für delivery_status_type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="delivery_status_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="http_status" type="{https://www.das-pflege.de}number_type"/>
 *         &lt;element name="error" type="{https://www.das-pflege.de}error_type" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_status_type" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "delivery_status_type", propOrder = {
    "httpStatus",
    "error"
})
public class DeliveryStatusType {

    @XmlElement(name = "http_status", required = true)
    protected NumberType httpStatus;
    protected List<ErrorType> error;
    @XmlAttribute(name = "value", required = true)
    protected EnumStatusType value;

    /**
     * Ruft den Wert der httpStatus-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link NumberType }
     *     
     */
    public NumberType getHttpStatus() {
        return httpStatus;
    }

    /**
     * Legt den Wert der httpStatus-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NumberType }
     *     
     */
    public void setHttpStatus(NumberType value) {
        this.httpStatus = value;
    }

    /**
     * Gets the value of the error property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the error property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getError().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ErrorType }
     * 
     * 
     */
    public List<ErrorType> getError() {
        if (error == null) {
            error = new ArrayList<ErrorType>();
        }
        return this.error;
    }

    /**
     * Ruft den Wert der value-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EnumStatusType }
     *     
     */
    public EnumStatusType getValue() {
        return value;
    }

    /**
     * Legt den Wert der value-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EnumStatusType }
     *     
     */
    public void setValue(EnumStatusType value) {
        this.value = value;
    }

}
