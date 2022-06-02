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
 * Komplexer Datentyp zur Aufnahme von Fehlermeldungen
 * 
 * <p>Java-Klasse fxFCr error_type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="error_type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="rule_id" type="{https://www.das-pflege.de}number_type" minOccurs="0"/&gt;
 *         &lt;element name="rule_text" type="{https://www.das-pflege.de}long_text_type"/&gt;
 *         &lt;element name="rule_type" type="{https://www.das-pflege.de}val_rule_type"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
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
