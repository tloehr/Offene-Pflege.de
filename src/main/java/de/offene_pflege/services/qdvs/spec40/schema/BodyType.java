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
 * <p>Java-Klasse für body_type complex type.</p>
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
 * 
 * <pre>{@code
 * <complexType name="body_type">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <choice>
 *         <element name="data_container" type="{https://www.das-pflege.de}care_data_type"/>
 *         <element name="commentation_container" type="{https://www.das-pflege.de}das_commentation_type"/>
 *       </choice>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "body_type", propOrder = {
    "dataContainer",
    "commentationContainer"
})
public class BodyType {

    /**
     * Umschlagselement für Qualitätssicherungsdaten
     * 
     */
    @XmlElement(name = "data_container")
    protected CareDataType dataContainer;
    /**
     * Umschlagselement für die Kommentierung
     * 
     */
    @XmlElement(name = "commentation_container")
    protected DasCommentationType commentationContainer;

    /**
     * Umschlagselement für Qualitätssicherungsdaten
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
     * @see #getDataContainer()
     */
    public void setDataContainer(CareDataType value) {
        this.dataContainer = value;
    }

    /**
     * Umschlagselement für die Kommentierung
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
     * @see #getCommentationContainer()
     */
    public void setCommentationContainer(DasCommentationType value) {
        this.commentationContainer = value;
    }

}
