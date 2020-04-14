package de.offene_pflege.gui.renderer;

import de.offene_pflege.backend.services.ResInfoCategoryService;

import javax.swing.*;

/**
 * Created by tloehr on 30.06.15.
 */
public class ResInfoCategoryTypesModel extends DefaultComboBoxModel<Integer> {
    public ResInfoCategoryTypesModel() {
        super(ResInfoCategoryService.TYPES);
    }
}
