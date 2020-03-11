//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2019.10.30 um 10:38:41 AM CET 
//


package de.offene_pflege.services.qdvs.schema;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Erhebungsbogen zur Erfassung von Versorgungsergebnissen der stationären Langzeitpflege
 *
 * <p>Java-Klasse für das_qs_data_type complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="das_qs_data_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IDBEWOHNER">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;pattern value="[0-9]{6}"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="WOHNBEREICH">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;maxLength value="255"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ERHEBUNGSDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="EINZUGSDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GEBURTSMONAT">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_monat_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GEBURTSJAHR">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}gYear">
 *                       &lt;minInclusive value="1900"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GESCHLECHT">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_geschlecht_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PFLEGEGRAD">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_pflegegrad_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="APOPLEX">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="APOPLEXDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="FRAKTUR">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="FRAKTURDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="HERZINFARKT">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="HERZINFARKTDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="AMPUTATION">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="AMPUTATIONDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KHBEHANDLUNG">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jnAnzahl_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KHBEGINNDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KHENDEDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KHBANZAHLAUFENTHALTE">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                       &lt;totalDigits value="2"/>
 *                       &lt;minInclusive value="2"/>
 *                       &lt;maxInclusive value="10"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KHBANZAHLTAGE">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                       &lt;totalDigits value="3"/>
 *                       &lt;minInclusive value="2"/>
 *                       &lt;maxInclusive value="200"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="BEATMUNG">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jnBeatmung_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="BEWUSSTSEINSZUSTAND">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_bewusstseinszustand_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DIAGNOSEN" maxOccurs="9">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_diagnose_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="MOBILPOSWECHSEL">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="MOBILSITZPOSITION">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="MOBILUMSETZEN">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="MOBILFORTBEWEGUNG">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="MOBILTREPPENSTEIGEN">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KKFERKENNEN">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KKFORIENTOERTLICH">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KKFORIENTZEITLICH">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KKFERINNERN">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KKFHANDLUNGEN">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KKFENTSCHEIDUNGEN">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KKFVERSTEHENINFO">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KKFGEFAHRERKENNEN">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KKFMITTEILEN">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KKFVERSTEHENAUF">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KKFBETEILIGUNG">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SVERNAEHRUNG">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SVFREMDHILFE">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_fremdhilfe_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SVERNAEHRUNGUMFANG">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_kuenstlicheErnaehrung_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SVHARNKONTINENZ">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_harnkontinenz_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SVSTUHLKONTINENZ">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_stuhlkontinenz_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SVOBERKOERPER">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SVKOPF">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SVINTIMBEREICH">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SVDUSCHENBADEN">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SVANAUSOBERKOERPER">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SVANAUSUNTERKOERPER">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SVNAHRUNGZUBEREITEN">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SVESSEN">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitTyp2_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SVTRINKEN">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitTyp1_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SVTOILETTE">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitTyp1_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SVHARNKONTINENZBEW">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SVSTUHLKONTINENZBEW">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GATAGESABLAUF">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GARUHENSCHLAFEN">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GABESCHAEFTIGEN">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GAPLANUNGEN">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GAINTERAKTION">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GAKONTAKTPFLEGE">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DEKUBITUS">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jnAnzahl_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DEKUBITUSSTADIUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_dekubitusStadium_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DEKUBITUS1BEGINNDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DEKUBITUS1ENDEDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DEKUBITUS1LOK">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_lokalisation_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DEKUBITUS2BEGINNDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DEKUBITUS2ENDEDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DEKUBITUS2LOK">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_lokalisation_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KOERPERGROESSE">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                       &lt;totalDigits value="3"/>
 *                       &lt;minInclusive value="100"/>
 *                       &lt;maxInclusive value="250"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KOERPERGEWICHT">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
 *                       &lt;totalDigits value="5"/>
 *                       &lt;maxInclusive value="500"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KOERPERGEWICHTDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KOERPERGEWICHTDOKU" maxOccurs="5">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_gewichtsverlust_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="STURZ">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jnAnzahl_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="STURZFOLGEN" maxOccurs="4">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_sturzfolgen_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GURT">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GURTHAUFIGKEIT">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_frequenz_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SEITENTEILE">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SEITENTEILEHAUFIGKEIT">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_frequenz_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SCHMERZEN">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SCHMERZFREI">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SCHMERZEINSCH">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SCHMERZEINSCHDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SCHMERZEINSCHINFO" maxOccurs="4">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_schmerzeinschaetzung_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="NEUEINZUG">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="EINZUGNACHKZ">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="EINZUGNACHKZDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="EINZUGKHBEHANDLUNG">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="EINZUGKHBEGINNDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="EINZUGKHENDEDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="EINZUGGESPR">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_jnNichtMoeglich_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="EINZUGGESPRDATUM">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="EINZUGGESPRTEILNEHMER" maxOccurs="4">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_teilnehmer_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="EINZUGGESPRDOKU">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "das_qs_data_type", propOrder = {
        "idbewohner",
        "wohnbereich",
        "erhebungsdatum",
        "einzugsdatum",
        "geburtsmonat",
        "geburtsjahr",
        "geschlecht",
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
        "khbanzahlaufenthalte",
        "khbanzahltage",
        "beatmung",
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
        "koerpergroesse",
        "koerpergewicht",
        "koerpergewichtdatum",
        "koerpergewichtdoku",
        "sturz",
        "sturzfolgen",
        "gurt",
        "gurthaufigkeit",
        "seitenteile",
        "seitenteilehaufigkeit",
        "schmerzen",
        "schmerzfrei",
        "schmerzeinsch",
        "schmerzeinschdatum",
        "schmerzeinschinfo",
        "neueinzug",
        "einzugnachkz",
        "einzugnachkzdatum",
        "einzugkhbehandlung",
        "einzugkhbeginndatum",
        "einzugkhendedatum",
        "einzuggespr",
        "einzuggesprdatum",
        "einzuggesprteilnehmer",
        "einzuggesprdoku"
})
public class DasQsDataType {

