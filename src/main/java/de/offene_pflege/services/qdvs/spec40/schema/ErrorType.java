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
 * Komplexer Datentyp zur Aufnahme von Fehlermeldungen
 * 
 * <p>Java-Klasse für error_type complex type.</p>
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
 * 
 * <pre>{@code
 * <complexType name="error_type">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="rule_id" type="{https://www.das-pflege.de}number_type" minOccurs="0"/>
 *         <element name="rule_text" type="{https://www.das-pflege.de}long_text_type"/>
 *         <element name="rule_type" type="{https://www.das-pflege.de}val_rule_type"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "error_type", propOrder = {
    "ruleId",
    "ruleText",
    "ruleType"
})
public class ErrorType {

    @XmlElement(name = "rule_id")
    protected NumberType ruleId;
    @XmlElement(name = "rule_text", required = true)
    protected LongTextType ruleText;
    @XmlElement(name = "rule_type", required = true)
    protected ValRuleType ruleType;

    /**
     * Ruft den Wert der ruleId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link NumberType }
     *     
     */
    public NumberType getRuleId() {
        return ruleId;
    }

    /**
     * Legt den Wert der ruleId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NumberType }
     *     
     */
    public void setRuleId(NumberType value) {
        this.ruleId = value;
    }

    /**
     * Ruft den Wert der ruleText-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LongTextType }
     *     
     */
    public LongTextType getRuleText() {
        return ruleText;
    }

    /**
     * Legt den Wert der ruleText-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LongTextType }
     *     
     */
    public void setRuleText(LongTextType value) {
        this.ruleText = value;
    }

    /**
     * Ruft den Wert der ruleType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ValRuleType }
     *     
     */
    public ValRuleType getRuleType() {
        return ruleType;
    }

    /**
     * Legt den Wert der ruleType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ValRuleType }
     *     
     */
    public void setRuleType(ValRuleType value) {
        this.ruleType = value;
    }

}
