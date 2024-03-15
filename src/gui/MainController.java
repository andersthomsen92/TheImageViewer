package gui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController {

    @FXML
    private Label label;
    @FXML
    private ImageView imgBackground, imgMainPicture;
    @FXML
    private StackPane centerStackPane;

    public void initialize() {

        BoxBlur blur = new BoxBlur(10,10,20); // Parameters: radius, iterations
        imgBackground.setEffect(blur);
        imgBackground.setOpacity(0.4);

        imgBackground.fitWidthProperty().bind(centerStackPane.widthProperty());
        imgBackground.fitHeightProperty().bind(centerStackPane.heightProperty());
        imgBackground.setPreserveRatio(true);

        imgMainPicture.fitWidthProperty().bind(Bindings.multiply(imgBackground.fitWidthProperty(), 0.9));
        imgMainPicture.fitHeightProperty().bind(Bindings.multiply(imgBackground.fitHeightProperty(), 0.9));
        imgMainPicture.setPreserveRatio(true);
    }

}

