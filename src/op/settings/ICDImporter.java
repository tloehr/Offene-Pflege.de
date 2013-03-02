package op.settings;

import entity.info.ICD;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.persistence.EntityManager;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 01.03.13
 * Time: 17:24
 * To change this template use File | Settings | File Templates.
 */
public class ICDImporter extends DefaultHandler {

    private final EntityManager em;
    private String firstElement;

    public ICDImporter(EntityManager em) throws Exception {
        this.em = em;
        firstElement = null;
    }

    @Override
    public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
        if (firstElement == null) {
            firstElement = tagName;
            if (!firstElement.equalsIgnoreCase("opdeicd")) throw new SAXException("not my kind of document");
        }

        if (tagName.equalsIgnoreCase("icd")) {


            String code = attributes.getValue("code");
            String content = attributes.getValue("content");

            // only persist if needed. Check only otherwise.
            if (em != null) {
                em.merge(new ICD(code, content));
            }

        }
    }

}
