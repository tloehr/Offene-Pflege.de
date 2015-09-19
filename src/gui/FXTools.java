package gui;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

/**
 * Created by tloehr on 17.09.15.
 */
public class FXTools {


    /**
     * https://community.oracle.com/message/11145722#11145722
     *
     * @return
     */
    public static TitledPane createTitledPane(Pane root, String text, Image icon, Node content, Node... buttons) {

//        final VBox root = new VBox();
        final TitledPane titledPane = new TitledPane();
        titledPane.setText(text);
        final HBox buttonBox = new HBox(5);
        buttonBox.getChildren().addAll(buttons);

        final Label label = new Label();
        label.textProperty().bind(titledPane.textProperty());

        final AnchorPane title = new AnchorPane();
        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(buttonBox, 0.0);
        title.getChildren().addAll(label, buttonBox);
        titledPane.setGraphic(title);
        titledPane.setContent(content);
        titledPane.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        titledPane.setExpanded(false);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                final Region arrow = (Region) titledPane.lookup(".arrow-button");
                title.prefWidthProperty().bind(new DoubleBinding() {
                    {
                        super.bind(arrow.widthProperty(), root.widthProperty());
                    }

                    @Override
                    protected double computeValue() {
                        double breathingSpace = 20;
                        double value = root.getWidth() - arrow.getWidth() - breathingSpace;
                        return value;
                    }
                });

            }
        });

        return titledPane;
    }


}
