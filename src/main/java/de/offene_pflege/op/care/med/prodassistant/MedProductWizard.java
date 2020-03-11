package de.offene_pflege.op.care.med.prodassistant;

import com.jidesoft.dialog.ButtonEvent;
import com.jidesoft.dialog.ButtonNames;
import com.jidesoft.dialog.PageEvent;
import com.jidesoft.dialog.PageList;
import com.jidesoft.wizard.*;
import de.offene_pflege.entity.prescription.*;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 30.04.12
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class MedProductWizard {

    public static final String internalClassID = "opde.medication.medproduct.wizard";

    private WizardDialog wizard;
    private MedProducts product;
    private TradeForm tradeform;
    private MedPackage aPackage;
    private ACME acme;
    private Closure finishAction;
    private String pzntemplate = null, prodtemplate = null;

    private final int PAGE_WELCOME = 0;
    private final int PAGE_PRODUCT = 1;
    private final int PAGE_SUBTEXT = 2;
    private final int PAGE_PACKAGE = 3;
    private final int PAGE_ACME = 4;
    private final int PAGE_COMPLETION = 5;

    public MedProductWizard(Closure finishAction) {
        this.finishAction = finishAction;
//
//        if (template != null) {
//            pzntemplate = MedPackageTools.parsePZN(template);
//            if (pzntemplate == null) {
//                prodtemplate = template.trim();
//            }
//        }
        createWizard();
    }

    private void createWizard() {
        wizard = new WizardDialog(OPDE.getMainframe(), false);
        PageList model = new PageList();

        AbstractWizardPage page1 = new WelcomePage(SYSTools.xx("opde.medication.medproduct.wizard.page1.title"),
                SYSTools.xx("opde.medication.medproduct.wizard.page1.description"));
        AbstractWizardPage page2 = new ProductPage(SYSTools.xx("opde.medication.medproduct.wizard.page2.title"),
                SYSTools.xx("opde.medication.medproduct.wizard.page2.description"));
        AbstractWizardPage page3 = new SubtextPage(SYSTools.xx("opde.medication.medproduct.wizard.page3.title"),
                SYSTools.xx("opde.medication.medproduct.wizard.page3.description"));
        AbstractWizardPage page4 = new PackagePage(SYSTools.xx("opde.medication.medproduct.wizard.page4.title"),
                SYSTools.xx("opde.medication.medproduct.wizard.page4.description"));
        AbstractWizardPage page5 = new ACMEPage(SYSTools.xx("opde.medication.medproduct.wizard.page5.title"),
                SYSTools.xx("opde.medication.medproduct.wizard.page5.description"));
        AbstractWizardPage page6 = new CompletionPage(SYSTools.xx("opde.medication.medproduct.wizard.page6.title"),
                SYSTools.xx("opde.medication.medproduct.wizard.page6.description"));

        model.append(page1);
        model.append(page2);
        model.append(page3);
        model.append(page4);
        model.append(page5);
        model.append(page6);

        wizard.setPageList(model);

        wizard.setFinishAction(new AbstractAction("Finish") {
            public void actionPerformed(ActionEvent e) {
                if (wizard.closeCurrentPage(wizard.getButtonPanel().getButtonByName(ButtonNames.FINISH))) {
                    save();
                }
            }
        });

        wizard.setCancelAction(new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent e) {
                if (wizard.closeCurrentPage(wizard.getButtonPanel().getButtonByName(ButtonNames.FINISH))) {
                    finishAction.execute(null);
                }
            }
        });
        ((JPanel) wizard.getContentPane()).setBorder(new LineBorder(Color.BLACK, 1));
        wizard.pack();
    }

    public WizardDialog getWizard() {
        return wizard;
    }

    private void save() {
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            product = em.merge(product);
            tradeform = em.merge(tradeform);
            aPackage = em.merge(aPackage);
            if (acme != null) {
                acme = em.merge(acme);
            }

            if (!product.getTradeforms().contains(tradeform)) {
                product.getTradeforms().add(tradeform);
            }

            tradeform.getPackages().add(aPackage);

            em.getTransaction().commit();
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(product.getText() + ", " + TradeFormTools.toPrettyString(tradeform) + ", " + MedPackageTools.toPrettyString(aPackage) + " " + SYSTools.xx("misc.msg.entrysuccessful")));
            finishAction.execute(aPackage);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(e);
        } finally {
            em.close();
        }
    }

    private class WelcomePage extends WelcomeWizardPage {

        public WelcomePage(String title, String description) {
            super(title, description);
            setLeftPaneItems(LEFTPANE_EMPTY);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();

            JPanel main = new JPanel(new BorderLayout(5, 5));

            JTextPane txt = new JTextPane();
            txt.setEditable(false);
            txt.setContentType("text/html");
            txt.setOpaque(false);
            txt.setText("<html>" + SYSConst.html_fontface +
                    SYSTools.xx("opde.medication.medproduct.wizard.welcome") +
                    "</font></html>");

            main.add(BorderLayout.CENTER, txt);
            JLabel gfx = new JLabel(SYSConst.gfx259x203medic0);
            main.add(BorderLayout.EAST, gfx);
            addComponent(main, true);
            addText(SYSTools.xx("opde.wizards.buttontext.letsgo"), SYSConst.ARIAL14);
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.BACK, SYSTools.xx("opde.wizards.buttontext.back"));
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.NEXT, SYSTools.xx("opde.wizards.buttontext.next"));
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.FINISH, SYSTools.xx("opde.wizards.buttontext.finish"));
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.CANCEL, SYSTools.xx("opde.wizards.buttontext.cancel"));

            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }
    }

    private class ProductPage extends DefaultWizardPage {

        public ProductPage(String title, String description) {
            super(title, description);
            setLeftPaneItems(LEFTPANE_STEPS);
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();

            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(product == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(new PnlProduct(o -> {
                product = (MedProducts) o;
                setupWizardButtons();
            }, prodtemplate), true);
        }


    }

    private class SubtextPage extends DefaultWizardPage {
        private PnlTradeForm pnlTradeForm;

        public SubtextPage(String title, String description) {
            super(title, description);
            setLeftPaneItems(LEFTPANE_STEPS);
            addPageListener(pageEvent -> {
                if (pageEvent.getID() == PageEvent.PAGE_CLOSING) {
                    OPDE.debug(pageEvent.getSource());
                } else if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                    OPDE.debug("SubtextPage OPENDED");
//                        tradeform = null;
                    pnlTradeForm.setProduct(product);
                }
            });
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(tradeform == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            pnlTradeForm = new PnlTradeForm(o -> {
                tradeform = (TradeForm) o;

                OPDE.debug(SYSTools.catchNull(tradeform.getID(), "null") + ": " + TradeFormTools.toPrettyString(tradeform));
                setupWizardButtons();
            }, product);
            addComponent(pnlTradeForm, true);
        }
    }

    private class PackagePage extends DefaultWizardPage {
        private PnlPackage pnlPackage;

        public PackagePage(String title, String description) {
            super(title, description);
            pnlPackage = new PnlPackage(o -> {
                aPackage = (MedPackage) o;
                setupWizardButtons();
            }, pzntemplate);

            setLeftPaneItems(LEFTPANE_STEPS);
            addPageListener(pageEvent -> {
                if (pageEvent.getID() == PageEvent.PAGE_CLOSING) {
                    OPDE.debug(pageEvent.getSource());
                } else if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                    OPDE.debug("PackagePage OPENDED");
//                        aPackage = null;
                    pnlPackage.setLabelEinheit(DosageFormTools.getPackageText(tradeform.getDosageForm()));
                    pnlPackage.setDarreichung(tradeform);
                }
            });
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(aPackage == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
            updateNextPage();
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(pnlPackage, true);
        }

        private void updateNextPage() {
            if (product.getACME() != null) {
                getOwner().setNextPage(getOwner().getPageList().getPage(PAGE_COMPLETION));
            } else {
                getOwner().setNextPage(getOwner().getPageList().getPage(PAGE_ACME));
            }
        }
    }

    private class ACMEPage extends DefaultWizardPage {
        private PnlACME pnlACME;

        public ACMEPage(String title, String description) {
            super(title, description);
            setLeftPaneItems(LEFTPANE_GRAPHIC);
            addPageListener(pageEvent -> {
                if (pageEvent.getID() == PageEvent.PAGE_CLOSING) {
                    OPDE.debug(pageEvent.getSource());
                } else if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                    OPDE.debug("ACMEPage OPENDED");
//                        acme = null;
                    pnlACME.setProdukt(product);
//                        setupWizardButtons();
                }
            });
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(acme == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            pnlACME = new PnlACME(o -> {
                acme = (ACME) o;
                setupWizardButtons();
            }, product, wizard);
            addComponent(pnlACME, true);
        }

    }

    private class CompletionPage extends CompletionWizardPage {
        JTextPane txt;

        public CompletionPage(String title, String description) {
            super(title, description);
            setLeftPaneItems(LEFTPANE_GRAPHIC);
            addPageListener(pageEvent -> {
                if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                    txt.setText("<html>" + SYSConst.html_fontface +
                            check() +
                            "</font></html>");
                }
            });
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            txt = new JTextPane();
            txt.setEditable(false);
            txt.setContentType("text/html");
            txt.setOpaque(false);
            addComponent(txt, true);
            addSpace();
            addText(SYSTools.xx("opde.wizards.buttontext.letsgo"), SYSConst.ARIAL14);
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        private String check() {
            String result = "<b>" + SYSTools.xx("opde.medication.medproduct.wizard.page6.summaryline1") + "</b><br/>";
            result += SYSTools.xx("opde.medication.medproduct.wizard.page6.summaryline2") + "<br/>";
            result += "<ul>";
            result += "<li>" + SYSTools.xx("misc.msg.drug") + ": <b>" + product.getText() + "</b>" + (product.getMedPID() == null ? " <i>" + SYSTools.xx("misc.msg.willBeCreated") + "</i>" : " <i>" + SYSTools.xx("misc.msg.alreadyExits") + "</i>") + "</li>";
            result += "<li>" + SYSTools.xx("opde.medication.medproduct.wizard.page3.title") + ": <b>" + TradeFormTools.toPrettyStringMediumWithExpiry(tradeform) + "</b>" + (tradeform.getID() == null ? " <i>" + SYSTools.xx("misc.msg.willBeCreated") + "</i>" : " <i>" + SYSTools.xx("misc.msg.alreadyExits") + "</i>") + "</li>";
            if (tradeform.getDosageForm().getUPRState() == DosageFormTools.STATE_UPRn) {

                result += "<li>" + SYSTools.xx("misc.msg.upr") + ": <b>";
                result += (tradeform.getConstantUPRn() == null ? SYSTools.xx("opde.medication.medproduct.wizard.page6.calcUPR") : SYSTools.xx("opde.medication.medproduct.wizard.page6.setUPR") + SYSConst.UNITS[tradeform.getDosageForm().getUsageUnit()] + " " + tradeform.getDosageForm().getUsageText() + " " + SYSTools.xx("misc.msg.to1") + " " + SYSConst.UNITS[tradeform.getDosageForm().getPackUnit()]) + "</b>" + "</li>";
            }
            result += "<li>" + SYSTools.xx("opde.medication.medproduct.wizard.page6.newPackageWillBeCreated") + ": <b>" + MedPackageTools.toPrettyString(aPackage) + "</b></li>";

            ACME displayFactory = acme == null ? product.getACME() : acme;
            result += "<li>" + SYSTools.xx("opde.medication.medproduct.wizard.page5.title") + ": <b>" + displayFactory.getName() + ", " + displayFactory.getCity() + "</b>" + (displayFactory.getMphid() == null ? " <i>" + SYSTools.xx("misc.msg.willBeCreated") + "</i>" : " <i>" + SYSTools.xx("misc.msg.alreadyExits") + "</i>") + "</li>";

            result += "</ul>";

            result += "<p>" + SYSTools.xx("opde.medication.medproduct.wizard.page6.summaryline1") + "</p>" +
                    "</font>";
            return result;
        }


    }
}