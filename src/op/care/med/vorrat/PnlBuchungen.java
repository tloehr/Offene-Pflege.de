/*
 * Created by JFormDesigner on Fri May 18 14:02:02 CEST 2012
 */

package op.care.med.vorrat;

import com.jidesoft.popup.JidePopup;
import entity.verordnungen.MedBestand;
import entity.verordnungen.MedBuchungen;
import entity.verordnungen.MedBuchungenTools;
import op.OPDE;
import op.system.DlgYesNo;
import op.threads.DisplayMessage;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import tablemodels.TMBuchungen;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Torsten Löhr
 */
public class PnlBuchungen extends JPanel {

    private MedBestand bestand;
    private JPopupMenu menuBuch;
    private Closure actionAfterUpdate;

    public PnlBuchungen(MedBestand bestand, Closure actionAfterUpdate) {
        this.bestand = bestand;
        this.actionAfterUpdate = actionAfterUpdate;
        initComponents();
        reloadTable();
    }

    private void jspBuchungComponentResized(ComponentEvent e) {
        TableColumnModel tcm1 = tblBuchung.getColumnModel();

        if (tcm1.getColumnCount() == 0) {
            return;
        }

        tcm1.getColumn(TMBuchungen.COL_ID).setHeaderValue("ID");
        tcm1.getColumn(TMBuchungen.COL_Datum).setHeaderValue("Datum");
        tcm1.getColumn(TMBuchungen.COL_Text).setHeaderValue("Text");
        tcm1.getColumn(TMBuchungen.COL_Menge).setHeaderValue("Menge");
        tcm1.getColumn(TMBuchungen.COL_User).setHeaderValue("MitarbeiterIn");

        SYSTools.packTable(tblBuchung, 2);
    }

    private void tblBuchungMousePressed(MouseEvent evt) {
        final TMBuchungen tm = (TMBuchungen) tblBuchung.getModel();
        if (tm.getRowCount() == 0) {
            bestand = null;
            return;
        }

        Point p = evt.getPoint();
//        Point p2 = evt.getPoint();
        // Convert a coordinate relative to a component's bounds to screen coordinates
//        SwingUtilities.convertPointToScreen(p2, tblBuchung);

//        final Point screenposition = p2;
        final int row = tblBuchung.rowAtPoint(p);
        ListSelectionModel lsm = tblBuchung.getSelectionModel();
        lsm.setSelectionInterval(row, row);

//        final MedBuchungen buchung = tm.getData().get(row);

        if (actionAfterUpdate != null && evt.isPopupTrigger()) {

            SYSTools.unregisterListeners(menuBuch);
            menuBuch = new JPopupMenu();

            // Menüeinträge

            JMenuItem itemPopupDelete = new JMenuItem("Löschen", new ImageIcon(getClass().getResource("/artwork/22x22/bw/trashcan_empty.png")));
            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    new DlgYesNo("Möchten Sie die Buchung Nr. " + tm.getData().get(row).getBuchID() + " wirklich löschen ?", new ImageIcon(getClass().getResource("/artwork/48x48/bw/trashcan_empty.png")), new Closure() {
                        @Override
                        public void execute(Object answer) {
                            if (answer.equals(JOptionPane.YES_OPTION)) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    MedBuchungen mybuchung = em.merge(tm.getData().get(row));
                                    bestand = em.merge(bestand);

                                    em.lock(mybuchung, LockModeType.OPTIMISTIC);
                                    em.lock(bestand, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                    em.lock(bestand.getVorrat(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                    bestand.getBuchungen().remove(mybuchung);
                                    em.remove(mybuchung);
                                    em.getTransaction().commit();

                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Buchung Nr. " + tm.getData().get(row).getBuchID() + " wurde gelöscht.", 2));
                                } catch (OptimisticLockException ole) {
                                    em.getTransaction().rollback();
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Dieser Bestand wurde zwischenzeitlich geändert.", DisplayMessage.IMMEDIATELY, OPDE.getErrorMessageTime()));
                                } catch (Exception e) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                                reloadTable();
                                actionAfterUpdate.execute(bestand);
                            }
                        }
                    });

                }
            });
            menuBuch.add(itemPopupDelete);

