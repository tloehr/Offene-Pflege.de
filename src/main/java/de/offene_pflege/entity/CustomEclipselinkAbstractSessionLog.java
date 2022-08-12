package de.offene_pflege.entity;


import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;

@Log4j2
public class CustomEclipselinkAbstractSessionLog extends AbstractSessionLog implements SessionLog {
    /* @see org.eclipse.persistence.logging.AbstractSessionLog#log(org.eclipse.persistence.logging.SessionLogEntry)
     */

    @Override
    public void log(SessionLogEntry sessionLogEntry) {
        // SELECTS nur wenn was schief gegangen ist oder bei hohem loglevel
        if (sessionLogEntry.hasException() || log.getLevel().isInRange(Level.DEBUG, Level.OFF)) {
            log.debug("[JPA] " + sessionLogEntry.getMessage());
        } else if (sessionLogEntry.getMessage().startsWith("INSERT") || sessionLogEntry.getMessage().startsWith("DELETE") || sessionLogEntry.getMessage().startsWith("UPDATE")) {
            log.debug("[JPA] " + sessionLogEntry.getMessage());
        }
    }
}