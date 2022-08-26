package de.offene_pflege.op.care.med.inventory;

import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.prescription.*;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.care.med.structure.TMMedOrders;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.NumberVerifier;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import de.offene_pflege.tablerenderer.RNDHTML;
import lombok.extern.log4j.Log4j2;
import org.jdesktop.swingx.HorizontalLayout;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Log4j2
public class PnlMedOrders extends JPanel {
    private final java.util.List<MedOrders> list_med_orders;
    private JPopupMenu menu;
    JTable tbl;
    JScrollPane scrl;
    private String internalClassID = "opde.medication";
    private final JComboBox<MedOrders> cmb_orders;


    public PnlMedOrders() {
        super();
        setLayout(new BorderLayout(5, 5));
        menu = null;

        list_med_orders = new ArrayList<>();
        cmb_orders = new JComboBox<>();
        cmb_orders.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) return;
            reload();
        });
        cmb_orders.setFont(new Font("Arial", Font.PLAIN, 18));
        cmb_orders.setRenderer((list, value, index, isSelected, cellHasFocus) ->
                new DefaultListCellRenderer().getListCellRendererComponent(list, value.getOrder_week().format(DateTimeFormatter.ISO_WEEK_DATE), index, isSelected, cellHasFocus));

        reload_combo_box();
        initPanel();
    }

    private void initPanel() {
        JPanel pnl = getButtonPanel();
        add(pnl, BorderLayout.SOUTH);
        scrl = new JScrollPane();
        add(scrl, BorderLayout.CENTER);
        tbl = new JTable();
        tbl.setAutoCreateRowSorter(true);
        tbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                tableMousePressed(e);
            }
        });
        reload();
        scrl.setViewportView(tbl);
    }

    public void reload() {
        MedOrders selected = list_med_orders.get(cmb_orders.getSelectedIndex());
        tbl.setModel(new TMMedOrders(selected.getOrderList()));
        OPDE.getDisplayManager().setMainMessage(get_label_text());

        SwingUtilities.invokeLater(() -> {
            JComboBox<GP> cmbGP = new JComboBox<>(SYSTools.list2cmb(GPTools.getAllActive()));
            cmbGP.setRenderer(GPTools.getRenderer());
            tbl.getColumnModel().getColumn(TMMedOrders.COL_TradeForm).setCellRenderer(new RNDHTML());
            tbl.getColumnModel().getColumn(TMMedOrders.COL_GP).setCellEditor(new DefaultCellEditor(cmbGP));
            JTextField txt = new JTextField();
            txt.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txt.selectAll();
                }
            });
            tbl.getColumnModel().getColumn(TMMedOrders.COL_note).setCellEditor(new DefaultCellEditor(txt));
            tbl.getColumnModel().getColumn(TMMedOrders.COL_note).setCellRenderer(new RNDHTML());
            tbl.getColumnModel().getColumn(TMMedOrders.COL_complete).setMaxWidth(30);
            tbl.revalidate();
            tbl.repaint();
        });
    }

    private void reload_combo_box() {
        list_med_orders.clear();
        list_med_orders.addAll(MedOrdersTools.getMedOrders(1));
        if (list_med_orders.isEmpty()) list_med_orders.add(MedOrdersTools.create(java.time.LocalDate.now()));
        cmb_orders.setModel(SYSTools.list2cmb(list_med_orders));
    }


//    private JLabel createLabel() {
//        JLabel lbl = new JLabel(get_label_text());
//        lbl.setFont(new Font("Arial", Font.PLAIN, 28));
//        return lbl;
//    }

    private String get_label_text() {
        MedOrders selected = ((MedOrders) cmb_orders.getSelectedItem());
        return String.format("Bestellung #%s - Für die Woche %s => %s %s",
                selected.getId(),
                selected.getOrder_week().format(DateTimeFormatter.ofPattern("dd.MM.YY")),
                selected.getOrder_week().with(DayOfWeek.SUNDAY).format(DateTimeFormatter.ofPattern("dd.MM.YY")),
                (selected.getClosed_by() != null ? "-abgeschlossen-" : "")
        );
    }

    private JPanel getButtonPanel() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new HorizontalLayout(5));


