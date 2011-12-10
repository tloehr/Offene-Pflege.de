/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package op.ma.admin;

import entity.Users;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import op.OPDE;

/**
 *
 * @author tloehr
 */
public class TxHdlrUsers extends TransferHandler {

    private HashMap<String, Users> cache;

    public TxHdlrUsers() {
        cache = new HashMap();
        //em = OPDE.createEM();
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
        // we only import Strings
        return info.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        //OPDE.getLogger().debug("isDrop: " + info.isDrop());
        if (!info.isDrop()) {
            return false;
        } else {

            JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
            // Target List
            JList list = (JList) info.getComponent();
            DefaultListModel listModel = (DefaultListModel) list.getModel();
            int index = dl.getIndex();

            // Get the string that is being dropped.
            Transferable t = info.getTransferable();
            String data;
            try {
                data = (String) t.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception e) {
                data = "";
            }

            StringTokenizer st = new StringTokenizer(data, "\n");
            EntityManager em = OPDE.createEM();
            Query query = em.createNamedQuery("Users.findByUKennung");
            while (st.hasMoreTokens()) {
                String key = st.nextToken();
                if (!cache.containsKey(key)) {
                    query.setParameter("uKennung", key);
                    cache.put(key, (Users) query.getSingleResult());
                }
                // List aktualisieren
                listModel.add(index, cache.get(key));
            }
            em.close();
            return true;
        }
    }

    @Override
    protected void exportDone(JComponent source, Transferable t, int action) {
        if (action == MOVE) {
            JList sourceList = (JList) source;
            String data;
            try {
                data = (String) t.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception e) {
                data = "";
            }
            StringTokenizer st = new StringTokenizer(data, "\n");
            EntityManager em = OPDE.createEM();
            Query query = em.createNamedQuery("Users.findByUKennung");
            while (st.hasMoreTokens()) {
                String key = st.nextToken();
                if (!cache.containsKey(key)) {
                    query.setParameter("uKennung", key);
                    cache.put(key, (Users) query.getSingleResult());
                }
                // Liste aktualisieren
                ((DefaultListModel) sourceList.getModel()).removeElement(cache.get(key));
            }
            em.close();
        }
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JList list = (JList) c;
        Object[] values = list.getSelectedValues();

        StringBuffer buff = new StringBuffer();

        for (int i = 0; i < values.length; i++) {
            Users user = (Users) values[i];
            buff.append(user.getUKennung());
            if (i != values.length - 1) {
                buff.append("\n");
            }
        }
        return new StringSelection(buff.toString());
    }
}
