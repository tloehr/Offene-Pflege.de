package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.done.Resident2File;
import de.offene_pflege.backend.entity.done.SYSFiles;
import de.offene_pflege.backend.entity.done.Resident;
import de.offene_pflege.backend.entity.system.OPUsers;

import java.util.Date;

public class Resident2FileService {
    public static Resident2File create(SYSFiles sysFiles, Resident resident, OPUsers opUsers){
        Resident2File resident2File = new Resident2File();
        resident2File.setSysfile(sysFiles);
        resident2File.setResident(resident);
        resident2File.setEditor(opUsers);
        resident2File.setPit(new Date());
        return resident2File;
    }
}
