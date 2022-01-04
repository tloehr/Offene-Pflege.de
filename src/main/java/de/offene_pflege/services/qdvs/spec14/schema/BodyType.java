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
 * <p>Java-Klasse für body_type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="body_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="data_container" type="{https://www.das-pflege.de}care_data_type"/>
 *         &lt;element name="commentation_container" type="{https://www.das-pflege.de}das_commentation_type"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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

    public BodyType() {
        dataContainer = new CareDataType();
    }

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