            menuBuch.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        pnl123 = new JPanel();
        jspBuchung = new JScrollPane();
        tblBuchung = new JTable();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        //======== pnl123 ========
        {
            pnl123.setBorder(new TitledBorder(null, "Buchungen", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
                    new Font("Arial", Font.PLAIN, 14)));
            pnl123.setLayout(new BoxLayout(pnl123, BoxLayout.PAGE_AXIS));

            //======== jspBuchung ========
            {
                jspBuchung.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        jspBuchungComponentResized(e);
                    }
                });

                //---- tblBuchung ----
                tblBuchung.setModel(new DefaultTableModel(4, 0));
                tblBuchung.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                tblBuchung.setFont(new Font("Arial", Font.PLAIN, 14));
                tblBuchung.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        tblBuchungMousePressed(e);
                    }
                });
                jspBuchung.setViewportView(tblBuchung);
            }
            pnl123.add(jspBuchung);
        }
        add(pnl123);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    public void setBestand(MedBestand bestand) {
        this.bestand = bestand;
        reloadTable();
    }

    public boolean hasBestand() {
        return bestand != null;
    }

    public DlgYesNo resetBuchungen() {
        return new DlgYesNo("Alle Buchungen von Bestand Nr." + bestand.getBestID() + " zurücksetzen. Sind Sie sicher ?", new ImageIcon(getClass().getResource("/artwork/48x48/bw/undo.png")), new Closure() {
            @Override
            public void execute(Object answer) {
                if (answer.equals(JOptionPane.YES_OPTION)) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();

                        bestand = em.merge(bestand);
                        em.lock(bestand, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                        Query query = em.createQuery("DELETE FROM MedBuchungen b WHERE b.bestand = :bestand AND b.status <> :notStatus ");
                        query.setParameter("bestand", bestand);
                        query.setParameter("notStatus", MedBuchungenTools.STATUS_EINBUCHEN_ANFANGSBESTAND);
                        query.executeUpdate();

                        em.getTransaction().commit();
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Bestand Nr. " + bestand.getBestID() + " wurde zurück gesetzt.", 2));
                        em.refresh(bestand);
                    } catch (OptimisticLockException ole) {
                        em.getTransaction().rollback();
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Dieser Bestand wurde zwischenzeitlich geändert.", DisplayMessage.IMMEDIATELY, OPDE.getErrorMessageTime()));
                    } catch (Exception e) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                    }
                    reloadTable();
                    actionAfterUpdate.execute(bestand);
                }
            }
        });
    }

    public JidePopup getNeueBuchungPopup(JComponent owner) {
        final JidePopup popup = new JidePopup();

        DlgEditBuchung dlg = new DlgEditBuchung(bestand, new Closure() {
            @Override
            public void execute(Object o) {
                MedBuchungen myBuchung = null;
                if (o != null) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();

                        myBuchung = (MedBuchungen) em.merge(o);
                        bestand = em.merge(bestand);

                        em.lock(bestand, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                        em.lock(bestand.getVorrat(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                        em.getTransaction().commit();

                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Neue Buchung wurde eingegeben.", 2));

                        em.refresh(bestand);

                    } catch (OptimisticLockException ole) {
                        em.getTransaction().rollback();
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Dieser Bestand wurde zwischenzeitlich geändert.", DisplayMessage.IMMEDIATELY, OPDE.getErrorMessageTime()));
                    } catch (Exception e) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                    }
                }
                reloadTable();
                popup.hidePopup();
                actionAfterUpdate.execute(myBuchung);
            }
        });

        popup.setMovable(false);
        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
        popup.getContentPane().add(dlg);
        popup.setOwner(owner);
        popup.removeExcludedComponent(owner);
        popup.setDefaultFocusComponent(dlg.getDefaultFocusComponent());
        return popup;
    }

    private void reloadTable() {
        if (bestand == null) {
            tblBuchung.setModel(new DefaultTableModel());
        } else {
            EntityManager em = OPDE.createEM();
            Query query = em.createNamedQuery("MedBuchungen.findByBestand");

            bestand = em.merge(bestand);

            query.setParameter("bestand", bestand);

            tblBuchung.setModel(new TMBuchungen(query.getResultList()));
            tblBuchung.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            jspBuchung.dispatchEvent(new ComponentEvent(jspBuchung, ComponentEvent.COMPONENT_RESIZED));

            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setHorizontalAlignment(SwingConstants.RIGHT);
            DefaultTableCellRenderer renderer2 = new DefaultTableCellRenderer();
            renderer2.setHorizontalAlignment(SwingConstants.CENTER);

            tblBuchung.getColumnModel().getColumn(TMBuchungen.COL_Menge).setCellRenderer(renderer);
            tblBuchung.getColumnModel().getColumn(TMBuchungen.COL_Text).setCellRenderer(renderer2);
            em.close();
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel pnl123;
    private JScrollPane jspBuchung;
    private JTable tblBuchung;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