//        JButton left = new JButton(SYSConst.icon22REV);
//        JButton right = new JButton(SYSConst.icon22FWD);
        // MANAGER ONLY
        JButton close = new JButton(SYSConst.icon22playerStop);
        JButton reopen = new JButton(SYSConst.icon22playerPlay);

        JTextField days_range = new JTextField("14");
        days_range.setInputVerifier(new NumberVerifier(BigDecimal.ONE, BigDecimal.valueOf(31), true));

        JButton generate_orders = new JButton(SYSConst.icon22calc);
        generate_orders.addActionListener(e -> {

            OPDE.getMainframe().setBlocked(true);
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));

            SwingWorker worker = new SwingWorker() {

                @Override
                protected Object doInBackground() {
                    EntityManager em = OPDE.createEM();
                    try {
                        MedOrders selected = list_med_orders.get(cmb_orders.getSelectedIndex());
                        em.getTransaction().begin();
                        MedOrders medOrders = em.merge(selected);
                        MedOrderTools.generate_orders(medOrders, Double.parseDouble(days_range.getText())).forEach(medOrder -> em.merge(medOrder));
                        em.getTransaction().commit();
                        list_med_orders.set(cmb_orders.getSelectedIndex(), medOrders);
                        reload();
                    } catch (OptimisticLockException | RollbackException ole) {
                        log.warn(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                            OPDE.getMainframe().emptyFrame();
                            OPDE.getMainframe().afterLogin();
                        }
                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                    } catch (Exception exc) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(exc);
                    } finally {
                        em.close();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();

        });
//        left.addActionListener(e -> MedOrdersTools.next(selected_med_orders, -1).ifPresent(medOrders -> {
//            selected_med_orders = medOrders;
//            tbl.setModel(new TMMedOrders(selected_med_orders.getOrderList()));
//        }));
//        right.addActionListener(e -> MedOrdersTools.next(selected_med_orders, -1).ifPresent(medOrders -> {
//            selected_med_orders = medOrders;
//            tbl.setModel(new TMMedOrders(selected_med_orders.getOrderList()));
//        }));
        reopen.addActionListener(e -> {
            MedOrders selected = list_med_orders.get(cmb_orders.getSelectedIndex());
            MedOrdersTools.open(selected).ifPresent(medOrders -> list_med_orders.set(cmb_orders.getSelectedIndex(), medOrders));
            OPDE.getDisplayManager().setMainMessage(get_label_text());
        });
        close.addActionListener(e -> {
            MedOrders selected = list_med_orders.get(cmb_orders.getSelectedIndex());
            MedOrdersTools.close(selected).ifPresent(medOrders -> list_med_orders.set(cmb_orders.getSelectedIndex(), medOrders));
            OPDE.getDisplayManager().setMainMessage(get_label_text());
        });
        pnl.add(cmb_orders);
        pnl.add(new JSeparator(SwingConstants.VERTICAL));
        pnl.add(days_range);
        pnl.add(generate_orders);
        pnl.add(new JSeparator(SwingConstants.VERTICAL));
        pnl.add(close);
        pnl.add(reopen);
        return pnl;
    }

    private void tableMousePressed(MouseEvent evt) {
        if (!SwingUtilities.isRightMouseButton(evt)) return;

        Point p = evt.getPoint();
        JTable tbl = (JTable) evt.getSource();
        ListSelectionModel lsm = tbl.getSelectionModel();
        Point p2 = evt.getPoint();
        SwingUtilities.convertPointToScreen(p2, tbl);

        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        final int row = tbl.rowAtPoint(p);
        final int col = tbl.columnAtPoint(p);

        if (singleRowSelected) {
            lsm.setSelectionInterval(row, row);
        }

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

    class Tooltip_cell_renderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//            c.setToolTipText(value.toString());
            c.setToolTipText(c.getText());
            return c;
        }
    }
}
