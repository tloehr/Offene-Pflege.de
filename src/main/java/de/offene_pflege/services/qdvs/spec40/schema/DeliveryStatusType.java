//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v4.0.5 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
//


package de.offene_pflege.services.qdvs.spec40.schema;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für delivery_status_type complex type.</p>
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
 * 
 * <pre>{@code
 * <complexType name="delivery_status_type">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="http_status" type="{https://www.das-pflege.de}number_type"/>
 *         <element name="error" type="{https://www.das-pflege.de}error_type" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_status_type" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "delivery_status_type", propOrder = {
    "httpStatus",
    "error"
})
public class DeliveryStatusType {

    /**
     * http-Status der Datenlieferung
     * 
     */
    @XmlElement(name = "http_status", required = true)
    protected NumberType httpStatus;
    /**
     * Umschlagselement für regelspezifische Informationen
     * 
     */
    protected List<ErrorType> error;
    @XmlAttribute(name = "value", required = true)
    protected EnumStatusType value;

    /**
     * http-Status der Datenlieferung
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
     * @see #getHttpStatus()
     */
    public void setHttpStatus(NumberType value) {
        this.httpStatus = value;
    }

    /**
     * Umschlagselement für regelspezifische Informationen
     * 
     * Gets the value of the error property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the error property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getError().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ErrorType }
     * </p>
     * 
     * 
     * @return
     *     The value of the error property.
     */
    public List<ErrorType> getError() {
        if (error == null) {
            error = new ArrayList<>();
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
