//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2019.10.30 um 10:38:41 AM CET 
//


package de.offene_pflege.services.qdvs.schema;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Dokumentspezifische Informationen
 *
 * <p>Java-Klasse für document_type complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="document_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="guid" type="{https://www.das-pflege.de}guid_type"/>
 *         &lt;element name="creation_date" type="{https://www.das-pflege.de}dateTime_type"/>
 *         &lt;element name="specification" type="{https://www.das-pflege.de}specification_type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
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


    public DocumentType() {
    }

    /**
     * Ruft den Wert der guid-Eigenschaft ab.
     *
     * @return possible object is {@link GuidType }
     */
    public GuidType getGuid() {
        return guid;
    }

    /**
     * Legt den Wert der guid-Eigenschaft fest.
     *
     * @param value allowed object is {@link GuidType }
     */
    public void setGuid(GuidType value) {
        this.guid = value;
    }

    /**
     * Ruft den Wert der creationDate-Eigenschaft ab.
     *
     * @return possible object is {@link DateTimeType }
     */
    public DateTimeType getCreationDate() {
        return creationDate;
    }

    /**
     * Legt den Wert der creationDate-Eigenschaft fest.
     *
     * @param value allowed object is {@link DateTimeType }
     */
    public void setCreationDate(DateTimeType value) {
        this.creationDate = value;
    }

    /**
     * Ruft den Wert der specification-Eigenschaft ab.
     *
     * @return possible object is {@link SpecificationType }
     */
    public SpecificationType getSpecification() {
        return specification;
    }

    /**
     * Legt den Wert der specification-Eigenschaft fest.
     *
     * @param value allowed object is {@link SpecificationType }
     */
    public void setSpecification(SpecificationType value) {
        this.specification = value;
    }

}
