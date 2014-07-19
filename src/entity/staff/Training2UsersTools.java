package entity.staff;

import entity.system.Users;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;

/**
 * Created by tloehr on 19.07.14.
 */
public class Training2UsersTools {


    public static boolean contains(Collection<Training2Users> collection, Users user){
        boolean yes = false;

        for (Training2Users training2Users : collection){
            if (training2Users.getAttendee().equals(user)){
                yes = true;
                break;
            }
        }

        return yes;
    }


}
