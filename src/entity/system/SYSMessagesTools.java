package entity.system;

import entity.EntityTools;
import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSPrint;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.Iterator;


/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 07.12.11
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
public class SYSMessagesTools {

    public static final int CMD_SHOW_MESSAGE = 0;
    public static final int CMD_DO_LOGOUT = 1;


    /**
     * Prüft ob es Nachrichten für diesen Host gibt und arbeitet sie ab. Wenn nötig,
     * wir der Host herunter gefahren.
     *
     */
    public static void processSystemMessage() {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("SYSMessages.findByReceiverHostAndUnprocessed");
        query.setParameter("receiverHost", OPDE.getHost());
        Iterator<SYSMessages> it = query.getResultList().iterator();
        while (it.hasNext()) {
            SYSMessages msg = it.next();
            if (msg.getCommand() == CMD_DO_LOGOUT) {

                // Das wird einfacher und besser, wenn das Programm auf die Ein-Fenster-Variante umgestellt ist.
                try {

                    OPDE.closeDB(); // JDBC Zugriff


                    String html = "<html><h1>Das Progamm musste automatisch beendet werden</h1><b>Der Grund:</b><br/>" + msg.getMessage() +
                            "</html>";

                    // Create temp file.
                    File temp = File.createTempFile("emergency-exit", ".html");

                    // Write to temp file
                    BufferedWriter out = new BufferedWriter(new FileWriter(temp));
                    out.write(SYSTools.htmlUmlautConversion(html));
                    out.close();
                    SYSPrint.handleFile(new JFrame(), temp.getAbsolutePath(), Desktop.Action.OPEN);

//                    SYSHostsTools.shutdown(0); // Den aktuellen HOST als "heruntergefahren" markieren
                } catch (Exception ex) {
                    OPDE.fatal(ex);
                    ex.printStackTrace();
                    System.exit(1);
                }

            } else if (msg.getCommand() == CMD_SHOW_MESSAGE) {
                OPDE.debug(SYSTools.catchNull(msg.getMessage()));
            }
            msg.setProcessed(new Date());
            EntityTools.merge(msg);
        }
    }

    /**
     * setzt einfach alle noch offenen Nachrichten auf erledigt. Wird bei einem Shutdown gebraucht.
     * Die Nachrichten werden zwar nicht mehr abgearbeitet, aber das spielt dann auch keine
     * Rolle mehr.
     *
     */
    public static void setAllMesages2Processed() {
        EntityManager em = OPDE.createEM();

        try {
            em.getTransaction().begin();
            Query query = em.createQuery("UPDATE SYSMessages s SET s.processed = current_timestamp WHERE s.processed = :processed AND s.receiverHost = :host");
            query.setParameter("processed", SYSConst.DATE_BIS_AUF_WEITERES);
            query.setParameter("host", OPDE.getHost());
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

    }

}
