package fx;

import entity.info.Resident;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import op.OPDE;

import javax.persistence.EntityManager;

/**
 * Created by tloehr on 16.09.15.
 */
public class RepCtrl extends JFXPanel {
    Resident resident;
    ReportsView view;

    public RepCtrl() {
        EntityManager em = OPDE.createEM();
        resident = em.find(Resident.class, "BW1");
        em.close();



        Platform.runLater(() -> {
            view = new ReportsView(resident);
            initFX();
        });
    }

    private void initFX() {

        setScene(view.createMainScene());
    }


}
