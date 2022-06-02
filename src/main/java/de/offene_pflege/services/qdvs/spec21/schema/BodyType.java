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
 * <p>Java-Klasse fxFCr body_type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="body_type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="data_container" type="{https://www.das-pflege.de}care_data_type"/&gt;
 *         &lt;element name="commentation_container" type="{https://www.das-pflege.de}das_commentation_type"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "body_type", propOrder = {
    "dataContainer",
    "commentationContainer"
})
public class BodyType {

    @XmlElement(name = "data_container")
    protected CareDataType dataContainer;
    @XmlElement(name = "commentation_container")
    protected DasCommentationType commentationContainer;

    /**
     * Ruft den Wert der dataContainer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CareDataType }
     *     
     */
    public CareDataType getDataContainer() {
        return dataContainer;
    }

    /**
     * Legt den Wert der dataContainer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CareDataType }
     *     
     */
    public void setDataContainer(CareDataType value) {
        this.dataContainer = value;
    }

    /**
     * Ruft den Wert der commentationContainer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DasCommentationType }
     *     
     */
    public DasCommentationType getCommentationContainer() {
        return commentationContainer;
    }

    /**
     * Legt den Wert der commentationContainer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DasCommentationType }
     *     
     */
    public void setCommentationContainer(DasCommentationType value) {
        this.commentationContainer = value;
    }

}
