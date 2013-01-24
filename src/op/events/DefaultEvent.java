package op.events;

import java.util.EventObject;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 15.06.11
 * Time: 14:43
 * To change this template use File | Settings | File Templates.
 */
public class DefaultEvent extends EventObject {
    protected Properties props;

    public DefaultEvent(Object source) {
        super(source);
        props = new Properties();
    }

    public Properties getProps() {
        return props;
    }
}
