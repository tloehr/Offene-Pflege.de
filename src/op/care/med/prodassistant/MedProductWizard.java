package op.care.med.prodassistant;

import com.jidesoft.dialog.*;
import com.jidesoft.wizard.*;
import entity.verordnungen.Darreichung;
import entity.verordnungen.MedProdukte;
import op.OPDE;
import org.apache.commons.collections.Closure;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 30.04.12
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class MedProductWizard {
    private int style;
    private WizardDialog wizard;
    private MedProdukte produkt;
    private Darreichung darreichung;

    public MedProductWizard() {
        style = WizardStyle.MACOSX_STYLE;
        wizard = new WizardDialog(new JFrame(), false);

        // setup model
        PageList model = new PageList();

        AbstractWizardPage page1 = new WelcomePage("Assistent zur Eingabe von Medizin-Produkten",
                "Dieser Assistent ermöglicht es Ihnen auf einfache Weise neue Medizinprodukte in die Datenbank von OPDE einzugeben.");

        AbstractWizardPage page2 = new ProduktPage("Produkteingabe", "Geben Sie hier zunächst den Produktnamen ohne irgendwelche Zusätze ein. Also besonders keine Angaben wie '10mg' oder 'retard'.");
//
        AbstractWizardPage page3 = new ZusatzPage("Zusatzbezeichnungen und Darreichungsformen",
                "Geben Sie jetzt evtl. vorhandene Zusatzbezeichnungen ein. Das können z.B. sein '10mg', 'retard', 'forte' etc. Häufig handelt es sich um Angaben zur Stärke oder Wirkstoffentfaltung. " +
                        "Ebenso müssen Sie hier die Darreichungsform auswählen.");
//
//        AbstractWizardPage page4 = new ChangeNextPagePage("Change Next Page",
//                "This page shows you how to change next page based on user selection.");
//
//        AbstractWizardPage page5 = new CompletionPage("Completing the JIDE Wizard Feature Demo",
//                "You have successfully run through the important features of Wizard Component from JIDE Software, Inc.");

        model.append(page1);
        model.append(page2);
        model.append(page3);
//        model.append(page3);
//        model.append(page5);

        wizard.setPageList(model);

        wizard.setStepsPaneNavigable(false);

        // the code below is to show you how to customize the finish and cancel action. You don't have to call these two methods.
//        wizard.setFinishAction(new AbstractAction("Finish") {
//            public void actionPerformed(ActionEvent e) {
//                if (wizard.closeCurrentPage(wizard.getButtonPanel().getButtonByName(ButtonNames.FINISH))) {
//                    if (exit) {
//                        System.exit(0);
//                    }
//                    else {
//                        wizard.dispose();
//                    }
//                }
//            }
//        });
//        wizard.setCancelAction(new AbstractAction("Cancel") {
//            public void actionPerformed(ActionEvent e) {
//                if (wizard.closeCurrentPage(wizard.getButtonPanel().getButtonByName(ButtonNames.CANCEL))) {
//                    if (exit) {
//                        System.exit(0);
//                    }
//                    else {
//                        wizard.dispose();
//                    }
//                }
//            }
//        });


        wizard.pack();
//        wizard.setResizable(false);
    }

    public WizardDialog getWizard() {
        return wizard;
    }

    private class WelcomePage extends WelcomeWizardPage {
        public WelcomePage(String title, String description) {
            super(title, description);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        OPDE.debug("WelcomePage OPENDED");
                        produkt = null;
                        darreichung = null;
                    }
                }
            });
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();

            JTextPane txt = new JTextPane();
            txt.setEditable(false);
            txt.setContentType("text/html");
            txt.setOpaque(false);
            txt.setText("<html><font face=\"" + OPDE.arial14.getFamily() + "\">Mit diesem Programm können Sie:" +
                    "<ul>" +
                    "<li>neue Medikamente ins System eintragen</li>" +
                    "<li>die Darreichungsform bestimmen</li>" +
                    "<li>Packungsgrößen eingeben</li>" +
                    "</ul>" +
                    "Sie werden hierzu Schritt für Schritt durch die Eingabe geführt.<p/>" +
                    "Die Bearbeitung kann jederzeit abgebrochen werden, drücken Sie dazu infach neben den Assistenten auf eine freie Fläche.</font></html>");

            addComponent(txt, true);
            addSpace();
            addText("Drücken Sie auf WEITER, wenn's los gehen soll.", OPDE.arial14);
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.CANCEL);
        }


    }

    private class ProduktPage extends DefaultWizardPage {
        public ProduktPage(String title, String description) {
            super(title, description);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() == PageEvent.PAGE_CLOSING) {
                        OPDE.debug(pageEvent.getSource());
                    } else if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        OPDE.debug("ProduktPage OPENDED");
                        produkt = null;
                    }
                }
            });
        }


        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(produkt == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(new PnlProdukt(new Closure() {
                @Override
                public void execute(Object o) {
                    produkt = (MedProdukte) o;
                    setupWizardButtons();
                }
            }), true);
        }


    }

    private class ZusatzPage extends DefaultWizardPage {
        public ZusatzPage(String title, String description) {
            super(title, description);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() == PageEvent.PAGE_CLOSING) {
                        OPDE.debug(pageEvent.getSource());
                    } else if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        OPDE.debug("ZusatzPage OPENDED");
                        darreichung = null;
                    }
                }
            });
        }


        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(darreichung == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(new PnlZusatz(new Closure() {
                @Override
                public void execute(Object o) {
                    darreichung = (Darreichung) o;
                    setupWizardButtons();
                }
            }, produkt), true);
        }


    }
}
