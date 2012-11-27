package op.care.med.prodassistant;

import com.jidesoft.dialog.*;
import com.jidesoft.wizard.*;
import entity.prescription.*;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.swing.*;
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

        AbstractWizardPage page1 = new WelcomePage(OPDE.lang.getString(internalClassID + ".page1.title"),
                OPDE.lang.getString(internalClassID + ".page1.description"));
        AbstractWizardPage page2 = new ProductPage(OPDE.lang.getString(internalClassID + ".page2.title"),
                OPDE.lang.getString(internalClassID + ".page2.description"));
        AbstractWizardPage page3 = new SubtextPage(OPDE.lang.getString(internalClassID + ".page3.title"),
                OPDE.lang.getString(internalClassID + ".page3.description"));
        AbstractWizardPage page4 = new PackagePage(OPDE.lang.getString(internalClassID + ".page4.title"),
                OPDE.lang.getString(internalClassID + ".page4.description"));
        AbstractWizardPage page5 = new ACMEPage(OPDE.lang.getString(internalClassID + ".page5.title"),
                OPDE.lang.getString(internalClassID + ".page5.description"));
        AbstractWizardPage page6 = new CompletionPage(OPDE.lang.getString(internalClassID + ".page6.title"),
                OPDE.lang.getString(internalClassID + ".page6.description"));

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
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(product.getBezeichnung() + ", " + TradeFormTools.toPrettyString(tradeform) + ", " + MedPackageTools.toPrettyString(aPackage) + " " + OPDE.lang.getString("misc.msg.entrysuccessful")));
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
                    OPDE.lang.getString(internalClassID + ".welcome") +
                    "</font></html>");

            main.add(BorderLayout.CENTER, txt);
            JLabel gfx = new JLabel(SYSConst.gfx259x203medic0);
            main.add(BorderLayout.EAST, gfx);
            addComponent(main, true);
            addText(OPDE.lang.getString("opde.wizards.buttontext.letsgo"), SYSConst.ARIAL14);
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.BACK, OPDE.lang.getString("opde.wizards.buttontext.back"));
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.NEXT, OPDE.lang.getString("opde.wizards.buttontext.next"));
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.FINISH, OPDE.lang.getString("opde.wizards.buttontext.finish"));
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.CANCEL, OPDE.lang.getString("opde.wizards.buttontext.cancel"));

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
            addComponent(new PnlProduct(new Closure() {
                @Override
                public void execute(Object o) {
                    product = (MedProducts) o;
                    setupWizardButtons();
                }
            }, prodtemplate), true);
        }


    }

    private class SubtextPage extends DefaultWizardPage {
        private PnlSubtext pnlSubtext;

        public SubtextPage(String title, String description) {
            super(title, description);
            setLeftPaneItems(LEFTPANE_STEPS);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() == PageEvent.PAGE_CLOSING) {
                        OPDE.debug(pageEvent.getSource());
                    } else if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        OPDE.debug("SubtextPage OPENDED");
//                        tradeform = null;
                        pnlSubtext.setProduct(product);
                    }
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
            pnlSubtext = new PnlSubtext(new Closure() {
                @Override
                public void execute(Object o) {
                    tradeform = (TradeForm) o;

                    OPDE.debug(SYSTools.catchNull(tradeform.getID(), "null") + ": " + TradeFormTools.toPrettyString(tradeform));
                    setupWizardButtons();
                }
            }, product);
            addComponent(pnlSubtext, true);
        }
    }

    private class PackagePage extends DefaultWizardPage {
        private PnlPackage pnlPackage;

        public PackagePage(String title, String description) {
            super(title, description);
            pnlPackage = new PnlPackage(new Closure() {
                @Override
                public void execute(Object o) {
                    aPackage = (MedPackage) o;
                    setupWizardButtons();
                }
            }, pzntemplate);

            setLeftPaneItems(LEFTPANE_STEPS);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() == PageEvent.PAGE_CLOSING) {
                        OPDE.debug(pageEvent.getSource());
                    } else if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        OPDE.debug("PackagePage OPENDED");
//                        aPackage = null;
                        pnlPackage.setLabelEinheit(DosageFormTools.toPrettyStringPackung(tradeform.getDosageForm()));
                        pnlPackage.setDarreichung(tradeform);
                    }
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
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() == PageEvent.PAGE_CLOSING) {
                        OPDE.debug(pageEvent.getSource());
                    } else if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        OPDE.debug("ACMEPage OPENDED");
//                        acme = null;
                        pnlACME.setProdukt(product);
//                        setupWizardButtons();
                    }
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
            pnlACME = new PnlACME(new Closure() {
                @Override
                public void execute(Object o) {
                    acme = (ACME) o;
                    setupWizardButtons();
                }
            }, product, wizard);
            addComponent(pnlACME, true);
        }

    }

    private class CompletionPage extends CompletionWizardPage {
        JTextPane txt;

        public CompletionPage(String title, String description) {
            super(title, description);
            setLeftPaneItems(LEFTPANE_GRAPHIC);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        txt.setText("<html>"+SYSConst.html_fontface +
                                check() +
                                "</font></html>");
                    }
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
            addText(OPDE.lang.getString("opde.wizards.buttontext.letsgo"), SYSConst.ARIAL14);
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        private String check() {
            String result = "<b>" + OPDE.lang.getString(internalClassID + ".page6.summaryline1") + "</b><br/>";
            result += OPDE.lang.getString(internalClassID + ".page6.summaryline2") + "<br/>";
            result += "<ul>";
            result += "<li>" + OPDE.lang.getString("misc.msg.drug") + ": <b>" + product.getBezeichnung() + "</b>" + (product.getMedPID() == null ? " <i>" + OPDE.lang.getString("misc.msg.willBeCreated") + "</i>" : " <i>" + OPDE.lang.getString("misc.msg.alreadyExits") + "</i>") + "</li>";
            result += "<li>" + OPDE.lang.getString(internalClassID + ".page3.title") + ": <b>" + TradeFormTools.toPrettyStringMedium(tradeform) + "</b>" + (tradeform.getID() == null ? " <i>" + OPDE.lang.getString("misc.msg.willBeCreated") + "</i>" : " <i>" + OPDE.lang.getString("misc.msg.alreadyExits") + "</i>") + "</li>";
            result += "<li>" + OPDE.lang.getString(internalClassID + ".page6.newPackageWillBeCreated") + ": <b>" + MedPackageTools.toPrettyString(aPackage) + "</b></li>";

            ACME displayFactory = acme == null ? product.getACME() : acme;
            result += "<li>" + OPDE.lang.getString(internalClassID + ".page5.title") + ": <b>" + displayFactory.getName() + ", " + displayFactory.getCity() + "</b>" + (displayFactory.getMphid() == null ? " <i>" + OPDE.lang.getString("misc.msg.willBeCreated") + "</i>" : " <i>" + OPDE.lang.getString("misc.msg.alreadyExits") + "</i>") + "</li>";

            result += "</ul>";

            result += "<p>" + OPDE.lang.getString(internalClassID + ".page6.summaryline1") + "</p>" +
                    "</font>";
            return result;
        }


    }
}