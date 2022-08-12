package de.offene_pflege.op.care.med.inventory;

import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.prescription.*;
import de.offene_pflege.op.care.med.structure.TMMedOrders;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import de.offene_pflege.tablerenderer.RNDHTML;
import lombok.extern.log4j.Log4j2;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

@Log4j2
public class PnlMedOrders extends JPanel {
    private MedOrders selected_med_orders;
    private JPopupMenu menu;
    JTable tbl;
    JScrollPane scrl;

    public PnlMedOrders() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        menu = null;
        selected_med_orders = MedOrdersTools.get_or_create_active_med_orders();
        initPanel();
    }

    private void initPanel() {
        JPanel pnl = getButtonPanel();
        pnl.add(getLabel());
        add(pnl);
        scrl = new JScrollPane();
        add(scrl);
        TMMedOrders tmMedOrders = new TMMedOrders(selected_med_orders.getOrderList());
        tbl = new JTable();
        load_table_model();
        tbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                tableMousePressed(e);
            }
        });
        JComboBox<GP> cmbGP = new JComboBox<>(GPTools.getAllActive().toArray(new GP[0]));
        cmbGP.setRenderer(GPTools.getRenderer());
        tbl.getColumnModel().getColumn(TMMedOrders.COL_TradeForm).setCellRenderer(new RNDHTML());
        tbl.getColumnModel().getColumn(TMMedOrders.COL_GP).setCellEditor(new DefaultCellEditor(cmbGP));
        scrl.setViewportView(tbl);
    }

    private void load_table_model() {
        tbl.setModel(new TMMedOrders(selected_med_orders.getOrderList()));
    }

    private JLabel getLabel() {
        JLabel lbl = new JLabel("Bestellung #" + selected_med_orders.getId() + "  Erstellt: " + selected_med_orders.getOpened_on());
        lbl.setFont(new Font("Arial", Font.PLAIN, 18));
        return lbl;
    }

    private JPanel getButtonPanel() {
        JPanel pnl = new JPanel(new HorizontalLayout(5));
        JButton left = new JButton(SYSConst.icon22playerBack);
        JButton right = new JButton(SYSConst.icon22playerPlay);

        left.addActionListener(e ->
                MedOrdersTools.next(selected_med_orders, -1).ifPresent(medOrders -> {
                    selected_med_orders = medOrders;
                    tbl.setModel(new TMMedOrders(selected_med_orders.getOrderList()));
                })
        );
        right.addActionListener(e ->
                MedOrdersTools.next(selected_med_orders, -1).ifPresent(medOrders -> {
                    selected_med_orders = medOrders;
                    tbl.setModel(new TMMedOrders(selected_med_orders.getOrderList()));
                })
        );
        pnl.add(left);
        pnl.add(right);
        return pnl;
    }

    private void tableMousePressed(MouseEvent evt) {
        if (!SwingUtilities.isRightMouseButton(evt)) return;

        Point p = evt.getPoint();
        JTable tbl = (JTable) evt.getSource();
        ListSelectionModel lsm = tbl.getSelectionModel();
        Point p2 = evt.getPoint();
        SwingUtilities.convertPointToScreen(p2, tbl);
        final Point screenposition = p2;

        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        final int row = tbl.rowAtPoint(p);
        final int col = tbl.columnAtPoint(p);

        if (singleRowSelected) {
            lsm.setSelectionInterval(row, row);
        }

        //        final TMSYSFiles tm = (TMSYSFiles) tblFiles.getModel();
        //        final SYSFiles sysfile = tm.getRow(tblFiles.convertRowIndexToModel(row));


        SYSTools.unregisterListeners(menu);
        menu = new JPopupMenu();

        JMenuItem itemPopupShow = new JMenuItem(SYSTools.xx("misc.commands.show"), SYSConst.icon22magnify1);
        menu.add(itemPopupShow);

        JMenuItem itemPopupDelete = new JMenuItem(SYSTools.xx("misc.commands.delete"), SYSConst.icon22delete);
        itemPopupDelete.addActionListener(evt1 -> {
            MedOrder medOrder = ((TMMedOrders) tbl.getModel()).get(row);
            EntityTools.delete(medOrder);
            ((TMMedOrders) tbl.getModel()).delete(row);
        });
        menu.add(itemPopupDelete);


        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
    }
}
