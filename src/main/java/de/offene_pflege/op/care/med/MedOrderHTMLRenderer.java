package de.offene_pflege.op.care.med;

import de.offene_pflege.entity.prescription.MedOrder;
import de.offene_pflege.entity.prescription.MedOrderTools;
import de.offene_pflege.op.care.med.structure.TMMedOrders;
import de.offene_pflege.tablerenderer.RNDHTML;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.*;

@Log4j2
public class MedOrderHTMLRenderer extends RNDHTML {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JPanel c = (JPanel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        MedOrder medOrder = ((TMMedOrders) table.getModel()).getMedOrderList().get(row);
        c.setToolTipText(MedOrderTools.toPrettyHTML(medOrder));
        return c;
    }
}
