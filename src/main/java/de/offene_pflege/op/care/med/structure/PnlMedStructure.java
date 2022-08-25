package de.offene_pflege.op.care.med.structure;

import com.jidesoft.popup.JidePopup;
import de.offene_pflege.entity.prescription.*;
import de.offene_pflege.gui.interfaces.CleanablePanel;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.system.InternalClassACL;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import lombok.extern.log4j.Log4j2;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Optional;

@Log4j2
public class PnlMedStructure extends CleanablePanel {
    Optional<JidePopup> optionalPopup;
    JScrollPane scrl;
    private boolean show_table_ids;
    final private JPopupMenu menu;
    private MedProducts product;

    public PnlMedStructure(boolean show_table_ids, MedProducts medProducts) {
        super("opde.medication");
        optionalPopup = Optional.empty();
        this.product = medProducts;
        this.show_table_ids = show_table_ids;
        menu = new JPopupMenu();
        initPanel();
        createTree(medProducts);
    }

    public void setShow_table_ids(boolean show_table_ids) {
        this.show_table_ids = show_table_ids;
        reload();
    }

    private void initPanel() {
        scrl = new JScrollPane();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(scrl);
    }


    @Override
    public void reload() {
        createTree(this.product);
    }

    public void createTree(MedProducts medProducts) {
        this.product = medProducts;
        SwingUtilities.invokeLater(() -> {
            JTree treeMed = new JTree();
            treeMed.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    treeMousePressed(e);
                }
            });
            DefaultMutableTreeNode root;
            root = new DefaultMutableTreeNode(product);
            SYSTools.addAllNodes(root, getTradeForms(product));
            treeMed.setModel(new DefaultTreeModel(root));
            treeMed.setCellRenderer(new TreeRenderer());
            scrl.setViewportView(treeMed);
            SYSTools.expandAll(treeMed);
            scrl.revalidate();
            scrl.repaint();
        });
    }

    public void cleanup() {
        optionalPopup.ifPresent(jidePopup -> {
            if (jidePopup.isShowing()) jidePopup.hidePopup();
        });
        SYSTools.unregisterListeners(menu);
    }

    private void treeMousePressed(MouseEvent evt) {
        final JTree treeMed = (JTree) evt.getSource();
        if (!OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, internalClassID)) return;

        if (SwingUtilities.isRightMouseButton(evt)) {
            optionalPopup.ifPresent(jidePopup -> SYSTools.unregisterListeners(jidePopup));
            optionalPopup = Optional.of(new JidePopup());

            if (treeMed.getRowForLocation(evt.getX(), evt.getY()) != -1 && OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
                JMenuItem itemedit = null;
                JMenuItem itemUPRedit = null;
                TreePath curPath = treeMed.getPathForLocation(evt.getX(), evt.getY());
                final DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) curPath.getLastPathComponent();
                treeMed.setSelectionPath(curPath);

                if (dmtn.getUserObject() instanceof TradeForm) {
                    final TradeForm tradeForm = (TradeForm) dmtn.getUserObject();
                    itemedit = new JMenuItem(SYSTools.xx("misc.msg.edit"));
                    itemedit.addActionListener(evt14 -> {
                        if (currentEditor != null) return;
                        currentEditor = new DlgTradeForm(tradeForm);
                        currentEditor.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent e) {
                                createTree(((DlgTradeForm) currentEditor).getTradeForm().getMedProduct());
                                currentEditor = null;
                            }
                        });
                        currentEditor.setVisible(true);
                    });
                    itemUPRedit = new JMenuItem(SYSTools.xx("upreditor.tooltip"));
                    itemUPRedit.addActionListener(evt13 -> new DlgUPREditor(tradeForm, o -> reload()));
                    itemUPRedit.setEnabled(tradeForm.getDosageForm().isUPRn());
                } else if (dmtn.getUserObject() instanceof MedPackage) {
                    final MedPackage packung = (MedPackage) dmtn.getUserObject();
                    itemedit = new JMenuItem(SYSTools.xx("misc.msg.edit"));
                    itemedit.addActionListener(evt12 -> {
                        if (currentEditor != null) return;
                        currentEditor = new DlgPack(SYSTools.xx("misc.msg.edit"), packung);
                        currentEditor.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent e) {
                                createTree(((DlgPack) currentEditor).getPackage().getTradeForm().getMedProduct());
                                currentEditor = null;
                            }
                        });
                        currentEditor.setVisible(true);

                    });
                } else if (dmtn.getUserObject() instanceof MedProducts) {
                    final MedProducts medProducts = (MedProducts) dmtn.getUserObject();
                    itemedit = new JMenuItem(SYSTools.xx("misc.msg.edit"));
                    itemedit.addActionListener(evt1 -> {
                        if (currentEditor != null) return;
                        currentEditor = new DlgProduct(SYSTools.xx("misc.msg.edit"), medProducts);
                        currentEditor.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosed(WindowEvent e) {
                                createTree(((DlgProduct) currentEditor).getProduct());
                                currentEditor = null;
                            }
                        });
                        currentEditor.setVisible(true);
                        log.debug("opened");
                    });
                }

                menu.removeAll();
                if (itemedit != null) menu.add(itemedit);
                if (itemUPRedit != null) menu.add(itemUPRedit);
            }
            menu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }

    private java.util.List getTradeForms(final MedProducts product) {
        java.util.List result = new ArrayList();

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT m FROM TradeForm m WHERE m.medProduct = :medProdukt ORDER BY m.dosageForm.preparation");
        query.setParameter("medProdukt", product);

        java.util.List<TradeForm> listDAF = query.getResultList();
        em.close();


        for (TradeForm daf : listDAF) {
            DefaultMutableTreeNode nodeDAF = new DefaultMutableTreeNode(daf);
            SYSTools.addAllNodes(nodeDAF, getPackung(daf));
            result.add(nodeDAF);
        }


        return result;
    }

    private java.util.List getPackung(TradeForm darreichung) {
        java.util.List result = new ArrayList();
        for (MedPackage aPackage : darreichung.getPackages()) {
            result.add(new DefaultMutableTreeNode(aPackage));
        }
        return result;
    }

    private class TreeRenderer extends DefaultTreeCellRenderer {
        TreeRenderer() {
            super();
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            JLabel component = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (node.getUserObject() instanceof MedProducts) {
                component.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/info.png")));
                MedProducts myprod = (MedProducts) node.getUserObject();
                component.setText((show_table_ids ? "[" + ((MedProducts) node.getUserObject()).getMedPID() + "] " : "") + myprod.getText() + ", " + myprod.getACME().getName() + ", " + myprod.getACME().getCity());
            } else if (node.getUserObject() instanceof TradeForm) {
                component.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/medical.png")));
                component.setText((show_table_ids ? "[" + ((TradeForm) node.getUserObject()).getID() + "] " : "") + TradeFormTools.toPrettyStringMediumWithExpiry((TradeForm) node.getUserObject()));
            } else if (node.getUserObject() instanceof MedPackage) {
                component.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/package.png")));
                component.setText((show_table_ids ? "[" + ((MedPackage) node.getUserObject()).getID() + "] " : "") + MedPackageTools.toPrettyString((MedPackage) node.getUserObject()));
            } else {
                component.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/filenew.png")));
                component.setText(null);
            }
            component.setFont(SYSConst.ARIAL14);
            //            setBackground(selected ? SYSConst.lightblue : Color.WHITE);

            return component;
        }

    }

}
