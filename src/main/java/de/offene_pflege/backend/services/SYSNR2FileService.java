package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.done.SYSFiles;
import de.offene_pflege.backend.entity.done.SYSNR2FILE;
import de.offene_pflege.backend.entity.reports.NReport;
import de.offene_pflege.backend.entity.system.OPUsers;

import java.util.Date;

public class SYSNR2FileService {

    public static SYSNR2FILE create(SYSFiles sysfiles, NReport nReport, OPUsers opUsers, Date pit) {
        SYSNR2FILE sysnr2FILE = new SYSNR2FILE();
        sysnr2FILE.setSysFiles(sysfiles);
        sysnr2FILE.setnReport(nReport);
        sysnr2FILE.setOpusers(opUsers);
        sysnr2FILE.setPit(pit);
        return sysnr2FILE;
    }

}
