/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import entity.system.Users;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author tloehr
 */
public class PB2UserTools {
    public static boolean containsUser(Collection<PB2User> list, Users user){
        boolean found = false;
        Iterator<PB2User> it = list.iterator();
        while (!found && it.hasNext()){
            found = it.next().getUser().equals(user);
        }
        return found;
    }
}
