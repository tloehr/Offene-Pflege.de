package de.offene_pflege.entity.info;

import de.offene_pflege.entity.system.OPUsers;
import de.offene_pflege.op.tools.JavaTimeConverter;
import de.offene_pflege.op.tools.SYSConst;
import org.javatuples.Pair;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResInfoToolsTest {

    private OPUsers getMockupUser() {
        OPUsers user = new OPUsers();
        user.setNachname("Löhr");
        user.setVorname("Torsten");
        user.setEMail("mockup@fake.net");
        user.setUID("mockuser");
        return user;
    }

    private Resident getMockupResident(OPUsers editor) {
        Resident resident = ResidentTools.createResident("Mustermann", "Max", ResidentTools.MALE, JavaTimeConverter.toDate("1970-08-12T12:00:00+02:00[Europe/Berlin]"), editor, true);
        resident.setIdbewohner(1234567);
        resident.setId("MM00");
        return resident;
    }

    @Test
    public void getMinMaxExpansion() {

        OPUsers mockupuser = getMockupUser();
        Resident resident = getMockupResident(mockupuser);

        ResInfoType mockupStayType = new ResInfoType("hauf");
        mockupStayType.setIntervalMode((short) ResInfoTypeTools.MODE_INTERVAL_BYSECOND);
        ResInfo hauf = ResInfoTools.createResInfo(mockupStayType, resident, mockupuser);
        hauf.setFrom(JavaTimeConverter.toDate("2010-01-01T12:00:00+02:00[Europe/Berlin]"));
        Properties props = new Properties();
        props.put(ResInfoTypeTools.STAY_KEY, "");
        props.put(ResInfoTypeTools.KZP_KEY, "false");
        ResInfoTools.setContent(hauf, props);

        ResInfoType mockupType = new ResInfoType("sometype");
        mockupType.setIntervalMode((short) ResInfoTypeTools.MODE_INTERVAL_BYSECOND);

        ResInfo period_resinfo0 = ResInfoTools.createResInfo(mockupType, resident, mockupuser);
        ResInfo period_resinfo1 = ResInfoTools.createResInfo(mockupType, resident, mockupuser);
        ResInfo period_resinfo2 = ResInfoTools.createResInfo(mockupType, resident, mockupuser);
        ResInfo period_resinfo3 = ResInfoTools.createResInfo(mockupType, resident, mockupuser);
        ResInfo period_resinfo4 = ResInfoTools.createResInfo(mockupType, resident, mockupuser);

        // Zeiträume definieren
        period_resinfo1.setFrom(JavaTimeConverter.toDate("2011-12-03T10:15:30+02:00[Europe/Berlin]"));
        period_resinfo1.setTo(JavaTimeConverter.toDate("2013-01-20T11:21:17+02:00[Europe/Berlin]"));

        period_resinfo2.setFrom(JavaTimeConverter.toDate("2013-01-20T11:21:18+02:00[Europe/Berlin]")); // nahtlos
        period_resinfo2.setTo(JavaTimeConverter.toDate("2015-05-05T15:00:00+02:00[Europe/Berlin]"));

        period_resinfo3.setFrom(JavaTimeConverter.toDate("2015-05-05T15:00:01+02:00[Europe/Berlin]"));
        period_resinfo3.setTo(JavaTimeConverter.toDate("2017-04-11T08:00:10+02:00[Europe/Berlin]"));  // lücke


        period_resinfo0.setFrom(JavaTimeConverter.toDate("2017-08-03T10:15:30+02:00[Europe/Berlin]"));
        period_resinfo0.setTo(JavaTimeConverter.toDate("2017-08-20T11:21:17+02:00[Europe/Berlin]"));


        period_resinfo4.setFrom(JavaTimeConverter.toDate("2018-04-11T15:12:49+02:00[Europe/Berlin]"));
        period_resinfo4.setTo(SYSConst.DATE_UNTIL_FURTHER_NOTICE);

        // 2017-04-11T08:00:11+02:00[Europe/Berlin]
        //2018-04-11T15:12:48+02:00[Europe/Berlin]

        ArrayList<ResInfo> listInfos = new ArrayList<>();
        listInfos.add(period_resinfo1);
        listInfos.add(period_resinfo2);
        listInfos.add(period_resinfo3);
        listInfos.add(period_resinfo4);
        listInfos.add(period_resinfo0);

        Pair<LocalDateTime, LocalDateTime> pair = ResInfoTools.getMinMaxExpansion(period_resinfo0, listInfos, hauf);

        assertEquals("2017-04-11T08:00:11+02:00[Europe/Berlin]", JavaTimeConverter.to_iso8601(pair.getValue0()));
        assertEquals("2018-04-11T15:12:48+02:00[Europe/Berlin]", JavaTimeConverter.to_iso8601(pair.getValue1()));

    }
}