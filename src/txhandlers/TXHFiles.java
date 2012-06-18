/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package txhandlers;

import entity.Users;
import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 *
 * @author tloehr
 */
public class TXHFiles extends TransferHandler {

    private HashMap<String, Users> cache;

    public TXHFiles() {
        cache = new HashMap();
        //em = OPDE.createEM();
    }

    @Override
    public boolean canImport(TransferSupport info) {
        // we only import Strings
        return info.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    @Override
    public boolean importData(TransferSupport info) {
        //OPDE.debug("isDrop: " + info.isDrop());
        if (!info.isDrop()) {
            return false;
        } else {
            JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
            // Target List
            JTable tbl = (JTable) info.getComponent();
            TableModel tm = tbl.getModel();

            // Get the string that is being dropped.
            Transferable t = info.getTransferable();
            String data;
            try {
                data = (String) t.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception e) {
                data = "";
            }

            StringTokenizer st = new StringTokenizer(data, "\n");

            while (st.hasMoreTokens()) {
                String filename = st.nextToken();
                try {
                    File file = new File(filename);
                    OPDE.debug(file);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            return true;
        }
    }

    @Override
    protected void exportDone(JComponent source, Transferable t, int action) {
//        if (action == MOVE) {
//            JList sourceList = (JList) source;
//            String data;
//            try {
//                data = (String) t.getTransferData(DataFlavor.stringFlavor);
//            } catch (Exception e) {
//                data = "";
//            }
//            StringTokenizer st = new StringTokenizer(data, "\n");
//            EntityManager em = OPDE.createEM();
//            Query query = em.createNamedQuery("Users.findByUKennung");
//            while (st.hasMoreTokens()) {
//                String key = st.nextToken();
//                if (!cache.containsKey(key)) {
//                    query.setParameter("uKennung", key);
//                    cache.put(key, (Users) query.getSingleResult());
//                }
//                // Liste aktualisieren
//                ((DefaultListModel) sourceList.getModel()).removeElement(cache.get(key));
//            }
//            em.close();
//        }
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY;
    }

//    @Override
//    protected Transferable createTransferable(JComponent c) {
//        JList list = (JList) c;
//        Object[] values = list.getSelectedValues();
//
//        StringBuffer buff = new StringBuffer();
//
//        for (int i = 0; i < values.length; i++) {
//            Users user = (Users) values[i];
//            buff.append(user.getUKennung());
//            if (i != values.length - 1) {
//                buff.append("\n");
//            }
//        }
//        return new StringSelection(buff.toString());
//    }
}
