//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v4.0.5 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
//


package de.offene_pflege.services.qdvs.spec40.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für das_commentation_type complex type.</p>
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
 * 
 * <pre>{@code
 * <complexType name="das_commentation_type">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="KOMMENTAR">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required">
 *                   <simpleType>
 *                     <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       <maxLength value="3000"/>
 *                     </restriction>
 *                   </simpleType>
 *                 </attribute>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
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
@XmlType(name = "das_commentation_type", propOrder = {
    "kommentar",
    "validationStatus"
})
public class DasCommentationType {

    /**
     * Kommentar
     * 
     */
    @XmlElement(name = "KOMMENTAR", required = true)
    protected DasCommentationType.KOMMENTAR kommentar;
    /**
     * Umschlagselement für Informationen zur Plausibilitätsprüfung
     * 
     */
    @XmlElement(name = "validation_status")
    protected ValidationStatusType validationStatus;

    /**
     * Kommentar
     * 
     * @return
     *     possible object is
     *     {@link DasCommentationType.KOMMENTAR }
     *     
     */
    public DasCommentationType.KOMMENTAR getKOMMENTAR() {
        return kommentar;
    }

    /**
     * Legt den Wert der kommentar-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DasCommentationType.KOMMENTAR }
     *     
     * @see #getKOMMENTAR()
     */
    public void setKOMMENTAR(DasCommentationType.KOMMENTAR value) {
        this.kommentar = value;
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


    /**
     * <p>Java-Klasse für anonymous complex type.</p>
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <attribute name="value" use="required">
     *         <simpleType>
     *           <restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             <maxLength value="3000"/>
     *           </restriction>
     *         </simpleType>
     *       </attribute>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KOMMENTAR {

        @XmlAttribute(name = "value", required = true)
        protected String value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

    }

}
