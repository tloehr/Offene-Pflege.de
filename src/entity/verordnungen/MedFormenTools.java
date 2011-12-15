package entity.verordnungen;

import op.tools.SYSConst;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 01.12.11
 * Time: 17:17
 * To change this template use File | Settings | File Templates.
 */
public class MedFormenTools {
    public static final int APV1 = 0;
    public static final int APV_PER_DAF = 1;
    public static final int APV_PER_BW = 2;

    public static final String EINHEIT[] = {"", "Stück", "ml", "l", "mg", "g", "cm", "m"}; // Für AnwEinheit, PackEinheit, Dimension

    public static String getAnwText(MedFormen form) {

        String result = "";

        if (form.getAnwText() != null && !form.getAnwText().isEmpty()) {
            result = form.getAnwText();
        } else {
            result = SYSConst.EINHEIT[form.getAnwEinheit()];
        }

        return result;
    }

}
