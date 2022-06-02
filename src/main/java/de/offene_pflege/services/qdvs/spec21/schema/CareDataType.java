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
 * <p>Java-Klasse fxFCr care_data_type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="care_data_type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="facility" type="{https://www.das-pflege.de}facility_data_type"/&gt;
 *         &lt;element name="residents" type="{https://www.das-pflege.de}residents_type"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
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
