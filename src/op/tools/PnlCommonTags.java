/*
 * Created by JFormDesigner on Fri May 30 15:30:55 CEST 2014
 */

package op.tools;

import com.jidesoft.swing.AutoCompletion;
import entity.system.Commontags;
import entity.system.CommontagsTools;
import op.OPDE;
import op.threads.DisplayMessage;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Torsten Löhr
 */
public class PnlCommonTags extends JPanel {

    HashMap<String, Commontags> mapAllTags = new HashMap<>();
    HashSet<Commontags> listSelectedTags;
    ArrayList<String> completionList;
    JTextField txtTags;


    public PnlCommonTags(HashSet<Commontags> listSelectedTags) {
        initComponents();

        this.listSelectedTags = listSelectedTags;
        this.completionList = new ArrayList<>();

        initPanel();
    }

    public HashSet<Commontags> getListSelectedTags() {
        return listSelectedTags;
    }

    private void initPanel() {

        txtTags = new JTextField(30);
        txtTags.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTagsActionPerformed(e);
            }
        });
        add(txtTags);

        for (Commontags commontags : CommontagsTools.getAllActive()) {
            mapAllTags.put(commontags.getText(), commontags);
        }

        for (Commontags selectedTags : listSelectedTags) {
            add(createButton(selectedTags));
        }

        ((AbstractDocument) txtTags.getDocument()).setDocumentFilter(new MyDocumentFilter());

        completionList = new ArrayList(mapAllTags.keySet());

        AutoCompletion autoCompletion = new AutoCompletion(txtTags, completionList);
        autoCompletion.setStrict(false);


    }

    private void txtTagsActionPerformed(ActionEvent e) {

        if (txtTags.getText().length() > 100) return;


        if (!mapAllTags.containsKey(SYSTools.tidy(txtTags.getText()))) {
            Commontags commontags = new Commontags(SYSTools.tidy(txtTags.getText()));
            mapAllTags.put(txtTags.getText(), commontags);
        }

        final boolean wasInItAlready = listSelectedTags.contains(mapAllTags.get(txtTags.getText()));

        listSelectedTags.add(mapAllTags.get(txtTags.getText()));

        if (!wasInItAlready) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    add(createButton(mapAllTags.get(txtTags.getText())));
                    revalidate();
                    repaint();
                }
            });
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========
        setLayout(new FlowLayout(FlowLayout.LEADING, 10, 5));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private JButton createButton(final Commontags commontags) {


        final JButton jButton = new JButton(commontags.getText(), SYSConst.icon16delete);
//        jButton.setContentAreaFilled(false);
//        jButton.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED, SYSConst.red2[SYSConst.light3], SYSConst.red2[SYSConst.medium2]));
//        jButton.setBorderPainted(false);
        jButton.setHorizontalTextPosition(SwingConstants.LEADING);
//        final Color defaultBackground = jButton.getBackground();

//        jButton.addMouseMotionListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                super.mouseEntered(e);
//                SwingUtilities.invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        jButton.setBackground(defaultBackground.brighter());
//                        jButton.repaint();
//                    }
//                });
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//                super.mouseExited(e);
//                SwingUtilities.invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        jButton.setBackground(defaultBackground);
//                        jButton.repaint();
//                    }
//                });
//            }
//        });

        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listSelectedTags.remove(commontags);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        remove(jButton);
                        revalidate();
                        repaint();
                    }
                });
            }
        });

        return jButton;
    }


    // http://stackoverflow.com/questions/14058505/jtextfield-accept-only-alphabet-and-white-space
    class MyDocumentFilter extends DocumentFilter {

        @Override
        public void replace(FilterBypass fb, int i, int i1, String string, AttributeSet as) throws BadLocationException {
            // an inserted string may be more than a single character i.e a copy and paste of 'aaa123d', also we iterate from the back as super.XX implementation will put last insterted string
            // first and so on thus 'aa123d' would be 'daa', but because we iterate from the back its 'aad' like we want
            for (int n = string.length(); n > 0; n--) {
                char c = string.charAt(n - 1);//get a single character of the string
                System.out.println(c);
                if (Character.isAlphabetic(c) || Character.isDigit(c)) {//if its an alphabetic character or white space
                    super.replace(fb, i, i1, String.valueOf(c), as);//allow update to take place for the given character
                } else {//it was not an alphabetic character or white space
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("not allowed"));
                }
            }
        }

        @Override
        public void remove(FilterBypass fb, int i, int i1) throws BadLocationException {
            super.remove(fb, i, i1);
        }

        @Override
        public void insertString(FilterBypass fb, int i, String string, AttributeSet as) throws BadLocationException {
            super.insertString(fb, i, string, as);

        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
