//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v4.0.5 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
//


package de.offene_pflege.services.qdvs.spec40.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Erhebungsbogen zur Erfassung von administrativen Angaben zur Vollzähligkeitsanalyse
 * 
 * <p>Java-Klasse für das_qs_data_facility_type complex type.</p>
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
 * 
 * <pre>{@code
 * <complexType name="das_qs_data_facility_type">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="BELEGUNGAMSTICHTAG">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required">
 *                   <simpleType>
 *                     <restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                       <totalDigits value="3"/>
 *                       <minInclusive value="0"/>
 *                     </restriction>
 *                   </simpleType>
 *                 </attribute>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "das_qs_data_facility_type", propOrder = {
    "belegungamstichtag"
})
public class DasQsDataFacilityType {

    /**
     * Belegungszahl am Stichtag
     * 
     */
    @XmlElement(name = "BELEGUNGAMSTICHTAG", required = true)
    protected DasQsDataFacilityType.BELEGUNGAMSTICHTAG belegungamstichtag;

    /**
     * Belegungszahl am Stichtag
     * 
     * @return
     *     possible object is
     *     {@link DasQsDataFacilityType.BELEGUNGAMSTICHTAG }
     *     
     */
    public DasQsDataFacilityType.BELEGUNGAMSTICHTAG getBELEGUNGAMSTICHTAG() {
        return belegungamstichtag;
    }

    /**
     * Legt den Wert der belegungamstichtag-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DasQsDataFacilityType.BELEGUNGAMSTICHTAG }
     *     
     * @see #getBELEGUNGAMSTICHTAG()
     */
    public void setBELEGUNGAMSTICHTAG(DasQsDataFacilityType.BELEGUNGAMSTICHTAG value) {
        this.belegungamstichtag = value;
    }


    /**
     * <p>Java-Klasse für anonymous complex type.</p>
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <attribute name="value" use="required">
     *         <simpleType>
     *           <restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *             <totalDigits value="3"/>
     *             <minInclusive value="0"/>
     *           </restriction>
     *         </simpleType>
     *       </attribute>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class BELEGUNGAMSTICHTAG {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         */
        public void setValue(int value) {
            this.value = value;
        }

    }

}
