package entity.roster;

import entity.system.Users;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 17.08.13
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */
public class TMRoster extends AbstractTableModel {
    HashMap<Users, ArrayList<RPlan>> content = new HashMap<Users, ArrayList<RPlan>>();
    ArrayList<Users> listUsers = new ArrayList<Users>();

    private final DateMidnight month;

    public TMRoster(ArrayList<RPlan> completeRoster, DateMidnight month) {
        this.month = month;
        prepareContent(completeRoster);
    }


    private void prepareContent(ArrayList<RPlan> input){
        for (RPlan rplan : input){

            if (!content.containsKey(rplan.getOwner())){
                content.put(rplan.getOwner(), new ArrayList<RPlan>(month.dayOfMonth().withMaximumValue().getDayOfMonth()));

                for (int i = 0; i < month.dayOfMonth().withMaximumValue().getDayOfMonth(); i++){
                    content.get(rplan.getOwner()).add(null);
                }


                listUsers.add(rplan.getOwner());
            }

            DateTime start = new DateTime(rplan.getStart());
            content.get(rplan.getOwner()).add(start.getDayOfMonth(), rplan);
        }


    }

    public void cleanup(){
        content.clear();
        listUsers.clear();
    }

    @Override
    public int getRowCount() {
        return listUsers.size();
    }

    @Override
    public int getColumnCount() {
        return month.dayOfMonth().withMaximumValue().getDayOfMonth()+1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
         switch (columnIndex){
             case 0: return listUsers.get(rowIndex).getFullname();
         }

        return content.get(listUsers.get(rowIndex)).get(columnIndex).getSymbolp();
    }
}
