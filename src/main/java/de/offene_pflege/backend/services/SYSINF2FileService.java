package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.done.SYSFiles;
import de.offene_pflege.backend.entity.done.SYSINF2FILE;
import de.offene_pflege.backend.entity.info.ResInfo;
import de.offene_pflege.backend.entity.system.OPUsers;

import java.util.Date;

public class SYSINF2FileService {
    public static SYSINF2FILE create(SYSFiles sysfile, ResInfo resInfo, OPUsers opuser, Date pit){
        SYSINF2FILE sysinf2FILE = new SYSINF2FILE();
        sysinf2FILE.setSysfiles(sysfile);
        sysinf2FILE.setResInfo(resInfo);
        sysinf2FILE.setOpusers(opuser);
        sysinf2FILE.setPit(pit);
        return sysinf2FILE;
    }
}
