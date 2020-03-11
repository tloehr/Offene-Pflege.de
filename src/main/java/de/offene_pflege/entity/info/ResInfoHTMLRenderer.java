package de.offene_pflege.entity.info;

import de.offene_pflege.op.tools.InfoTreeNodeBean;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;

import java.util.ArrayList;

public class ResInfoHTMLRenderer implements ResInfoRenderInterface {
    public ResInfoHTMLRenderer() {
    }

    @Override
    public String renderBodyparts(ArrayList<String> bodyparts) {
        String text = "";
        for (String bodykey : bodyparts) {
            text += renderListEntry(bodykey);
        }
        return text.isEmpty() ? "" : renderGroupOfEntries("misc.msg.bodylocations", text);
    }

    @Override
    public String renderKeyValue(String key, String value) {
        return SYSConst.html_bold(SYSTools.xx(key) + ": ") + SYSTools.xx(value);
    }

    @Override
    public String renderListEntry(String entry) {
        return entry.isEmpty() ? "" : SYSConst.html_li(entry);
    }

    @Override
    public String renderNewLine() {
        return "<br/>";
    }

    @Override
    public String renderOption(InfoTreeNodeBean parent, InfoTreeNodeBean child) {
        if (parent.getValue().isPresent() && parent.getValue().get().toString().equals(child.getName()))
            return child.getLabel();
        return "";
    }

    @Override
    public String renderBoolean(String label, String sBool, boolean showFalseEntries) {
        Boolean bool = Boolean.parseBoolean(sBool);
        // Items mit nicht angeklickten Checkboxen weglassen.
        if (showFalseEntries)
            return (bool ? "\u2713" : "\u274d") + " " + label;
        else
            return bool ? label : "";
    }

    @Override
    public String renderLabel(String label) {
        return SYSConst.html_paragraph(SYSConst.html_bold(label));
    }

    @Override
    public String renderGroupOfEntries(String header, String list) {
        return list.isEmpty() ? "" : SYSConst.html_h3(header) + SYSConst.html_ul(list);
    }
}
