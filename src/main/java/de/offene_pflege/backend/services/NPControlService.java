package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.done.NursingProcess;
import de.offene_pflege.backend.entity.done.NPControl;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSTools;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA. User: tloehr Date: 19.07.12 Time: 16:23 To change this template use File | Settings | File
 * Templates.
 */
public class NPControlService {


    public static NPControl create(String bemerkung, NursingProcess nursingProcess) {
        NPControl npc = new NPControl();
        npc.setBemerkung(SYSTools.tidy(bemerkung));
        npc.setLastValidation(false);
        npc.setNursingProcess(nursingProcess);
        npc.setOpUsers(OPDE.getLogin().getUser());
        npc.setDatum(new Date());
        return npc;
    }

    public static String getAsHTML(NPControl npcontrol) {
        String result = "";
        DateFormat df = DateFormat.getDateInstance();
        result += "<b>" + df.format(npcontrol.getDatum()) + "</b>; <u>" + OPUsersService.getFullname(npcontrol.getOpUsers()) + "</u>; " + npcontrol.getBemerkung();
//        result += "<p><b>Durchgeführt von:</b> " + kontrolle.getUser().getFullname() + "</p>";
//        result += "<p><b>Ergebnis:</b> " + kontrolle.getText() + "</p>";
        if (npcontrol.getLastValidation()) {
            result += "<br/><b>" + SYSTools.xx("nursingrecords.nursingprocess.isClosedAfterThisNPControl") + "</b>";
        }

        return result;
    }
}
