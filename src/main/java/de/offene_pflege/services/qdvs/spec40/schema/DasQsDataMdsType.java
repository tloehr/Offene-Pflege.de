//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v4.0.5 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
//


package de.offene_pflege.services.qdvs.spec40.schema;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Erhebungsbogen zur Erfassung von Versorgungsergebnissen der stationären Langzeitpflege sofern ein Ausschlussgrund vorliegt = Minimaldatensatz (MDS)
 * 
 * <p>Java-Klasse für das_qs_data_mds_type complex type.</p>
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
 * 
 * <pre>{@code
 * <complexType name="das_qs_data_mds_type">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="IDBEWOHNER">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required">
 *                   <simpleType>
 *                     <restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       <pattern value="[0-9]{6}"/>
 *                     </restriction>
 *                   </simpleType>
 *                 </attribute>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="WOHNBEREICH">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{https://www.das-pflege.de}enum_wohnbereich_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="ERHEBUNGSDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="EINZUGSDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="GEBURTSMONAT">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_monat_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="GEBURTSJAHR">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required">
 *                   <simpleType>
 *                     <restriction base="{http://www.w3.org/2001/XMLSchema}gYear">
 *                       <minInclusive value="1900"/>
 *                     </restriction>
 *                   </simpleType>
 *                 </attribute>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="AUSSCHLUSSGRUND">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_ausschlussgrund_type" />
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
@XmlType(name = "das_qs_data_mds_type", propOrder = {
    "idbewohner",
    "wohnbereich",
    "erhebungsdatum",
    "einzugsdatum",
    "geburtsmonat",
    "geburtsjahr",
    "ausschlussgrund"
})
public class DasQsDataMdsType {

    /**
     * Bewohnerbezogene Nummer
     * 
     */
    @XmlElement(name = "IDBEWOHNER", required = true)
    protected IDBEWOHNER idbewohner;
    /**
     * Wohnbereich
     * 
     */
    @XmlElement(name = "WOHNBEREICH", required = true)
    protected WOHNBEREICH wohnbereich;
    /**
     * Datum der Ergebniserfassung
     * 
     */
    @XmlElement(name = "ERHEBUNGSDATUM", required = true)
    protected ERHEBUNGSDATUM erhebungsdatum;
    /**
     * Datum des Einzugs (Beginn der vollstationären Langzeitpflege)
     * 
     */
    @XmlElement(name = "EINZUGSDATUM", required = true)
    protected EINZUGSDATUM einzugsdatum;
    /**
     * Geburtsmonat
     * 
     */
    @XmlElement(name = "GEBURTSMONAT", required = true)
    protected GEBURTSMONAT geburtsmonat;
    /**
     * Geburtsjahr
     * 
     */
    @XmlElement(name = "GEBURTSJAHR", required = true)
    protected GEBURTSJAHR geburtsjahr;
    /**
     * Ausschlussgrund
     * 
     */
    @XmlElement(name = "AUSSCHLUSSGRUND", required = true)
    protected AUSSCHLUSSGRUND ausschlussgrund;

    /**
     * Bewohnerbezogene Nummer
     * 
     * @return
     *     possible object is
     *     {@link IDBEWOHNER }
     *     
     */
    public IDBEWOHNER getIDBEWOHNER() {
        return idbewohner;
    }

    /**
     * Legt den Wert der idbewohner-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IDBEWOHNER }
     *     
     * @see #getIDBEWOHNER()
     */
    public void setIDBEWOHNER(IDBEWOHNER value) {
        this.idbewohner = value;
    }

    /**
     * Wohnbereich
     * 
     * @return
     *     possible object is
     *     {@link WOHNBEREICH }
     *     
     */
    public WOHNBEREICH getWOHNBEREICH() {
        return wohnbereich;
    }

    /**
     * Legt den Wert der wohnbereich-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link WOHNBEREICH }
     *     
     * @see #getWOHNBEREICH()
     */
    public void setWOHNBEREICH(WOHNBEREICH value) {
        this.wohnbereich = value;
    }

    /**
     * Datum der Ergebniserfassung
     * 
     * @return
     *     possible object is
     *     {@link ERHEBUNGSDATUM }
     *     
     */
    public ERHEBUNGSDATUM getERHEBUNGSDATUM() {
        return erhebungsdatum;
    }

    /**
     * Legt den Wert der erhebungsdatum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ERHEBUNGSDATUM }
     *     
     * @see #getERHEBUNGSDATUM()
     */
    public void setERHEBUNGSDATUM(ERHEBUNGSDATUM value) {
        this.erhebungsdatum = value;
    }

    /**
     * Datum des Einzugs (Beginn der vollstationären Langzeitpflege)
     * 
     * @return
     *     possible object is
     *     {@link EINZUGSDATUM }
     *     
     */
    public EINZUGSDATUM getEINZUGSDATUM() {
        return einzugsdatum;
    }

    /**
     * Legt den Wert der einzugsdatum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EINZUGSDATUM }
     *     
     * @see #getEINZUGSDATUM()
     */
    public void setEINZUGSDATUM(EINZUGSDATUM value) {
        this.einzugsdatum = value;
    }

    /**
     * Geburtsmonat
     * 
     * @return
     *     possible object is
     *     {@link GEBURTSMONAT }
     *     
     */
    public GEBURTSMONAT getGEBURTSMONAT() {
        return geburtsmonat;
    }

    /**
     * Legt den Wert der geburtsmonat-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GEBURTSMONAT }
     *     
     * @see #getGEBURTSMONAT()
     */
    public void setGEBURTSMONAT(GEBURTSMONAT value) {
        this.geburtsmonat = value;
    }

    /**
     * Geburtsjahr
     * 
     * @return
     *     possible object is
     *     {@link GEBURTSJAHR }
     *     
     */
    public GEBURTSJAHR getGEBURTSJAHR() {
        return geburtsjahr;
    }

    /**
     * Legt den Wert der geburtsjahr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GEBURTSJAHR }
     *     
     * @see #getGEBURTSJAHR()
     */
    public void setGEBURTSJAHR(GEBURTSJAHR value) {
        this.geburtsjahr = value;
    }

    /**
     * Ausschlussgrund
     * 
     * @return
     *     possible object is
     *     {@link AUSSCHLUSSGRUND }
     *     
     */
    public AUSSCHLUSSGRUND getAUSSCHLUSSGRUND() {
        return ausschlussgrund;
    }

    /**
     * Legt den Wert der ausschlussgrund-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AUSSCHLUSSGRUND }
     *     
     * @see #getAUSSCHLUSSGRUND()
     */
    public void setAUSSCHLUSSGRUND(AUSSCHLUSSGRUND value) {
        this.ausschlussgrund = value;
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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_ausschlussgrund_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class AUSSCHLUSSGRUND {

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


    /**
     * <p>Java-Klasse für anonymous complex type.</p>
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class EINZUGSDATUM {

        @XmlAttribute(name = "value")
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setValue(XMLGregorianCalendar value) {
            this.value = value;
        }

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
     *       <attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ERHEBUNGSDATUM {

        @XmlAttribute(name = "value", required = true)
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setValue(XMLGregorianCalendar value) {
            this.value = value;
        }

    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     *
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="value" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}gYear">
     *             &lt;minInclusive value="1900"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GEBURTSJAHR {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return
         *     possible object is
         *     {@link int }
         *
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value
         *     allowed object is
         *     {@link int }
         *
         */
        public void setValue(int value) {
            this.value = value;
        }

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_monat_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GEBURTSMONAT {

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
     *           <restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             <pattern value="[0-9]{6}"/>
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
    public static class IDBEWOHNER {

        @XmlAttribute(name = "value", required = true)
        protected String value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

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
     *       <attribute name="value" type="{https://www.das-pflege.de}enum_wohnbereich_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class WOHNBEREICH {

        @XmlAttribute(name = "value")
        protected EnumWohnbereichType value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link EnumWohnbereichType }
         *     
         */
        public EnumWohnbereichType getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link EnumWohnbereichType }
         *     
         */
        public void setValue(EnumWohnbereichType value) {
            this.value = value;
        }

    }

}
