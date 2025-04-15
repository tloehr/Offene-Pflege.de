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
 * <p>Java-Klasse für care_data_type complex type.</p>
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
 * 
 * <pre>{@code
 * <complexType name="care_data_type">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="facility" type="{https://www.das-pflege.de}facility_data_type"/>
 *         <element name="residents" type="{https://www.das-pflege.de}residents_type"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
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

    /**
     * Ruft den Wert der facility-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FacilityDataType }
     *     
     */
    public FacilityDataType getFacility() {
        return facility;
    }

    /**
     * Legt den Wert der facility-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FacilityDataType }
     *     
     */
    public void setFacility(FacilityDataType value) {
        this.facility = value;
    }

    /**
     * Ruft den Wert der residents-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ResidentsType }
     *     
     */
    public ResidentsType getResidents() {
        return residents;
    }

    /**
     * Legt den Wert der residents-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ResidentsType }
     *     
     */
    public void setResidents(ResidentsType value) {
        this.residents = value;
    }

}
