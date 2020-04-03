package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.done.SYSFiles;
import de.offene_pflege.backend.entity.done.SYSPRE2FILE;
import de.offene_pflege.backend.entity.prescription.Prescription;
import de.offene_pflege.backend.entity.system.OPUsers;

import java.util.Date;

public class SYSPRE2FileService {
    public static SYSPRE2FILE create(SYSFiles sysfiles, Prescription prescription, OPUsers opUsers, Date pit) {
        SYSPRE2FILE syspre2FILE = new SYSPRE2FILE();
        syspre2FILE.setSysFiles(sysfiles);
        syspre2FILE.setPrescription(prescription);
        syspre2FILE.setOpUsers(opUsers);
        syspre2FILE.setPit(pit);
        return syspre2FILE;

    }
}
