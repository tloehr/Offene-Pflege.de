/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package op.care.nursingprocess.massnahmen;

import op.tools.ListElement;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;

public class RNDMassTree implements TreeCellRenderer {

    private Color bg;

    public RNDMassTree() {
        super();
    }

    public Color getBackground() {
        return bg;
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        if (selected) {
            bg = SYSConst.grey80;
        } else {
            bg = Color.WHITE;
        }

        JPanel pnl = new JPanel();
        pnl.setOpaque(true);
        pnl.setBackground(bg);
        JLabel lbl1 = new JLabel();
        lbl1.setBackground(bg);
        JLabel lbl2 = null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        ListElement le = (ListElement) node.getUserObject();
        Object[] o = (Object[]) le.getObject();
        //Object[] o = new Object[]{beschreibung, zeit,tloehr new Vector(), new Vector(), typ, 0d};
        //Object[] modfaktor = new Object[]{label, beschreibung, zeit, prozent, new Boolean(selected)};
        Vector mdfs = (Vector) o[ParserMassnahmen.O_MODFAKTOR];
        String label = le.getValue();
        String beschreibung = o[ParserMassnahmen.O_BESCHREIBUNG].toString();
        pnl.setToolTipText(beschreibung);
        Double zeit = (Double) o[ParserMassnahmen.O_ZEIT];
        Double sum = (Double) o[ParserMassnahmen.O_SUMME];
        int typ = (Integer) o[ParserMassnahmen.O_TYP];
        lbl1.setFont(new java.awt.Font("Dialog", 0, 10));
        //OPDE.debug(mdfs.size());
        if (modfaktorenSelected(mdfs)) {
            pnl.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        } else {
            pnl.setBorder(null);
        }
        if (zeit > 0) {
            label += " <font color=\"blue\">" + zeit + " Min</font>";
        }
        if (sum > 0 && sum.doubleValue() != zeit.doubleValue()) {
            label += " <font color=\"green\"><b>" + sum + " Min</b></font>";
            //OPDE.debug(label + zeit + " " + new Boolean(sum != zeit).toString());
        }
        lbl1.setText(SYSTools.toHTML(label + modfaktoren2html(mdfs)));

        lbl1.setOpaque(selected);

        switch (typ) {
            case ParserMassnahmen.TYPE_ROOT: {
                lbl1.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/work.png")));
                break;
            }
            case ParserMassnahmen.TYPE_Vorbereitung: {
                lbl1.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/switchuser.png")));
                break;
            }
            case ParserMassnahmen.TYPE_Nachbereitung: {
                lbl1.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/logout.png")));
                break;
            }
            case ParserMassnahmen.TYPE_DF: {
                lbl1.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/util.png")));
                break;
            }
//                case ParserMassnahmen.TYPE_Teilschritt: {
//                    lbl1.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/work.png")));
//                    break;
//                }
            default: {
                lbl1.setIcon(null);
            }
        }

        pnl.setLayout(new FlowLayout());
        pnl.add(lbl1);

        if (mdfs.size() > 0) {
            lbl2 = new JLabel();
            lbl2.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/plusminus.png")));
            lbl2.setBackground(bg);
            pnl.add(lbl2);
        }
        return pnl;
    }

    private boolean modfaktorenSelected(Vector mdf) {
        boolean yes = false;
        Enumeration e = mdf.elements();
        while (e.hasMoreElements() && !yes) {
            Object[] o = (Object[]) e.nextElement();
            yes = (Boolean) o[4];
        }
        return yes;
    }

    private String modfaktoren2html(Vector mdf) {
        //Object[] modfaktor = new Object[]{label, beschreibung, zeit, prozent, new Boolean(selected)};
        String result = "";
        Enumeration e = mdf.elements();
        while (e.hasMoreElements()) {
            Object[] o = (Object[]) e.nextElement();
            boolean selected = (Boolean) o[4];
            if (selected) {
                if (result.equals("")) { // Noch leer, dann müssen wir erst die unsorted Umgebung beginnen.
                    result += "<ul>";
                }

                String label = o[0].toString();
                double zeit = (Double) o[2];
                double prozent = (Double) o[3];

                String additional = "";
                if (zeit != 0) {
                    additional = " (" + (zeit > 0 ? "+" : "") + zeit + " Min)";
                }

                if (prozent != 0) {
                    additional = " (" + (prozent > 0 ? "+" : "") + prozent + "%)";
                }

                String prefix = "";
                String postfix = "</font>";
                if (zeit < 0 || prozent < 0) {
                    prefix = "<font color=\"green\">";
                } else {
                    prefix = "<font color=\"red\">";
                }

                result += "<li>" + prefix + label + additional + postfix + "</li>";
            }

        }
        if (!result.equals("")) {
            result += "</ul>";
        }

        return result;
    }
}