    @XmlElement(name = "IDBEWOHNER", required = true)
    protected IDBEWOHNER idbewohner;
    @XmlElement(name = "WOHNBEREICH", required = true)
    protected WOHNBEREICH wohnbereich;
    @XmlElement(name = "ERHEBUNGSDATUM", required = true)
    protected ERHEBUNGSDATUM erhebungsdatum;
    @XmlElement(name = "EINZUGSDATUM", required = true)
    protected EINZUGSDATUM einzugsdatum;
    @XmlElement(name = "GEBURTSMONAT", required = true)
    protected GEBURTSMONAT geburtsmonat;
    @XmlElement(name = "GEBURTSJAHR", required = true)
    protected GEBURTSJAHR geburtsjahr;
    @XmlElement(name = "GESCHLECHT", required = true)
    protected GESCHLECHT geschlecht;
    @XmlElement(name = "PFLEGEGRAD", required = true)
    protected PFLEGEGRAD pflegegrad;
    @XmlElement(name = "APOPLEX", required = true)
    protected APOPLEX apoplex;
    @XmlElement(name = "APOPLEXDATUM", required = true)
    protected APOPLEXDATUM apoplexdatum;
    @XmlElement(name = "FRAKTUR", required = true)
    protected FRAKTUR fraktur;
    @XmlElement(name = "FRAKTURDATUM", required = true)
    protected FRAKTURDATUM frakturdatum;
    @XmlElement(name = "HERZINFARKT", required = true)
    protected HERZINFARKT herzinfarkt;
    @XmlElement(name = "HERZINFARKTDATUM", required = true)
    protected HERZINFARKTDATUM herzinfarktdatum;
    @XmlElement(name = "AMPUTATION", required = true)
    protected AMPUTATION amputation;
    @XmlElement(name = "AMPUTATIONDATUM", required = true)
    protected AMPUTATIONDATUM amputationdatum;
    @XmlElement(name = "KHBEHANDLUNG", required = true)
    protected KHBEHANDLUNG khbehandlung;
    @XmlElement(name = "KHBEGINNDATUM", required = true)
    protected KHBEGINNDATUM khbeginndatum;
    @XmlElement(name = "KHENDEDATUM", required = true)
    protected KHENDEDATUM khendedatum;
    @XmlElement(name = "KHBANZAHLAUFENTHALTE", required = true)
    protected KHBANZAHLAUFENTHALTE khbanzahlaufenthalte;
    @XmlElement(name = "KHBANZAHLTAGE", required = true)
    protected KHBANZAHLTAGE khbanzahltage;
    @XmlElement(name = "BEATMUNG", required = true)
    protected BEATMUNG beatmung;
    @XmlElement(name = "BEWUSSTSEINSZUSTAND", required = true)
    protected BEWUSSTSEINSZUSTAND bewusstseinszustand;
    @XmlElement(name = "DIAGNOSEN", required = true)
    protected List<DIAGNOSEN> diagnosen;
    @XmlElement(name = "MOBILPOSWECHSEL", required = true)
    protected MOBILPOSWECHSEL mobilposwechsel;
    @XmlElement(name = "MOBILSITZPOSITION", required = true)
    protected MOBILSITZPOSITION mobilsitzposition;
    @XmlElement(name = "MOBILUMSETZEN", required = true)
    protected MOBILUMSETZEN mobilumsetzen;
    @XmlElement(name = "MOBILFORTBEWEGUNG", required = true)
    protected MOBILFORTBEWEGUNG mobilfortbewegung;
    @XmlElement(name = "MOBILTREPPENSTEIGEN", required = true)
    protected MOBILTREPPENSTEIGEN mobiltreppensteigen;
    @XmlElement(name = "KKFERKENNEN", required = true)
    protected KKFERKENNEN kkferkennen;
    @XmlElement(name = "KKFORIENTOERTLICH", required = true)
    protected KKFORIENTOERTLICH kkforientoertlich;
    @XmlElement(name = "KKFORIENTZEITLICH", required = true)
    protected KKFORIENTZEITLICH kkforientzeitlich;
    @XmlElement(name = "KKFERINNERN", required = true)
    protected KKFERINNERN kkferinnern;
    @XmlElement(name = "KKFHANDLUNGEN", required = true)
    protected KKFHANDLUNGEN kkfhandlungen;
    @XmlElement(name = "KKFENTSCHEIDUNGEN", required = true)
    protected KKFENTSCHEIDUNGEN kkfentscheidungen;
    @XmlElement(name = "KKFVERSTEHENINFO", required = true)
    protected KKFVERSTEHENINFO kkfversteheninfo;
    @XmlElement(name = "KKFGEFAHRERKENNEN", required = true)
    protected KKFGEFAHRERKENNEN kkfgefahrerkennen;
    @XmlElement(name = "KKFMITTEILEN", required = true)
    protected KKFMITTEILEN kkfmitteilen;
    @XmlElement(name = "KKFVERSTEHENAUF", required = true)
    protected KKFVERSTEHENAUF kkfverstehenauf;
    @XmlElement(name = "KKFBETEILIGUNG", required = true)
    protected KKFBETEILIGUNG kkfbeteiligung;
    @XmlElement(name = "SVERNAEHRUNG", required = true)
    protected SVERNAEHRUNG svernaehrung;
    @XmlElement(name = "SVFREMDHILFE", required = true)
    protected SVFREMDHILFE svfremdhilfe;
    @XmlElement(name = "SVERNAEHRUNGUMFANG", required = true)
    protected SVERNAEHRUNGUMFANG svernaehrungumfang;
    @XmlElement(name = "SVHARNKONTINENZ", required = true)
    protected SVHARNKONTINENZ svharnkontinenz;
    @XmlElement(name = "SVSTUHLKONTINENZ", required = true)
    protected SVSTUHLKONTINENZ svstuhlkontinenz;
    @XmlElement(name = "SVOBERKOERPER", required = true)
    protected SVOBERKOERPER svoberkoerper;
    @XmlElement(name = "SVKOPF", required = true)
    protected SVKOPF svkopf;
    @XmlElement(name = "SVINTIMBEREICH", required = true)
    protected SVINTIMBEREICH svintimbereich;
    @XmlElement(name = "SVDUSCHENBADEN", required = true)
    protected SVDUSCHENBADEN svduschenbaden;
    @XmlElement(name = "SVANAUSOBERKOERPER", required = true)
    protected SVANAUSOBERKOERPER svanausoberkoerper;
    @XmlElement(name = "SVANAUSUNTERKOERPER", required = true)
    protected SVANAUSUNTERKOERPER svanausunterkoerper;
    @XmlElement(name = "SVNAHRUNGZUBEREITEN", required = true)
    protected SVNAHRUNGZUBEREITEN svnahrungzubereiten;
    @XmlElement(name = "SVESSEN", required = true)
    protected SVESSEN svessen;
    @XmlElement(name = "SVTRINKEN", required = true)
    protected SVTRINKEN svtrinken;
    @XmlElement(name = "SVTOILETTE", required = true)
    protected SVTOILETTE svtoilette;
    @XmlElement(name = "SVHARNKONTINENZBEW", required = true)
    protected SVHARNKONTINENZBEW svharnkontinenzbew;
    @XmlElement(name = "SVSTUHLKONTINENZBEW", required = true)
    protected SVSTUHLKONTINENZBEW svstuhlkontinenzbew;
    @XmlElement(name = "GATAGESABLAUF", required = true)
    protected GATAGESABLAUF gatagesablauf;
    @XmlElement(name = "GARUHENSCHLAFEN", required = true)
    protected GARUHENSCHLAFEN garuhenschlafen;
    @XmlElement(name = "GABESCHAEFTIGEN", required = true)
    protected GABESCHAEFTIGEN gabeschaeftigen;
    @XmlElement(name = "GAPLANUNGEN", required = true)
    protected GAPLANUNGEN gaplanungen;
    @XmlElement(name = "GAINTERAKTION", required = true)
    protected GAINTERAKTION gainteraktion;
    @XmlElement(name = "GAKONTAKTPFLEGE", required = true)
    protected GAKONTAKTPFLEGE gakontaktpflege;
    @XmlElement(name = "DEKUBITUS", required = true)
    protected DEKUBITUS dekubitus;
    @XmlElement(name = "DEKUBITUSSTADIUM", required = true)
    protected DEKUBITUSSTADIUM dekubitusstadium;
    @XmlElement(name = "DEKUBITUS1BEGINNDATUM", required = true)
    protected DEKUBITUS1BEGINNDATUM dekubitus1BEGINNDATUM;
    @XmlElement(name = "DEKUBITUS1ENDEDATUM", required = true)
    protected DEKUBITUS1ENDEDATUM dekubitus1ENDEDATUM;
    @XmlElement(name = "DEKUBITUS1LOK", required = true)
    protected DEKUBITUS1LOK dekubitus1LOK;
    @XmlElement(name = "DEKUBITUS2BEGINNDATUM", required = true)
    protected DEKUBITUS2BEGINNDATUM dekubitus2BEGINNDATUM;
    @XmlElement(name = "DEKUBITUS2ENDEDATUM", required = true)
    protected DEKUBITUS2ENDEDATUM dekubitus2ENDEDATUM;
    @XmlElement(name = "DEKUBITUS2LOK", required = true)
    protected DEKUBITUS2LOK dekubitus2LOK;
    @XmlElement(name = "KOERPERGROESSE", required = true)
    protected KOERPERGROESSE koerpergroesse;
    @XmlElement(name = "KOERPERGEWICHT", required = true)
    protected KOERPERGEWICHT koerpergewicht;
    @XmlElement(name = "KOERPERGEWICHTDATUM", required = true)
    protected KOERPERGEWICHTDATUM koerpergewichtdatum;
    @XmlElement(name = "KOERPERGEWICHTDOKU", required = true)
    protected List<KOERPERGEWICHTDOKU> koerpergewichtdoku;
    @XmlElement(name = "STURZ", required = true)
    protected STURZ sturz;
    @XmlElement(name = "STURZFOLGEN", required = true)
    protected List<STURZFOLGEN> sturzfolgen;
    @XmlElement(name = "GURT", required = true)
    protected GURT gurt;
    @XmlElement(name = "GURTHAUFIGKEIT", required = true)
    protected GURTHAUFIGKEIT gurthaufigkeit;
    @XmlElement(name = "SEITENTEILE", required = true)
    protected SEITENTEILE seitenteile;
    @XmlElement(name = "SEITENTEILEHAUFIGKEIT", required = true)
    protected SEITENTEILEHAUFIGKEIT seitenteilehaufigkeit;
    @XmlElement(name = "SCHMERZEN", required = true)
    protected SCHMERZEN schmerzen;
    @XmlElement(name = "SCHMERZFREI", required = true)
    protected SCHMERZFREI schmerzfrei;
    @XmlElement(name = "SCHMERZEINSCH", required = true)
    protected SCHMERZEINSCH schmerzeinsch;
    @XmlElement(name = "SCHMERZEINSCHDATUM", required = true)
    protected SCHMERZEINSCHDATUM schmerzeinschdatum;
    @XmlElement(name = "SCHMERZEINSCHINFO", required = true)
    protected List<SCHMERZEINSCHINFO> schmerzeinschinfo;
    @XmlElement(name = "NEUEINZUG", required = true)
    protected NEUEINZUG neueinzug;
    @XmlElement(name = "EINZUGNACHKZ", required = true)
    protected EINZUGNACHKZ einzugnachkz;
    @XmlElement(name = "EINZUGNACHKZDATUM", required = true)
    protected EINZUGNACHKZDATUM einzugnachkzdatum;
    @XmlElement(name = "EINZUGKHBEHANDLUNG", required = true)
    protected EINZUGKHBEHANDLUNG einzugkhbehandlung;
    @XmlElement(name = "EINZUGKHBEGINNDATUM", required = true)
    protected EINZUGKHBEGINNDATUM einzugkhbeginndatum;
    @XmlElement(name = "EINZUGKHENDEDATUM", required = true)
    protected EINZUGKHENDEDATUM einzugkhendedatum;
    @XmlElement(name = "EINZUGGESPR", required = true)
    protected EINZUGGESPR einzuggespr;
    @XmlElement(name = "EINZUGGESPRDATUM", required = true)
    protected EINZUGGESPRDATUM einzuggesprdatum;
    @XmlElement(name = "EINZUGGESPRTEILNEHMER", required = true)
    protected List<EINZUGGESPRTEILNEHMER> einzuggesprteilnehmer;
    @XmlElement(name = "EINZUGGESPRDOKU", required = true)
    protected EINZUGGESPRDOKU einzuggesprdoku;

    /**
     * Ruft den Wert der idbewohner-Eigenschaft ab.
     *
     * @return possible object is {@link IDBEWOHNER }
     */
    public IDBEWOHNER getIDBEWOHNER() {
        return idbewohner;
    }

    /**
     * Legt den Wert der idbewohner-Eigenschaft fest.
     *
     * @param value allowed object is {@link IDBEWOHNER }
     */
    public void setIDBEWOHNER(IDBEWOHNER value) {
        this.idbewohner = value;
    }

    /**
     * Ruft den Wert der wohnbereich-Eigenschaft ab.
     *
     * @return possible object is {@link WOHNBEREICH }
     */
    public WOHNBEREICH getWOHNBEREICH() {
        return wohnbereich;
    }

