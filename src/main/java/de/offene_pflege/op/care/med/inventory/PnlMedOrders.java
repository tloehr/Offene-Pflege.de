package de.offene_pflege.op.care.med.inventory;

import de.offene_pflege.entity.files.SYSFilesTools;
import de.offene_pflege.entity.prescription.*;
import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.system.InternalClassACL;
import de.offene_pflege.gui.ButtonColumn;
import de.offene_pflege.op.tools.HTMLTools;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import de.offene_pflege.services.HomesService;
import de.offene_pflege.tablerenderer.RNDHTML;
import lombok.extern.log4j.Log4j2;
import org.jdesktop.swingx.HorizontalLayout;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.List;

@Log4j2
public class PnlMedOrders extends JPanel {
    private JPopupMenu menu;
    JTable tbl;
    JScrollPane scrl;
    JLabel lbl_week;

    private String internalClassID = "opde.medication";

    public PnlMedOrders() {
        super();
        initPanel();
    }

    private void initPanel() {
        lbl_week = new JLabel("--");
        lbl_week.setFont(SYSConst.ARIAL28);

        setLayout(new BorderLayout(5, 5));
        menu = null;

        add(lbl_week, BorderLayout.NORTH);
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

        scrl.setViewportView(tbl);
    }

    public List<MedOrder> get_list() {
        return ((TMMedOrders) tbl.getModel()).getMedOrderList();
    }

