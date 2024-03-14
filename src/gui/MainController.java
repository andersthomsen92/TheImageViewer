package gui;

import javafx.fxml.FXML;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController {

    @FXML
    private ImageView imgBackground;
    @FXML
    private ImageView imgMainPicture;


    public void initialize() {

        BoxBlur blur = new BoxBlur(10,10,20); // Parameters: radius, iterations
        imgBackground.setEffect(blur);
        imgBackground.setOpacity(0.4);
    }

}

