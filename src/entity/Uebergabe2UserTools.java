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
public class Uebergabe2UserTools {
 public static boolean containsUser(Collection<Uebergabe2User> list, Users user){
        boolean found = false;
        Iterator<Uebergabe2User> it = list.iterator();
        while (!found && it.hasNext()){
            found = it.next().getUser().equals(user);
        }
        return found;
    }
}
