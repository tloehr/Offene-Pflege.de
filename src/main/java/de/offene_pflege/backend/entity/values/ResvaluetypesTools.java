package de.offene_pflege.backend.entity.values;

import de.offene_pflege.gui.GUITools;
import de.offene_pflege.op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 27.10.12
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */
public class ResvaluetypesTools {
    public static final short RR = 1;
    public static final short PULSE = 2;
    public static final short TEMP = 3;
    public static final short GLUCOSE = 4;
    public static final short WEIGHT = 5;
    public static final short HEIGHT = 6;
    public static final short BREATHING = 7;
    public static final short QUICK = 8;
    public static final short STOOL = 9;
    public static final short VOMIT = 10;
    public static final short LIQUIDBALANCE = 11;
    public static final short O2SATURATION = 12;
    public static final short ASPIRATION = 13;
    public static final short PAIN = 14;

    public static Resvaluetypes getType(short type) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT v FROM Resvaluetypes v WHERE v.valType = :type");

        query.setParameter("type", type);
        Resvaluetypes result = (Resvaluetypes) query.getSingleResult();
        return result;
    }

    public static Color getColor(Resvaluetypes Resvaluetypes) {
          String color = "black";
          switch (Resvaluetypes.getValType()) {
              case ResvaluetypesTools.STOOL: {
                  color = "006600";
                  break;
              }
              case ResvaluetypesTools.LIQUIDBALANCE: {
                  color = "00cccc";
                  break;
              }
              case ResvaluetypesTools.GLUCOSE: {
                  color = "3399ff";
                  break;
              }
              case ResvaluetypesTools.HEIGHT: {
                  color = "ff00ff";
                  break;
              }
              case ResvaluetypesTools.VOMIT: {
                  color = "ffcc00";
                  break;
              }
              case ResvaluetypesTools.QUICK: {
                  color = "9900ff";
                  break;
              }
              case ResvaluetypesTools.RR: {
                  color = "ff99cc";
                  break;
              }
              case ResvaluetypesTools.PULSE: {
                  color = "ff0000";
                  break;
              }
              case ResvaluetypesTools.TEMP: {
                  color = "00ff66";
                  break;
              }
              case ResvaluetypesTools.BREATHING: {
                  color = "cccc00";
                  break;
              }
              case ResvaluetypesTools.WEIGHT: {
                  color = "cc99ff";
                  break;
              }
              case ResvaluetypesTools.O2SATURATION: {
                  color = "GRAY";
                  break;
              }
              case ResvaluetypesTools.ASPIRATION: {
                  color = "0000cc";
                  break;
              }
              default: {
                  color = "black";
                  break;
              }
          }

          return GUITools.getColor(color);
      }


}