    /**
     * Legt den Wert der wohnbereich-Eigenschaft fest.
     *
     * @param value allowed object is {@link WOHNBEREICH }
     */
    public void setWOHNBEREICH(WOHNBEREICH value) {
        this.wohnbereich = value;
    }

    /**
     * Ruft den Wert der erhebungsdatum-Eigenschaft ab.
     *
     * @return possible object is {@link ERHEBUNGSDATUM }
     */
    public ERHEBUNGSDATUM getERHEBUNGSDATUM() {
        return erhebungsdatum;
    }

    /**
     * Legt den Wert der erhebungsdatum-Eigenschaft fest.
     *
     * @param value allowed object is {@link ERHEBUNGSDATUM }
     */
    public void setERHEBUNGSDATUM(ERHEBUNGSDATUM value) {
        this.erhebungsdatum = value;
    }

    /**
     * Ruft den Wert der einzugsdatum-Eigenschaft ab.
     *
     * @return possible object is {@link EINZUGSDATUM }
     */
    public EINZUGSDATUM getEINZUGSDATUM() {
        return einzugsdatum;
    }

    /**
     * Legt den Wert der einzugsdatum-Eigenschaft fest.
     *
     * @param value allowed object is {@link EINZUGSDATUM }
     */
    public void setEINZUGSDATUM(EINZUGSDATUM value) {
        this.einzugsdatum = value;
    }

    /**
     * Ruft den Wert der geburtsmonat-Eigenschaft ab.
     *
     * @return possible object is {@link GEBURTSMONAT }
     */
    public GEBURTSMONAT getGEBURTSMONAT() {
        return geburtsmonat;
    }

    /**
     * Legt den Wert der geburtsmonat-Eigenschaft fest.
     *
     * @param value allowed object is {@link GEBURTSMONAT }
     */
    public void setGEBURTSMONAT(GEBURTSMONAT value) {
        this.geburtsmonat = value;
    }

    /**
     * Ruft den Wert der geburtsjahr-Eigenschaft ab.
     *
     * @return possible object is {@link GEBURTSJAHR }
     */
    public GEBURTSJAHR getGEBURTSJAHR() {
        return geburtsjahr;
    }

    /**
     * Legt den Wert der geburtsjahr-Eigenschaft fest.
     *
     * @param value allowed object is {@link GEBURTSJAHR }
     */
    public void setGEBURTSJAHR(GEBURTSJAHR value) {
        this.geburtsjahr = value;
    }

    /**
     * Ruft den Wert der geschlecht-Eigenschaft ab.
     *
     * @return possible object is {@link GESCHLECHT }
     */
    public GESCHLECHT getGESCHLECHT() {
        return geschlecht;
    }

    /**
     * Legt den Wert der geschlecht-Eigenschaft fest.
     *
     * @param value allowed object is {@link GESCHLECHT }
     */
    public void setGESCHLECHT(GESCHLECHT value) {
        this.geschlecht = value;
    }

    /**
     * Ruft den Wert der pflegegrad-Eigenschaft ab.
     *
     * @return possible object is {@link PFLEGEGRAD }
     */
    public PFLEGEGRAD getPFLEGEGRAD() {
        return pflegegrad;
    }

    /**
     * Legt den Wert der pflegegrad-Eigenschaft fest.
     *
     * @param value allowed object is {@link PFLEGEGRAD }
     */
    public void setPFLEGEGRAD(PFLEGEGRAD value) {
        this.pflegegrad = value;
    }

    /**
     * Ruft den Wert der apoplex-Eigenschaft ab.
     *
     * @return possible object is {@link APOPLEX }
     */
    public APOPLEX getAPOPLEX() {
        return apoplex;
    }

    /**
     * Legt den Wert der apoplex-Eigenschaft fest.
     *
     * @param value allowed object is {@link APOPLEX }
     */
    public void setAPOPLEX(APOPLEX value) {
        this.apoplex = value;
    }

    /**
     * Ruft den Wert der apoplexdatum-Eigenschaft ab.
     *
     * @return possible object is {@link APOPLEXDATUM }
     */
    public APOPLEXDATUM getAPOPLEXDATUM() {
        return apoplexdatum;
    }

    /**
     * Legt den Wert der apoplexdatum-Eigenschaft fest.
     *
     * @param value allowed object is {@link APOPLEXDATUM }
     */
    public void setAPOPLEXDATUM(APOPLEXDATUM value) {
        this.apoplexdatum = value;
    }

    /**
     * Ruft den Wert der fraktur-Eigenschaft ab.
     *
     * @return possible object is {@link FRAKTUR }
     */
    public FRAKTUR getFRAKTUR() {
        return fraktur;
    }

    /**
     * Legt den Wert der fraktur-Eigenschaft fest.
     *
     * @param value allowed object is {@link FRAKTUR }
     */
    public void setFRAKTUR(FRAKTUR value) {
        this.fraktur = value;
    }

    /**
     * Ruft den Wert der frakturdatum-Eigenschaft ab.
     *
     * @return possible object is {@link FRAKTURDATUM }
     */
    public FRAKTURDATUM getFRAKTURDATUM() {
        return frakturdatum;
    }

    /**
     * Legt den Wert der frakturdatum-Eigenschaft fest.
     *
     * @param value allowed object is {@link FRAKTURDATUM }
     */
    public void setFRAKTURDATUM(FRAKTURDATUM value) {
        this.frakturdatum = value;
    }

    /**
     * Ruft den Wert der herzinfarkt-Eigenschaft ab.
     *
     * @return possible object is {@link HERZINFARKT }
     */
    public HERZINFARKT getHERZINFARKT() {
        return herzinfarkt;
    }

    /**
     * Legt den Wert der herzinfarkt-Eigenschaft fest.
     *
     * @param value allowed object is {@link HERZINFARKT }
     */
    public void setHERZINFARKT(HERZINFARKT value) {
        this.herzinfarkt = value;
    }

    /**
     * Ruft den Wert der herzinfarktdatum-Eigenschaft ab.
     *
     * @return possible object is {@link HERZINFARKTDATUM }
     */
    public HERZINFARKTDATUM getHERZINFARKTDATUM() {
        return herzinfarktdatum;
    }

    /**
     * Legt den Wert der herzinfarktdatum-Eigenschaft fest.
     *
     * @param value allowed object is {@link HERZINFARKTDATUM }
     */
    public void setHERZINFARKTDATUM(HERZINFARKTDATUM value) {
        this.herzinfarktdatum = value;
    }

    /**
     * Ruft den Wert der amputation-Eigenschaft ab.
     *
     * @return possible object is {@link AMPUTATION }
     */
    public AMPUTATION getAMPUTATION() {
        return amputation;
    }

    /**
     * Legt den Wert der amputation-Eigenschaft fest.
     *
     * @param value allowed object is {@link AMPUTATION }
     */
    public void setAMPUTATION(AMPUTATION value) {
        this.amputation = value;
    }

    /**
     * Ruft den Wert der amputationdatum-Eigenschaft ab.
     *
     * @return possible object is {@link AMPUTATIONDATUM }
     */
    public AMPUTATIONDATUM getAMPUTATIONDATUM() {
        return amputationdatum;
    }

    /**
     * Legt den Wert der amputationdatum-Eigenschaft fest.
     *
     * @param value allowed object is {@link AMPUTATIONDATUM }
     */
    public void setAMPUTATIONDATUM(AMPUTATIONDATUM value) {
        this.amputationdatum = value;
    }

    /**
     * Ruft den Wert der khbehandlung-Eigenschaft ab.
     *
     * @return possible object is {@link KHBEHANDLUNG }
     */
    public KHBEHANDLUNG getKHBEHANDLUNG() {
        return khbehandlung;
    }

    /**
     * Legt den Wert der khbehandlung-Eigenschaft fest.
     *
     * @param value allowed object is {@link KHBEHANDLUNG }
     */
    public void setKHBEHANDLUNG(KHBEHANDLUNG value) {
        this.khbehandlung = value;
    }

    /**
     * Ruft den Wert der khbeginndatum-Eigenschaft ab.
     *
     * @return possible object is {@link KHBEGINNDATUM }
     */
    public KHBEGINNDATUM getKHBEGINNDATUM() {
        return khbeginndatum;
    }

    /**
     * Legt den Wert der khbeginndatum-Eigenschaft fest.
     *
     * @param value allowed object is {@link KHBEGINNDATUM }
     */
    public void setKHBEGINNDATUM(KHBEGINNDATUM value) {
        this.khbeginndatum = value;
    }

    /**
     * Ruft den Wert der khendedatum-Eigenschaft ab.
     *
     * @return possible object is {@link KHENDEDATUM }
     */
    public KHENDEDATUM getKHENDEDATUM() {
        return khendedatum;
    }

    /**
     * Legt den Wert der khendedatum-Eigenschaft fest.
     *
     * @param value allowed object is {@link KHENDEDATUM }
     */
    public void setKHENDEDATUM(KHENDEDATUM value) {
        this.khendedatum = value;
    }

    /**
     * Ruft den Wert der khbanzahlaufenthalte-Eigenschaft ab.
     *
     * @return possible object is {@link KHBANZAHLAUFENTHALTE }
     */
    public KHBANZAHLAUFENTHALTE getKHBANZAHLAUFENTHALTE() {
        return khbanzahlaufenthalte;
    }

    /**
     * Legt den Wert der khbanzahlaufenthalte-Eigenschaft fest.
     *
     * @param value allowed object is {@link KHBANZAHLAUFENTHALTE }
     */
    public void setKHBANZAHLAUFENTHALTE(KHBANZAHLAUFENTHALTE value) {
        this.khbanzahlaufenthalte = value;
    }

