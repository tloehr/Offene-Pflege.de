package entity.system;

import entity.EntityTools;
import op.OPDE;
import op.OPMain;
import op.tools.SYSTools;
import javax.persistence.Query;
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


    public static void processSystemMessage() {
        boolean logout = false;
        Query query = OPDE.getEM().createNamedQuery("SYSMessages.findByReceiverHostAndUnprocessed");
        query.setParameter("receiverHost", OPDE.getHost());
        Iterator<SYSMessages> it = query.getResultList().iterator();
        while (it.hasNext()){
            SYSMessages msg = it.next();
            if (msg.getCommand() == CMD_DO_LOGOUT){
                logout = true;
            } else if (msg.getCommand() == CMD_SHOW_MESSAGE){
                OPDE.debug(SYSTools.catchNull(msg.getMessage()));
            }
            msg.setProcessed(new Date());
            EntityTools.merge(msg);
        }

        if (logout){

        }
    }

}
