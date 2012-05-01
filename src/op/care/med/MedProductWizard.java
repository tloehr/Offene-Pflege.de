package op.care.med;

import com.jidesoft.dialog.ButtonNames;
import com.jidesoft.dialog.PageList;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.wizard.AbstractWizardPage;
import com.jidesoft.wizard.WelcomeWizardPage;
import com.jidesoft.wizard.WizardDialog;
import com.jidesoft.wizard.WizardStyle;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 30.04.12
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class MedProductWizard {
    private int style;

    public MedProductWizard() {
        style = WizardStyle.MACOSX_STYLE;

        final WizardDialog wizard = new WizardDialog(new JFrame(), "JIDE Wizard Demo");

        // setup model
        PageList model = new PageList();

        AbstractWizardPage page1 = new WelcomePage("Assistent zur Eingabe von Medizin-Produkten",
                "Dieser Assistent ermöglicht es Ihnen auf einfache Weise neue Medizinprodukte in die Datenbank von OPDE einzugeben.");

//        AbstractWizardPage page2 = new LicensePage("License Agreement",
//                "This page shows you how to enable/disable a button based on user selection.");
//
//        AbstractWizardPage page3 = new InfoWarningPage("Infos and Warnings",
//                "This page shows you how to show info message and warning message.");
//
//        AbstractWizardPage page4 = new ChangeNextPagePage("Change Next Page",
//                "This page shows you how to change next page based on user selection.");
//
//        AbstractWizardPage page5 = new CompletionPage("Completing the JIDE Wizard Feature Demo",
//                "You have successfully run through the important features of Wizard Component from JIDE Software, Inc.");

        model.append(page1);
//        model.append(page2);
//        model.append(page4);
//        model.append(page3);
//        model.append(page5);

        wizard.setPageList(model);

        wizard.setStepsPaneNavigable(true);

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
        wizard.setResizable(true); // for wizard, it's better to make it not resizable.
        JideSwingUtilities.globalCenterWindow(wizard);
        wizard.setVisible(true);
    }


    public static class WelcomePage extends WelcomeWizardPage {
        public WelcomePage(String title, String description) {
            super(title, description);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addText("Mit diesem Programm können Sie:\n" +
                    "neue Medikamente im System eintragen\n" +
                    "die Darreichungsform bestimmen\n" +
                    "Packungsgrößen eingeben\n" +
                    "Sie werden hierzu Schritt für Schritt durch die Eingabe geführt.\n" +
                    "Sie können jederzeit die Bearbeitung abbrechen, indem Sie auf den X Knopf unten rechts drücken. Drücken Sie auf die blauen Pfeil-Knöpfe um zur nächsten Fragezu gelangen.");
            addSpace();
            addText("Drücken Sie auf WEITER, wenn's los gehen soll.");
        }
    }
}
