package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.done.ACME;
import de.offene_pflege.op.tools.SYSTools;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA. User: tloehr Date: 05.01.12 Time: 15:43 To change this template use File | Settings | File
 * Templates.
 */
public class ACMEService {

    public static ACME create(String name, String street, String zipcode, String city, String tel, String fax, String www) {
        ACME acme = new ACME();

        acme.setName(name);
        acme.setStreet(street);
        acme.setZipcode(zipcode);
        acme.setCity(city);
        acme.setTel(tel);
        acme.setFax(fax);
        acme.setWww(www);
        return acme;

    }

    public static ListCellRenderer getRenderer(int maxlen) {
        final int max = maxlen;
        return (jList, o, i, isSelected, cellHasFocus) -> {
            String text;
            if (o == null) {
                text = SYSTools.toHTML("<i>Keine Auswahl</i>");
            } else if (o instanceof ACME) {
                text = toPrettyStringShort((ACME) o);
            } else {
                text = o.toString();
            }
            if (max > 0) {
                text = SYSTools.left(text, max);
            }
            return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
        };
    }

    public static String toPrettyStringShort(ACME acme) {
        return acme.getName() + SYSTools.catchNull(acme.getCity(), ", ", "");
    }
}