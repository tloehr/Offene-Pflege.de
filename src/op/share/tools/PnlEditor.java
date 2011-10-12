/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 * Teilweise übernommen aus O'Reilly Java Swing (2002)#
 * http://www.offene-pflege.de/quellenverzeichnis#JSW2
 *
 *
 */

/*
 * PnlEditor.java
 *
 * Created on 07.06.2011, 14:31:36
 */
package op.share.tools;

import op.OPDE;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.MinimalHTMLWriter;
import java.awt.event.ActionEvent;
import java.io.*;

/**
 * @author tloehr
 */
public class PnlEditor extends JPanel {

    JPanel pnl;
//    private Action openAction = new OpenAction();
//    private Action saveAction = new SaveAction();

    public PnlEditor() {
        this(null);
    }

    /**
     * Creates new form PnlEditor
     */
    public PnlEditor(String html) {
        initComponents();
        pnl = this;
        textComp.setEditorKit(new HTMLEditorKit());
        fixActions();
        createButtons();
        setHTML(html);
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        textComp.requestFocus();
    }

    protected void createButtons() {
        //myToolbar.add(openAction);
        //myToolbar.add(saveAction);
        myToolbar.setFloatable(false);
        myToolbar.add(textComp.getActionMap().get("font-bold"));
        myToolbar.add(textComp.getActionMap().get("font-italic"));
        myToolbar.add(textComp.getActionMap().get("font-underline"));
        myToolbar.addSeparator();
        myToolbar.add(textComp.getActionMap().get(HTMLEditorKit.cutAction));
        myToolbar.add(textComp.getActionMap().get(HTMLEditorKit.copyAction));
        myToolbar.add(textComp.getActionMap().get(HTMLEditorKit.pasteAction));
    }

    protected void fixActions() {


//        InputMap map = textComp.getInputMap();
//        int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
//        KeyStroke bold = KeyStroke.getKeyStroke(KeyEvent.VK_B, mask, false);
//        KeyStroke italic = KeyStroke.getKeyStroke(KeyEvent.VK_I, mask, false);
//        KeyStroke under = KeyStroke.getKeyStroke(KeyEvent.VK_U, mask, false);
//        map.put(bold, "font-bold");
//        map.put(italic, "font-italic");
//        map.put(under, "font-underline");

        Action a = null;


//        for (int i = 0; i < textComp.getActionMap().allKeys().length; i++) {
//            OPDE.debug(textComp.getActionMap().allKeys()[i]);
//        }

        //ActionMap actionMap = textComp.getActionMap().

        a = textComp.getActionMap().get(HTMLEditorKit.cutAction);
        a.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/artwork/16x16/cut.png")));
        //a.putValue(Action.NAME, "Cut");
        a.putValue(Action.SHORT_DESCRIPTION, "Ausschneiden");

        a = textComp.getActionMap().get(HTMLEditorKit.copyAction);
        a.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/artwork/16x16/copy.png")));
        //a.putValue(Action.NAME, "Copy");

        a = textComp.getActionMap().get(HTMLEditorKit.pasteAction);
        a.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/artwork/16x16/paste.png")));
        //a.putValue(Action.NAME, "Paste");

        a = textComp.getActionMap().get(HTMLEditorKit.selectAllAction);
        //a.putValue(Action.NAME, "Select All");


        a = textComp.getActionMap().get("font-bold");
        a.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/artwork/16x16/bold.png")));
        //a.putValue(Action.NAME, "Bold");

        a = textComp.getActionMap().get("font-italic");
        a.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/artwork/16x16/italic.png")));
        //a.putValue(Action.NAME, "Italic");

        a = textComp.getActionMap().get("font-underline");
        a.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/artwork/16x16/underline.png")));
        //a.putValue(Action.NAME, "Underline");
//
//        a = textComp.getActionMap().get(HTMLEditorKit.);
//        a.putValue(Action.NAME, "SansSerif");
//
//        a = textComp.getActionMap().get("font-family-Monospaced");
//        a.putValue(Action.NAME, "Monospaced");
//
//        a = textComp.getActionMap().get("font-family-Serif");
//        a.putValue(Action.NAME, "Serif");
//
//        a = textComp.getActionMap().get("font-size-10");
//        a.putValue(Action.NAME, "10");
//
//        a = textComp.getActionMap().get("font-size-12");
//        a.putValue(Action.NAME, "12");
//
//        a = textComp.getActionMap().get("font-size-16");
//        a.putValue(Action.NAME, "16");
//
//        a = textComp.getActionMap().get("font-size-24");
//        a.putValue(Action.NAME, "24");
    }


