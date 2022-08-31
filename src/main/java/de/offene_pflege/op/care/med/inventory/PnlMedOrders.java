package de.offene_pflege.op.care.med.inventory;

import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.prescription.*;
import de.offene_pflege.entity.system.SYSPropsTools;
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
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

@Log4j2
public class PnlMedOrders extends JPanel {
    private JPopupMenu menu;
    JTable tbl;
    JScrollPane scrl;
    private String internalClassID = "opde.medication";

    public PnlMedOrders() {
        super();
        initPanel();
    }

    private void initPanel() {
        setLayout(new BorderLayout(5, 5));
        menu = null;
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
        tbl.setModel(new TMMedOrders(MedOrderTools.get_open_orders()));
        OPDE.getDisplayManager().setMainMessage("Medikamenten Bestellungen");

        SwingUtilities.invokeLater(() -> {
            java.util.List<HasName> liste = new ArrayList();
            liste.addAll(GPTools.getAllActive());
            liste.addAll(HospitalTools.getAll());
            Collections.sort(liste, (o1, o2) -> {
                return o1.getName().compareTo(o2.getName());
            });
            JComboBox cmb = new JComboBox<>(SYSTools.list2cmb(liste));
            cmb.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    return super.getListCellRendererComponent(list, (value instanceof GP ?
                                    ((GP) value).getName() + ", " + ((GP) value).getFirstname() :
                                    ((Hospital) value).getName() + ", " + ((Hospital) value).getCity()
                            ),
                            index, isSelected, cellHasFocus);
                }
            });
            tbl.getColumnModel().getColumn(TMMedOrders.COL_TradeForm).setCellRenderer(new RNDHTML());
            tbl.getColumnModel().getColumn(TMMedOrders.COL_WHERE_TO_ORDER).setCellEditor(new DefaultCellEditor(cmb));

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
            tbl.getColumnModel().getColumn(TMMedOrders.COL_ORDER_DATE).setMaxWidth(80);
            tbl.revalidate();
            tbl.repaint();
        });
    }

    private JPanel getButtonPanel() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new HorizontalLayout(5));

        JTextField days_range = new JTextField(10);
        days_range.setText(OPDE.getProps().getProperty("opde.medorder:auto_order_day_range","7"));
        days_range.setInputVerifier(new NumberVerifier(BigDecimal.ONE, BigDecimal.valueOf(31), true));
        days_range.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                SYSPropsTools.storeProp("opde.medorder:auto_order_day_range", ((JTextComponent) e.getSource()).getText().trim(), OPDE.getLogin().getUser());
            }
        });

        JButton generate_orders = new JButton(SYSConst.icon22calc);
        generate_orders.addActionListener(e -> {

            OPDE.getMainframe().setBlocked(true);
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));

            SwingWorker worker = new SwingWorker() {

                @Override
                protected Object doInBackground() {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        MedOrderTools.generate_orders(Double.parseDouble(days_range.getText()), MedOrderTools.get_open_orders(em)).forEach(medOrder -> em.merge(medOrder));
                        em.getTransaction().commit();
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
        pnl.add(days_range);
        pnl.add(generate_orders);
        //todo: abgeschlossene der letzten x tage einblenden
        // mit checkbox und startdatum (immer 7 Tage zurück)
        pnl.add(new JSeparator(SwingConstants.VERTICAL));
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

            for (int sel : tbl.getSelectionModel().getSelectedIndices()) {
                MedOrder medOrder = ((TMMedOrders) tbl.getModel()).get(sel);
                EntityTools.delete(medOrder);
                ((TMMedOrders) tbl.getModel()).delete(row);
            }
//            EntityTools.delete(medOrder);
//            ((TMMedOrders) tbl.getModel()).delete(row);
        });
        menu.add(itemPopupDelete);


        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
    }

    class Tooltip_cell_renderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            log.debug(value);
//            c.setToolTipText(value.toString());
            c.setToolTipText(c.getText());
            return c;
        }
    }
}
