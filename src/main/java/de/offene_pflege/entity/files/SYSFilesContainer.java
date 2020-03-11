package de.offene_pflege.entity.files;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 05.07.12
 * Time: 15:11
 * To change this template use File | Settings | File Templates.
 */
public interface SYSFilesContainer {
    public SYSFilesLink attachFile(SYSFiles sysFiles);
    public SYSFilesLink detachFile(SYSFiles sysFiles);
}