    public void setHTML(String html) {
        if (html == null) {
            html = "";
        }

        try {
            BufferedReader br = new BufferedReader(new java.io.StringReader(html));
            textComp.read(br, null);
        } catch (IOException e) {
            OPDE.fatal(e);
        }
    }

    public String getHTML() {
        StringWriter writer = new StringWriter();
        try {
            textComp.write(writer);
        } catch (IOException ex) {
            OPDE.error(ex);
        }
        return writer.toString();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        myToolbar = new JToolBar();
        jScrollPane1 = new JScrollPane();
        textComp = new JEditorPane();

        //======== this ========

        //======== myToolbar ========
        {
            myToolbar.setRollover(true);
        }

        //======== jScrollPane1 ========
        {
            jScrollPane1.setViewportView(textComp);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(myToolbar, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(myToolbar, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // ********** ACTION INNER CLASSES ********** //
    // A very simple exit action
    public class ExitAction extends AbstractAction {

        public ExitAction() {
            super("Exit");
        }

        public void actionPerformed(ActionEvent ev) {
            System.exit(0);
        }
    }

    // An action that opens an existing file
    class OpenAction extends AbstractAction {

        public OpenAction() {
            super("Open");
        }

        // Query user for a filename and attempt to open and read the file into the
        // text component.
        public void actionPerformed(ActionEvent ev) {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(pnl)
                    != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File file = chooser.getSelectedFile();
            if (file == null) {
                return;
            }

            FileReader reader = null;
            try {
                reader = new FileReader(file);
                textComp.read(reader, null);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(pnl,
                        "File Not Found", "ERROR", JOptionPane.ERROR_MESSAGE);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException x) {
                    }
                }
            }
        }
    }

    // An action that saves the document to a file
    class SaveAction extends AbstractAction {

        public SaveAction() {
            super("Save");
        }

        // Query user for a filename and attempt to open and write the text
        // componentâs content to the file.
        public void actionPerformed(ActionEvent ev) {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(pnl)
                    != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File file = chooser.getSelectedFile();
            if (file == null) {
                return;
            }

            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
                textComp.write(writer);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(pnl,
                        "File Not Saved", "ERROR", JOptionPane.ERROR_MESSAGE);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException x) {
                    }
                }
            }
        }
    }

    class SaveAsHtmlAction extends AbstractAction {

        public SaveAsHtmlAction() {
            super("Save As HTML...", null);
        }

        // Query user for a filename and attempt to open and write the text
        // component's content to the file.
        public void actionPerformed(ActionEvent ev) {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(pnl)
                    != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File file = chooser.getSelectedFile();
            if (file == null) {
                return;
            }
            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
                MinimalHTMLWriter htmlWriter = new MinimalHTMLWriter(writer,
                        (StyledDocument) textComp.getDocument());
                htmlWriter.write();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(pnl,
                        "HTML File Not Saved", "ERROR", JOptionPane.ERROR_MESSAGE);
            } catch (BadLocationException ex) {
                JOptionPane.showMessageDialog(pnl,
                        "HTML File Corrupt", "ERROR", JOptionPane.ERROR_MESSAGE);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException x) {
                    }
                }
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JToolBar myToolbar;
    private JScrollPane jScrollPane1;
    private JEditorPane textComp;
    // End of variables declaration//GEN-END:variables
}
