package gui.renderer;

import entity.info.ResInfoCategoryTools;

import javax.swing.*;

/**
 * Created by tloehr on 30.06.15.
 */
public class ResInfoCategoryTypesModel extends DefaultComboBoxModel<Integer> {
    public ResInfoCategoryTypesModel() {
        super(ResInfoCategoryTools.TYPES);
    }
}
