/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.system;

import entity.roster.UserContracts;
import entity.roster.UsersXML;
import op.OPDE;
import op.tools.GUITools;
import op.tools.SYSTools;
import org.eclipse.persistence.platform.xml.DefaultErrorHandler;
import org.joda.time.LocalDate;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author tloehr
 */
public class UsersTools {
    public static final short STATUS_INACTIVE = 0;
    public static final short STATUS_ACTIVE = 1;
    public static final short STATUS_ROOT = 2;

    public static ArrayList<Users> getUsers(boolean inactiveToo) {
        EntityManager em = OPDE.createEM();

        Query query;
        if (inactiveToo) {
            query = em.createQuery("SELECT u FROM Users u WHERE u.status <> :status ORDER BY u.nachname, u.vorname ");
            query.setParameter("status", STATUS_ROOT);
        } else {
            query = em.createQuery("SELECT u FROM Users u WHERE u.status = :status ORDER BY u.nachname, u.vorname ");
            query.setParameter("status", STATUS_ACTIVE);
        }

        ArrayList<Users> list = new ArrayList<Users>(query.getResultList());

        em.close();

        return list;

    }

    public static ListCellRenderer getRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = OPDE.lang.getString("misc.commands.>>noselection<<");
                } else if (o instanceof Users) {
                    text = o.toString();
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }

    public static boolean isAdmin(Users user) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT g FROM Groups g WHERE g.gid = 'admin' AND :user MEMBER OF g.members");
        query.setParameter("user", user);
        boolean admin = query.getResultList().size() > 0;
//        OPDE.debug("Benutzer ist " + (admin ? "" : "kein") + " Admin");
        em.close();
        return admin;
    }

    public static Color getBG1(Users user) {
        Color active = GUITools.getColor("CEF0FF");
        Color closed = GUITools.getColor("C0C0C0");
        if (user.isActive()) {
            return active;
        }

        return closed;
    }

    public static boolean isQualified(Users user) {
        boolean qualified = false;
        for (Groups group : user.getGroups()) {
            if (group.isQualified()) {
                qualified = true;
                break;
            }
        }
        return qualified;
    }

    public static Users checkPassword(String username, String password) {
        EntityManager em = OPDE.createEM();
        Users user = null;
        try {
            Query query = em.createQuery("SELECT o FROM Users o WHERE o.uid = :uKennung AND o.md5pw = :md5pw");
            query.setParameter("uKennung", username);
            query.setParameter("md5pw", SYSTools.hashword(password));
            user = (Users) query.getSingleResult();
        } catch (Exception e) {
            OPDE.info(e);
        } finally {
            em.close();
        }

        return user;
    }


    public static HashMap<Users, UserContracts> getUsersWithValidContractsIn(LocalDate month) {
        ArrayList<Users> listAllUsers = getUsers(true);
        HashMap<Users, UserContracts> mapUsers = new HashMap<Users, UserContracts>();
        // hier gehts weiter
        for (Users user : listAllUsers) {
            if (user.hasContracts()) {
                UserContracts contracts = getContracts(user);
                if (contracts.hasValidContractsInMonth(month)) {
                    mapUsers.put(user, contracts);
                }
            }
        }
        return mapUsers;
    }

    public static UserContracts getContracts(Users user) {
        if (SYSTools.catchNull(user.getXml()).isEmpty()){
            return null;
        }
        UsersXML usersXML = new UsersXML();

        SAXParserFactory spf = SAXParserFactory.newInstance();
        //        spf.setValidating(true);
        //        spf.setNamespaceAware(true);
        try {
            SAXParser saxParser = spf.newSAXParser();

            XMLReader reader = saxParser.getXMLReader();
            reader.setErrorHandler(new DefaultErrorHandler());
            reader.setContentHandler(usersXML);
            reader.parse(new InputSource(new StringReader(user.getXml())));
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        return usersXML.getUserContracts();
    }

}