    public void reload(List<MedOrder> list, boolean show_notes, Optional<LocalDate> week) {

        lbl_week.setText("alles");
        week.ifPresent(localDate -> {
            LocalDate start_day = localDate.with(DayOfWeek.of(SYSPropsTools.getInteger(SYSPropsTools.KEY_CALC_MEDI_START_ORDER_WEEK, DayOfWeek.MONDAY.getValue())));
            LocalDate end_day = start_day.plusDays(6);
            lbl_week.setText(start_day.atStartOfDay().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)) + " => " + end_day.atTime(LocalTime.MAX).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)));
        });


        tbl.setModel(new TMMedOrders(list, OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID), OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, internalClassID), show_notes));
        tbl.getModel().addTableModelListener(e -> {
            if (e.getColumn() == TMMedOrders.COL_WHERE_TO_ORDER)
                firePropertyChange("table_where_to_order_changed", -1, e.getFirstRow());
        });

        SwingUtilities.invokeLater(() -> {

            java.util.List<HasName> liste = new ArrayList();
            liste.addAll(GPTools.getAllActive());
            liste.addAll(HospitalTools.getAll());
            Collections.sort(liste, (o1, o2) -> {
                return o1.getName().compareTo(o2.getName());
            });
            JComboBox<HasName> cmb = new JComboBox<>(SYSTools.list2cmb(liste));
            cmb.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    return super.getListCellRendererComponent(list, (value instanceof GP ?
                                    SYSTools.anonymizeName(((GP) value).getName(), SYSTools.INDEX_LASTNAME) + ", " + SYSTools.anonymizeName(((GP) value).getFirstname(), SYSTools.INDEX_FIRSTNAME_MALE) :
                                    ((Hospital) value).getName() + ", " + ((Hospital) value).getCity()
                            ),
                            index, isSelected, cellHasFocus);
                }
            });

            tbl.getColumnModel().getColumn(TMMedOrders.COL_TradeForm).setCellRenderer(new RNDHTML());
            tbl.getColumnModel().getColumn(TMMedOrders.COL_Resident).setCellRenderer(new RNDHTML());
            tbl.getColumnModel().getColumn(TMMedOrders.COL_WHERE_TO_ORDER).setCellRenderer(new RNDHTML());
            tbl.getColumnModel().getColumn(TMMedOrders.COL_ORDER_INFO).setCellRenderer(new RNDHTML());
            tbl.getColumnModel().getColumn(TMMedOrders.COL_WHERE_TO_ORDER).setCellEditor(new DefaultCellEditor(cmb));
            ((DefaultCellEditor) tbl.getColumnModel().getColumn(TMMedOrders.COL_WHERE_TO_ORDER).getCellEditor()).setClickCountToStart(2);


            JTextField txt = new JTextField();
            txt.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txt.selectAll();
                }
            });
            tbl.getColumnModel().getColumn(TMMedOrders.COL_complete).setMaxWidth(45);
            tbl.getColumnModel().getColumn(TMMedOrders.COL_DELETE).setMaxWidth(45);
            tbl.getColumnModel().getColumn(TMMedOrders.COL_CONFIRMED).setMaxWidth(45);
            //tbl.getColumnModel().getColumn(TMMedOrders.COL_ORDER_INFO).setMaxWidth(80);

            ButtonColumn confirm_button = new ButtonColumn(tbl, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JTable table = (JTable) e.getSource();
                    int row_of_button = Integer.valueOf(e.getActionCommand());
                    ((TMMedOrders) table.getModel()).confirm(tbl.convertRowIndexToModel(row_of_button));
                }
            }, TMMedOrders.COL_CONFIRMED);
            ButtonColumn complete_button = new ButtonColumn(tbl, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JTable table = (JTable) e.getSource();
                    int row_of_button = Integer.valueOf(e.getActionCommand());
                    ((TMMedOrders) table.getModel()).complete(tbl.convertRowIndexToModel(row_of_button));
                }
            }, TMMedOrders.COL_complete);
            ButtonColumn delete_button = new ButtonColumn(tbl, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JTable table = (JTable) e.getSource();
                    int row_of_button = Integer.valueOf(e.getActionCommand());
                    ((TMMedOrders) table.getModel()).delete(tbl.convertRowIndexToModel(row_of_button));
                }
            }, TMMedOrders.COL_DELETE);

            tbl.revalidate();
            tbl.repaint();
        });
    }

    private JPanel getButtonPanel() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new HorizontalLayout(5));

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

        JMenuItem itemPopupInfo = new JMenuItem(SYSTools.xx("misc.filters.showDetails"), SYSConst.icon22info);
        itemPopupInfo.addActionListener(e -> {
            MedOrder medOrder = ((TMMedOrders) tbl.getModel()).getMedOrderList().get(tbl.convertRowIndexToModel(tbl.getSelectionModel().getLeadSelectionIndex()));
            GUITools.showPopup(GUITools.getHTMLPopup(tbl, MedOrderTools.toPrettyHTMLOrderInfos(medOrder, true)), SwingConstants.NORTH);
        });
        menu.add(itemPopupInfo);


        menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
    }

    public List<MedOrder> getSelected() {
        ArrayList<MedOrder> list = new ArrayList<>();
        for (int sel : tbl.getSelectionModel().getSelectedIndices()) {
            MedOrder medOrder = ((TMMedOrders) tbl.getModel()).get(tbl.convertRowIndexToModel(sel));
            list.add(medOrder);
        }
        return list;
    }

    private String filter_text(MedOrder medOrder) {
        return medOrder.getGp() != null ? medOrder.getGp().getName() :
                medOrder.getHospital().getName();
    }

    public void print(Optional<HasName> filter) {
        TMMedOrders model = (TMMedOrders) tbl.getModel();
        String table_content = SYSConst.html_h1("Medikamenten Bestellungen");

        table_content += SYSConst.html_paragraph(HomesService.getAsTextForTX(HomesService.get()));

        int cols = model.getColumnCount();

        ArrayList<String> row_content = new ArrayList<>();
        //ArrayList<MedOrder> list_of_free_text = new ArrayList<>();
        for (int row = 0; row < model.getRowCount(); row++) {
            row_content.clear();

            if (filter.isEmpty() || filter_text(model.get(tbl.convertRowIndexToModel(row))).equalsIgnoreCase(filter.get().getName())) {
                for (int col = 0; col < cols - 3; col++) {
                    row_content.add(SYSTools.catchNull(model.getValueAt(tbl.convertRowIndexToModel(row), col)));
                }
                table_content += HTMLTools.getTableRow("td style=\"vertical-align:top\"", "", row_content);
            }
        }

        SYSFilesTools.print(
                HTMLTools.getTable(
                        HTMLTools.getTableRow("th", "fonttextgray", Arrays.asList(Arrays.copyOfRange(model.getHeader(), 0, cols - 3))) +
                                table_content, "border=1"
                ), false
        );
    }

}
