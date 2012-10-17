/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.reports;

import entity.system.Users;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author tloehr
 */
public class NR2UserTools {
    public static boolean containsUser(Collection<NR2User> list, Users user) {
        boolean found = false;
        for (NR2User conn : list) {
            found = conn.getUser().equals(user);
            if (found) break;
        }
        return found;
    }
}
