//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2019.10.30 um 10:38:41 AM CET 
//


package de.offene_pflege.services.qdvs.spec14.schema;

import de.offene_pflege.op.OPDE;

import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * de.xmltest.schema package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation for XML content. The Java representation of XML content can
 * consist of schema derived interfaces and classes representing the binding of schema type definitions, element
 * declarations and model groups.  Factory methods for each of these are provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Root_QNAME = new QName("https://www.das-pflege.de", "root");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
     * de.xmltest.schema
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DasQsDataFacilityType }
     */
    public DasQsDataFacilityType createDasQsDataFacilityType() {
        return new DasQsDataFacilityType();
    }

    /**
     * Create an instance of {@link DasQsDataType }
     */
    public DasQsDataType createDasQsDataType() {
        return new DasQsDataType();
    }

    /**
     * Create an instance of {@link DasCommentationType }
     */
    public DasCommentationType createDasCommentationType() {
        return new DasCommentationType();
    }

    /**
     * Create an instance of {@link DasQsDataMdsType }
     */
    public DasQsDataMdsType createDasQsDataMdsType() {
        return new DasQsDataMdsType();
    }

    /**
     * Create an instance of {@link RootType }
     */
    public RootType createRootType() {
        return new RootType();
    }

    /**
     * Create an instance of {@link DateType }
     */
    public DateType createDateType() {
        return createDateType(LocalDate.now());
    }

    /**
     * Create an instance of {@link DateType }
     */
    public DateType createDateType(LocalDate ld) {
        DateType dt = new DateType();
        try {
            dt.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(ld.toString()));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return dt;
    }

    public DateType createDateType(LocalDateTime ld) {
        return createDateType(ld.toLocalDate());
    }

    /**
     * Create an instance of {@link NumberType }
     */
    public NumberType createNumberType() {
        return new NumberType();
    }

    /**
     * Create an instance of {@link SpecificationType }
     * <p>
     * Versionskürzel der in der Datenlieferung verwendeten Spezifikation
     */
    public SpecificationType createSpecificationType() {
        return new SpecificationType();
    }

    /**
     * Create an instance of {@link ErrorType }
     */
    public ErrorType createErrorType() {
        return new ErrorType();
    }

    /**
     * Create an instance of {@link BodyType }
     */
    public BodyType createBodyType() {
        return new BodyType();
    }

    /**
     * Create an instance of {@link TextType }
     */
    public TextType createTextType(String text) {
        TextType textType = new TextType();
        textType.setValue(text);
        return textType;

    }

    /**
     * Create an instance of {@link LongTextType }
     */
    public LongTextType createLongTextType() {
        return new LongTextType();
    }

    /**
     * Create an instance of {@link HeaderType }
     */
    public HeaderType createHeaderType() {
        return new HeaderType();
    }

    /**
     * Create an instance of {@link ValRuleType }
     */
    public ValRuleType createValRuleType() {
        return new ValRuleType();
    }

    /**
     * Create an instance of {@link GuidType } Eindeutige ID des Dokuments.
     * <p>
     * Die ID wird vom Dokumentersteller generiert und ist anschließend nicht mehr modifizierbar. Die ID dient
     * vorwiegend der Kommunikation. Jede Datenlieferung einer Einrichtung muss über eine eindeutige ID verfügen. Die
     * mehrfache Verwendung der gleichen ID ist somit ausgeschlossen.
     */
    public GuidType createGuidType() {
        GuidType guidType = new GuidType();
        guidType.setValue(UUID.randomUUID().toString());
        return guidType;
    }

    /**
     * Create an instance of {@link RegistrationType }
     */
    public RegistrationType createRegistrationType() {
        return new RegistrationType();
    }

    /**
     * Create an instance of {@link ResidentsType }
     */
    public ResidentsType createResidentsType() {
        return new ResidentsType();
    }

    /**
     * Create an instance of {@link ValidationStatusType }
     */
    public ValidationStatusType createValidationStatusType() {
        return new ValidationStatusType();
    }

    public int jntype(boolean ja){
        return new JN_TYPE(ja).getJN();
    }

    /**
     * Create an instance of {@link CareDataType }
     */
    public CareDataType createCareDataType() {
        return new CareDataType();
    }

    /**
     * Create an instance of {@link DocumentType }
     */
    public DocumentType createDocumentType() {
        return new DocumentType();
    }

    /**
     * Create an instance of {@link DateTimeType }  https://stackoverflow.com/questions/13568543/how-do-you-specify-the-date-format-used-when-jaxb-marshals-xsddatetime
     */
    public DateTimeType createDateTimeType(LocalDateTime ldt) {
        DateTimeType dateTimeType = new DateTimeType();
        try {
            dateTimeType.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(ldt.toString()));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return dateTimeType;
    }

    /**
     * Erstellungsdatum/-zeit des Dokuments
     *
     * @return
     */
    public DateTimeType createDateTimeType() {
        return createDateTimeType(LocalDateTime.now()); //ZoneId.of("Z")
    }

    /**
     * Create an instance of {@link ResidentType }
     */
    public ResidentType createResidentType() {
        return new ResidentType();
    }

    /**
     * Create an instance of {@link DeliveryStatusType }
     */
    public DeliveryStatusType createDeliveryStatusType() {
        return new DeliveryStatusType();
    }

    /**
     * Create an instance of {@link SoftwareType }
     */
    public SoftwareType createSoftwareType() {
        SoftwareType softwareType = new SoftwareType();
        softwareType.setProvider(createTextType("Offene-Pflege.de"));
        softwareType.setName(createTextType("OPDE"));
        softwareType.setVersion(createTextType(OPDE.getAppInfo().getProperty("opde.major") + "." + OPDE.getAppInfo().getProperty("opde.minor")));
        softwareType.setRelease(createTextType(OPDE.getAppInfo().getProperty("opde.release")));
        return softwareType;
    }

    /**
     * Create an instance of {@link FacilityDataType }
     */
    public FacilityDataType createFacilityDataType() {
        return new FacilityDataType();
    }

    /**
     * Create an instance of {@link CareProviderType }
     */
    public CareProviderType createCareProviderType() {
        return new CareProviderType();
    }

    /**
     * Create an instance of {@link DasQsDataFacilityType.BELEGUNGSKAPAZITAET }
     */
    public DasQsDataFacilityType.BELEGUNGSKAPAZITAET createDasQsDataFacilityTypeBELEGUNGSKAPAZITAET() {
        return new DasQsDataFacilityType.BELEGUNGSKAPAZITAET();
    }

    /**
     * Create an instance of {@link DasQsDataFacilityType.BELEGUNGAMSTICHTAG }
     */
    public DasQsDataFacilityType.BELEGUNGAMSTICHTAG createDasQsDataFacilityTypeBELEGUNGAMSTICHTAG() {
        return new DasQsDataFacilityType.BELEGUNGAMSTICHTAG();
    }

    /**
     * Create an instance of {@link DasQsDataType.IDBEWOHNER }
     */
    public DasQsDataType.IDBEWOHNER createDasQsDataTypeIDBEWOHNER() {
        return new DasQsDataType.IDBEWOHNER();
    }

    /**
     * Create an instance of {@link DasQsDataType.WOHNBEREICH }
     */
    public DasQsDataType.WOHNBEREICH createDasQsDataTypeWOHNBEREICH() {
        return new DasQsDataType.WOHNBEREICH();
    }

    /**
     * Create an instance of {@link DasQsDataType.ERHEBUNGSDATUM }
     */
    public DasQsDataType.ERHEBUNGSDATUM createDasQsDataTypeERHEBUNGSDATUM() {
        return new DasQsDataType.ERHEBUNGSDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.EINZUGSDATUM }
     */
    public DasQsDataType.EINZUGSDATUM createDasQsDataTypeEINZUGSDATUM() {
        return new DasQsDataType.EINZUGSDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.GEBURTSMONAT }
     */
    public DasQsDataType.GEBURTSMONAT createDasQsDataTypeGEBURTSMONAT() {
        return new DasQsDataType.GEBURTSMONAT();
    }

    /**
     * Create an instance of {@link DasQsDataType.GEBURTSJAHR }
     */
    public DasQsDataType.GEBURTSJAHR createDasQsDataTypeGEBURTSJAHR() {
        return new DasQsDataType.GEBURTSJAHR();
    }

    /**
     * Create an instance of {@link DasQsDataType.GESCHLECHT }
     */
    public DasQsDataType.GESCHLECHT createDasQsDataTypeGESCHLECHT() {
        return new DasQsDataType.GESCHLECHT();
    }

    /**
     * Create an instance of {@link DasQsDataType.PFLEGEGRAD }
     */
    public DasQsDataType.PFLEGEGRAD createDasQsDataTypePFLEGEGRAD() {
        return new DasQsDataType.PFLEGEGRAD();
    }

    /**
     * Create an instance of {@link DasQsDataType.APOPLEX }
     */
    public DasQsDataType.APOPLEX createDasQsDataTypeAPOPLEX() {
        return new DasQsDataType.APOPLEX();
    }

    /**
     * Create an instance of {@link DasQsDataType.APOPLEXDATUM }
     */
    public DasQsDataType.APOPLEXDATUM createDasQsDataTypeAPOPLEXDATUM() {
        return new DasQsDataType.APOPLEXDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.FRAKTUR }
     */
    public DasQsDataType.FRAKTUR createDasQsDataTypeFRAKTUR() {
        return new DasQsDataType.FRAKTUR();
    }

    /**
     * Create an instance of {@link DasQsDataType.FRAKTURDATUM }
     */
    public DasQsDataType.FRAKTURDATUM createDasQsDataTypeFRAKTURDATUM() {
        return new DasQsDataType.FRAKTURDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.HERZINFARKT }
     */
    public DasQsDataType.HERZINFARKT createDasQsDataTypeHERZINFARKT() {
        return new DasQsDataType.HERZINFARKT();
    }

    /**
     * Create an instance of {@link DasQsDataType.HERZINFARKTDATUM }
     */
    public DasQsDataType.HERZINFARKTDATUM createDasQsDataTypeHERZINFARKTDATUM() {
        return new DasQsDataType.HERZINFARKTDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.AMPUTATION }
     */
    public DasQsDataType.AMPUTATION createDasQsDataTypeAMPUTATION() {
        return new DasQsDataType.AMPUTATION();
    }


    /**
     * Create an instance of {@link DasQsDataType.AMPUTATIONDATUM }
     */
    public DasQsDataType.AMPUTATIONDATUM createDasQsDataTypeAMPUTATIONDATUM() {
        return new DasQsDataType.AMPUTATIONDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.KHBEHANDLUNG }
     */
    public DasQsDataType.KHBEHANDLUNG createDasQsDataTypeKHBEHANDLUNG() {
        return new DasQsDataType.KHBEHANDLUNG();
    }

    /**
     * Create an instance of {@link DasQsDataType.KHBEGINNDATUM }
     */
    public DasQsDataType.KHBEGINNDATUM createDasQsDataTypeKHBEGINNDATUM() {
        return new DasQsDataType.KHBEGINNDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.KHENDEDATUM }
     */
    public DasQsDataType.KHENDEDATUM createDasQsDataTypeKHENDEDATUM() {
        return new DasQsDataType.KHENDEDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.KHBANZAHLAUFENTHALTE }
     */
    public DasQsDataType.KHBANZAHLAUFENTHALTE createDasQsDataTypeKHBANZAHLAUFENTHALTE() {
        return new DasQsDataType.KHBANZAHLAUFENTHALTE();
    }

    /**
     * Create an instance of {@link DasQsDataType.KHBANZAHLTAGE }
     */
    public DasQsDataType.KHBANZAHLTAGE createDasQsDataTypeKHBANZAHLTAGE() {
        return new DasQsDataType.KHBANZAHLTAGE();
    }

    /**
     * Create an instance of {@link DasQsDataType.BEATMUNG }
     */
    public DasQsDataType.BEATMUNG createDasQsDataTypeBEATMUNG() {
        return new DasQsDataType.BEATMUNG();
    }

    /**
     * Create an instance of {@link DasQsDataType.BEWUSSTSEINSZUSTAND }
     */
    public DasQsDataType.BEWUSSTSEINSZUSTAND createDasQsDataTypeBEWUSSTSEINSZUSTAND() {
        return new DasQsDataType.BEWUSSTSEINSZUSTAND();
    }

    /**
     * Create an instance of {@link DasQsDataType.DIAGNOSEN }
     */
    public DasQsDataType.DIAGNOSEN createDasQsDataTypeDIAGNOSEN() {
        return new DasQsDataType.DIAGNOSEN();
    }

    /**
     * Create an instance of {@link DasQsDataType.MOBILPOSWECHSEL }
     */
    public DasQsDataType.MOBILPOSWECHSEL createDasQsDataTypeMOBILPOSWECHSEL() {
        return new DasQsDataType.MOBILPOSWECHSEL();
    }

    /**
     * Create an instance of {@link DasQsDataType.MOBILSITZPOSITION }
     */
    public DasQsDataType.MOBILSITZPOSITION createDasQsDataTypeMOBILSITZPOSITION() {
        return new DasQsDataType.MOBILSITZPOSITION();
    }

    /**
     * Create an instance of {@link DasQsDataType.MOBILUMSETZEN }
     */
    public DasQsDataType.MOBILUMSETZEN createDasQsDataTypeMOBILUMSETZEN() {
        return new DasQsDataType.MOBILUMSETZEN();
    }

    /**
     * Create an instance of {@link DasQsDataType.MOBILFORTBEWEGUNG }
     */
    public DasQsDataType.MOBILFORTBEWEGUNG createDasQsDataTypeMOBILFORTBEWEGUNG() {
        return new DasQsDataType.MOBILFORTBEWEGUNG();
    }

    /**
     * Create an instance of {@link DasQsDataType.MOBILTREPPENSTEIGEN }
     */
    public DasQsDataType.MOBILTREPPENSTEIGEN createDasQsDataTypeMOBILTREPPENSTEIGEN() {
        return new DasQsDataType.MOBILTREPPENSTEIGEN();
    }

    /**
     * Create an instance of {@link DasQsDataType.KKFERKENNEN }
     */
    public DasQsDataType.KKFERKENNEN createDasQsDataTypeKKFERKENNEN() {
        return new DasQsDataType.KKFERKENNEN();
    }

    /**
     * Create an instance of {@link DasQsDataType.KKFORIENTOERTLICH }
     */
    public DasQsDataType.KKFORIENTOERTLICH createDasQsDataTypeKKFORIENTOERTLICH() {
        return new DasQsDataType.KKFORIENTOERTLICH();
    }

    /**
     * Create an instance of {@link DasQsDataType.KKFORIENTZEITLICH }
     */
    public DasQsDataType.KKFORIENTZEITLICH createDasQsDataTypeKKFORIENTZEITLICH() {
        return new DasQsDataType.KKFORIENTZEITLICH();
    }

    /**
     * Create an instance of {@link DasQsDataType.KKFERINNERN }
     */
    public DasQsDataType.KKFERINNERN createDasQsDataTypeKKFERINNERN() {
        return new DasQsDataType.KKFERINNERN();
    }

    /**
     * Create an instance of {@link DasQsDataType.KKFHANDLUNGEN }
     */
    public DasQsDataType.KKFHANDLUNGEN createDasQsDataTypeKKFHANDLUNGEN() {
        return new DasQsDataType.KKFHANDLUNGEN();
    }

    /**
     * Create an instance of {@link DasQsDataType.KKFENTSCHEIDUNGEN }
     */
    public DasQsDataType.KKFENTSCHEIDUNGEN createDasQsDataTypeKKFENTSCHEIDUNGEN() {
        return new DasQsDataType.KKFENTSCHEIDUNGEN();
    }

    /**
     * Create an instance of {@link DasQsDataType.KKFVERSTEHENINFO }
     */
    public DasQsDataType.KKFVERSTEHENINFO createDasQsDataTypeKKFVERSTEHENINFO() {
        return new DasQsDataType.KKFVERSTEHENINFO();
    }

    /**
     * Create an instance of {@link DasQsDataType.KKFGEFAHRERKENNEN }
     */
    public DasQsDataType.KKFGEFAHRERKENNEN createDasQsDataTypeKKFGEFAHRERKENNEN() {
        return new DasQsDataType.KKFGEFAHRERKENNEN();
    }

    /**
     * Create an instance of {@link DasQsDataType.KKFMITTEILEN }
     */
    public DasQsDataType.KKFMITTEILEN createDasQsDataTypeKKFMITTEILEN() {
        return new DasQsDataType.KKFMITTEILEN();
    }

    /**
     * Create an instance of {@link DasQsDataType.KKFVERSTEHENAUF }
     */
    public DasQsDataType.KKFVERSTEHENAUF createDasQsDataTypeKKFVERSTEHENAUF() {
        return new DasQsDataType.KKFVERSTEHENAUF();
    }

    /**
     * Create an instance of {@link DasQsDataType.KKFBETEILIGUNG }
     */
    public DasQsDataType.KKFBETEILIGUNG createDasQsDataTypeKKFBETEILIGUNG() {
        return new DasQsDataType.KKFBETEILIGUNG();
    }

    /**
     * Create an instance of {@link DasQsDataType.SVERNAEHRUNG }
     */
    public DasQsDataType.SVERNAEHRUNG createDasQsDataTypeSVERNAEHRUNG() {
        return new DasQsDataType.SVERNAEHRUNG();
    }

    /**
     * Create an instance of {@link DasQsDataType.SVFREMDHILFE }
     */
    public DasQsDataType.SVFREMDHILFE createDasQsDataTypeSVFREMDHILFE() {
        return new DasQsDataType.SVFREMDHILFE();
    }

    /**
     * Create an instance of {@link DasQsDataType.SVERNAEHRUNGUMFANG }
     */
    public DasQsDataType.SVERNAEHRUNGUMFANG createDasQsDataTypeSVERNAEHRUNGUMFANG() {
        return new DasQsDataType.SVERNAEHRUNGUMFANG();
    }

    /**
     * Create an instance of {@link DasQsDataType.SVHARNKONTINENZ }
     */
    public DasQsDataType.SVHARNKONTINENZ createDasQsDataTypeSVHARNKONTINENZ() {
        return new DasQsDataType.SVHARNKONTINENZ();
    }

    /**
     * Create an instance of {@link DasQsDataType.SVSTUHLKONTINENZ }
     */
    public DasQsDataType.SVSTUHLKONTINENZ createDasQsDataTypeSVSTUHLKONTINENZ() {
        return new DasQsDataType.SVSTUHLKONTINENZ();
    }

    /**
     * Create an instance of {@link DasQsDataType.SVOBERKOERPER }
     */
    public DasQsDataType.SVOBERKOERPER createDasQsDataTypeSVOBERKOERPER() {
        return new DasQsDataType.SVOBERKOERPER();
    }

    /**
     * Create an instance of {@link DasQsDataType.SVKOPF }
     */
    public DasQsDataType.SVKOPF createDasQsDataTypeSVKOPF() {
        return new DasQsDataType.SVKOPF();
    }

    /**
     * Create an instance of {@link DasQsDataType.SVINTIMBEREICH }
     */
    public DasQsDataType.SVINTIMBEREICH createDasQsDataTypeSVINTIMBEREICH() {
        return new DasQsDataType.SVINTIMBEREICH();
    }

    /**
     * Create an instance of {@link DasQsDataType.SVDUSCHENBADEN }
     */
    public DasQsDataType.SVDUSCHENBADEN createDasQsDataTypeSVDUSCHENBADEN() {
        return new DasQsDataType.SVDUSCHENBADEN();
    }

    /**
     * Create an instance of {@link DasQsDataType.SVANAUSOBERKOERPER }
     */
    public DasQsDataType.SVANAUSOBERKOERPER createDasQsDataTypeSVANAUSOBERKOERPER() {
        return new DasQsDataType.SVANAUSOBERKOERPER();
    }

    /**
     * Create an instance of {@link DasQsDataType.SVANAUSUNTERKOERPER }
     */
    public DasQsDataType.SVANAUSUNTERKOERPER createDasQsDataTypeSVANAUSUNTERKOERPER() {
        return new DasQsDataType.SVANAUSUNTERKOERPER();
    }

    /**
     * Create an instance of {@link DasQsDataType.SVNAHRUNGZUBEREITEN }
     */
    public DasQsDataType.SVNAHRUNGZUBEREITEN createDasQsDataTypeSVNAHRUNGZUBEREITEN() {
        return new DasQsDataType.SVNAHRUNGZUBEREITEN();
    }

    /**
     * Create an instance of {@link DasQsDataType.SVESSEN }
     */
    public DasQsDataType.SVESSEN createDasQsDataTypeSVESSEN() {
        return new DasQsDataType.SVESSEN();
    }

    /**
     * Create an instance of {@link DasQsDataType.SVTRINKEN }
     */
    public DasQsDataType.SVTRINKEN createDasQsDataTypeSVTRINKEN() {
        return new DasQsDataType.SVTRINKEN();
    }

    /**
     * Create an instance of {@link DasQsDataType.SVTOILETTE }
     */
    public DasQsDataType.SVTOILETTE createDasQsDataTypeSVTOILETTE() {
        return new DasQsDataType.SVTOILETTE();
    }

    /**
     * Create an instance of {@link DasQsDataType.SVHARNKONTINENZBEW }
     */
    public DasQsDataType.SVHARNKONTINENZBEW createDasQsDataTypeSVHARNKONTINENZBEW() {
        return new DasQsDataType.SVHARNKONTINENZBEW();
    }

    /**
     * Create an instance of {@link DasQsDataType.SVSTUHLKONTINENZBEW }
     */
    public DasQsDataType.SVSTUHLKONTINENZBEW createDasQsDataTypeSVSTUHLKONTINENZBEW() {
        return new DasQsDataType.SVSTUHLKONTINENZBEW();
    }

    /**
     * Create an instance of {@link DasQsDataType.GATAGESABLAUF }
     */
    public DasQsDataType.GATAGESABLAUF createDasQsDataTypeGATAGESABLAUF() {
        return new DasQsDataType.GATAGESABLAUF();
    }

    /**
     * Create an instance of {@link DasQsDataType.GARUHENSCHLAFEN }
     */
    public DasQsDataType.GARUHENSCHLAFEN createDasQsDataTypeGARUHENSCHLAFEN() {
        return new DasQsDataType.GARUHENSCHLAFEN();
    }

    /**
     * Create an instance of {@link DasQsDataType.GABESCHAEFTIGEN }
     */
    public DasQsDataType.GABESCHAEFTIGEN createDasQsDataTypeGABESCHAEFTIGEN() {
        return new DasQsDataType.GABESCHAEFTIGEN();
    }

    /**
     * Create an instance of {@link DasQsDataType.GAPLANUNGEN }
     */
    public DasQsDataType.GAPLANUNGEN createDasQsDataTypeGAPLANUNGEN() {
        return new DasQsDataType.GAPLANUNGEN();
    }

    /**
     * Create an instance of {@link DasQsDataType.GAINTERAKTION }
     */
    public DasQsDataType.GAINTERAKTION createDasQsDataTypeGAINTERAKTION() {
        return new DasQsDataType.GAINTERAKTION();
    }

    /**
     * Create an instance of {@link DasQsDataType.GAKONTAKTPFLEGE }
     */
    public DasQsDataType.GAKONTAKTPFLEGE createDasQsDataTypeGAKONTAKTPFLEGE() {
        return new DasQsDataType.GAKONTAKTPFLEGE();
    }

    /**
     * Create an instance of {@link DasQsDataType.DEKUBITUS }
     */
    public DasQsDataType.DEKUBITUS createDasQsDataTypeDEKUBITUS() {
        return new DasQsDataType.DEKUBITUS();
    }

    /**
     * Create an instance of {@link DasQsDataType.DEKUBITUSSTADIUM }
     */
    public DasQsDataType.DEKUBITUSSTADIUM createDasQsDataTypeDEKUBITUSSTADIUM() {
        return new DasQsDataType.DEKUBITUSSTADIUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.DEKUBITUS1BEGINNDATUM }
     */
    public DasQsDataType.DEKUBITUS1BEGINNDATUM createDasQsDataTypeDEKUBITUS1BEGINNDATUM() {
        return new DasQsDataType.DEKUBITUS1BEGINNDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.DEKUBITUS1ENDEDATUM }
     */
    public DasQsDataType.DEKUBITUS1ENDEDATUM createDasQsDataTypeDEKUBITUS1ENDEDATUM() {
        return new DasQsDataType.DEKUBITUS1ENDEDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.DEKUBITUS1LOK }
     */
    public DasQsDataType.DEKUBITUS1LOK createDasQsDataTypeDEKUBITUS1LOK() {
        return new DasQsDataType.DEKUBITUS1LOK();
    }

    /**
     * Create an instance of {@link DasQsDataType.DEKUBITUS2BEGINNDATUM }
     */
    public DasQsDataType.DEKUBITUS2BEGINNDATUM createDasQsDataTypeDEKUBITUS2BEGINNDATUM() {
        return new DasQsDataType.DEKUBITUS2BEGINNDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.DEKUBITUS2ENDEDATUM }
     */
    public DasQsDataType.DEKUBITUS2ENDEDATUM createDasQsDataTypeDEKUBITUS2ENDEDATUM() {
        return new DasQsDataType.DEKUBITUS2ENDEDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.DEKUBITUS2LOK }
     */
    public DasQsDataType.DEKUBITUS2LOK createDasQsDataTypeDEKUBITUS2LOK() {
        return new DasQsDataType.DEKUBITUS2LOK();
    }

    /**
     * Create an instance of {@link DasQsDataType.KOERPERGROESSE }
     */
    public DasQsDataType.KOERPERGROESSE createDasQsDataTypeKOERPERGROESSE() {
        return new DasQsDataType.KOERPERGROESSE();
    }

    /**
     * Create an instance of {@link DasQsDataType.KOERPERGEWICHT }
     */
    public DasQsDataType.KOERPERGEWICHT createDasQsDataTypeKOERPERGEWICHT() {
        return new DasQsDataType.KOERPERGEWICHT();
    }

    /**
     * Create an instance of {@link DasQsDataType.KOERPERGEWICHTDATUM }
     */
    public DasQsDataType.KOERPERGEWICHTDATUM createDasQsDataTypeKOERPERGEWICHTDATUM() {
        return new DasQsDataType.KOERPERGEWICHTDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.KOERPERGEWICHTDOKU }
     */
    public DasQsDataType.KOERPERGEWICHTDOKU createDasQsDataTypeKOERPERGEWICHTDOKU() {
        return new DasQsDataType.KOERPERGEWICHTDOKU();
    }

    /**
     * Create an instance of {@link DasQsDataType.STURZ }
     */
    public DasQsDataType.STURZ createDasQsDataTypeSTURZ() {
        return new DasQsDataType.STURZ();
    }

    /**
     * Create an instance of {@link DasQsDataType.STURZFOLGEN }
     */
    public DasQsDataType.STURZFOLGEN createDasQsDataTypeSTURZFOLGEN() {
        return new DasQsDataType.STURZFOLGEN();
    }

    /**
     * Create an instance of {@link DasQsDataType.GURT }
     */
    public DasQsDataType.GURT createDasQsDataTypeGURT() {
        return new DasQsDataType.GURT();
    }

    /**
     * Create an instance of {@link DasQsDataType.GURTHAUFIGKEIT }
     */
    public DasQsDataType.GURTHAUFIGKEIT createDasQsDataTypeGURTHAUFIGKEIT() {
        return new DasQsDataType.GURTHAUFIGKEIT();
    }

    /**
     * Create an instance of {@link DasQsDataType.SEITENTEILE }
     */
    public DasQsDataType.SEITENTEILE createDasQsDataTypeSEITENTEILE() {
        return new DasQsDataType.SEITENTEILE();
    }

    /**
     * Create an instance of {@link DasQsDataType.SEITENTEILEHAUFIGKEIT }
     */
    public DasQsDataType.SEITENTEILEHAUFIGKEIT createDasQsDataTypeSEITENTEILEHAUFIGKEIT() {
        return new DasQsDataType.SEITENTEILEHAUFIGKEIT();
    }

    /**
     * Create an instance of {@link DasQsDataType.SCHMERZEN }
     */
    public DasQsDataType.SCHMERZEN createDasQsDataTypeSCHMERZEN() {
        return new DasQsDataType.SCHMERZEN();
    }

    /**
     * Create an instance of {@link DasQsDataType.SCHMERZFREI }
     */
    public DasQsDataType.SCHMERZFREI createDasQsDataTypeSCHMERZFREI() {
        return new DasQsDataType.SCHMERZFREI();
    }

    /**
     * Create an instance of {@link DasQsDataType.SCHMERZEINSCH }
     */
    public DasQsDataType.SCHMERZEINSCH createDasQsDataTypeSCHMERZEINSCH() {
        return new DasQsDataType.SCHMERZEINSCH();
    }

    /**
     * Create an instance of {@link DasQsDataType.SCHMERZEINSCHDATUM }
     */
    public DasQsDataType.SCHMERZEINSCHDATUM createDasQsDataTypeSCHMERZEINSCHDATUM() {
        return new DasQsDataType.SCHMERZEINSCHDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.SCHMERZEINSCHINFO }
     */
    public DasQsDataType.SCHMERZEINSCHINFO createDasQsDataTypeSCHMERZEINSCHINFO() {
        return new DasQsDataType.SCHMERZEINSCHINFO();
    }

    /**
     * Create an instance of {@link DasQsDataType.NEUEINZUG }
     */
    public DasQsDataType.NEUEINZUG createDasQsDataTypeNEUEINZUG() {
        return new DasQsDataType.NEUEINZUG();
    }

    /**
     * Create an instance of {@link DasQsDataType.EINZUGNACHKZ }
     */
    public DasQsDataType.EINZUGNACHKZ createDasQsDataTypeEINZUGNACHKZ() {
        return new DasQsDataType.EINZUGNACHKZ();
    }

    /**
     * Create an instance of {@link DasQsDataType.EINZUGNACHKZDATUM }
     */
    public DasQsDataType.EINZUGNACHKZDATUM createDasQsDataTypeEINZUGNACHKZDATUM() {
        return new DasQsDataType.EINZUGNACHKZDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.EINZUGKHBEHANDLUNG }
     */
    public DasQsDataType.EINZUGKHBEHANDLUNG createDasQsDataTypeEINZUGKHBEHANDLUNG() {
        return new DasQsDataType.EINZUGKHBEHANDLUNG();
    }

    /**
     * Create an instance of {@link DasQsDataType.EINZUGKHBEGINNDATUM }
     */
    public DasQsDataType.EINZUGKHBEGINNDATUM createDasQsDataTypeEINZUGKHBEGINNDATUM() {
        return new DasQsDataType.EINZUGKHBEGINNDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.EINZUGKHENDEDATUM }
     */
    public DasQsDataType.EINZUGKHENDEDATUM createDasQsDataTypeEINZUGKHENDEDATUM() {
        return new DasQsDataType.EINZUGKHENDEDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.EINZUGGESPR }
     */
    public DasQsDataType.EINZUGGESPR createDasQsDataTypeEINZUGGESPR() {
        return new DasQsDataType.EINZUGGESPR();
    }

    /**
     * Create an instance of {@link DasQsDataType.EINZUGGESPRDATUM }
     */
    public DasQsDataType.EINZUGGESPRDATUM createDasQsDataTypeEINZUGGESPRDATUM() {
        return new DasQsDataType.EINZUGGESPRDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataType.EINZUGGESPRTEILNEHMER }
     */
    public DasQsDataType.EINZUGGESPRTEILNEHMER createDasQsDataTypeEINZUGGESPRTEILNEHMER() {
        return new DasQsDataType.EINZUGGESPRTEILNEHMER();
    }

    /**
     * Create an instance of {@link DasQsDataType.EINZUGGESPRDOKU }
     */
    public DasQsDataType.EINZUGGESPRDOKU createDasQsDataTypeEINZUGGESPRDOKU() {
        return new DasQsDataType.EINZUGGESPRDOKU();
    }

    /**
     * Create an instance of {@link DasCommentationType.KOMMENTAR }
     */
    public DasCommentationType.KOMMENTAR createDasCommentationTypeKOMMENTAR() {
        return new DasCommentationType.KOMMENTAR();
    }

    /**
     * Create an instance of {@link DasQsDataMdsType.IDBEWOHNER }
     */
    public DasQsDataMdsType.IDBEWOHNER createDasQsDataMdsTypeIDBEWOHNER() {
        return new DasQsDataMdsType.IDBEWOHNER();
    }

    /**
     * Create an instance of {@link DasQsDataMdsType.WOHNBEREICH }
     */
    public DasQsDataMdsType.WOHNBEREICH createDasQsDataMdsTypeWOHNBEREICH() {
        return new DasQsDataMdsType.WOHNBEREICH();
    }

    /**
     * Create an instance of {@link DasQsDataMdsType.ERHEBUNGSDATUM }
     */
    public DasQsDataMdsType.ERHEBUNGSDATUM createDasQsDataMdsTypeERHEBUNGSDATUM() {
        return new DasQsDataMdsType.ERHEBUNGSDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataMdsType.EINZUGSDATUM }
     */
    public DasQsDataMdsType.EINZUGSDATUM createDasQsDataMdsTypeEINZUGSDATUM() {
        return new DasQsDataMdsType.EINZUGSDATUM();
    }

    /**
     * Create an instance of {@link DasQsDataMdsType.GEBURTSMONAT }
     */
    public DasQsDataMdsType.GEBURTSMONAT createDasQsDataMdsTypeGEBURTSMONAT() {
        return new DasQsDataMdsType.GEBURTSMONAT();
    }

    /**
     * Create an instance of {@link DasQsDataMdsType.GEBURTSJAHR }
     */
    public DasQsDataMdsType.GEBURTSJAHR createDasQsDataMdsTypeGEBURTSJAHR() {
        return new DasQsDataMdsType.GEBURTSJAHR();
    }

    /**
     * Create an instance of {@link DasQsDataMdsType.GESCHLECHT }
     */
    public DasQsDataMdsType.GESCHLECHT createDasQsDataMdsTypeGESCHLECHT() {
        return new DasQsDataMdsType.GESCHLECHT();
    }

    /**
     * Create an instance of {@link DasQsDataMdsType.AUSSCHLUSSGRUND }
     */
    public DasQsDataMdsType.AUSSCHLUSSGRUND createDasQsDataMdsTypeAUSSCHLUSSGRUND() {
        return new DasQsDataMdsType.AUSSCHLUSSGRUND();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RootType }{@code >}}
     */
    @XmlElementDecl(namespace = "https://www.das-pflege.de", name = "xsi:schemaLocation=\"https://www.das-pflege.de ../das_interface.xsd\"")
    public JAXBElement<RootType> createRoot(RootType value) {
        return new JAXBElement<>(_Root_QNAME, RootType.class, null, value);
    }

}
