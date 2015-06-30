package gui.interfaces;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import java.awt.*;

/**
 * Taken from: http://www.java2s.com/Tutorials/Java/Swing/JTextField/Set_max_length_for_JTextField_in_Java.htm
 */
public class BoundedTextField extends JTextField implements BoundedPlainDocument.InsertErrorListener {
    public BoundedTextField() {
        this(null, 0, 0);
    }

    public BoundedTextField(String text, int columns, int maxLength) {
        super(null, text, columns);

        if (text != null && maxLength == 0) {
            maxLength = text.length();
        }
        BoundedPlainDocument plainDoc = (BoundedPlainDocument) getDocument();
        plainDoc.setMaxLength(maxLength);

        plainDoc.addInsertErrorListener(this);
    }

    public BoundedTextField(int columns, int maxLength) {
        this(null, columns, maxLength);
    }

    public BoundedTextField(String text, int maxLength) {
        this(text, 0, maxLength);
    }

    public void setMaxLength(int maxLength) {
        ((BoundedPlainDocument) getDocument()).setMaxLength(maxLength);
    }

    public int getMaxLength() {
        return ((BoundedPlainDocument) getDocument()).getMaxLength();
    }

    // Override to handle insertion error
    public void insertFailed(BoundedPlainDocument doc, int offset, String str,
                             AttributeSet a) {
        // By default, just beep
        Toolkit.getDefaultToolkit().beep();
    }

    // Method to create default model
    protected Document createDefaultModel() {
        return new BoundedPlainDocument();
    }
}