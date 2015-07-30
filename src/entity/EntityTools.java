package entity;

import entity.system.SYSPropsTools;
import gui.GUITools;
import gui.interfaces.NotRemovableUnlessEmpty;
import op.OPDE;
import op.threads.DisplayManager;
import op.tools.SYSTools;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 16.06.11
 * Time: 14:19
 * To change this template use File | Settings | File Templates.
 */
public class EntityTools {

    static Logger logger = Logger.getLogger(EntityTools.class);

    public static boolean persist(Object entity) {
        boolean success = false;

        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            success = true;
        } catch (Exception e) {
            OPDE.fatal(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return success;
    }

    public static <T> T find(Class<T> entity, Object id) {
        T foundEntity = null;
        EntityManager em = OPDE.createEM();

        try {
            foundEntity = em.find(entity, id);
        } catch (Exception e) {
            OPDE.fatal(e);
        }
        return foundEntity;
    }

    /**
     * @param entity to merge
     * @param <T>
     * @return merged entity. null if failed. happens only with optimistic locking exceptions.
     */
    public static <T> T merge(T entity) {

        T mergedEntity = null;
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            mergedEntity = em.merge(entity);
            em.lock(mergedEntity, LockModeType.OPTIMISTIC);
            em.getTransaction().commit();
        } catch (OptimisticLockException ole) {
            OPDE.warn(logger, ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                OPDE.getMainframe().emptyFrame();
                OPDE.getMainframe().afterLogin();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(logger, e);
        } finally {
            em.close();
        }
        return mergedEntity;
    }

//    public static <T> T store(T entity) {
//        boolean success = false;
//        EntityManager em = OPDE.createEM();
//        try {
//            em.getTransaction().begin();
//            if (em.contains(entity)) {
//                em.merge(entity);
//            } else {
//                em.persist(entity);
//            }
//            em.getTransaction().commit();
//            em.refresh(entity);
//            success = true;
//        } catch (Exception e) {
//            OPDE.fatal(e);
//            em.getTransaction().rollback();
//        }
//        return success;
//    }

    public static boolean delete(Object entity) {
        boolean success = false;
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            em.remove(em.merge(entity));
            em.getTransaction().commit();
            success = true;
        } catch (Exception e) {
            OPDE.fatal(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return success;
    }

    public static <T> T refresh(T entity) {

        EntityManager em = OPDE.createEM();
        try {
            em.refresh(em.merge(entity));
//            success = true;
        } catch (Exception e) {
            Logger.getLogger(EntityTools.class).error(e);
        } finally {
            em.close();
        }
        return entity;
    }

    /**
     * Erzeugt einen String, der die PrimärSchlüssel durch komma getrennt enthält.
     * Ist ein Workaround für Queries mit dem Schlüsselwort IN.
     *
     * @param entities
     * @return
     */
    public static String getIDList(List entities) {
        EntityManager em = OPDE.createEM();
        String list = "";
        Iterator it = entities.iterator();
        while (it.hasNext()) {
            list += em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(it.next());
            list += it.hasNext() ? "," : "";
        }
        em.close();
        return list;
    }

    public static String getMySQLsearchPattern(String pattern) {
        pattern = pattern.trim().replaceAll("%", "");
        pattern = "%" + pattern + "%";
        return pattern;
    }


    /**
     * checks if an entity may be deleted due to the constraints of the @NotRemovableUnlessEmpty annotation
     *
     * @param entity
     * @return
     */
    public static String mayBeDeleted(Object entity) {
        HashSet<String> mayBeDeleted = new HashSet<>();

        if (entity.getClass().isAnnotationPresent(NotRemovableUnlessEmpty.class) && !entity.getClass().getAnnotation(NotRemovableUnlessEmpty.class).evalualedByClass().isEmpty()) {
            try {
                NotRemovableUnlessEmpty annotation = entity.getClass().getAnnotation(NotRemovableUnlessEmpty.class);
                Class evalClazz = Class.forName(annotation.evalualedByClass());

                Boolean removable = (Boolean) evalClazz.getMethod("isRemovable", Object.class).invoke(evalClazz.newInstance(), entity);

                if (!removable) {
                    mayBeDeleted.add(annotation.message());
                }

            } catch (Exception e) {
                OPDE.fatal(Logger.getLogger(entity.getClass()), e);
            }
        }

        Field[] fields = entity.getClass().getDeclaredFields();
        for (final Field field : fields) {
            if (field.isAnnotationPresent(NotRemovableUnlessEmpty.class)) {
                NotRemovableUnlessEmpty annotation = field.getAnnotation(NotRemovableUnlessEmpty.class);

                try {
                    if (PropertyUtils.getProperty(entity, field.getName()) instanceof Collection) {
                        if (!((Collection) PropertyUtils.getProperty(entity, field.getName())).isEmpty()) {
                            mayBeDeleted.add(annotation.message());
                        }
                    }
                } catch (Exception e) {
                    OPDE.fatal(Logger.getLogger(entity.getClass()), e);
                }
            }
        }

        return GUITools.createStringListFrom(mayBeDeleted);
    }


    public static int getDatabaseSchemaVersion(Connection jdbcConnection) throws SQLException {
        int version = -1;

        String query = " SELECT p.V FROM sysprops p WHERE p.K = ? ";
        PreparedStatement stmt = jdbcConnection.prepareStatement(query);
        stmt.setString(1, SYSPropsTools.KEY_DB_VERSION);
        ResultSet rs = stmt.executeQuery();

        if (rs.first()) {
            String v = rs.getString("V");
            version = Integer.parseInt(v);
        }

        return version;
    }

    public static String getJDBCUrl(String host, String port, String catalog) {
        return "jdbc:mysql://" + SYSTools.catchNull(host) + ":" + SYSTools.catchNull(port) + (SYSTools.catchNull(catalog).isEmpty() ? "" : "/" + SYSTools.catchNull(catalog));
    }


    /**
     * kind of locks the database and makes the other possibly client logout from OPDE.
     * they can't login until this lock has been removed again.
     *
     * @param jdbcConnection
     * @param locked
     * @throws SQLException
     */
    public static void setServerLocked(Connection jdbcConnection, boolean locked) throws SQLException {
        String query = " UPDATE sysprops p SET p.V = ? WHERE p.K = ? ";
        PreparedStatement stmt = jdbcConnection.prepareStatement(query);
        stmt.setString(1, Boolean.toString(locked).toLowerCase());
        stmt.setString(2, SYSPropsTools.KEY_MAINTENANCE_MODE);
        int result = stmt.executeUpdate();
        stmt.close();

        // just in case the appropriate record does not yet exist.
        if (result == 0) {
            query = " INSERT INTO sysprops (K, V) VALUES (?, ?) ";
            stmt = jdbcConnection.prepareStatement(query);
            stmt.setString(1, SYSPropsTools.KEY_MAINTENANCE_MODE);
            stmt.setString(2, Boolean.toString(locked).toLowerCase());
            stmt.executeUpdate();
            stmt.close();
        }
    }

    public static boolean isServerLocked(Connection jdbcConnection) throws SQLException {
        String query = " SELECT * FROM sysprops p WHERE p.K = ? AND p.v = 'true' ";
        PreparedStatement stmt = jdbcConnection.prepareStatement(query);
        stmt.setString(1, SYSPropsTools.KEY_MAINTENANCE_MODE);
        ResultSet rs = stmt.executeQuery();
        rs.last();
        int result = rs.getRow();
        rs.close();
        stmt.close();
        return result > 0;
    }


//    public static boolean setupDB(Connection jdbcConnection, String catalog) {
//           boolean ok = false;
//
//           try {
//               String query = " SELECT p.V FROM SYSProps p WHERE p.K = ? ";
//               PreparedStatement stmt = jdbcConnection.prepareStatement(query);
//               stmt.setString(1, "dbstructure");
//               ResultSet rs = stmt.executeQuery();
//
//               if (!rs.first()) {
//                   ok = false;
//               } else {
//                   String version = rs.getString("V");
//                   ok = Integer.parseInt(version) == Main.getHistory().get(Main.getBuildnum()).getDbstructure();
//               }
//
//               jdbcConnection.close();
//           } catch (SQLException e) {
//               Main.logger.error(e);
//               ok = false;
//           }
//           return ok;
//       }
}
