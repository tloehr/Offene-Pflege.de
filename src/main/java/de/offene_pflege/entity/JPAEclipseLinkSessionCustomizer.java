package de.offene_pflege.entity;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.logging.JavaLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;


/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 22.05.12
 * Time: 15:41
 * To change this template use File | Settings | File Templates.
 */
public class JPAEclipseLinkSessionCustomizer implements SessionCustomizer {
    public void customize(Session aSession) throws Exception {

        // create a custom logger
        SessionLog aCustomLogger = new CustomEclipselinkAbstractSessionLog();
        aCustomLogger.setLevel(JavaLog.FINEST);
        aSession.setSessionLog(aCustomLogger);
    }
}