//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2019.10.30 um 10:38:41 AM CET 
//


package de.offene_pflege.services.qdvs.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für care_data_type complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="care_data_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="facility" type="{https://www.das-pflege.de}facility_data_type"/>
 *         &lt;element name="residents" type="{https://www.das-pflege.de}residents_type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "care_data_type", propOrder = {
        "facility",
        "residents"
})
public class CareDataType {

    @XmlElement(required = true)
    protected FacilityDataType facility;
    @XmlElement(required = true)
    protected ResidentsType residents;

    public CareDataType() {
    }
    
    /**
     * Ruft den Wert der facility-Eigenschaft ab.
     *
     * @return possible object is {@link FacilityDataType }
     */
    public FacilityDataType getFacility() {
        return facility;
    }

    /**
     * Legt den Wert der facility-Eigenschaft fest.
     *
     * @param value allowed object is {@link FacilityDataType }
     */
    public void setFacility(FacilityDataType value) {
        this.facility = value;
    }

    /**
     * Ruft den Wert der residents-Eigenschaft ab.
     *
     * @return possible object is {@link ResidentsType }
     */
    public ResidentsType getResidents() {
        return residents;
    }

    /**
     * Legt den Wert der residents-Eigenschaft fest.
     *
     * @param value allowed object is {@link ResidentsType }
     */
    public void setResidents(ResidentsType value) {
        this.residents = value;
    }

}
