package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.done.SYSFiles;
import de.offene_pflege.backend.entity.done.SYSVAL2FILE;
import de.offene_pflege.backend.entity.system.OPUsers;
import de.offene_pflege.backend.entity.values.ResValue;

import java.util.Date;

public class SYSVAL2FileService {
    public static SYSVAL2FILE create(SYSFiles sysfiles, ResValue resValue, OPUsers opUsers, Date pit){
        SYSVAL2FILE sysval2FILE = new SYSVAL2FILE();
        sysval2FILE.setSysfiles(sysfiles);
        sysval2FILE.setResValue(resValue);
        sysval2FILE.setOpUsers(opUsers);
        sysval2FILE.setPit(pit);
        return sysval2FILE;
    }
}
