package de.offene_pflege.entity;


import lombok.extern.log4j.Log4j2;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.JavaLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;

/**
 * Created by IntelliJ IDEA. User: tloehr Date: 22.05.12 Time: 15:37 To change this template use File | Settings | File
 * Templates.
 */
@Log4j2
public class CustomEclipselinkAbstractSessionLog extends AbstractSessionLog implements SessionLog {
    /* @see org.eclipse.persistence.logging.AbstractSessionLog#log(org.eclipse.persistence.logging.SessionLogEntry)
     */

    @Override
    public void log(SessionLogEntry sessionLogEntry) {
        if (sessionLogEntry.getLevel() >= JavaLog.INFO || sessionLogEntry.hasException()) {
            log.debug("[JPA] " + sessionLogEntry.getMessage());
        } else if (sessionLogEntry.getMessage().startsWith("INSERT") || sessionLogEntry.getMessage().startsWith("DELETE") || sessionLogEntry.getMessage().startsWith("UPDATE")) {
            log.debug("[JPA] " + sessionLogEntry.getMessage());
        }
    }
}