    /**
     * Ruft den Wert der khbanzahltage-Eigenschaft ab.
     *
     * @return possible object is {@link KHBANZAHLTAGE }
     */
    public KHBANZAHLTAGE getKHBANZAHLTAGE() {
        return khbanzahltage;
    }

    /**
     * Legt den Wert der khbanzahltage-Eigenschaft fest.
     *
     * @param value allowed object is {@link KHBANZAHLTAGE }
     */
    public void setKHBANZAHLTAGE(KHBANZAHLTAGE value) {
        this.khbanzahltage = value;
    }

    /**
     * Ruft den Wert der beatmung-Eigenschaft ab.
     *
     * @return possible object is {@link BEATMUNG }
     */
    public BEATMUNG getBEATMUNG() {
        return beatmung;
    }

    /**
     * Legt den Wert der beatmung-Eigenschaft fest.
     *
     * @param value allowed object is {@link BEATMUNG }
     */
    public void setBEATMUNG(BEATMUNG value) {
        this.beatmung = value;
    }

    /**
     * Ruft den Wert der bewusstseinszustand-Eigenschaft ab.
     *
     * @return possible object is {@link BEWUSSTSEINSZUSTAND }
     */
    public BEWUSSTSEINSZUSTAND getBEWUSSTSEINSZUSTAND() {
        return bewusstseinszustand;
    }

    /**
     * Legt den Wert der bewusstseinszustand-Eigenschaft fest.
     *
     * @param value allowed object is {@link BEWUSSTSEINSZUSTAND }
     */
    public void setBEWUSSTSEINSZUSTAND(BEWUSSTSEINSZUSTAND value) {
        this.bewusstseinszustand = value;
    }

    /**
     * Gets the value of the diagnosen property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the diagnosen property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDIAGNOSEN().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link DIAGNOSEN }
     */
    public List<DIAGNOSEN> getDIAGNOSEN() {
        if (diagnosen == null) {
            diagnosen = new ArrayList<DIAGNOSEN>();
        }
        return this.diagnosen;
    }

    /**
     * Ruft den Wert der mobilposwechsel-Eigenschaft ab.
     *
     * @return possible object is {@link MOBILPOSWECHSEL }
     */
    public MOBILPOSWECHSEL getMOBILPOSWECHSEL() {
        return mobilposwechsel;
    }

    /**
     * Legt den Wert der mobilposwechsel-Eigenschaft fest.
     *
     * @param value allowed object is {@link MOBILPOSWECHSEL }
     */
    public void setMOBILPOSWECHSEL(MOBILPOSWECHSEL value) {
        this.mobilposwechsel = value;
    }

    /**
     * Ruft den Wert der mobilsitzposition-Eigenschaft ab.
     *
     * @return possible object is {@link MOBILSITZPOSITION }
     */
    public MOBILSITZPOSITION getMOBILSITZPOSITION() {
        return mobilsitzposition;
    }

    /**
     * Legt den Wert der mobilsitzposition-Eigenschaft fest.
     *
     * @param value allowed object is {@link MOBILSITZPOSITION }
     */
    public void setMOBILSITZPOSITION(MOBILSITZPOSITION value) {
        this.mobilsitzposition = value;
    }

    /**
     * Ruft den Wert der mobilumsetzen-Eigenschaft ab.
     *
     * @return possible object is {@link MOBILUMSETZEN }
     */
    public MOBILUMSETZEN getMOBILUMSETZEN() {
        return mobilumsetzen;
    }

    /**
     * Legt den Wert der mobilumsetzen-Eigenschaft fest.
     *
     * @param value allowed object is {@link MOBILUMSETZEN }
     */
    public void setMOBILUMSETZEN(MOBILUMSETZEN value) {
        this.mobilumsetzen = value;
    }

    /**
     * Ruft den Wert der mobilfortbewegung-Eigenschaft ab.
     *
     * @return possible object is {@link MOBILFORTBEWEGUNG }
     */
    public MOBILFORTBEWEGUNG getMOBILFORTBEWEGUNG() {
        return mobilfortbewegung;
    }

    /**
     * Legt den Wert der mobilfortbewegung-Eigenschaft fest.
     *
     * @param value allowed object is {@link MOBILFORTBEWEGUNG }
     */
    public void setMOBILFORTBEWEGUNG(MOBILFORTBEWEGUNG value) {
        this.mobilfortbewegung = value;
    }

    /**
     * Ruft den Wert der mobiltreppensteigen-Eigenschaft ab.
     *
     * @return possible object is {@link MOBILTREPPENSTEIGEN }
     */
    public MOBILTREPPENSTEIGEN getMOBILTREPPENSTEIGEN() {
        return mobiltreppensteigen;
    }

    /**
     * Legt den Wert der mobiltreppensteigen-Eigenschaft fest.
     *
     * @param value allowed object is {@link MOBILTREPPENSTEIGEN }
     */
    public void setMOBILTREPPENSTEIGEN(MOBILTREPPENSTEIGEN value) {
        this.mobiltreppensteigen = value;
    }

    /**
     * Ruft den Wert der kkferkennen-Eigenschaft ab.
     *
     * @return possible object is {@link KKFERKENNEN }
     */
    public KKFERKENNEN getKKFERKENNEN() {
        return kkferkennen;
    }

    /**
     * Legt den Wert der kkferkennen-Eigenschaft fest.
     *
     * @param value allowed object is {@link KKFERKENNEN }
     */
    public void setKKFERKENNEN(KKFERKENNEN value) {
        this.kkferkennen = value;
    }

    /**
     * Ruft den Wert der kkforientoertlich-Eigenschaft ab.
     *
     * @return possible object is {@link KKFORIENTOERTLICH }
     */
    public KKFORIENTOERTLICH getKKFORIENTOERTLICH() {
        return kkforientoertlich;
    }

    /**
     * Legt den Wert der kkforientoertlich-Eigenschaft fest.
     *
     * @param value allowed object is {@link KKFORIENTOERTLICH }
     */
    public void setKKFORIENTOERTLICH(KKFORIENTOERTLICH value) {
        this.kkforientoertlich = value;
    }

    /**
     * Ruft den Wert der kkforientzeitlich-Eigenschaft ab.
     *
     * @return possible object is {@link KKFORIENTZEITLICH }
     */
    public KKFORIENTZEITLICH getKKFORIENTZEITLICH() {
        return kkforientzeitlich;
    }

    /**
     * Legt den Wert der kkforientzeitlich-Eigenschaft fest.
     *
     * @param value allowed object is {@link KKFORIENTZEITLICH }
     */
    public void setKKFORIENTZEITLICH(KKFORIENTZEITLICH value) {
        this.kkforientzeitlich = value;
    }

    /**
     * Ruft den Wert der kkferinnern-Eigenschaft ab.
     *
     * @return possible object is {@link KKFERINNERN }
     */
    public KKFERINNERN getKKFERINNERN() {
        return kkferinnern;
    }

    /**
     * Legt den Wert der kkferinnern-Eigenschaft fest.
     *
     * @param value allowed object is {@link KKFERINNERN }
     */
    public void setKKFERINNERN(KKFERINNERN value) {
        this.kkferinnern = value;
    }

    /**
     * Ruft den Wert der kkfhandlungen-Eigenschaft ab.
     *
     * @return possible object is {@link KKFHANDLUNGEN }
     */
    public KKFHANDLUNGEN getKKFHANDLUNGEN() {
        return kkfhandlungen;
    }

    /**
     * Legt den Wert der kkfhandlungen-Eigenschaft fest.
     *
     * @param value allowed object is {@link KKFHANDLUNGEN }
     */
    public void setKKFHANDLUNGEN(KKFHANDLUNGEN value) {
        this.kkfhandlungen = value;
    }

    /**
     * Ruft den Wert der kkfentscheidungen-Eigenschaft ab.
     *
     * @return possible object is {@link KKFENTSCHEIDUNGEN }
     */
    public KKFENTSCHEIDUNGEN getKKFENTSCHEIDUNGEN() {
        return kkfentscheidungen;
    }

    /**
     * Legt den Wert der kkfentscheidungen-Eigenschaft fest.
     *
     * @param value allowed object is {@link KKFENTSCHEIDUNGEN }
     */
    public void setKKFENTSCHEIDUNGEN(KKFENTSCHEIDUNGEN value) {
        this.kkfentscheidungen = value;
    }

    /**
     * Ruft den Wert der kkfversteheninfo-Eigenschaft ab.
     *
     * @return possible object is {@link KKFVERSTEHENINFO }
     */
    public KKFVERSTEHENINFO getKKFVERSTEHENINFO() {
        return kkfversteheninfo;
    }

    /**
     * Legt den Wert der kkfversteheninfo-Eigenschaft fest.
     *
     * @param value allowed object is {@link KKFVERSTEHENINFO }
     */
    public void setKKFVERSTEHENINFO(KKFVERSTEHENINFO value) {
        this.kkfversteheninfo = value;
    }

    /**
     * Ruft den Wert der kkfgefahrerkennen-Eigenschaft ab.
     *
     * @return possible object is {@link KKFGEFAHRERKENNEN }
     */
    public KKFGEFAHRERKENNEN getKKFGEFAHRERKENNEN() {
        return kkfgefahrerkennen;
    }

    /**
     * Legt den Wert der kkfgefahrerkennen-Eigenschaft fest.
     *
     * @param value allowed object is {@link KKFGEFAHRERKENNEN }
     */
    public void setKKFGEFAHRERKENNEN(KKFGEFAHRERKENNEN value) {
        this.kkfgefahrerkennen = value;
    }

