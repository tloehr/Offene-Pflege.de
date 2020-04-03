package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.done.SYSFiles;
import de.offene_pflege.backend.entity.done.SYSNP2FILE;
import de.offene_pflege.backend.entity.nursingprocess.NursingProcess;
import de.offene_pflege.backend.entity.system.OPUsers;

import java.util.Date;

public class SYSNP2FileService {


    public static SYSNP2FILE create(SYSFiles sysfile, NursingProcess nursingProcess, OPUsers opusers, Date pit) {
        SYSNP2FILE sysnp2FILE = new SYSNP2FILE();
        sysnp2FILE.setSysfiles(sysfile);
        sysnp2FILE.setNursingProcess(nursingProcess);
        sysnp2FILE.setOpusers(opusers);
        sysnp2FILE.setPit(pit);
        return sysnp2FILE;
    }

}
