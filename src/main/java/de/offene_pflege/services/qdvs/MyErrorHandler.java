package de.offene_pflege.services.qdvs;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyErrorHandler implements ErrorHandler {
    private XMLStreamReader reader;
    public static final int RESULT_OK = 0;
    public static final int RESULT_WARN = 1;
    public static final int RESULT_ERROR = 2;
    public static final int RESULT_FATAL = 3;
    private int RESULT;
    private MultiKeyMap<MultiKey<Integer>,  ArrayList<String>> ERRORS;
//    private int errno = 0;
//    private Pattern p1 = Pattern.compile("\\((.*?)\\)");
    private Pattern p2 = Pattern.compile("(?<=if)(.*)(?=then)");


    public MyErrorHandler(XMLStreamReader reader) {
        this.reader = reader;
        RESULT = RESULT_OK;
        ERRORS = new MultiKeyMap();
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        RESULT = Math.max(RESULT, RESULT_ERROR);
        warning(e);
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        RESULT = Math.max(RESULT, RESULT_FATAL);
        warning(e);
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        RESULT = Math.max(RESULT, RESULT_WARN);
        // einmal das äußere rausschneiden
        String strippedMessage = "";
        Matcher m2 = p2.matcher(e.getMessage());
        while (m2.find()) {
            strippedMessage = m2.group(1);
        }

        strippedMessage = strippedMessage.trim();
        strippedMessage = strippedMessage.substring(1, strippedMessage.length() - 1); // Klammern weg

        ERRORS.putIfAbsent(new MultiKey(e.getLineNumber(), e.getColumnNumber()), new ArrayList<>());
        ERRORS.get(e.getLineNumber(), e.getColumnNumber()).add(strippedMessage);

    }

    public MultiKeyMap<MultiKey<Integer>,  ArrayList<String>> getERRORS() {
        return ERRORS;
    }
}