    /**
     * Ruft den Wert der kkfmitteilen-Eigenschaft ab.
     *
     * @return possible object is {@link KKFMITTEILEN }
     */
    public KKFMITTEILEN getKKFMITTEILEN() {
        return kkfmitteilen;
    }

    /**
     * Legt den Wert der kkfmitteilen-Eigenschaft fest.
     *
     * @param value allowed object is {@link KKFMITTEILEN }
     */
    public void setKKFMITTEILEN(KKFMITTEILEN value) {
        this.kkfmitteilen = value;
    }

    /**
     * Ruft den Wert der kkfverstehenauf-Eigenschaft ab.
     *
     * @return possible object is {@link KKFVERSTEHENAUF }
     */
    public KKFVERSTEHENAUF getKKFVERSTEHENAUF() {
        return kkfverstehenauf;
    }

    /**
     * Legt den Wert der kkfverstehenauf-Eigenschaft fest.
     *
     * @param value allowed object is {@link KKFVERSTEHENAUF }
     */
    public void setKKFVERSTEHENAUF(KKFVERSTEHENAUF value) {
        this.kkfverstehenauf = value;
    }

    /**
     * Ruft den Wert der kkfbeteiligung-Eigenschaft ab.
     *
     * @return possible object is {@link KKFBETEILIGUNG }
     */
    public KKFBETEILIGUNG getKKFBETEILIGUNG() {
        return kkfbeteiligung;
    }

    /**
     * Legt den Wert der kkfbeteiligung-Eigenschaft fest.
     *
     * @param value allowed object is {@link KKFBETEILIGUNG }
     */
    public void setKKFBETEILIGUNG(KKFBETEILIGUNG value) {
        this.kkfbeteiligung = value;
    }

    /**
     * Ruft den Wert der svernaehrung-Eigenschaft ab.
     *
     * @return possible object is {@link SVERNAEHRUNG }
     */
    public SVERNAEHRUNG getSVERNAEHRUNG() {
        return svernaehrung;
    }

    /**
     * Legt den Wert der svernaehrung-Eigenschaft fest.
     *
     * @param value allowed object is {@link SVERNAEHRUNG }
     */
    public void setSVERNAEHRUNG(SVERNAEHRUNG value) {
        this.svernaehrung = value;
    }

    /**
     * Ruft den Wert der svfremdhilfe-Eigenschaft ab.
     *
     * @return possible object is {@link SVFREMDHILFE }
     */
    public SVFREMDHILFE getSVFREMDHILFE() {
        return svfremdhilfe;
    }

    /**
     * Legt den Wert der svfremdhilfe-Eigenschaft fest.
     *
     * @param value allowed object is {@link SVFREMDHILFE }
     */
    public void setSVFREMDHILFE(SVFREMDHILFE value) {
        this.svfremdhilfe = value;
    }

    /**
     * Ruft den Wert der svernaehrungumfang-Eigenschaft ab.
     *
     * @return possible object is {@link SVERNAEHRUNGUMFANG }
     */
    public SVERNAEHRUNGUMFANG getSVERNAEHRUNGUMFANG() {
        return svernaehrungumfang;
    }

    /**
     * Legt den Wert der svernaehrungumfang-Eigenschaft fest.
     *
     * @param value allowed object is {@link SVERNAEHRUNGUMFANG }
     */
    public void setSVERNAEHRUNGUMFANG(SVERNAEHRUNGUMFANG value) {
        this.svernaehrungumfang = value;
    }

    /**
     * Ruft den Wert der svharnkontinenz-Eigenschaft ab.
     *
     * @return possible object is {@link SVHARNKONTINENZ }
     */
    public SVHARNKONTINENZ getSVHARNKONTINENZ() {
        return svharnkontinenz;
    }

    /**
     * Legt den Wert der svharnkontinenz-Eigenschaft fest.
     *
     * @param value allowed object is {@link SVHARNKONTINENZ }
     */
    public void setSVHARNKONTINENZ(SVHARNKONTINENZ value) {
        this.svharnkontinenz = value;
    }

    /**
     * Ruft den Wert der svstuhlkontinenz-Eigenschaft ab.
     *
     * @return possible object is {@link SVSTUHLKONTINENZ }
     */
    public SVSTUHLKONTINENZ getSVSTUHLKONTINENZ() {
        return svstuhlkontinenz;
    }

    /**
     * Legt den Wert der svstuhlkontinenz-Eigenschaft fest.
     *
     * @param value allowed object is {@link SVSTUHLKONTINENZ }
     */
    public void setSVSTUHLKONTINENZ(SVSTUHLKONTINENZ value) {
        this.svstuhlkontinenz = value;
    }

    /**
     * Ruft den Wert der svoberkoerper-Eigenschaft ab.
     *
     * @return possible object is {@link SVOBERKOERPER }
     */
    public SVOBERKOERPER getSVOBERKOERPER() {
        return svoberkoerper;
    }

    /**
     * Legt den Wert der svoberkoerper-Eigenschaft fest.
     *
     * @param value allowed object is {@link SVOBERKOERPER }
     */
    public void setSVOBERKOERPER(SVOBERKOERPER value) {
        this.svoberkoerper = value;
    }

    /**
     * Ruft den Wert der svkopf-Eigenschaft ab.
     *
     * @return possible object is {@link SVKOPF }
     */
    public SVKOPF getSVKOPF() {
        return svkopf;
    }

    /**
     * Legt den Wert der svkopf-Eigenschaft fest.
     *
     * @param value allowed object is {@link SVKOPF }
     */
    public void setSVKOPF(SVKOPF value) {
        this.svkopf = value;
    }

    /**
     * Ruft den Wert der svintimbereich-Eigenschaft ab.
     *
     * @return possible object is {@link SVINTIMBEREICH }
     */
    public SVINTIMBEREICH getSVINTIMBEREICH() {
        return svintimbereich;
    }

    /**
     * Legt den Wert der svintimbereich-Eigenschaft fest.
     *
     * @param value allowed object is {@link SVINTIMBEREICH }
     */
    public void setSVINTIMBEREICH(SVINTIMBEREICH value) {
        this.svintimbereich = value;
    }

    /**
     * Ruft den Wert der svduschenbaden-Eigenschaft ab.
     *
     * @return possible object is {@link SVDUSCHENBADEN }
     */
    public SVDUSCHENBADEN getSVDUSCHENBADEN() {
        return svduschenbaden;
    }

    /**
     * Legt den Wert der svduschenbaden-Eigenschaft fest.
     *
     * @param value allowed object is {@link SVDUSCHENBADEN }
     */
    public void setSVDUSCHENBADEN(SVDUSCHENBADEN value) {
        this.svduschenbaden = value;
    }

    /**
     * Ruft den Wert der svanausoberkoerper-Eigenschaft ab.
     *
     * @return possible object is {@link SVANAUSOBERKOERPER }
     */
    public SVANAUSOBERKOERPER getSVANAUSOBERKOERPER() {
        return svanausoberkoerper;
    }

    /**
     * Legt den Wert der svanausoberkoerper-Eigenschaft fest.
     *
     * @param value allowed object is {@link SVANAUSOBERKOERPER }
     */
    public void setSVANAUSOBERKOERPER(SVANAUSOBERKOERPER value) {
        this.svanausoberkoerper = value;
    }

    /**
     * Ruft den Wert der svanausunterkoerper-Eigenschaft ab.
     *
     * @return possible object is {@link SVANAUSUNTERKOERPER }
     */
    public SVANAUSUNTERKOERPER getSVANAUSUNTERKOERPER() {
        return svanausunterkoerper;
    }

    /**
     * Legt den Wert der svanausunterkoerper-Eigenschaft fest.
     *
     * @param value allowed object is {@link SVANAUSUNTERKOERPER }
     */
    public void setSVANAUSUNTERKOERPER(SVANAUSUNTERKOERPER value) {
        this.svanausunterkoerper = value;
    }

    /**
     * Ruft den Wert der svnahrungzubereiten-Eigenschaft ab.
     *
     * @return possible object is {@link SVNAHRUNGZUBEREITEN }
     */
    public SVNAHRUNGZUBEREITEN getSVNAHRUNGZUBEREITEN() {
        return svnahrungzubereiten;
    }

    /**
     * Legt den Wert der svnahrungzubereiten-Eigenschaft fest.
     *
     * @param value allowed object is {@link SVNAHRUNGZUBEREITEN }
     */
    public void setSVNAHRUNGZUBEREITEN(SVNAHRUNGZUBEREITEN value) {
        this.svnahrungzubereiten = value;
    }

    /**
     * Ruft den Wert der svessen-Eigenschaft ab.
     *
     * @return possible object is {@link SVESSEN }
     */
    public SVESSEN getSVESSEN() {
        return svessen;
    }

    /**
     * Legt den Wert der svessen-Eigenschaft fest.
     *
     * @param value allowed object is {@link SVESSEN }
     */
    public void setSVESSEN(SVESSEN value) {
        this.svessen = value;
    }

    /**
     * Ruft den Wert der svtrinken-Eigenschaft ab.
     *
     * @return possible object is {@link SVTRINKEN }
     */
    public SVTRINKEN getSVTRINKEN() {
        return svtrinken;
    }

    /**
     * Legt den Wert der svtrinken-Eigenschaft fest.
     *
     * @param value allowed object is {@link SVTRINKEN }
     */
    public void setSVTRINKEN(SVTRINKEN value) {
        this.svtrinken = value;
    }

