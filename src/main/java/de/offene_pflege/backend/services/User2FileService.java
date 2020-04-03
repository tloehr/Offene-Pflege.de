package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.done.SYSFiles;
import de.offene_pflege.backend.entity.done.User2File;
import de.offene_pflege.backend.entity.system.OPUsers;

import java.util.Date;

public class User2FileService {
    public static User2File create(SYSFiles sysfiles, OPUsers opusers, OPUsers editor, Date pit){
        User2File user2File = new User2File();
        user2File.setSysfiles(sysfiles);
        user2File.setOpUsers(opusers);
        user2File.setEditor(editor);
        user2File.setPit(pit);
        return user2File;
    }
}
