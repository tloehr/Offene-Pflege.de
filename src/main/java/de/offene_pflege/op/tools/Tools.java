package de.offene_pflege.op.tools;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

public class Tools {

    public static String toString(Object o, Class<?> clazz) {
           ArrayList<String> list = new ArrayList<>();
           Field f[] = clazz.getDeclaredFields();
           AccessibleObject.setAccessible(f, true);
           for (int i = 0; i < f.length; i++) {
               if (!Collection.class.isAssignableFrom(f[i].getType())) {
                   try {
                       list.add(f[i].getName() + "=" + f[i].get(o));
                   } catch (IllegalAccessException e) {
                       e.printStackTrace();
                   }
               }
   //            if (clazz.getSuperclass().getSuperclass() != null) {
   //                toString(o, clazz.getSuperclass(), list);
   //            }

           }
           return "{classname=" + clazz.getName() + "=> " + list.toString() + "}";


       }
}
