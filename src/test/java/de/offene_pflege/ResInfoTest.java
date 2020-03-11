package de.offene_pflege;

import de.offene_pflege.op.tools.SYSTools;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;

public class ResInfoTest {

//  @Test
//    public void testAmputation() {
//        ResInfoType resInfoType = new ResInfoType("amput01");
//        resInfoType.setIntervalMode((short) 0);
//        resInfoType.setType(ResInfoTypeTools.TYPE_AMPUTATION);
//        ResInfo resInfo = new ResInfo(resInfoType);
//
//        Properties props = new Properties();
//        props.setProperty("upperleft", "hand");
//        props.setProperty("upperright", "none");
//        props.setProperty("lowerleft", "foot");
//        props.setProperty("lowerright", "none");
//        props.setProperty("dateupperleft", "10.08.2014");
//        props.setProperty("dateupperright", "");
//        props.setProperty("datelowerleft", "10.08.2014");
//        props.setProperty("datelowerright", "");
//
//        ResInfoTools.setContent(resInfo, props);
//
//        try {
//            assertNotNull(ResInfoTools.getLetzteAmputation(resInfo));
//        } catch (MissingInformationException e) {
//            System.out.println("Datum fehlt");
//        }
//
//    }
//




    @Test
    public void testDate() {

        GregorianCalendar calendar = new GregorianCalendar();
        LocalDate ld = LocalDate.now();

        try {


            System.out.println(SYSTools.xx("opde.settings.model.btnAddCategory"));
            System.out.println(SYSTools.xx("bi4.oberkoerper.selbst0"));

            DatatypeFactory df = DatatypeFactory.newInstance();

            XMLGregorianCalendar in = df.newXMLGregorianCalendar(calendar);
            XMLGregorianCalendar out = df.newXMLGregorianCalendar(in.toString());
            System.out.println(calendar.toString());
            System.out.println(in.toString());

            System.out.println(out.toString());
            System.out.println(ld.format(DateTimeFormatter.ISO_LOCAL_DATE));

        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }


    }

}
