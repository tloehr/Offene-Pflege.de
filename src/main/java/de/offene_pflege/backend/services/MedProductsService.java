package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.done.ACME;
import de.offene_pflege.backend.entity.done.MedProducts;
import de.offene_pflege.op.tools.SYSTools;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA. User: tloehr Date: 14.12.11 Time: 11:27 To change this template use File | Settings | File
 * Templates.
 */
public class MedProductsService {

    public static MedProducts create(ACME acme, String text) {
        MedProducts mp = new MedProducts();
        mp.setAcme(acme);
        mp.setText(text);
        return mp;
    }

    public static ListCellRenderer getMedProdukteRenderer() {
        return (jList, o, i, isSelected, cellHasFocus) -> {
            String text;
            if (o == null) {
                text = SYSTools.toHTML("<i>Keine Auswahl</i>");
            } else if (o instanceof MedProducts) {
                MedProducts produkt = (MedProducts) o;
                text = produkt.getText() + " [" + produkt.getAcme().getName() + "]";
            } else {
                text = o.toString();
            }
            return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
        };
    }
}
