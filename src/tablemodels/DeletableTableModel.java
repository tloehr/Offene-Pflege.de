/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tablemodels;

/**
 * Dieses Interface wird gebraucht, damit der DelButtonEditor fÃ¼r alle TableModels benutzt werden kann,
 * die dieses Interface implementieren.
 *
 * @author tloehr
 */
public interface DeletableTableModel {
    public void removeRow(int row);
}