    /**
     * Ruft den Wert der svtoilette-Eigenschaft ab.
     *
     * @return possible object is {@link SVTOILETTE }
     */
    public SVTOILETTE getSVTOILETTE() {
        return svtoilette;
    }

    /**
     * Legt den Wert der svtoilette-Eigenschaft fest.
     *
     * @param value allowed object is {@link SVTOILETTE }
     */
    public void setSVTOILETTE(SVTOILETTE value) {
        this.svtoilette = value;
    }

    /**
     * Ruft den Wert der svharnkontinenzbew-Eigenschaft ab.
     *
     * @return possible object is {@link SVHARNKONTINENZBEW }
     */
    public SVHARNKONTINENZBEW getSVHARNKONTINENZBEW() {
        return svharnkontinenzbew;
    }

    /**
     * Legt den Wert der svharnkontinenzbew-Eigenschaft fest.
     *
     * @param value allowed object is {@link SVHARNKONTINENZBEW }
     */
    public void setSVHARNKONTINENZBEW(SVHARNKONTINENZBEW value) {
        this.svharnkontinenzbew = value;
    }

    /**
     * Ruft den Wert der svstuhlkontinenzbew-Eigenschaft ab.
     *
     * @return possible object is {@link SVSTUHLKONTINENZBEW }
     */
    public SVSTUHLKONTINENZBEW getSVSTUHLKONTINENZBEW() {
        return svstuhlkontinenzbew;
    }

    /**
     * Legt den Wert der svstuhlkontinenzbew-Eigenschaft fest.
     *
     * @param value allowed object is {@link SVSTUHLKONTINENZBEW }
     */
    public void setSVSTUHLKONTINENZBEW(SVSTUHLKONTINENZBEW value) {
        this.svstuhlkontinenzbew = value;
    }

    /**
     * Ruft den Wert der gatagesablauf-Eigenschaft ab.
     *
     * @return possible object is {@link GATAGESABLAUF }
     */
    public GATAGESABLAUF getGATAGESABLAUF() {
        return gatagesablauf;
    }

    /**
     * Legt den Wert der gatagesablauf-Eigenschaft fest.
     *
     * @param value allowed object is {@link GATAGESABLAUF }
     */
    public void setGATAGESABLAUF(GATAGESABLAUF value) {
        this.gatagesablauf = value;
    }

    /**
     * Ruft den Wert der garuhenschlafen-Eigenschaft ab.
     *
     * @return possible object is {@link GARUHENSCHLAFEN }
     */
    public GARUHENSCHLAFEN getGARUHENSCHLAFEN() {
        return garuhenschlafen;
    }

    /**
     * Legt den Wert der garuhenschlafen-Eigenschaft fest.
     *
     * @param value allowed object is {@link GARUHENSCHLAFEN }
     */
    public void setGARUHENSCHLAFEN(GARUHENSCHLAFEN value) {
        this.garuhenschlafen = value;
    }

    /**
     * Ruft den Wert der gabeschaeftigen-Eigenschaft ab.
     *
     * @return possible object is {@link GABESCHAEFTIGEN }
     */
    public GABESCHAEFTIGEN getGABESCHAEFTIGEN() {
        return gabeschaeftigen;
    }

    /**
     * Legt den Wert der gabeschaeftigen-Eigenschaft fest.
     *
     * @param value allowed object is {@link GABESCHAEFTIGEN }
     */
    public void setGABESCHAEFTIGEN(GABESCHAEFTIGEN value) {
        this.gabeschaeftigen = value;
    }

    /**
     * Ruft den Wert der gaplanungen-Eigenschaft ab.
     *
     * @return possible object is {@link GAPLANUNGEN }
     */
    public GAPLANUNGEN getGAPLANUNGEN() {
        return gaplanungen;
    }

    /**
     * Legt den Wert der gaplanungen-Eigenschaft fest.
     *
     * @param value allowed object is {@link GAPLANUNGEN }
     */
    public void setGAPLANUNGEN(GAPLANUNGEN value) {
        this.gaplanungen = value;
    }

    /**
     * Ruft den Wert der gainteraktion-Eigenschaft ab.
     *
     * @return possible object is {@link GAINTERAKTION }
     */
    public GAINTERAKTION getGAINTERAKTION() {
        return gainteraktion;
    }

    /**
     * Legt den Wert der gainteraktion-Eigenschaft fest.
     *
     * @param value allowed object is {@link GAINTERAKTION }
     */
    public void setGAINTERAKTION(GAINTERAKTION value) {
        this.gainteraktion = value;
    }

    /**
     * Ruft den Wert der gakontaktpflege-Eigenschaft ab.
     *
     * @return possible object is {@link GAKONTAKTPFLEGE }
     */
    public GAKONTAKTPFLEGE getGAKONTAKTPFLEGE() {
        return gakontaktpflege;
    }

    /**
     * Legt den Wert der gakontaktpflege-Eigenschaft fest.
     *
     * @param value allowed object is {@link GAKONTAKTPFLEGE }
     */
    public void setGAKONTAKTPFLEGE(GAKONTAKTPFLEGE value) {
        this.gakontaktpflege = value;
    }

    /**
     * Ruft den Wert der dekubitus-Eigenschaft ab.
     *
     * @return possible object is {@link DEKUBITUS }
     */
    public DEKUBITUS getDEKUBITUS() {
        return dekubitus;
    }

    /**
     * Legt den Wert der dekubitus-Eigenschaft fest.
     *
     * @param value allowed object is {@link DEKUBITUS }
     */
    public void setDEKUBITUS(DEKUBITUS value) {
        this.dekubitus = value;
    }

    /**
     * Ruft den Wert der dekubitusstadium-Eigenschaft ab.
     *
     * @return possible object is {@link DEKUBITUSSTADIUM }
     */
    public DEKUBITUSSTADIUM getDEKUBITUSSTADIUM() {
        return dekubitusstadium;
    }

    /**
     * Legt den Wert der dekubitusstadium-Eigenschaft fest.
     *
     * @param value allowed object is {@link DEKUBITUSSTADIUM }
     */
    public void setDEKUBITUSSTADIUM(DEKUBITUSSTADIUM value) {
        this.dekubitusstadium = value;
    }

    /**
     * Ruft den Wert der dekubitus1BEGINNDATUM-Eigenschaft ab.
     *
     * @return possible object is {@link DEKUBITUS1BEGINNDATUM }
     */
    public DEKUBITUS1BEGINNDATUM getDEKUBITUS1BEGINNDATUM() {
        return dekubitus1BEGINNDATUM;
    }

    /**
     * Legt den Wert der dekubitus1BEGINNDATUM-Eigenschaft fest.
     *
     * @param value allowed object is {@link DEKUBITUS1BEGINNDATUM }
     */
    public void setDEKUBITUS1BEGINNDATUM(DEKUBITUS1BEGINNDATUM value) {
        this.dekubitus1BEGINNDATUM = value;
    }

    /**
     * Ruft den Wert der dekubitus1ENDEDATUM-Eigenschaft ab.
     *
     * @return possible object is {@link DEKUBITUS1ENDEDATUM }
     */
    public DEKUBITUS1ENDEDATUM getDEKUBITUS1ENDEDATUM() {
        return dekubitus1ENDEDATUM;
    }

    /**
     * Legt den Wert der dekubitus1ENDEDATUM-Eigenschaft fest.
     *
     * @param value allowed object is {@link DEKUBITUS1ENDEDATUM }
     */
    public void setDEKUBITUS1ENDEDATUM(DEKUBITUS1ENDEDATUM value) {
        this.dekubitus1ENDEDATUM = value;
    }

    /**
     * Ruft den Wert der dekubitus1LOK-Eigenschaft ab.
     *
     * @return possible object is {@link DEKUBITUS1LOK }
     */
    public DEKUBITUS1LOK getDEKUBITUS1LOK() {
        return dekubitus1LOK;
    }

    /**
     * Legt den Wert der dekubitus1LOK-Eigenschaft fest.
     *
     * @param value allowed object is {@link DEKUBITUS1LOK }
     */
    public void setDEKUBITUS1LOK(DEKUBITUS1LOK value) {
        this.dekubitus1LOK = value;
    }

    /**
     * Ruft den Wert der dekubitus2BEGINNDATUM-Eigenschaft ab.
     *
     * @return possible object is {@link DEKUBITUS2BEGINNDATUM }
     */
    public DEKUBITUS2BEGINNDATUM getDEKUBITUS2BEGINNDATUM() {
        return dekubitus2BEGINNDATUM;
    }

    /**
     * Legt den Wert der dekubitus2BEGINNDATUM-Eigenschaft fest.
     *
     * @param value allowed object is {@link DEKUBITUS2BEGINNDATUM }
     */
    public void setDEKUBITUS2BEGINNDATUM(DEKUBITUS2BEGINNDATUM value) {
        this.dekubitus2BEGINNDATUM = value;
    }

    /**
     * Ruft den Wert der dekubitus2ENDEDATUM-Eigenschaft ab.
     *
     * @return possible object is {@link DEKUBITUS2ENDEDATUM }
     */
    public DEKUBITUS2ENDEDATUM getDEKUBITUS2ENDEDATUM() {
        return dekubitus2ENDEDATUM;
    }

    /**
     * Legt den Wert der dekubitus2ENDEDATUM-Eigenschaft fest.
     *
     * @param value allowed object is {@link DEKUBITUS2ENDEDATUM }
     */
    public void setDEKUBITUS2ENDEDATUM(DEKUBITUS2ENDEDATUM value) {
        this.dekubitus2ENDEDATUM = value;
    }

    /**
     * Ruft den Wert der dekubitus2LOK-Eigenschaft ab.
     *
     * @return possible object is {@link DEKUBITUS2LOK }
     */
    public DEKUBITUS2LOK getDEKUBITUS2LOK() {
        return dekubitus2LOK;
    }

