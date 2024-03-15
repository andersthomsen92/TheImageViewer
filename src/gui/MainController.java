package gui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

    private List<Image> loadedImages = new ArrayList<>();

    // Index to keep track of the currently displayed image
    private int currentImageIndex = 0;

    /**
     * Method to open multiple files into the program
     * It adds them to the loadedImages list. so we can go through it.
     */
    private void openFileExplorerAndLoadImages() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Images");

        // Set file filters to show only image files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(extFilter);

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if (selectedFiles != null) {
            loadedImages.clear(); // Clear previous images
            for (File file : selectedFiles) {
                try {
                    // Load image and add it to loadedImages list
                    Image image = new Image(file.toURI().toString());
                    loadedImages.add(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Reset currentImageIndex if no images loaded
            if (loadedImages.isEmpty()) {
                currentImageIndex = 0;
            }
        }
    }

    // Method to display next image in loadedImages list
    @FXML
    private void onDisplayNextImage() {
        if (!loadedImages.isEmpty()) {
            // Increment currentImageIndex and cycle back to 0 if end is reached
            currentImageIndex = (currentImageIndex + 1) % loadedImages.size();
            // Display image in ImageView
            imgMainPicture.setImage(loadedImages.get(currentImageIndex));
        } else {
            // If no images loaded, display placeholder or handle accordingly
            imgMainPicture.setImage(null);
        }
    }

    @FXML
    private void onDisplayPreviousImage(){
        if (!loadedImages.isEmpty()) {
            // Increment currentImageIndex and cycle back to 0 if end is reached
            currentImageIndex = (currentImageIndex - 1) % loadedImages.size();
            // Display image in ImageView
            imgMainPicture.setImage(loadedImages.get(currentImageIndex));
        } else {
            // If no images loaded, display placeholder or handle accordingly
            imgMainPicture.setImage(null);
        }
    }
    public void onLoadImages() {
        openFileExplorerAndLoadImages();
        // Display first image if images loaded
        if (!loadedImages.isEmpty()) {
            imgMainPicture.setImage(loadedImages.get(0));
        }
    }

}

