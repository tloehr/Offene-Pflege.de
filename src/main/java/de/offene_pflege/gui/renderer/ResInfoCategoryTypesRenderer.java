package de.offene_pflege.gui.renderer;

import de.offene_pflege.backend.services.ResInfoCategoryService;
import de.offene_pflege.op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Created by tloehr on 30.06.15.
 */
public class ResInfoCategoryTypesRenderer implements ListCellRenderer {
    HashMap<Integer, String> model = new HashMap();

    public ResInfoCategoryTypesRenderer() {
        int i = 0;
        for (int type : ResInfoCategoryService.TYPES) {
            model.put(type, ResInfoCategoryService.TYPESS[i]);
            i++;
        }
    }

    @Override
    public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
        String text;
        if (o == null) {
            text = SYSTools.toHTML(SYSTools.xx("misc.commands.>>noselection<<"));
        } else {
            text = SYSTools.xx(model.get(o));
        }
        return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
    }
}