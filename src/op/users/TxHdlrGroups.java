/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package op.users;

import entity.Groups;
import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * @author tloehr
 */
public class TxHdlrGroups extends TransferHandler {

    private HashMap<String, Groups> cache;


    public TxHdlrGroups() {
        cache = new HashMap();
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
        // we only import Strings
        return info.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        //OPDE.debug("isDrop: " + info.isDrop());
        if (!info.isDrop()) {
            return false;
        } else {


            JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
            // Target List
            JList list = (JList) info.getComponent();
            DefaultListModel listModel = (DefaultListModel) list.getModel();
            int index = dl.getIndex();
            boolean insert = dl.isInsert();

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
            Query query = em.createNamedQuery("Groups.findByGkennung");
            while (st.hasMoreTokens()) {
                String key = st.nextToken();
                if (!cache.containsKey(key)) {
                    query.setParameter("gkennung", key);
                    cache.put(key, (Groups) query.getSingleResult());
                }
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
            Query query = em.createNamedQuery("Groups.findByGkennung");
            while (st.hasMoreTokens()) {
                String key = st.nextToken();
                if (!cache.containsKey(key)) {
                    query.setParameter("gkennung", key);
                    cache.put(key, (Groups) query.getSingleResult());
                }
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
            Groups grp = (Groups) values[i];
            buff.append(grp.getGkennung());
            if (i != values.length - 1) {
                buff.append("\n");
            }
        }
        return new StringSelection(buff.toString());
    }
}
