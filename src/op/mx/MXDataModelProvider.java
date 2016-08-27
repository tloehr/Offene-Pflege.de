package op.mx;

import entity.mx.MXmsg;

import java.util.ArrayList;

/**
 * Created by tloehr on 24.08.16.
 */
public abstract class MXDataModelProvider {
    private final ArrayList<MXmsg> model;
    private final String key;


    public MXDataModelProvider(String key) {
        this.model = new ArrayList<>();
        this.key = key;
        loadModel();
    }

    public void setModel(ArrayList<MXmsg> model) {
        this.model.clear();
        this.model.addAll(model);
    }

    public String getKey() {
        return key;
    }

    public ArrayList<MXmsg> getDataModel() {
        return model;
    }

    public abstract void loadModel();
}
