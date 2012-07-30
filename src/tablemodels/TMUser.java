/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tablemodels;

import entity.Users;

import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * @author tloehr
 */
public class TMUser extends DefaultTableModel {

    private BeanTableModel mymodel;

    public TMUser(List<Users> modelData) {
        mymodel = new BeanTableModel(Users.class, modelData);
    }

    @Override
    public Object getValueAt(int row, int column) {
        Users ocuser = (Users) mymodel.getRow(row);
        String value = "";
        switch (column) {
            case 0: {

                value = ocuser.getNameUndVorname();

                break;
            }
//            case 1: {
//                value = rezept.getDiaet();
//                break;
//            }
//            case 2: {
//                value = rezept.getVegetarisch();
//                break;
//            }
            default: {
                value = null;
            }
        }
        return value;
    }

    public Users getUserAt(int row) {
        return (Users) mymodel.getRow(row);
    }

    public void updateRow(int row) {
        fireTableRowsUpdated(row, row);
    }

    public void updateTable() {
        fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        Class<?> myclass;
        switch (column) {
            case 0: {
                myclass = String.class;
                break;
            }
//            case 1: {
//                myclass = Boolean.class;
//                break;
//            }
//            case 2: {
//                myclass = Boolean.class;
//                break;
//            }
            default: {
                myclass = String.class;
            }
        }
        return myclass;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public String getColumnName(int column) {
        String name;
        switch (column) {
            case 0: {
                name = "Name";
                break;
            }
//            case 1: {
//                name = "Diätkost";
//                break;
//            }
//            case 2: {
//                name = "Vegetarisch";
//                break;
//            }
            default: {
                name = Integer.toString(column);
            }
        }
        return name;
    }

    @Override
    public int getRowCount() {
        int rowcount = 0;

        if (mymodel != null) {
            rowcount = mymodel.getRowCount();
        }
        return rowcount;
    }
}