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
 * Datenlieferantspezifische Informationen
 *
 * <p>Java-Klasse für care_provider_type complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="care_provider_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="registration" type="{https://www.das-pflege.de}registration_type"/>
 *         &lt;element name="target_date" type="{https://www.das-pflege.de}date_type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "care_provider_type", propOrder = {
        "registration",
        "targetDate"
})
public class CareProviderType {

    @XmlElement(required = true)
    protected RegistrationType registration;
    @XmlElement(name = "target_date", required = true)
    protected DateType targetDate;

    public CareProviderType() {
    }


    /**
     * Ruft den Wert der registration-Eigenschaft ab.
     *
     * @return possible object is {@link RegistrationType }
     */
    public RegistrationType getRegistration() {
        return registration;
    }

    /**
     * Legt den Wert der registration-Eigenschaft fest.
     *
     * @param value allowed object is {@link RegistrationType }
     */
    public void setRegistration(RegistrationType value) {
        this.registration = value;
    }

    /**
     * Ruft den Wert der targetDate-Eigenschaft ab.
     *
     * @return possible object is {@link DateType }
     */
    public DateType getTargetDate() {
        return targetDate;
    }

    /**
     * Legt den Wert der targetDate-Eigenschaft fest.
     * <p>
     * Mit Hilfe der Angabe im Element <target_date> erfolgt die Zuordnung einer Datenlieferung zu einem spe- zifischen
     * einrichtungsindividuellen Stichtag. Sofern der angegebene Stichtag nicht mit einem bei der DAS Pflege
     * hinterlegten bzw. von der DAS Pflege berechneten Stichtag für die entsprechende Pflegeeinrichtung übereinstimmt
     * wird die Datenlieferung abgewiesen. Die Wichtigkeit der Angabe des korrekten Stichtags resul- tiert aus der
     * Möglichkeit, dass zum gleichen Zeitpunkt im Verlauf der Zyklen einer Einrichtung zu mehreren Stichtagen bzw.
     * Erhebungszeiträumen Daten geliefert werden können (maximal zwei). Dies betrifft den Ergeb- niserfassungs- sowie
     * den Korrekturzeitraum. In diesen Zeiträumen können Daten zum vergangenen, aber auch für den bereits laufenden
     * Erhebungszeitraum (s. auch Abschnitt 2.1) geliefert werden.
     *
     * @param value allowed object is {@link DateType }
     */
    public void setTargetDate(DateType value) {
        this.targetDate = value;
    }

}
