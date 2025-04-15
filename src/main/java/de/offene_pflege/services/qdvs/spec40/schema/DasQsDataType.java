//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v4.0.5 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
//


package de.offene_pflege.services.qdvs.spec40.schema;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Erhebungsbogen zur Erfassung von Versorgungsergebnissen der stationären Langzeitpflege
 * 
 * <p>Java-Klasse für das_qs_data_type complex type.</p>
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
 * 
 * <pre>{@code
 * <complexType name="das_qs_data_type">
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
 *                 <attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
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
 *         <element name="PFLEGEGRAD">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="APOPLEX">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="APOPLEXDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="FRAKTUR">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="FRAKTURDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="HERZINFARKT">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="HERZINFARKTDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="AMPUTATION">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="AMPUTATIONDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="KHBEHANDLUNG">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jnAnzahl_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="KHBEGINNDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="KHENDEDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="BEWUSSTSEINSZUSTAND">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_bewusstseinszustand_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="DIAGNOSEN" maxOccurs="3">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_diagnose_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="MOBILPOSWECHSEL">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="MOBILSITZPOSITION">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="MOBILUMSETZEN">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="MOBILFORTBEWEGUNG">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="MOBILTREPPENSTEIGEN">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="KKFERKENNEN">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="KKFORIENTOERTLICH">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="KKFORIENTZEITLICH">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="KKFERINNERN">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="KKFHANDLUNGEN">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="KKFENTSCHEIDUNGEN">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="KKFVERSTEHENINFO">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="KKFGEFAHRERKENNEN">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="KKFMITTEILEN">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="KKFVERSTEHENAUF">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="KKFBETEILIGUNG">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SVERNAEHRUNG">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SVFREMDHILFE">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{https://www.das-pflege.de}enum_fremdhilfe_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SVERNAEHRUNGUMFANG">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{https://www.das-pflege.de}enum_kuenstlicheErnaehrung_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SVHARNKONTINENZ">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_harnkontinenz_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SVSTUHLKONTINENZ">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_stuhlkontinenz_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SVOBERKOERPER">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SVKOPF">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SVINTIMBEREICH">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SVDUSCHENBADEN">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SVANAUSOBERKOERPER">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SVANAUSUNTERKOERPER">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SVNAHRUNGZUBEREITEN">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SVESSEN">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitTyp2_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SVTRINKEN">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitTyp1_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SVTOILETTE">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitTyp1_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SVHARNKONTINENZBEW">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SVSTUHLKONTINENZBEW">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="GATAGESABLAUF">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="GARUHENSCHLAFEN">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="GABESCHAEFTIGEN">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="GAPLANUNGEN">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="GAINTERAKTION">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="GAKONTAKTPFLEGE">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="DEKUBITUS">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jnAnzahl_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="DEKUBITUSSTADIUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{https://www.das-pflege.de}enum_dekubitusStadium_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="DEKUBITUS1BEGINNDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="DEKUBITUS1ENDEDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="DEKUBITUS1LOK">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{https://www.das-pflege.de}enum_lokalisation_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="DEKUBITUS2BEGINNDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="DEKUBITUS2ENDEDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="DEKUBITUS2LOK">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{https://www.das-pflege.de}enum_lokalisation_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="KOERPERGEWICHT">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value">
 *                   <simpleType>
 *                     <restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
 *                       <totalDigits value="5"/>
 *                       <fractionDigits value="2"/>
 *                       <minInclusive value="0"/>
 *                       <maxInclusive value="500"/>
 *                     </restriction>
 *                   </simpleType>
 *                 </attribute>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="KOERPERGEWICHTDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="KOERPERGEWICHTDOKU" maxOccurs="5">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_gewichtsverlust_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="STURZ">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jnAnzahl_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="STURZFOLGEN" maxOccurs="4">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{https://www.das-pflege.de}enum_sturzfolgen_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="GURT">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SEITENTEILE">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SCHMERZEN">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SCHMERZFREI">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SCHMERZEINSCH">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SCHMERZEINSCHDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="SCHMERZEINSCHINFO" maxOccurs="4">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{https://www.das-pflege.de}enum_schmerzeinschaetzung_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="NEUEINZUG">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="EINZUGNACHKZP">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="EINZUGNACHKZPDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="EINZUGKHBEHANDLUNG">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="EINZUGKHBEGINNDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="EINZUGKHENDEDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="EINZUGGESPR">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{https://www.das-pflege.de}enum_jnNichtMoeglich_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="EINZUGGESPRDATUM">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="EINZUGGESPRTEILNEHMER" maxOccurs="4">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{https://www.das-pflege.de}enum_teilnehmer_type" />
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="EINZUGGESPRDOKU">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
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
@XmlType(name = "das_qs_data_type", propOrder = {
    "idbewohner",
    "wohnbereich",
    "erhebungsdatum",
    "einzugsdatum",
    "geburtsmonat",
    "geburtsjahr",
    "pflegegrad",
    "apoplex",
    "apoplexdatum",
    "fraktur",
    "frakturdatum",
    "herzinfarkt",
    "herzinfarktdatum",
    "amputation",
    "amputationdatum",
    "khbehandlung",
    "khbeginndatum",
    "khendedatum",
    "bewusstseinszustand",
    "diagnosen",
    "mobilposwechsel",
    "mobilsitzposition",
    "mobilumsetzen",
    "mobilfortbewegung",
    "mobiltreppensteigen",
    "kkferkennen",
    "kkforientoertlich",
    "kkforientzeitlich",
    "kkferinnern",
    "kkfhandlungen",
    "kkfentscheidungen",
    "kkfversteheninfo",
    "kkfgefahrerkennen",
    "kkfmitteilen",
    "kkfverstehenauf",
    "kkfbeteiligung",
    "svernaehrung",
    "svfremdhilfe",
    "svernaehrungumfang",
    "svharnkontinenz",
    "svstuhlkontinenz",
    "svoberkoerper",
    "svkopf",
    "svintimbereich",
    "svduschenbaden",
    "svanausoberkoerper",
    "svanausunterkoerper",
    "svnahrungzubereiten",
    "svessen",
    "svtrinken",
    "svtoilette",
    "svharnkontinenzbew",
    "svstuhlkontinenzbew",
    "gatagesablauf",
    "garuhenschlafen",
    "gabeschaeftigen",
    "gaplanungen",
    "gainteraktion",
    "gakontaktpflege",
    "dekubitus",
    "dekubitusstadium",
    "dekubitus1BEGINNDATUM",
    "dekubitus1ENDEDATUM",
    "dekubitus1LOK",
    "dekubitus2BEGINNDATUM",
    "dekubitus2ENDEDATUM",
    "dekubitus2LOK",
    "koerpergewicht",
    "koerpergewichtdatum",
    "koerpergewichtdoku",
    "sturz",
    "sturzfolgen",
    "gurt",
    "seitenteile",
    "schmerzen",
    "schmerzfrei",
    "schmerzeinsch",
    "schmerzeinschdatum",
    "schmerzeinschinfo",
    "neueinzug",
    "einzugnachkzp",
    "einzugnachkzpdatum",
    "einzugkhbehandlung",
    "einzugkhbeginndatum",
    "einzugkhendedatum",
    "einzuggespr",
    "einzuggesprdatum",
    "einzuggesprteilnehmer",
    "einzuggesprdoku"
})
public class DasQsDataType {

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
     * Ist ein Pflegegrad vorhanden?
     * 
     */
    @XmlElement(name = "PFLEGEGRAD", required = true)
    protected PFLEGEGRAD pflegegrad;
    /**
     * Ist es bei der Bewohnerin bzw. dem Bewohner seit der letzten Ergebniserfassung zu einem Apoplex gekommen?
     * 
     */
    @XmlElement(name = "APOPLEX", required = true)
    protected APOPLEX apoplex;
    /**
     * Datum des Apoplex
     * 
     */
    @XmlElement(name = "APOPLEXDATUM", required = true)
    protected APOPLEXDATUM apoplexdatum;
    /**
     * Ist es bei der Bewohnerin bzw. dem Bewohner seit der letzten Ergebniserfassung zu einer Fraktur gekommen?
     * 
     */
    @XmlElement(name = "FRAKTUR", required = true)
    protected FRAKTUR fraktur;
    /**
     * Datum der Fraktur
     * 
     */
    @XmlElement(name = "FRAKTURDATUM", required = true)
    protected FRAKTURDATUM frakturdatum;
    /**
     * Ist es bei der Bewohnerin bzw. dem Bewohner seit der letzten Ergebniserfassung zu einem Herzinfarkt gekommen?
     * 
     */
    @XmlElement(name = "HERZINFARKT", required = true)
    protected HERZINFARKT herzinfarkt;
    /**
     * Datum des Herzinfarkts
     * 
     */
    @XmlElement(name = "HERZINFARKTDATUM", required = true)
    protected HERZINFARKTDATUM herzinfarktdatum;
    /**
     * Ist es bei der Bewohnerin bzw. dem Bewohner seit der letzten Ergebniserfassung zu einer Amputation gekommen?
     * 
     */
    @XmlElement(name = "AMPUTATION", required = true)
    protected AMPUTATION amputation;
    /**
     * Datum der Amputation
     * 
     */
    @XmlElement(name = "AMPUTATIONDATUM", required = true)
    protected AMPUTATIONDATUM amputationdatum;
    /**
     * Wurde die Bewohnerin bzw. der Bewohner seit der letzten Ergebniserfassung in einem Krankenhaus behandelt?
     * 
     */
    @XmlElement(name = "KHBEHANDLUNG", required = true)
    protected KHBEHANDLUNG khbehandlung;
    /**
     * Datum: Beginn des Krankenhausaufenthalts (bei mehreren Aufenthalten bitte den Aufenthalt mit der längsten Dauer wählen)
     * 
     */
    @XmlElement(name = "KHBEGINNDATUM", required = true)
    protected KHBEGINNDATUM khbeginndatum;
    /**
     * Datum: Ende des Krankenhausaufenthalts (bei mehreren Aufenthalten bitte den Aufenthalt mit der längsten Dauer wählen)
     * 
     */
    @XmlElement(name = "KHENDEDATUM", required = true)
    protected KHENDEDATUM khendedatum;
    /**
     * Bewusstseinszustand der Bewohnerin bzw. des Bewohners
     * 
     */
    @XmlElement(name = "BEWUSSTSEINSZUSTAND", required = true)
    protected BEWUSSTSEINSZUSTAND bewusstseinszustand;
    /**
     * Ärztliche Diagnosen für die Bewohnerin bzw. den Bewohner
     * 
     */
    @XmlElement(name = "DIAGNOSEN", required = true)
    protected List<DIAGNOSEN> diagnosen;
    /**
     * Positionswechsel im Bett
     * 
     */
    @XmlElement(name = "MOBILPOSWECHSEL", required = true)
    protected MOBILPOSWECHSEL mobilposwechsel;
    /**
     * Halten einer stabilen Sitzposition
     * 
     */
    @XmlElement(name = "MOBILSITZPOSITION", required = true)
    protected MOBILSITZPOSITION mobilsitzposition;
    /**
     * Sich Umsetzen
     * 
     */
    @XmlElement(name = "MOBILUMSETZEN", required = true)
    protected MOBILUMSETZEN mobilumsetzen;
    /**
     * Fortbewegen innerhalb des Wohnbereichs
     * 
     */
    @XmlElement(name = "MOBILFORTBEWEGUNG", required = true)
    protected MOBILFORTBEWEGUNG mobilfortbewegung;
    /**
     * Treppensteigen
     * 
     */
    @XmlElement(name = "MOBILTREPPENSTEIGEN", required = true)
    protected MOBILTREPPENSTEIGEN mobiltreppensteigen;
    /**
     * Erkennen von Personen aus dem näheren Umfeld
     * 
     */
    @XmlElement(name = "KKFERKENNEN", required = true)
    protected KKFERKENNEN kkferkennen;
    /**
     * Örtliche Orientierung
     * 
     */
    @XmlElement(name = "KKFORIENTOERTLICH", required = true)
    protected KKFORIENTOERTLICH kkforientoertlich;
    /**
     * Zeitliche Orientierung
     * 
     */
    @XmlElement(name = "KKFORIENTZEITLICH", required = true)
    protected KKFORIENTZEITLICH kkforientzeitlich;
    /**
     * Sich Erinnern
     * 
     */
    @XmlElement(name = "KKFERINNERN", required = true)
    protected KKFERINNERN kkferinnern;
    /**
     * Steuern von mehrschrittigen Alltagshandlungen
     * 
     */
    @XmlElement(name = "KKFHANDLUNGEN", required = true)
    protected KKFHANDLUNGEN kkfhandlungen;
    /**
     * Treffen von Entscheidungen im Alltagsleben
     * 
     */
    @XmlElement(name = "KKFENTSCHEIDUNGEN", required = true)
    protected KKFENTSCHEIDUNGEN kkfentscheidungen;
    /**
     * Verstehen von Sachverhalten und Informationen
     * 
     */
    @XmlElement(name = "KKFVERSTEHENINFO", required = true)
    protected KKFVERSTEHENINFO kkfversteheninfo;
    /**
     * Erkennen von Risiken und Gefahren
     * 
     */
    @XmlElement(name = "KKFGEFAHRERKENNEN", required = true)
    protected KKFGEFAHRERKENNEN kkfgefahrerkennen;
    /**
     * Mitteilen von elementaren Bedürfnissen
     * 
     */
    @XmlElement(name = "KKFMITTEILEN", required = true)
    protected KKFMITTEILEN kkfmitteilen;
    /**
     * Verstehen von Aufforderungen
     * 
     */
    @XmlElement(name = "KKFVERSTEHENAUF", required = true)
    protected KKFVERSTEHENAUF kkfverstehenauf;
    /**
     * Beteiligung an einem Gespräch
     * 
     */
    @XmlElement(name = "KKFBETEILIGUNG", required = true)
    protected KKFBETEILIGUNG kkfbeteiligung;
    /**
     * Erfolgt die Ernährung der Bewohnerin bzw. des Bewohners parenteral oder über eine Sonde?
     * 
     */
    @XmlElement(name = "SVERNAEHRUNG", required = true)
    protected SVERNAEHRUNG svernaehrung;
    /**
     * Erfolgt die Bedienung selbständig oder mit Fremdhilfe?
     * 
     */
    @XmlElement(name = "SVFREMDHILFE", required = true)
    protected SVFREMDHILFE svfremdhilfe;
    /**
     * In welchem Umfang erfolgt eine künstliche Ernährung?
     * 
     */
    @XmlElement(name = "SVERNAEHRUNGUMFANG", required = true)
    protected SVERNAEHRUNGUMFANG svernaehrungumfang;
    /**
     * Blasenkontrolle/Harnkontinenz
     * 
     */
    @XmlElement(name = "SVHARNKONTINENZ", required = true)
    protected SVHARNKONTINENZ svharnkontinenz;
    /**
     * Darmkontrolle/Stuhlkontinenz
     * 
     */
    @XmlElement(name = "SVSTUHLKONTINENZ", required = true)
    protected SVSTUHLKONTINENZ svstuhlkontinenz;
    /**
     * Waschen des vorderen Oberkörpers
     * 
     */
    @XmlElement(name = "SVOBERKOERPER", required = true)
    protected SVOBERKOERPER svoberkoerper;
    /**
     * Körperpflege im Bereich des Kopfes
     * 
     */
    @XmlElement(name = "SVKOPF", required = true)
    protected SVKOPF svkopf;
    /**
     * Waschen des Intimbereichs
     * 
     */
    @XmlElement(name = "SVINTIMBEREICH", required = true)
    protected SVINTIMBEREICH svintimbereich;
    /**
     * Duschen oder Baden einschließlich Waschen der Haare
     * 
     */
    @XmlElement(name = "SVDUSCHENBADEN", required = true)
    protected SVDUSCHENBADEN svduschenbaden;
    /**
     * An- und Auskleiden des Oberkörpers
     * 
     */
    @XmlElement(name = "SVANAUSOBERKOERPER", required = true)
    protected SVANAUSOBERKOERPER svanausoberkoerper;
    /**
     * An- und Auskleiden des Unterkörpers
     * 
     */
    @XmlElement(name = "SVANAUSUNTERKOERPER", required = true)
    protected SVANAUSUNTERKOERPER svanausunterkoerper;
    /**
     * Mundgerechtes Zubereiten der Nahrung, Eingießen von Getränken
     * 
     */
    @XmlElement(name = "SVNAHRUNGZUBEREITEN", required = true)
    protected SVNAHRUNGZUBEREITEN svnahrungzubereiten;
    /**
     * Essen
     * 
     */
    @XmlElement(name = "SVESSEN", required = true)
    protected SVESSEN svessen;
    /**
     * Trinken
     * 
     */
    @XmlElement(name = "SVTRINKEN", required = true)
    protected SVTRINKEN svtrinken;
    /**
     * Benutzen einer Toilette oder eines Toilettenstuhls
     * 
     */
    @XmlElement(name = "SVTOILETTE", required = true)
    protected SVTOILETTE svtoilette;
    /**
     * Bewältigung der Folgen einer Harninkontinenz (auch Umgang mit Dauerkatheter/Urostoma)
     * 
     */
    @XmlElement(name = "SVHARNKONTINENZBEW", required = true)
    protected SVHARNKONTINENZBEW svharnkontinenzbew;
    /**
     * Bewältigung der Folgen einer Stuhlinkontinenz (auch Umgang mit Stoma)
     * 
     */
    @XmlElement(name = "SVSTUHLKONTINENZBEW", required = true)
    protected SVSTUHLKONTINENZBEW svstuhlkontinenzbew;
    /**
     * Tagesablauf gestalten und an Veränderungen anpassen
     * 
     */
    @XmlElement(name = "GATAGESABLAUF", required = true)
    protected GATAGESABLAUF gatagesablauf;
    /**
     * Ruhen und Schlafen
     * 
     */
    @XmlElement(name = "GARUHENSCHLAFEN", required = true)
    protected GARUHENSCHLAFEN garuhenschlafen;
    /**
     * Sich beschäftigen
     * 
     */
    @XmlElement(name = "GABESCHAEFTIGEN", required = true)
    protected GABESCHAEFTIGEN gabeschaeftigen;
    /**
     * In die Zukunft gerichtete Planungen vornehmen
     * 
     */
    @XmlElement(name = "GAPLANUNGEN", required = true)
    protected GAPLANUNGEN gaplanungen;
    /**
     * Interaktion mit Personen im direkten Kontakt
     * 
     */
    @XmlElement(name = "GAINTERAKTION", required = true)
    protected GAINTERAKTION gainteraktion;
    /**
     * Kontaktpflege zu Personen außerhalb des direkten Umfeldes
     * 
     */
    @XmlElement(name = "GAKONTAKTPFLEGE", required = true)
    protected GAKONTAKTPFLEGE gakontaktpflege;
    /**
     * Hatte die Bewohnerin bzw. der Bewohner in der Zeit seit der letzten Ergebniserfassung einen Dekubitus?
     * 
     */
    @XmlElement(name = "DEKUBITUS", required = true)
    protected DEKUBITUS dekubitus;
    /**
     * Maximales Dekubitusstadium im Erhebungszeitraum
     * 
     */
    @XmlElement(name = "DEKUBITUSSTADIUM", required = true)
    protected DEKUBITUSSTADIUM dekubitusstadium;
    /**
     * Datum: Beginn Dekubitus 1
     * 
     */
    @XmlElement(name = "DEKUBITUS1BEGINNDATUM", required = true)
    protected DEKUBITUS1BEGINNDATUM dekubitus1BEGINNDATUM;
    /**
     * Datum: Ende Dekubitus 1 (ggf. bis heute)
     * 
     */
    @XmlElement(name = "DEKUBITUS1ENDEDATUM", required = true)
    protected DEKUBITUS1ENDEDATUM dekubitus1ENDEDATUM;
    /**
     * Wo ist der Dekubitus 1 entstanden?
     * 
     */
    @XmlElement(name = "DEKUBITUS1LOK", required = true)
    protected DEKUBITUS1LOK dekubitus1LOK;
    /**
     * Datum: Beginn Dekubitus 2
     * 
     */
    @XmlElement(name = "DEKUBITUS2BEGINNDATUM", required = true)
    protected DEKUBITUS2BEGINNDATUM dekubitus2BEGINNDATUM;
    /**
     * Datum: Ende Dekubitus 2 (ggf. bis heute)
     * 
     */
    @XmlElement(name = "DEKUBITUS2ENDEDATUM", required = true)
    protected DEKUBITUS2ENDEDATUM dekubitus2ENDEDATUM;
    /**
     * Wo ist der Dekubitus 2 entstanden?
     * 
     */
    @XmlElement(name = "DEKUBITUS2LOK", required = true)
    protected DEKUBITUS2LOK dekubitus2LOK;
    /**
     * Aktuelles Körpergewicht in kg
     * 
     */
    @XmlElement(name = "KOERPERGEWICHT", required = true)
    protected KOERPERGEWICHT koerpergewicht;
    /**
     * Datum: Dokumentation des Körpergewichts
     * 
     */
    @XmlElement(name = "KOERPERGEWICHTDATUM", required = true)
    protected KOERPERGEWICHTDATUM koerpergewichtdatum;
    /**
     * Welche der aufgeführten Punkte trafen laut Pflegedokumentation für die Bewohnerin bzw. den Bewohner seit der letzten Ergebniserfassung zu?
     * 
     */
    @XmlElement(name = "KOERPERGEWICHTDOKU", required = true)
    protected List<KOERPERGEWICHTDOKU> koerpergewichtdoku;
    /**
     * Ist die Bewohnerin bzw. der Bewohner seit der letzten Ergebniserfassung in der Einrichtung gestürzt?
     * 
     */
    @XmlElement(name = "STURZ", required = true)
    protected STURZ sturz;
    /**
     * Welche Sturzfolgen sind aufgetreten?
     * 
     */
    @XmlElement(name = "STURZFOLGEN", required = true)
    protected List<STURZFOLGEN> sturzfolgen;
    /**
     * Wurden bei der Bewohnerin bzw. dem Bewohner in den vergangenen 4 Wochen Gurte angewendet?
     * 
     */
    @XmlElement(name = "GURT", required = true)
    protected GURT gurt;
    /**
     * Wurden bei der Bewohnerin bzw. dem Bewohner in den vergangenen 4 Wochen Bettseitenteile angewendet?
     * 
     */
    @XmlElement(name = "SEITENTEILE", required = true)
    protected SEITENTEILE seitenteile;
    /**
     * Liegen bei der Bewohnerin bzw. dem Bewohner Anzeichen für länger andauernde Schmerzen vor (z.B. Äußerungen der Bewohnerin bzw. des Bewohners oder Einnahme von Analgetika)?
     * 
     */
    @XmlElement(name = "SCHMERZEN", required = true)
    protected SCHMERZEN schmerzen;
    /**
     * Ist die Bewohnerin bzw. der Bewohner durch eine medikamentöse Schmerzbehandlung schmerzfrei?
     * 
     */
    @XmlElement(name = "SCHMERZFREI", required = true)
    protected SCHMERZFREI schmerzfrei;
    /**
     * Wurde bei der Bewohnerin bzw. dem Bewohner eine differenzierte Schmerzeinschätzung vorgenommen?
     * 
     */
    @XmlElement(name = "SCHMERZEINSCH", required = true)
    protected SCHMERZEINSCH schmerzeinsch;
    /**
     * Datum: Dokumentation der Schmerzeinschätzung
     * 
     */
    @XmlElement(name = "SCHMERZEINSCHDATUM", required = true)
    protected SCHMERZEINSCHDATUM schmerzeinschdatum;
    /**
     * Welche Informationen liegen über die Ergebnisse dieser Schmerzeinschätzung vor?
     * 
     */
    @XmlElement(name = "SCHMERZEINSCHINFO", required = true)
    protected List<SCHMERZEINSCHINFO> schmerzeinschinfo;
    /**
     * Ist die Bewohnerin bzw. der Bewohner nach der letzten Ergebniserfassung neu in die Einrichtung eingezogen?
     * 
     */
    @XmlElement(name = "NEUEINZUG", required = true)
    protected NEUEINZUG neueinzug;
    /**
     * Erfolgte der Einzug direkt im Anschluss an einen Kurzzeit- bzw. Verhinderungspflegeaufenthalt in der Einrichtung (ohne zeitliche Lücke)?
     * 
     */
    @XmlElement(name = "EINZUGNACHKZP", required = true)
    protected EINZUGNACHKZP einzugnachkzp;
    /**
     * Datum: Beginn des Kurzzeit- bzw. Verhinderungspflegeaufenthalts
     * 
     */
    @XmlElement(name = "EINZUGNACHKZPDATUM", required = true)
    protected EINZUGNACHKZPDATUM einzugnachkzpdatum;
    /**
     * Ist die Bewohnerin bzw. der Bewohner innerhalb der ersten 8 Wochen nach dem Einzug länger als drei Tage in einem Krankenhaus versorgt worden?
     * 
     */
    @XmlElement(name = "EINZUGKHBEHANDLUNG", required = true)
    protected EINZUGKHBEHANDLUNG einzugkhbehandlung;
    /**
     * Datum: Beginn des Krankenhausaufenthalts direkt nach dem Einzug
     * 
     */
    @XmlElement(name = "EINZUGKHBEGINNDATUM", required = true)
    protected EINZUGKHBEGINNDATUM einzugkhbeginndatum;
    /**
     * Datum: Ende des Krankenhausaufenthalts direkt nach dem Einzug
     * 
     */
    @XmlElement(name = "EINZUGKHENDEDATUM", required = true)
    protected EINZUGKHENDEDATUM einzugkhendedatum;
    /**
     * Ist in den Wochen nach dem Einzug mit der Bewohnerin bzw. dem Bewohner und/oder einer ihrer bzw. seiner Angehörigen oder sonstigen Vertrauenspersonen ein Gespräch über ihr bzw. sein Einleben und die zukünftige Versorgung geführt worden?
     * 
     */
    @XmlElement(name = "EINZUGGESPR", required = true)
    protected EINZUGGESPR einzuggespr;
    /**
     * Datum des Integrationsgesprächs
     * 
     */
    @XmlElement(name = "EINZUGGESPRDATUM", required = true)
    protected EINZUGGESPRDATUM einzuggesprdatum;
    /**
     * Wer hat an dem Integrationsgespräch teilgenommen?
     * 
     */
    @XmlElement(name = "EINZUGGESPRTEILNEHMER", required = true)
    protected List<EINZUGGESPRTEILNEHMER> einzuggesprteilnehmer;
    /**
     * Wurden die Ergebnisse dieses Gespräches dokumentiert?
     * 
     */
    @XmlElement(name = "EINZUGGESPRDOKU", required = true)
    protected EINZUGGESPRDOKU einzuggesprdoku;

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
     * Ist ein Pflegegrad vorhanden?
     * 
     * @return
     *     possible object is
     *     {@link PFLEGEGRAD }
     *     
     */
    public PFLEGEGRAD getPFLEGEGRAD() {
        return pflegegrad;
    }

    /**
     * Legt den Wert der pflegegrad-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PFLEGEGRAD }
     *     
     * @see #getPFLEGEGRAD()
     */
    public void setPFLEGEGRAD(PFLEGEGRAD value) {
        this.pflegegrad = value;
    }

    /**
     * Ist es bei der Bewohnerin bzw. dem Bewohner seit der letzten Ergebniserfassung zu einem Apoplex gekommen?
     * 
     * @return
     *     possible object is
     *     {@link APOPLEX }
     *     
     */
    public APOPLEX getAPOPLEX() {
        return apoplex;
    }

    /**
     * Legt den Wert der apoplex-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link APOPLEX }
     *     
     * @see #getAPOPLEX()
     */
    public void setAPOPLEX(APOPLEX value) {
        this.apoplex = value;
    }

    /**
     * Datum des Apoplex
     * 
     * @return
     *     possible object is
     *     {@link APOPLEXDATUM }
     *     
     */
    public APOPLEXDATUM getAPOPLEXDATUM() {
        return apoplexdatum;
    }

    /**
     * Legt den Wert der apoplexdatum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link APOPLEXDATUM }
     *     
     * @see #getAPOPLEXDATUM()
     */
    public void setAPOPLEXDATUM(APOPLEXDATUM value) {
        this.apoplexdatum = value;
    }

    /**
     * Ist es bei der Bewohnerin bzw. dem Bewohner seit der letzten Ergebniserfassung zu einer Fraktur gekommen?
     * 
     * @return
     *     possible object is
     *     {@link FRAKTUR }
     *     
     */
    public FRAKTUR getFRAKTUR() {
        return fraktur;
    }

    /**
     * Legt den Wert der fraktur-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FRAKTUR }
     *     
     * @see #getFRAKTUR()
     */
    public void setFRAKTUR(FRAKTUR value) {
        this.fraktur = value;
    }

    /**
     * Datum der Fraktur
     * 
     * @return
     *     possible object is
     *     {@link FRAKTURDATUM }
     *     
     */
    public FRAKTURDATUM getFRAKTURDATUM() {
        return frakturdatum;
    }

    /**
     * Legt den Wert der frakturdatum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FRAKTURDATUM }
     *     
     * @see #getFRAKTURDATUM()
     */
    public void setFRAKTURDATUM(FRAKTURDATUM value) {
        this.frakturdatum = value;
    }

    /**
     * Ist es bei der Bewohnerin bzw. dem Bewohner seit der letzten Ergebniserfassung zu einem Herzinfarkt gekommen?
     * 
     * @return
     *     possible object is
     *     {@link HERZINFARKT }
     *     
     */
    public HERZINFARKT getHERZINFARKT() {
        return herzinfarkt;
    }

    /**
     * Legt den Wert der herzinfarkt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link HERZINFARKT }
     *     
     * @see #getHERZINFARKT()
     */
    public void setHERZINFARKT(HERZINFARKT value) {
        this.herzinfarkt = value;
    }

    /**
     * Datum des Herzinfarkts
     * 
     * @return
     *     possible object is
     *     {@link HERZINFARKTDATUM }
     *     
     */
    public HERZINFARKTDATUM getHERZINFARKTDATUM() {
        return herzinfarktdatum;
    }

    /**
     * Legt den Wert der herzinfarktdatum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link HERZINFARKTDATUM }
     *     
     * @see #getHERZINFARKTDATUM()
     */
    public void setHERZINFARKTDATUM(HERZINFARKTDATUM value) {
        this.herzinfarktdatum = value;
    }

    /**
     * Ist es bei der Bewohnerin bzw. dem Bewohner seit der letzten Ergebniserfassung zu einer Amputation gekommen?
     * 
     * @return
     *     possible object is
     *     {@link AMPUTATION }
     *     
     */
    public AMPUTATION getAMPUTATION() {
        return amputation;
    }

    /**
     * Legt den Wert der amputation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AMPUTATION }
     *     
     * @see #getAMPUTATION()
     */
    public void setAMPUTATION(AMPUTATION value) {
        this.amputation = value;
    }

    /**
     * Datum der Amputation
     * 
     * @return
     *     possible object is
     *     {@link AMPUTATIONDATUM }
     *     
     */
    public AMPUTATIONDATUM getAMPUTATIONDATUM() {
        return amputationdatum;
    }

    /**
     * Legt den Wert der amputationdatum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AMPUTATIONDATUM }
     *     
     * @see #getAMPUTATIONDATUM()
     */
    public void setAMPUTATIONDATUM(AMPUTATIONDATUM value) {
        this.amputationdatum = value;
    }

    /**
     * Wurde die Bewohnerin bzw. der Bewohner seit der letzten Ergebniserfassung in einem Krankenhaus behandelt?
     * 
     * @return
     *     possible object is
     *     {@link KHBEHANDLUNG }
     *     
     */
    public KHBEHANDLUNG getKHBEHANDLUNG() {
        return khbehandlung;
    }

    /**
     * Legt den Wert der khbehandlung-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KHBEHANDLUNG }
     *     
     * @see #getKHBEHANDLUNG()
     */
    public void setKHBEHANDLUNG(KHBEHANDLUNG value) {
        this.khbehandlung = value;
    }

    /**
     * Datum: Beginn des Krankenhausaufenthalts (bei mehreren Aufenthalten bitte den Aufenthalt mit der längsten Dauer wählen)
     * 
     * @return
     *     possible object is
     *     {@link KHBEGINNDATUM }
     *     
     */
    public KHBEGINNDATUM getKHBEGINNDATUM() {
        return khbeginndatum;
    }

    /**
     * Legt den Wert der khbeginndatum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KHBEGINNDATUM }
     *     
     * @see #getKHBEGINNDATUM()
     */
    public void setKHBEGINNDATUM(KHBEGINNDATUM value) {
        this.khbeginndatum = value;
    }

    /**
     * Datum: Ende des Krankenhausaufenthalts (bei mehreren Aufenthalten bitte den Aufenthalt mit der längsten Dauer wählen)
     * 
     * @return
     *     possible object is
     *     {@link KHENDEDATUM }
     *     
     */
    public KHENDEDATUM getKHENDEDATUM() {
        return khendedatum;
    }

    /**
     * Legt den Wert der khendedatum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KHENDEDATUM }
     *     
     * @see #getKHENDEDATUM()
     */
    public void setKHENDEDATUM(KHENDEDATUM value) {
        this.khendedatum = value;
    }

    /**
     * Bewusstseinszustand der Bewohnerin bzw. des Bewohners
     * 
     * @return
     *     possible object is
     *     {@link BEWUSSTSEINSZUSTAND }
     *     
     */
    public BEWUSSTSEINSZUSTAND getBEWUSSTSEINSZUSTAND() {
        return bewusstseinszustand;
    }

    /**
     * Legt den Wert der bewusstseinszustand-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BEWUSSTSEINSZUSTAND }
     *     
     * @see #getBEWUSSTSEINSZUSTAND()
     */
    public void setBEWUSSTSEINSZUSTAND(BEWUSSTSEINSZUSTAND value) {
        this.bewusstseinszustand = value;
    }

    /**
     * Ärztliche Diagnosen für die Bewohnerin bzw. den Bewohner
     * 
     * Gets the value of the diagnosen property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the diagnosen property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getDIAGNOSEN().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DIAGNOSEN }
     * </p>
     * 
     * 
     * @return
     *     The value of the diagnosen property.
     */
    public List<DIAGNOSEN> getDIAGNOSEN() {
        if (diagnosen == null) {
            diagnosen = new ArrayList<>();
        }
        return this.diagnosen;
    }

    /**
     * Positionswechsel im Bett
     * 
     * @return
     *     possible object is
     *     {@link MOBILPOSWECHSEL }
     *     
     */
    public MOBILPOSWECHSEL getMOBILPOSWECHSEL() {
        return mobilposwechsel;
    }

    /**
     * Legt den Wert der mobilposwechsel-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MOBILPOSWECHSEL }
     *     
     * @see #getMOBILPOSWECHSEL()
     */
    public void setMOBILPOSWECHSEL(MOBILPOSWECHSEL value) {
        this.mobilposwechsel = value;
    }

    /**
     * Halten einer stabilen Sitzposition
     * 
     * @return
     *     possible object is
     *     {@link MOBILSITZPOSITION }
     *     
     */
    public MOBILSITZPOSITION getMOBILSITZPOSITION() {
        return mobilsitzposition;
    }

    /**
     * Legt den Wert der mobilsitzposition-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MOBILSITZPOSITION }
     *     
     * @see #getMOBILSITZPOSITION()
     */
    public void setMOBILSITZPOSITION(MOBILSITZPOSITION value) {
        this.mobilsitzposition = value;
    }

    /**
     * Sich Umsetzen
     * 
     * @return
     *     possible object is
     *     {@link MOBILUMSETZEN }
     *     
     */
    public MOBILUMSETZEN getMOBILUMSETZEN() {
        return mobilumsetzen;
    }

    /**
     * Legt den Wert der mobilumsetzen-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MOBILUMSETZEN }
     *     
     * @see #getMOBILUMSETZEN()
     */
    public void setMOBILUMSETZEN(MOBILUMSETZEN value) {
        this.mobilumsetzen = value;
    }

    /**
     * Fortbewegen innerhalb des Wohnbereichs
     * 
     * @return
     *     possible object is
     *     {@link MOBILFORTBEWEGUNG }
     *     
     */
    public MOBILFORTBEWEGUNG getMOBILFORTBEWEGUNG() {
        return mobilfortbewegung;
    }

    /**
     * Legt den Wert der mobilfortbewegung-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MOBILFORTBEWEGUNG }
     *     
     * @see #getMOBILFORTBEWEGUNG()
     */
    public void setMOBILFORTBEWEGUNG(MOBILFORTBEWEGUNG value) {
        this.mobilfortbewegung = value;
    }

    /**
     * Treppensteigen
     * 
     * @return
     *     possible object is
     *     {@link MOBILTREPPENSTEIGEN }
     *     
     */
    public MOBILTREPPENSTEIGEN getMOBILTREPPENSTEIGEN() {
        return mobiltreppensteigen;
    }

    /**
     * Legt den Wert der mobiltreppensteigen-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MOBILTREPPENSTEIGEN }
     *     
     * @see #getMOBILTREPPENSTEIGEN()
     */
    public void setMOBILTREPPENSTEIGEN(MOBILTREPPENSTEIGEN value) {
        this.mobiltreppensteigen = value;
    }

    /**
     * Erkennen von Personen aus dem näheren Umfeld
     * 
     * @return
     *     possible object is
     *     {@link KKFERKENNEN }
     *     
     */
    public KKFERKENNEN getKKFERKENNEN() {
        return kkferkennen;
    }

    /**
     * Legt den Wert der kkferkennen-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KKFERKENNEN }
     *     
     * @see #getKKFERKENNEN()
     */
    public void setKKFERKENNEN(KKFERKENNEN value) {
        this.kkferkennen = value;
    }

    /**
     * Örtliche Orientierung
     * 
     * @return
     *     possible object is
     *     {@link KKFORIENTOERTLICH }
     *     
     */
    public KKFORIENTOERTLICH getKKFORIENTOERTLICH() {
        return kkforientoertlich;
    }

    /**
     * Legt den Wert der kkforientoertlich-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KKFORIENTOERTLICH }
     *     
     * @see #getKKFORIENTOERTLICH()
     */
    public void setKKFORIENTOERTLICH(KKFORIENTOERTLICH value) {
        this.kkforientoertlich = value;
    }

    /**
     * Zeitliche Orientierung
     * 
     * @return
     *     possible object is
     *     {@link KKFORIENTZEITLICH }
     *     
     */
    public KKFORIENTZEITLICH getKKFORIENTZEITLICH() {
        return kkforientzeitlich;
    }

    /**
     * Legt den Wert der kkforientzeitlich-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KKFORIENTZEITLICH }
     *     
     * @see #getKKFORIENTZEITLICH()
     */
    public void setKKFORIENTZEITLICH(KKFORIENTZEITLICH value) {
        this.kkforientzeitlich = value;
    }

    /**
     * Sich Erinnern
     * 
     * @return
     *     possible object is
     *     {@link KKFERINNERN }
     *     
     */
    public KKFERINNERN getKKFERINNERN() {
        return kkferinnern;
    }

    /**
     * Legt den Wert der kkferinnern-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KKFERINNERN }
     *     
     * @see #getKKFERINNERN()
     */
    public void setKKFERINNERN(KKFERINNERN value) {
        this.kkferinnern = value;
    }

    /**
     * Steuern von mehrschrittigen Alltagshandlungen
     * 
     * @return
     *     possible object is
     *     {@link KKFHANDLUNGEN }
     *     
     */
    public KKFHANDLUNGEN getKKFHANDLUNGEN() {
        return kkfhandlungen;
    }

    /**
     * Legt den Wert der kkfhandlungen-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KKFHANDLUNGEN }
     *     
     * @see #getKKFHANDLUNGEN()
     */
    public void setKKFHANDLUNGEN(KKFHANDLUNGEN value) {
        this.kkfhandlungen = value;
    }

    /**
     * Treffen von Entscheidungen im Alltagsleben
     * 
     * @return
     *     possible object is
     *     {@link KKFENTSCHEIDUNGEN }
     *     
     */
    public KKFENTSCHEIDUNGEN getKKFENTSCHEIDUNGEN() {
        return kkfentscheidungen;
    }

    /**
     * Legt den Wert der kkfentscheidungen-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KKFENTSCHEIDUNGEN }
     *     
     * @see #getKKFENTSCHEIDUNGEN()
     */
    public void setKKFENTSCHEIDUNGEN(KKFENTSCHEIDUNGEN value) {
        this.kkfentscheidungen = value;
    }

    /**
     * Verstehen von Sachverhalten und Informationen
     * 
     * @return
     *     possible object is
     *     {@link KKFVERSTEHENINFO }
     *     
     */
    public KKFVERSTEHENINFO getKKFVERSTEHENINFO() {
        return kkfversteheninfo;
    }

    /**
     * Legt den Wert der kkfversteheninfo-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KKFVERSTEHENINFO }
     *     
     * @see #getKKFVERSTEHENINFO()
     */
    public void setKKFVERSTEHENINFO(KKFVERSTEHENINFO value) {
        this.kkfversteheninfo = value;
    }

    /**
     * Erkennen von Risiken und Gefahren
     * 
     * @return
     *     possible object is
     *     {@link KKFGEFAHRERKENNEN }
     *     
     */
    public KKFGEFAHRERKENNEN getKKFGEFAHRERKENNEN() {
        return kkfgefahrerkennen;
    }

    /**
     * Legt den Wert der kkfgefahrerkennen-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KKFGEFAHRERKENNEN }
     *     
     * @see #getKKFGEFAHRERKENNEN()
     */
    public void setKKFGEFAHRERKENNEN(KKFGEFAHRERKENNEN value) {
        this.kkfgefahrerkennen = value;
    }

    /**
     * Mitteilen von elementaren Bedürfnissen
     * 
     * @return
     *     possible object is
     *     {@link KKFMITTEILEN }
     *     
     */
    public KKFMITTEILEN getKKFMITTEILEN() {
        return kkfmitteilen;
    }

    /**
     * Legt den Wert der kkfmitteilen-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KKFMITTEILEN }
     *     
     * @see #getKKFMITTEILEN()
     */
    public void setKKFMITTEILEN(KKFMITTEILEN value) {
        this.kkfmitteilen = value;
    }

    /**
     * Verstehen von Aufforderungen
     * 
     * @return
     *     possible object is
     *     {@link KKFVERSTEHENAUF }
     *     
     */
    public KKFVERSTEHENAUF getKKFVERSTEHENAUF() {
        return kkfverstehenauf;
    }

    /**
     * Legt den Wert der kkfverstehenauf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KKFVERSTEHENAUF }
     *     
     * @see #getKKFVERSTEHENAUF()
     */
    public void setKKFVERSTEHENAUF(KKFVERSTEHENAUF value) {
        this.kkfverstehenauf = value;
    }

    /**
     * Beteiligung an einem Gespräch
     * 
     * @return
     *     possible object is
     *     {@link KKFBETEILIGUNG }
     *     
     */
    public KKFBETEILIGUNG getKKFBETEILIGUNG() {
        return kkfbeteiligung;
    }

    /**
     * Legt den Wert der kkfbeteiligung-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KKFBETEILIGUNG }
     *     
     * @see #getKKFBETEILIGUNG()
     */
    public void setKKFBETEILIGUNG(KKFBETEILIGUNG value) {
        this.kkfbeteiligung = value;
    }

    /**
     * Erfolgt die Ernährung der Bewohnerin bzw. des Bewohners parenteral oder über eine Sonde?
     * 
     * @return
     *     possible object is
     *     {@link SVERNAEHRUNG }
     *     
     */
    public SVERNAEHRUNG getSVERNAEHRUNG() {
        return svernaehrung;
    }

    /**
     * Legt den Wert der svernaehrung-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SVERNAEHRUNG }
     *     
     * @see #getSVERNAEHRUNG()
     */
    public void setSVERNAEHRUNG(SVERNAEHRUNG value) {
        this.svernaehrung = value;
    }

    /**
     * Erfolgt die Bedienung selbständig oder mit Fremdhilfe?
     * 
     * @return
     *     possible object is
     *     {@link SVFREMDHILFE }
     *     
     */
    public SVFREMDHILFE getSVFREMDHILFE() {
        return svfremdhilfe;
    }

    /**
     * Legt den Wert der svfremdhilfe-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SVFREMDHILFE }
     *     
     * @see #getSVFREMDHILFE()
     */
    public void setSVFREMDHILFE(SVFREMDHILFE value) {
        this.svfremdhilfe = value;
    }

    /**
     * In welchem Umfang erfolgt eine künstliche Ernährung?
     * 
     * @return
     *     possible object is
     *     {@link SVERNAEHRUNGUMFANG }
     *     
     */
    public SVERNAEHRUNGUMFANG getSVERNAEHRUNGUMFANG() {
        return svernaehrungumfang;
    }

    /**
     * Legt den Wert der svernaehrungumfang-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SVERNAEHRUNGUMFANG }
     *     
     * @see #getSVERNAEHRUNGUMFANG()
     */
    public void setSVERNAEHRUNGUMFANG(SVERNAEHRUNGUMFANG value) {
        this.svernaehrungumfang = value;
    }

    /**
     * Blasenkontrolle/Harnkontinenz
     * 
     * @return
     *     possible object is
     *     {@link SVHARNKONTINENZ }
     *     
     */
    public SVHARNKONTINENZ getSVHARNKONTINENZ() {
        return svharnkontinenz;
    }

    /**
     * Legt den Wert der svharnkontinenz-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SVHARNKONTINENZ }
     *     
     * @see #getSVHARNKONTINENZ()
     */
    public void setSVHARNKONTINENZ(SVHARNKONTINENZ value) {
        this.svharnkontinenz = value;
    }

    /**
     * Darmkontrolle/Stuhlkontinenz
     * 
     * @return
     *     possible object is
     *     {@link SVSTUHLKONTINENZ }
     *     
     */
    public SVSTUHLKONTINENZ getSVSTUHLKONTINENZ() {
        return svstuhlkontinenz;
    }

    /**
     * Legt den Wert der svstuhlkontinenz-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SVSTUHLKONTINENZ }
     *     
     * @see #getSVSTUHLKONTINENZ()
     */
    public void setSVSTUHLKONTINENZ(SVSTUHLKONTINENZ value) {
        this.svstuhlkontinenz = value;
    }

    /**
     * Waschen des vorderen Oberkörpers
     * 
     * @return
     *     possible object is
     *     {@link SVOBERKOERPER }
     *     
     */
    public SVOBERKOERPER getSVOBERKOERPER() {
        return svoberkoerper;
    }

    /**
     * Legt den Wert der svoberkoerper-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SVOBERKOERPER }
     *     
     * @see #getSVOBERKOERPER()
     */
    public void setSVOBERKOERPER(SVOBERKOERPER value) {
        this.svoberkoerper = value;
    }

    /**
     * Körperpflege im Bereich des Kopfes
     * 
     * @return
     *     possible object is
     *     {@link SVKOPF }
     *     
     */
    public SVKOPF getSVKOPF() {
        return svkopf;
    }

    /**
     * Legt den Wert der svkopf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SVKOPF }
     *     
     * @see #getSVKOPF()
     */
    public void setSVKOPF(SVKOPF value) {
        this.svkopf = value;
    }

    /**
     * Waschen des Intimbereichs
     * 
     * @return
     *     possible object is
     *     {@link SVINTIMBEREICH }
     *     
     */
    public SVINTIMBEREICH getSVINTIMBEREICH() {
        return svintimbereich;
    }

    /**
     * Legt den Wert der svintimbereich-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SVINTIMBEREICH }
     *     
     * @see #getSVINTIMBEREICH()
     */
    public void setSVINTIMBEREICH(SVINTIMBEREICH value) {
        this.svintimbereich = value;
    }

    /**
     * Duschen oder Baden einschließlich Waschen der Haare
     * 
     * @return
     *     possible object is
     *     {@link SVDUSCHENBADEN }
     *     
     */
    public SVDUSCHENBADEN getSVDUSCHENBADEN() {
        return svduschenbaden;
    }

    /**
     * Legt den Wert der svduschenbaden-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SVDUSCHENBADEN }
     *     
     * @see #getSVDUSCHENBADEN()
     */
    public void setSVDUSCHENBADEN(SVDUSCHENBADEN value) {
        this.svduschenbaden = value;
    }

    /**
     * An- und Auskleiden des Oberkörpers
     * 
     * @return
     *     possible object is
     *     {@link SVANAUSOBERKOERPER }
     *     
     */
    public SVANAUSOBERKOERPER getSVANAUSOBERKOERPER() {
        return svanausoberkoerper;
    }

    /**
     * Legt den Wert der svanausoberkoerper-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SVANAUSOBERKOERPER }
     *     
     * @see #getSVANAUSOBERKOERPER()
     */
    public void setSVANAUSOBERKOERPER(SVANAUSOBERKOERPER value) {
        this.svanausoberkoerper = value;
    }

    /**
     * An- und Auskleiden des Unterkörpers
     * 
     * @return
     *     possible object is
     *     {@link SVANAUSUNTERKOERPER }
     *     
     */
    public SVANAUSUNTERKOERPER getSVANAUSUNTERKOERPER() {
        return svanausunterkoerper;
    }

    /**
     * Legt den Wert der svanausunterkoerper-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SVANAUSUNTERKOERPER }
     *     
     * @see #getSVANAUSUNTERKOERPER()
     */
    public void setSVANAUSUNTERKOERPER(SVANAUSUNTERKOERPER value) {
        this.svanausunterkoerper = value;
    }

    /**
     * Mundgerechtes Zubereiten der Nahrung, Eingießen von Getränken
     * 
     * @return
     *     possible object is
     *     {@link SVNAHRUNGZUBEREITEN }
     *     
     */
    public SVNAHRUNGZUBEREITEN getSVNAHRUNGZUBEREITEN() {
        return svnahrungzubereiten;
    }

    /**
     * Legt den Wert der svnahrungzubereiten-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SVNAHRUNGZUBEREITEN }
     *     
     * @see #getSVNAHRUNGZUBEREITEN()
     */
    public void setSVNAHRUNGZUBEREITEN(SVNAHRUNGZUBEREITEN value) {
        this.svnahrungzubereiten = value;
    }

    /**
     * Essen
     * 
     * @return
     *     possible object is
     *     {@link SVESSEN }
     *     
     */
    public SVESSEN getSVESSEN() {
        return svessen;
    }

    /**
     * Legt den Wert der svessen-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SVESSEN }
     *     
     * @see #getSVESSEN()
     */
    public void setSVESSEN(SVESSEN value) {
        this.svessen = value;
    }

    /**
     * Trinken
     * 
     * @return
     *     possible object is
     *     {@link SVTRINKEN }
     *     
     */
    public SVTRINKEN getSVTRINKEN() {
        return svtrinken;
    }

    /**
     * Legt den Wert der svtrinken-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SVTRINKEN }
     *     
     * @see #getSVTRINKEN()
     */
    public void setSVTRINKEN(SVTRINKEN value) {
        this.svtrinken = value;
    }

    /**
     * Benutzen einer Toilette oder eines Toilettenstuhls
     * 
     * @return
     *     possible object is
     *     {@link SVTOILETTE }
     *     
     */
    public SVTOILETTE getSVTOILETTE() {
        return svtoilette;
    }

    /**
     * Legt den Wert der svtoilette-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SVTOILETTE }
     *     
     * @see #getSVTOILETTE()
     */
    public void setSVTOILETTE(SVTOILETTE value) {
        this.svtoilette = value;
    }

    /**
     * Bewältigung der Folgen einer Harninkontinenz (auch Umgang mit Dauerkatheter/Urostoma)
     * 
     * @return
     *     possible object is
     *     {@link SVHARNKONTINENZBEW }
     *     
     */
    public SVHARNKONTINENZBEW getSVHARNKONTINENZBEW() {
        return svharnkontinenzbew;
    }

    /**
     * Legt den Wert der svharnkontinenzbew-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SVHARNKONTINENZBEW }
     *     
     * @see #getSVHARNKONTINENZBEW()
     */
    public void setSVHARNKONTINENZBEW(SVHARNKONTINENZBEW value) {
        this.svharnkontinenzbew = value;
    }

    /**
     * Bewältigung der Folgen einer Stuhlinkontinenz (auch Umgang mit Stoma)
     * 
     * @return
     *     possible object is
     *     {@link SVSTUHLKONTINENZBEW }
     *     
     */
    public SVSTUHLKONTINENZBEW getSVSTUHLKONTINENZBEW() {
        return svstuhlkontinenzbew;
    }

    /**
     * Legt den Wert der svstuhlkontinenzbew-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SVSTUHLKONTINENZBEW }
     *     
     * @see #getSVSTUHLKONTINENZBEW()
     */
    public void setSVSTUHLKONTINENZBEW(SVSTUHLKONTINENZBEW value) {
        this.svstuhlkontinenzbew = value;
    }

    /**
     * Tagesablauf gestalten und an Veränderungen anpassen
     * 
     * @return
     *     possible object is
     *     {@link GATAGESABLAUF }
     *     
     */
    public GATAGESABLAUF getGATAGESABLAUF() {
        return gatagesablauf;
    }

    /**
     * Legt den Wert der gatagesablauf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GATAGESABLAUF }
     *     
     * @see #getGATAGESABLAUF()
     */
    public void setGATAGESABLAUF(GATAGESABLAUF value) {
        this.gatagesablauf = value;
    }

    /**
     * Ruhen und Schlafen
     * 
     * @return
     *     possible object is
     *     {@link GARUHENSCHLAFEN }
     *     
     */
    public GARUHENSCHLAFEN getGARUHENSCHLAFEN() {
        return garuhenschlafen;
    }

    /**
     * Legt den Wert der garuhenschlafen-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GARUHENSCHLAFEN }
     *     
     * @see #getGARUHENSCHLAFEN()
     */
    public void setGARUHENSCHLAFEN(GARUHENSCHLAFEN value) {
        this.garuhenschlafen = value;
    }

    /**
     * Sich beschäftigen
     * 
     * @return
     *     possible object is
     *     {@link GABESCHAEFTIGEN }
     *     
     */
    public GABESCHAEFTIGEN getGABESCHAEFTIGEN() {
        return gabeschaeftigen;
    }

    /**
     * Legt den Wert der gabeschaeftigen-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GABESCHAEFTIGEN }
     *     
     * @see #getGABESCHAEFTIGEN()
     */
    public void setGABESCHAEFTIGEN(GABESCHAEFTIGEN value) {
        this.gabeschaeftigen = value;
    }

    /**
     * In die Zukunft gerichtete Planungen vornehmen
     * 
     * @return
     *     possible object is
     *     {@link GAPLANUNGEN }
     *     
     */
    public GAPLANUNGEN getGAPLANUNGEN() {
        return gaplanungen;
    }

    /**
     * Legt den Wert der gaplanungen-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GAPLANUNGEN }
     *     
     * @see #getGAPLANUNGEN()
     */
    public void setGAPLANUNGEN(GAPLANUNGEN value) {
        this.gaplanungen = value;
    }

    /**
     * Interaktion mit Personen im direkten Kontakt
     * 
     * @return
     *     possible object is
     *     {@link GAINTERAKTION }
     *     
     */
    public GAINTERAKTION getGAINTERAKTION() {
        return gainteraktion;
    }

    /**
     * Legt den Wert der gainteraktion-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GAINTERAKTION }
     *     
     * @see #getGAINTERAKTION()
     */
    public void setGAINTERAKTION(GAINTERAKTION value) {
        this.gainteraktion = value;
    }

    /**
     * Kontaktpflege zu Personen außerhalb des direkten Umfeldes
     * 
     * @return
     *     possible object is
     *     {@link GAKONTAKTPFLEGE }
     *     
     */
    public GAKONTAKTPFLEGE getGAKONTAKTPFLEGE() {
        return gakontaktpflege;
    }

    /**
     * Legt den Wert der gakontaktpflege-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GAKONTAKTPFLEGE }
     *     
     * @see #getGAKONTAKTPFLEGE()
     */
    public void setGAKONTAKTPFLEGE(GAKONTAKTPFLEGE value) {
        this.gakontaktpflege = value;
    }

    /**
     * Hatte die Bewohnerin bzw. der Bewohner in der Zeit seit der letzten Ergebniserfassung einen Dekubitus?
     * 
     * @return
     *     possible object is
     *     {@link DEKUBITUS }
     *     
     */
    public DEKUBITUS getDEKUBITUS() {
        return dekubitus;
    }

    /**
     * Legt den Wert der dekubitus-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DEKUBITUS }
     *     
     * @see #getDEKUBITUS()
     */
    public void setDEKUBITUS(DEKUBITUS value) {
        this.dekubitus = value;
    }

    /**
     * Maximales Dekubitusstadium im Erhebungszeitraum
     * 
     * @return
     *     possible object is
     *     {@link DEKUBITUSSTADIUM }
     *     
     */
    public DEKUBITUSSTADIUM getDEKUBITUSSTADIUM() {
        return dekubitusstadium;
    }

    /**
     * Legt den Wert der dekubitusstadium-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DEKUBITUSSTADIUM }
     *     
     * @see #getDEKUBITUSSTADIUM()
     */
    public void setDEKUBITUSSTADIUM(DEKUBITUSSTADIUM value) {
        this.dekubitusstadium = value;
    }

    /**
     * Datum: Beginn Dekubitus 1
     * 
     * @return
     *     possible object is
     *     {@link DEKUBITUS1BEGINNDATUM }
     *     
     */
    public DEKUBITUS1BEGINNDATUM getDEKUBITUS1BEGINNDATUM() {
        return dekubitus1BEGINNDATUM;
    }

    /**
     * Legt den Wert der dekubitus1BEGINNDATUM-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DEKUBITUS1BEGINNDATUM }
     *     
     * @see #getDEKUBITUS1BEGINNDATUM()
     */
    public void setDEKUBITUS1BEGINNDATUM(DEKUBITUS1BEGINNDATUM value) {
        this.dekubitus1BEGINNDATUM = value;
    }

    /**
     * Datum: Ende Dekubitus 1 (ggf. bis heute)
     * 
     * @return
     *     possible object is
     *     {@link DEKUBITUS1ENDEDATUM }
     *     
     */
    public DEKUBITUS1ENDEDATUM getDEKUBITUS1ENDEDATUM() {
        return dekubitus1ENDEDATUM;
    }

    /**
     * Legt den Wert der dekubitus1ENDEDATUM-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DEKUBITUS1ENDEDATUM }
     *     
     * @see #getDEKUBITUS1ENDEDATUM()
     */
    public void setDEKUBITUS1ENDEDATUM(DEKUBITUS1ENDEDATUM value) {
        this.dekubitus1ENDEDATUM = value;
    }

    /**
     * Wo ist der Dekubitus 1 entstanden?
     * 
     * @return
     *     possible object is
     *     {@link DEKUBITUS1LOK }
     *     
     */
    public DEKUBITUS1LOK getDEKUBITUS1LOK() {
        return dekubitus1LOK;
    }

    /**
     * Legt den Wert der dekubitus1LOK-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DEKUBITUS1LOK }
     *     
     * @see #getDEKUBITUS1LOK()
     */
    public void setDEKUBITUS1LOK(DEKUBITUS1LOK value) {
        this.dekubitus1LOK = value;
    }

    /**
     * Datum: Beginn Dekubitus 2
     * 
     * @return
     *     possible object is
     *     {@link DEKUBITUS2BEGINNDATUM }
     *     
     */
    public DEKUBITUS2BEGINNDATUM getDEKUBITUS2BEGINNDATUM() {
        return dekubitus2BEGINNDATUM;
    }

    /**
     * Legt den Wert der dekubitus2BEGINNDATUM-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DEKUBITUS2BEGINNDATUM }
     *     
     * @see #getDEKUBITUS2BEGINNDATUM()
     */
    public void setDEKUBITUS2BEGINNDATUM(DEKUBITUS2BEGINNDATUM value) {
        this.dekubitus2BEGINNDATUM = value;
    }

    /**
     * Datum: Ende Dekubitus 2 (ggf. bis heute)
     * 
     * @return
     *     possible object is
     *     {@link DEKUBITUS2ENDEDATUM }
     *     
     */
    public DEKUBITUS2ENDEDATUM getDEKUBITUS2ENDEDATUM() {
        return dekubitus2ENDEDATUM;
    }

    /**
     * Legt den Wert der dekubitus2ENDEDATUM-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DEKUBITUS2ENDEDATUM }
     *     
     * @see #getDEKUBITUS2ENDEDATUM()
     */
    public void setDEKUBITUS2ENDEDATUM(DEKUBITUS2ENDEDATUM value) {
        this.dekubitus2ENDEDATUM = value;
    }

    /**
     * Wo ist der Dekubitus 2 entstanden?
     * 
     * @return
     *     possible object is
     *     {@link DEKUBITUS2LOK }
     *     
     */
    public DEKUBITUS2LOK getDEKUBITUS2LOK() {
        return dekubitus2LOK;
    }

    /**
     * Legt den Wert der dekubitus2LOK-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DEKUBITUS2LOK }
     *     
     * @see #getDEKUBITUS2LOK()
     */
    public void setDEKUBITUS2LOK(DEKUBITUS2LOK value) {
        this.dekubitus2LOK = value;
    }

    /**
     * Aktuelles Körpergewicht in kg
     * 
     * @return
     *     possible object is
     *     {@link KOERPERGEWICHT }
     *     
     */
    public KOERPERGEWICHT getKOERPERGEWICHT() {
        return koerpergewicht;
    }

    /**
     * Legt den Wert der koerpergewicht-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KOERPERGEWICHT }
     *     
     * @see #getKOERPERGEWICHT()
     */
    public void setKOERPERGEWICHT(KOERPERGEWICHT value) {
        this.koerpergewicht = value;
    }

    /**
     * Datum: Dokumentation des Körpergewichts
     * 
     * @return
     *     possible object is
     *     {@link KOERPERGEWICHTDATUM }
     *     
     */
    public KOERPERGEWICHTDATUM getKOERPERGEWICHTDATUM() {
        return koerpergewichtdatum;
    }

    /**
     * Legt den Wert der koerpergewichtdatum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KOERPERGEWICHTDATUM }
     *     
     * @see #getKOERPERGEWICHTDATUM()
     */
    public void setKOERPERGEWICHTDATUM(KOERPERGEWICHTDATUM value) {
        this.koerpergewichtdatum = value;
    }

    /**
     * Welche der aufgeführten Punkte trafen laut Pflegedokumentation für die Bewohnerin bzw. den Bewohner seit der letzten Ergebniserfassung zu?
     * 
     * Gets the value of the koerpergewichtdoku property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the koerpergewichtdoku property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getKOERPERGEWICHTDOKU().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KOERPERGEWICHTDOKU }
     * </p>
     * 
     * 
     * @return
     *     The value of the koerpergewichtdoku property.
     */
    public List<KOERPERGEWICHTDOKU> getKOERPERGEWICHTDOKU() {
        if (koerpergewichtdoku == null) {
            koerpergewichtdoku = new ArrayList<>();
        }
        return this.koerpergewichtdoku;
    }

    /**
     * Ist die Bewohnerin bzw. der Bewohner seit der letzten Ergebniserfassung in der Einrichtung gestürzt?
     * 
     * @return
     *     possible object is
     *     {@link STURZ }
     *     
     */
    public STURZ getSTURZ() {
        return sturz;
    }

    /**
     * Legt den Wert der sturz-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link STURZ }
     *     
     * @see #getSTURZ()
     */
    public void setSTURZ(STURZ value) {
        this.sturz = value;
    }

    /**
     * Welche Sturzfolgen sind aufgetreten?
     * 
     * Gets the value of the sturzfolgen property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sturzfolgen property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getSTURZFOLGEN().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link STURZFOLGEN }
     * </p>
     * 
     * 
     * @return
     *     The value of the sturzfolgen property.
     */
    public List<STURZFOLGEN> getSTURZFOLGEN() {
        if (sturzfolgen == null) {
            sturzfolgen = new ArrayList<>();
        }
        return this.sturzfolgen;
    }

    /**
     * Wurden bei der Bewohnerin bzw. dem Bewohner in den vergangenen 4 Wochen Gurte angewendet?
     * 
     * @return
     *     possible object is
     *     {@link GURT }
     *     
     */
    public GURT getGURT() {
        return gurt;
    }

    /**
     * Legt den Wert der gurt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GURT }
     *     
     * @see #getGURT()
     */
    public void setGURT(GURT value) {
        this.gurt = value;
    }

    /**
     * Wurden bei der Bewohnerin bzw. dem Bewohner in den vergangenen 4 Wochen Bettseitenteile angewendet?
     * 
     * @return
     *     possible object is
     *     {@link SEITENTEILE }
     *     
     */
    public SEITENTEILE getSEITENTEILE() {
        return seitenteile;
    }

    /**
     * Legt den Wert der seitenteile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SEITENTEILE }
     *     
     * @see #getSEITENTEILE()
     */
    public void setSEITENTEILE(SEITENTEILE value) {
        this.seitenteile = value;
    }

    /**
     * Liegen bei der Bewohnerin bzw. dem Bewohner Anzeichen für länger andauernde Schmerzen vor (z.B. Äußerungen der Bewohnerin bzw. des Bewohners oder Einnahme von Analgetika)?
     * 
     * @return
     *     possible object is
     *     {@link SCHMERZEN }
     *     
     */
    public SCHMERZEN getSCHMERZEN() {
        return schmerzen;
    }

    /**
     * Legt den Wert der schmerzen-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SCHMERZEN }
     *     
     * @see #getSCHMERZEN()
     */
    public void setSCHMERZEN(SCHMERZEN value) {
        this.schmerzen = value;
    }

    /**
     * Ist die Bewohnerin bzw. der Bewohner durch eine medikamentöse Schmerzbehandlung schmerzfrei?
     * 
     * @return
     *     possible object is
     *     {@link SCHMERZFREI }
     *     
     */
    public SCHMERZFREI getSCHMERZFREI() {
        return schmerzfrei;
    }

    /**
     * Legt den Wert der schmerzfrei-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SCHMERZFREI }
     *     
     * @see #getSCHMERZFREI()
     */
    public void setSCHMERZFREI(SCHMERZFREI value) {
        this.schmerzfrei = value;
    }

    /**
     * Wurde bei der Bewohnerin bzw. dem Bewohner eine differenzierte Schmerzeinschätzung vorgenommen?
     * 
     * @return
     *     possible object is
     *     {@link SCHMERZEINSCH }
     *     
     */
    public SCHMERZEINSCH getSCHMERZEINSCH() {
        return schmerzeinsch;
    }

    /**
     * Legt den Wert der schmerzeinsch-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SCHMERZEINSCH }
     *     
     * @see #getSCHMERZEINSCH()
     */
    public void setSCHMERZEINSCH(SCHMERZEINSCH value) {
        this.schmerzeinsch = value;
    }

    /**
     * Datum: Dokumentation der Schmerzeinschätzung
     * 
     * @return
     *     possible object is
     *     {@link SCHMERZEINSCHDATUM }
     *     
     */
    public SCHMERZEINSCHDATUM getSCHMERZEINSCHDATUM() {
        return schmerzeinschdatum;
    }

    /**
     * Legt den Wert der schmerzeinschdatum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SCHMERZEINSCHDATUM }
     *     
     * @see #getSCHMERZEINSCHDATUM()
     */
    public void setSCHMERZEINSCHDATUM(SCHMERZEINSCHDATUM value) {
        this.schmerzeinschdatum = value;
    }

    /**
     * Welche Informationen liegen über die Ergebnisse dieser Schmerzeinschätzung vor?
     * 
     * Gets the value of the schmerzeinschinfo property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the schmerzeinschinfo property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getSCHMERZEINSCHINFO().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SCHMERZEINSCHINFO }
     * </p>
     * 
     * 
     * @return
     *     The value of the schmerzeinschinfo property.
     */
    public List<SCHMERZEINSCHINFO> getSCHMERZEINSCHINFO() {
        if (schmerzeinschinfo == null) {
            schmerzeinschinfo = new ArrayList<>();
        }
        return this.schmerzeinschinfo;
    }

    /**
     * Ist die Bewohnerin bzw. der Bewohner nach der letzten Ergebniserfassung neu in die Einrichtung eingezogen?
     * 
     * @return
     *     possible object is
     *     {@link NEUEINZUG }
     *     
     */
    public NEUEINZUG getNEUEINZUG() {
        return neueinzug;
    }

    /**
     * Legt den Wert der neueinzug-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NEUEINZUG }
     *     
     * @see #getNEUEINZUG()
     */
    public void setNEUEINZUG(NEUEINZUG value) {
        this.neueinzug = value;
    }

    /**
     * Erfolgte der Einzug direkt im Anschluss an einen Kurzzeit- bzw. Verhinderungspflegeaufenthalt in der Einrichtung (ohne zeitliche Lücke)?
     * 
     * @return
     *     possible object is
     *     {@link EINZUGNACHKZP }
     *     
     */
    public EINZUGNACHKZP getEINZUGNACHKZP() {
        return einzugnachkzp;
    }

    /**
     * Legt den Wert der einzugnachkzp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EINZUGNACHKZP }
     *     
     * @see #getEINZUGNACHKZP()
     */
    public void setEINZUGNACHKZP(EINZUGNACHKZP value) {
        this.einzugnachkzp = value;
    }

    /**
     * Datum: Beginn des Kurzzeit- bzw. Verhinderungspflegeaufenthalts
     * 
     * @return
     *     possible object is
     *     {@link EINZUGNACHKZPDATUM }
     *     
     */
    public EINZUGNACHKZPDATUM getEINZUGNACHKZPDATUM() {
        return einzugnachkzpdatum;
    }

    /**
     * Legt den Wert der einzugnachkzpdatum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EINZUGNACHKZPDATUM }
     *     
     * @see #getEINZUGNACHKZPDATUM()
     */
    public void setEINZUGNACHKZPDATUM(EINZUGNACHKZPDATUM value) {
        this.einzugnachkzpdatum = value;
    }

    /**
     * Ist die Bewohnerin bzw. der Bewohner innerhalb der ersten 8 Wochen nach dem Einzug länger als drei Tage in einem Krankenhaus versorgt worden?
     * 
     * @return
     *     possible object is
     *     {@link EINZUGKHBEHANDLUNG }
     *     
     */
    public EINZUGKHBEHANDLUNG getEINZUGKHBEHANDLUNG() {
        return einzugkhbehandlung;
    }

    /**
     * Legt den Wert der einzugkhbehandlung-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EINZUGKHBEHANDLUNG }
     *     
     * @see #getEINZUGKHBEHANDLUNG()
     */
    public void setEINZUGKHBEHANDLUNG(EINZUGKHBEHANDLUNG value) {
        this.einzugkhbehandlung = value;
    }

    /**
     * Datum: Beginn des Krankenhausaufenthalts direkt nach dem Einzug
     * 
     * @return
     *     possible object is
     *     {@link EINZUGKHBEGINNDATUM }
     *     
     */
    public EINZUGKHBEGINNDATUM getEINZUGKHBEGINNDATUM() {
        return einzugkhbeginndatum;
    }

    /**
     * Legt den Wert der einzugkhbeginndatum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EINZUGKHBEGINNDATUM }
     *     
     * @see #getEINZUGKHBEGINNDATUM()
     */
    public void setEINZUGKHBEGINNDATUM(EINZUGKHBEGINNDATUM value) {
        this.einzugkhbeginndatum = value;
    }

    /**
     * Datum: Ende des Krankenhausaufenthalts direkt nach dem Einzug
     * 
     * @return
     *     possible object is
     *     {@link EINZUGKHENDEDATUM }
     *     
     */
    public EINZUGKHENDEDATUM getEINZUGKHENDEDATUM() {
        return einzugkhendedatum;
    }

    /**
     * Legt den Wert der einzugkhendedatum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EINZUGKHENDEDATUM }
     *     
     * @see #getEINZUGKHENDEDATUM()
     */
    public void setEINZUGKHENDEDATUM(EINZUGKHENDEDATUM value) {
        this.einzugkhendedatum = value;
    }

    /**
     * Ist in den Wochen nach dem Einzug mit der Bewohnerin bzw. dem Bewohner und/oder einer ihrer bzw. seiner Angehörigen oder sonstigen Vertrauenspersonen ein Gespräch über ihr bzw. sein Einleben und die zukünftige Versorgung geführt worden?
     * 
     * @return
     *     possible object is
     *     {@link EINZUGGESPR }
     *     
     */
    public EINZUGGESPR getEINZUGGESPR() {
        return einzuggespr;
    }

    /**
     * Legt den Wert der einzuggespr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EINZUGGESPR }
     *     
     * @see #getEINZUGGESPR()
     */
    public void setEINZUGGESPR(EINZUGGESPR value) {
        this.einzuggespr = value;
    }

    /**
     * Datum des Integrationsgesprächs
     * 
     * @return
     *     possible object is
     *     {@link EINZUGGESPRDATUM }
     *     
     */
    public EINZUGGESPRDATUM getEINZUGGESPRDATUM() {
        return einzuggesprdatum;
    }

    /**
     * Legt den Wert der einzuggesprdatum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EINZUGGESPRDATUM }
     *     
     * @see #getEINZUGGESPRDATUM()
     */
    public void setEINZUGGESPRDATUM(EINZUGGESPRDATUM value) {
        this.einzuggesprdatum = value;
    }

    /**
     * Wer hat an dem Integrationsgespräch teilgenommen?
     * 
     * Gets the value of the einzuggesprteilnehmer property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the einzuggesprteilnehmer property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getEINZUGGESPRTEILNEHMER().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EINZUGGESPRTEILNEHMER }
     * </p>
     * 
     * 
     * @return
     *     The value of the einzuggesprteilnehmer property.
     */
    public List<EINZUGGESPRTEILNEHMER> getEINZUGGESPRTEILNEHMER() {
        if (einzuggesprteilnehmer == null) {
            einzuggesprteilnehmer = new ArrayList<>();
        }
        return this.einzuggesprteilnehmer;
    }

    /**
     * Wurden die Ergebnisse dieses Gespräches dokumentiert?
     * 
     * @return
     *     possible object is
     *     {@link EINZUGGESPRDOKU }
     *     
     */
    public EINZUGGESPRDOKU getEINZUGGESPRDOKU() {
        return einzuggesprdoku;
    }

    /**
     * Legt den Wert der einzuggesprdoku-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EINZUGGESPRDOKU }
     *     
     * @see #getEINZUGGESPRDOKU()
     */
    public void setEINZUGGESPRDOKU(EINZUGGESPRDOKU value) {
        this.einzuggesprdoku = value;
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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class AMPUTATION {

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
    public static class AMPUTATIONDATUM {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class APOPLEX {

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
    public static class APOPLEXDATUM {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_bewusstseinszustand_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class BEWUSSTSEINSZUSTAND {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jnAnzahl_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DEKUBITUS {

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
    public static class DEKUBITUS1BEGINNDATUM {

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
    public static class DEKUBITUS1ENDEDATUM {

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
     *       <attribute name="value" type="{https://www.das-pflege.de}enum_lokalisation_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DEKUBITUS1LOK {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setValue(Integer value) {
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
    public static class DEKUBITUS2BEGINNDATUM {

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
    public static class DEKUBITUS2ENDEDATUM {

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
     *       <attribute name="value" type="{https://www.das-pflege.de}enum_lokalisation_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DEKUBITUS2LOK {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setValue(Integer value) {
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
     *       <attribute name="value" type="{https://www.das-pflege.de}enum_dekubitusStadium_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DEKUBITUSSTADIUM {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setValue(Integer value) {
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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_diagnose_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DIAGNOSEN {

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
     *       <attribute name="value" type="{https://www.das-pflege.de}enum_jnNichtMoeglich_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class EINZUGGESPR {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setValue(Integer value) {
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
    public static class EINZUGGESPRDATUM {

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
     *       <attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class EINZUGGESPRDOKU {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setValue(Integer value) {
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
     *       <attribute name="value" type="{https://www.das-pflege.de}enum_teilnehmer_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class EINZUGGESPRTEILNEHMER {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setValue(Integer value) {
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
    public static class EINZUGKHBEGINNDATUM {

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
     *       <attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class EINZUGKHBEHANDLUNG {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setValue(Integer value) {
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
    public static class EINZUGKHENDEDATUM {

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
     *       <attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class EINZUGNACHKZP {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setValue(Integer value) {
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
    public static class EINZUGNACHKZPDATUM {

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
    public static class EINZUGSDATUM {

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
     * <p>Java-Klasse für anonymous complex type.</p>
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.</p>
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class FRAKTUR {

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
    public static class FRAKTURDATUM {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GABESCHAEFTIGEN {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GAINTERAKTION {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GAKONTAKTPFLEGE {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GAPLANUNGEN {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GARUHENSCHLAFEN {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GATAGESABLAUF {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GURT {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class HERZINFARKT {

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
    public static class HERZINFARKTDATUM {

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
    public static class KHBEGINNDATUM {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jnAnzahl_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KHBEHANDLUNG {

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
    public static class KHENDEDATUM {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFBETEILIGUNG {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFENTSCHEIDUNGEN {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFERINNERN {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFERKENNEN {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFGEFAHRERKENNEN {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFHANDLUNGEN {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFMITTEILEN {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFORIENTOERTLICH {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFORIENTZEITLICH {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFVERSTEHENAUF {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFVERSTEHENINFO {

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
     *       <attribute name="value">
     *         <simpleType>
     *           <restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
     *             <totalDigits value="5"/>
     *             <fractionDigits value="2"/>
     *             <minInclusive value="0"/>
     *             <maxInclusive value="500"/>
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
    public static class KOERPERGEWICHT {

        @XmlAttribute(name = "value")
        protected BigDecimal value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setValue(BigDecimal value) {
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
    public static class KOERPERGEWICHTDATUM {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_gewichtsverlust_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KOERPERGEWICHTDOKU {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class MOBILFORTBEWEGUNG {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class MOBILPOSWECHSEL {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class MOBILSITZPOSITION {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class MOBILTREPPENSTEIGEN {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class MOBILUMSETZEN {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class NEUEINZUG {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class PFLEGEGRAD {

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
     *       <attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SCHMERZEINSCH {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setValue(Integer value) {
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
    public static class SCHMERZEINSCHDATUM {

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
     *       <attribute name="value" type="{https://www.das-pflege.de}enum_schmerzeinschaetzung_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SCHMERZEINSCHINFO {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setValue(Integer value) {
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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SCHMERZEN {

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
     *       <attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SCHMERZFREI {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setValue(Integer value) {
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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SEITENTEILE {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jnAnzahl_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class STURZ {

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
     *       <attribute name="value" type="{https://www.das-pflege.de}enum_sturzfolgen_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class STURZFOLGEN {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setValue(Integer value) {
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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVANAUSOBERKOERPER {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVANAUSUNTERKOERPER {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVDUSCHENBADEN {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVERNAEHRUNG {

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
     *       <attribute name="value" type="{https://www.das-pflege.de}enum_kuenstlicheErnaehrung_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVERNAEHRUNGUMFANG {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setValue(Integer value) {
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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitTyp2_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVESSEN {

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
     *       <attribute name="value" type="{https://www.das-pflege.de}enum_fremdhilfe_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVFREMDHILFE {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setValue(Integer value) {
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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_harnkontinenz_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVHARNKONTINENZ {

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
     *       <attribute name="value" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVHARNKONTINENZBEW {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setValue(Integer value) {
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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVINTIMBEREICH {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVKOPF {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVNAHRUNGZUBEREITEN {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVOBERKOERPER {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_stuhlkontinenz_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVSTUHLKONTINENZ {

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
     *       <attribute name="value" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVSTUHLKONTINENZBEW {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setValue(Integer value) {
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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitTyp1_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVTOILETTE {

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
     *       <attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitTyp1_type" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVTRINKEN {

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