    /**
     * Legt den Wert der dekubitus2LOK-Eigenschaft fest.
     *
     * @param value allowed object is {@link DEKUBITUS2LOK }
     */
    public void setDEKUBITUS2LOK(DEKUBITUS2LOK value) {
        this.dekubitus2LOK = value;
    }

    /**
     * Ruft den Wert der koerpergroesse-Eigenschaft ab.
     *
     * @return possible object is {@link KOERPERGROESSE }
     */
    public KOERPERGROESSE getKOERPERGROESSE() {
        return koerpergroesse;
    }

    /**
     * Legt den Wert der koerpergroesse-Eigenschaft fest.
     *
     * @param value allowed object is {@link KOERPERGROESSE }
     */
    public void setKOERPERGROESSE(KOERPERGROESSE value) {
        this.koerpergroesse = value;
    }

    /**
     * Ruft den Wert der koerpergewicht-Eigenschaft ab.
     *
     * @return possible object is {@link KOERPERGEWICHT }
     */
    public KOERPERGEWICHT getKOERPERGEWICHT() {
        return koerpergewicht;
    }

    /**
     * Legt den Wert der koerpergewicht-Eigenschaft fest.
     *
     * @param value allowed object is {@link KOERPERGEWICHT }
     */
    public void setKOERPERGEWICHT(KOERPERGEWICHT value) {
        this.koerpergewicht = value;
    }

    /**
     * Ruft den Wert der koerpergewichtdatum-Eigenschaft ab.
     *
     * @return possible object is {@link KOERPERGEWICHTDATUM }
     */
    public KOERPERGEWICHTDATUM getKOERPERGEWICHTDATUM() {
        return koerpergewichtdatum;
    }

    /**
     * Legt den Wert der koerpergewichtdatum-Eigenschaft fest.
     *
     * @param value allowed object is {@link KOERPERGEWICHTDATUM }
     */
    public void setKOERPERGEWICHTDATUM(KOERPERGEWICHTDATUM value) {
        this.koerpergewichtdatum = value;
    }

    /**
     * Gets the value of the koerpergewichtdoku property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the koerpergewichtdoku property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKOERPERGEWICHTDOKU().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link KOERPERGEWICHTDOKU }
     */
    public List<KOERPERGEWICHTDOKU> getKOERPERGEWICHTDOKU() {
        if (koerpergewichtdoku == null) {
            koerpergewichtdoku = new ArrayList<KOERPERGEWICHTDOKU>();
        }
        return this.koerpergewichtdoku;
    }

    /**
     * Ruft den Wert der sturz-Eigenschaft ab.
     *
     * @return possible object is {@link STURZ }
     */
    public STURZ getSTURZ() {
        return sturz;
    }

    /**
     * Legt den Wert der sturz-Eigenschaft fest.
     *
     * @param value allowed object is {@link STURZ }
     */
    public void setSTURZ(STURZ value) {
        this.sturz = value;
    }

    /**
     * Gets the value of the sturzfolgen property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the sturzfolgen property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSTURZFOLGEN().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link STURZFOLGEN }
     */
    public List<STURZFOLGEN> getSTURZFOLGEN() {
        if (sturzfolgen == null) {
            sturzfolgen = new ArrayList<STURZFOLGEN>();
        }
        return this.sturzfolgen;
    }

    /**
     * Ruft den Wert der gurt-Eigenschaft ab.
     *
     * @return possible object is {@link GURT }
     */
    public GURT getGURT() {
        return gurt;
    }

    /**
     * Legt den Wert der gurt-Eigenschaft fest.
     *
     * @param value allowed object is {@link GURT }
     */
    public void setGURT(GURT value) {
        this.gurt = value;
    }

    /**
     * Ruft den Wert der gurthaufigkeit-Eigenschaft ab.
     *
     * @return possible object is {@link GURTHAUFIGKEIT }
     */
    public GURTHAUFIGKEIT getGURTHAUFIGKEIT() {
        return gurthaufigkeit;
    }

    /**
     * Legt den Wert der gurthaufigkeit-Eigenschaft fest.
     *
     * @param value allowed object is {@link GURTHAUFIGKEIT }
     */
    public void setGURTHAUFIGKEIT(GURTHAUFIGKEIT value) {
        this.gurthaufigkeit = value;
    }

    /**
     * Ruft den Wert der seitenteile-Eigenschaft ab.
     *
     * @return possible object is {@link SEITENTEILE }
     */
    public SEITENTEILE getSEITENTEILE() {
        return seitenteile;
    }

    /**
     * Legt den Wert der seitenteile-Eigenschaft fest.
     *
     * @param value allowed object is {@link SEITENTEILE }
     */
    public void setSEITENTEILE(SEITENTEILE value) {
        this.seitenteile = value;
    }

    /**
     * Ruft den Wert der seitenteilehaufigkeit-Eigenschaft ab.
     *
     * @return possible object is {@link SEITENTEILEHAUFIGKEIT }
     */
    public SEITENTEILEHAUFIGKEIT getSEITENTEILEHAUFIGKEIT() {
        return seitenteilehaufigkeit;
    }

    /**
     * Legt den Wert der seitenteilehaufigkeit-Eigenschaft fest.
     *
     * @param value allowed object is {@link SEITENTEILEHAUFIGKEIT }
     */
    public void setSEITENTEILEHAUFIGKEIT(SEITENTEILEHAUFIGKEIT value) {
        this.seitenteilehaufigkeit = value;
    }

    /**
     * Ruft den Wert der schmerzen-Eigenschaft ab.
     *
     * @return possible object is {@link SCHMERZEN }
     */
    public SCHMERZEN getSCHMERZEN() {
        return schmerzen;
    }

    /**
     * Legt den Wert der schmerzen-Eigenschaft fest.
     *
     * @param value allowed object is {@link SCHMERZEN }
     */
    public void setSCHMERZEN(SCHMERZEN value) {
        this.schmerzen = value;
    }

    /**
     * Ruft den Wert der schmerzfrei-Eigenschaft ab.
     *
     * @return possible object is {@link SCHMERZFREI }
     */
    public SCHMERZFREI getSCHMERZFREI() {
        return schmerzfrei;
    }

    /**
     * Legt den Wert der schmerzfrei-Eigenschaft fest.
     *
     * @param value allowed object is {@link SCHMERZFREI }
     */
    public void setSCHMERZFREI(SCHMERZFREI value) {
        this.schmerzfrei = value;
    }

    /**
     * Ruft den Wert der schmerzeinsch-Eigenschaft ab.
     *
     * @return possible object is {@link SCHMERZEINSCH }
     */
    public SCHMERZEINSCH getSCHMERZEINSCH() {
        return schmerzeinsch;
    }

    /**
     * Legt den Wert der schmerzeinsch-Eigenschaft fest.
     *
     * @param value allowed object is {@link SCHMERZEINSCH }
     */
    public void setSCHMERZEINSCH(SCHMERZEINSCH value) {
        this.schmerzeinsch = value;
    }

    /**
     * Ruft den Wert der schmerzeinschdatum-Eigenschaft ab.
     *
     * @return possible object is {@link SCHMERZEINSCHDATUM }
     */
    public SCHMERZEINSCHDATUM getSCHMERZEINSCHDATUM() {
        return schmerzeinschdatum;
    }

    /**
     * Legt den Wert der schmerzeinschdatum-Eigenschaft fest.
     *
     * @param value allowed object is {@link SCHMERZEINSCHDATUM }
     */
    public void setSCHMERZEINSCHDATUM(SCHMERZEINSCHDATUM value) {
        this.schmerzeinschdatum = value;
    }

    /**
     * Gets the value of the schmerzeinschinfo property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the schmerzeinschinfo property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSCHMERZEINSCHINFO().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link SCHMERZEINSCHINFO }
     */
    public List<SCHMERZEINSCHINFO> getSCHMERZEINSCHINFO() {
        if (schmerzeinschinfo == null) {
            schmerzeinschinfo = new ArrayList<SCHMERZEINSCHINFO>();
        }
        return this.schmerzeinschinfo;
    }

    /**
     * Ruft den Wert der neueinzug-Eigenschaft ab.
     *
     * @return possible object is {@link NEUEINZUG }
     */
    public NEUEINZUG getNEUEINZUG() {
        return neueinzug;
    }

    /**
     * Legt den Wert der neueinzug-Eigenschaft fest.
     *
     * @param value allowed object is {@link NEUEINZUG }
     */
    public void setNEUEINZUG(NEUEINZUG value) {
        this.neueinzug = value;
    }

    /**
     * Ruft den Wert der einzugnachkz-Eigenschaft ab.
     *
     * @return possible object is {@link EINZUGNACHKZ }
     */
    public EINZUGNACHKZ getEINZUGNACHKZ() {
        return einzugnachkz;
    }

    /**
     * Legt den Wert der einzugnachkz-Eigenschaft fest.
     *
     * @param value allowed object is {@link EINZUGNACHKZ }
     */
    public void setEINZUGNACHKZ(EINZUGNACHKZ value) {
        this.einzugnachkz = value;
    }

    /**
     * Ruft den Wert der einzugnachkzdatum-Eigenschaft ab.
     *
     * @return possible object is {@link EINZUGNACHKZDATUM }
     */
    public EINZUGNACHKZDATUM getEINZUGNACHKZDATUM() {
        return einzugnachkzdatum;
    }

    /**
     * Legt den Wert der einzugnachkzdatum-Eigenschaft fest.
     *
     * @param value allowed object is {@link EINZUGNACHKZDATUM }
     */
    public void setEINZUGNACHKZDATUM(EINZUGNACHKZDATUM value) {
        this.einzugnachkzdatum = value;
    }

