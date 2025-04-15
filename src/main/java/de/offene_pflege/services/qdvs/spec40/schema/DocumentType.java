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
 * Dokumentspezifische Informationen
 * 
 * <p>Java-Klasse für document_type complex type.</p>
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
 * 
 * <pre>{@code
 * <complexType name="document_type">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="guid" type="{https://www.das-pflege.de}guid_type"/>
 *         <element name="creation_date" type="{https://www.das-pflege.de}dateTime_type"/>
 *         <element name="specification" type="{https://www.das-pflege.de}specification_type"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "document_type", propOrder = {
    "guid",
    "creationDate",
    "specification"
})
public class DocumentType {

    @XmlElement(required = true)
    protected GuidType guid;
    @XmlElement(name = "creation_date", required = true)
    protected DateTimeType creationDate;
    @XmlElement(required = true)
    protected SpecificationType specification;

    /**
     * Ruft den Wert der guid-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GuidType }
     *     
     */
    public GuidType getGuid() {
        return guid;
    }

    /**
     * Legt den Wert der guid-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GuidType }
     *     
     */
    public void setGuid(GuidType value) {
        this.guid = value;
    }

    /**
     * Ruft den Wert der creationDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeType }
     *     
     */
    public DateTimeType getCreationDate() {
        return creationDate;
    }

    /**
     * Legt den Wert der creationDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeType }
     *     
     */
    public void setCreationDate(DateTimeType value) {
        this.creationDate = value;
    }

    /**
     * Ruft den Wert der specification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SpecificationType }
     *     
     */
    public SpecificationType getSpecification() {
        return specification;
    }

    /**
     * Legt den Wert der specification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SpecificationType }
     *     
     */
    public void setSpecification(SpecificationType value) {
        this.specification = value;
    }

}