    /**
     * Ruft den Wert der einzugkhbehandlung-Eigenschaft ab.
     *
     * @return possible object is {@link EINZUGKHBEHANDLUNG }
     */
    public EINZUGKHBEHANDLUNG getEINZUGKHBEHANDLUNG() {
        return einzugkhbehandlung;
    }

    /**
     * Legt den Wert der einzugkhbehandlung-Eigenschaft fest.
     *
     * @param value allowed object is {@link EINZUGKHBEHANDLUNG }
     */
    public void setEINZUGKHBEHANDLUNG(EINZUGKHBEHANDLUNG value) {
        this.einzugkhbehandlung = value;
    }

    /**
     * Ruft den Wert der einzugkhbeginndatum-Eigenschaft ab.
     *
     * @return possible object is {@link EINZUGKHBEGINNDATUM }
     */
    public EINZUGKHBEGINNDATUM getEINZUGKHBEGINNDATUM() {
        return einzugkhbeginndatum;
    }

    /**
     * Legt den Wert der einzugkhbeginndatum-Eigenschaft fest.
     *
     * @param value allowed object is {@link EINZUGKHBEGINNDATUM }
     */
    public void setEINZUGKHBEGINNDATUM(EINZUGKHBEGINNDATUM value) {
        this.einzugkhbeginndatum = value;
    }

    /**
     * Ruft den Wert der einzugkhendedatum-Eigenschaft ab.
     *
     * @return possible object is {@link EINZUGKHENDEDATUM }
     */
    public EINZUGKHENDEDATUM getEINZUGKHENDEDATUM() {
        return einzugkhendedatum;
    }

    /**
     * Legt den Wert der einzugkhendedatum-Eigenschaft fest.
     *
     * @param value allowed object is {@link EINZUGKHENDEDATUM }
     */
    public void setEINZUGKHENDEDATUM(EINZUGKHENDEDATUM value) {
        this.einzugkhendedatum = value;
    }

    /**
     * Ruft den Wert der einzuggespr-Eigenschaft ab.
     *
     * @return possible object is {@link EINZUGGESPR }
     */
    public EINZUGGESPR getEINZUGGESPR() {
        return einzuggespr;
    }

    /**
     * Legt den Wert der einzuggespr-Eigenschaft fest.
     *
     * @param value allowed object is {@link EINZUGGESPR }
     */
    public void setEINZUGGESPR(EINZUGGESPR value) {
        this.einzuggespr = value;
    }

    /**
     * Ruft den Wert der einzuggesprdatum-Eigenschaft ab.
     *
     * @return possible object is {@link EINZUGGESPRDATUM }
     */
    public EINZUGGESPRDATUM getEINZUGGESPRDATUM() {
        return einzuggesprdatum;
    }

    /**
     * Legt den Wert der einzuggesprdatum-Eigenschaft fest.
     *
     * @param value allowed object is {@link EINZUGGESPRDATUM }
     */
    public void setEINZUGGESPRDATUM(EINZUGGESPRDATUM value) {
        this.einzuggesprdatum = value;
    }

    /**
     * Gets the value of the einzuggesprteilnehmer property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the einzuggesprteilnehmer property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEINZUGGESPRTEILNEHMER().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link EINZUGGESPRTEILNEHMER }
     */
    public List<EINZUGGESPRTEILNEHMER> getEINZUGGESPRTEILNEHMER() {
        if (einzuggesprteilnehmer == null) {
            einzuggesprteilnehmer = new ArrayList<EINZUGGESPRTEILNEHMER>();
        }
        return this.einzuggesprteilnehmer;
    }

    /**
     * Ruft den Wert der einzuggesprdoku-Eigenschaft ab.
     *
     * @return possible object is {@link EINZUGGESPRDOKU }
     */
    public EINZUGGESPRDOKU getEINZUGGESPRDOKU() {
        return einzuggesprdoku;
    }

    /**
     * Legt den Wert der einzuggesprdoku-Eigenschaft fest.
     *
     * @param value allowed object is {@link EINZUGGESPRDOKU }
     */
    public void setEINZUGGESPRDOKU(EINZUGGESPRDOKU value) {
        this.einzuggesprdoku = value;
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class AMPUTATION {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
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
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class APOPLEX {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
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
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jnBeatmung_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class BEATMUNG {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_bewusstseinszustand_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class BEWUSSTSEINSZUSTAND {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jnAnzahl_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DEKUBITUS {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
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
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
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
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_lokalisation_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DEKUBITUS1LOK {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
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
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
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
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_lokalisation_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DEKUBITUS2LOK {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_dekubitusStadium_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DEKUBITUSSTADIUM {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_diagnose_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DIAGNOSEN {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_jnNichtMoeglich_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class EINZUGGESPR {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
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
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class EINZUGGESPRDOKU {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_teilnehmer_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class EINZUGGESPRTEILNEHMER {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
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
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class EINZUGKHBEHANDLUNG {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
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
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class EINZUGNACHKZ {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class EINZUGNACHKZDATUM {

        @XmlAttribute(name = "value")
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
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
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
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
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class FRAKTUR {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
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
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GABESCHAEFTIGEN {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GAINTERAKTION {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GAKONTAKTPFLEGE {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GAPLANUNGEN {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GARUHENSCHLAFEN {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GATAGESABLAUF {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GEBURTSJAHR {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_monat_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GEBURTSMONAT {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_geschlecht_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GESCHLECHT {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GURT {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_frequenz_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GURTHAUFIGKEIT {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class HERZINFARKT {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
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
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;pattern value="[0-9]{6}"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class IDBEWOHNER {

        @XmlAttribute(name = "value", required = true)
        protected String value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link String }
         */
        public String getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link String }
         */
        public void setValue(String value) {
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
     *       &lt;attribute name="value">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *             &lt;totalDigits value="2"/>
     *             &lt;minInclusive value="2"/>
     *             &lt;maxInclusive value="10"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KHBANZAHLAUFENTHALTE {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *             &lt;totalDigits value="3"/>
     *             &lt;minInclusive value="2"/>
     *             &lt;maxInclusive value="200"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KHBANZAHLTAGE {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
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
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jnAnzahl_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KHBEHANDLUNG {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
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
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFBETEILIGUNG {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFENTSCHEIDUNGEN {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFERINNERN {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFERKENNEN {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFGEFAHRERKENNEN {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFHANDLUNGEN {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFMITTEILEN {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFORIENTOERTLICH {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFORIENTZEITLICH {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFVERSTEHENAUF {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_vorhandenseinStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KKFVERSTEHENINFO {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
     *             &lt;totalDigits value="5"/>
     *             &lt;maxInclusive value="500"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KOERPERGEWICHT {

        @XmlAttribute(name = "value")
        protected BigDecimal value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link BigDecimal }
         */
        public BigDecimal getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link BigDecimal }
         */
        public void setValue(BigDecimal value) {
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
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
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
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_gewichtsverlust_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KOERPERGEWICHTDOKU {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *             &lt;totalDigits value="3"/>
     *             &lt;minInclusive value="100"/>
     *             &lt;maxInclusive value="250"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class KOERPERGROESSE {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class MOBILFORTBEWEGUNG {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class MOBILPOSWECHSEL {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class MOBILSITZPOSITION {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class MOBILTREPPENSTEIGEN {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class MOBILUMSETZEN {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class NEUEINZUG {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_pflegegrad_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class PFLEGEGRAD {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SCHMERZEINSCH {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}date" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
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
         * @return possible object is {@link XMLGregorianCalendar }
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link XMLGregorianCalendar }
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_schmerzeinschaetzung_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SCHMERZEINSCHINFO {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SCHMERZEN {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_jn_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SCHMERZFREI {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SEITENTEILE {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_frequenz_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SEITENTEILEHAUFIGKEIT {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jnAnzahl_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class STURZ {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_sturzfolgen_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class STURZFOLGEN {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVANAUSOBERKOERPER {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVANAUSUNTERKOERPER {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVDUSCHENBADEN {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_jn_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVERNAEHRUNG {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_kuenstlicheErnaehrung_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVERNAEHRUNGUMFANG {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitTyp2_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVESSEN {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_fremdhilfe_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVFREMDHILFE {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_harnkontinenz_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVHARNKONTINENZ {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVHARNKONTINENZBEW {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVINTIMBEREICH {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVKOPF {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVNAHRUNGZUBEREITEN {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVOBERKOERPER {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_stuhlkontinenz_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVSTUHLKONTINENZ {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" type="{https://www.das-pflege.de}enum_selbststaendigkeitStandard_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVSTUHLKONTINENZBEW {

        @XmlAttribute(name = "value")
        protected Integer value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link Integer }
         */
        public Integer getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link Integer }
         */
        public void setValue(Integer value) {
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitTyp1_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVTOILETTE {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value" use="required" type="{https://www.das-pflege.de}enum_selbststaendigkeitTyp1_type" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SVTRINKEN {

        @XmlAttribute(name = "value", required = true)
        protected int value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         */
        public int getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
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
     *       &lt;attribute name="value">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;maxLength value="255"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class WOHNBEREICH {

        @XmlAttribute(name = "value")
        protected String value;

        /**
         * Ruft den Wert der value-Eigenschaft ab.
         *
         * @return possible object is {@link String }
         */
        public String getValue() {
            return value;
        }

        /**
         * Legt den Wert der value-Eigenschaft fest.
         *
         * @param value allowed object is {@link String }
         */
        public void setValue(String value) {
            this.value = value;
        }

    }

}
