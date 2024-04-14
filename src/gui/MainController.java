package gui;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainController {

    @FXML
    private Label label;
    @FXML
    private ImageView imgBackground, imgMainPicture;
    @FXML
    private StackPane centerStackPane;
    private List<Image> loadedImages = new ArrayList<>();
    private int currentImageIndex = -1;
    private ScheduledExecutorService slideshowExecutor;
    private final AtomicBoolean slideshowRunning = new AtomicBoolean(false);


    public void initialize() {

        BoxBlur blur = new BoxBlur(10, 10, 20); // Parameters: radius, iterations
        imgBackground.setEffect(blur);
        imgBackground.setOpacity(0.4);

        imgBackground.fitWidthProperty().bind(centerStackPane.widthProperty());
        imgBackground.fitHeightProperty().bind(centerStackPane.heightProperty());
        imgBackground.setPreserveRatio(true);

        imgMainPicture.fitWidthProperty().bind(Bindings.multiply(imgBackground.fitWidthProperty(), 0.9));
        imgMainPicture.fitHeightProperty().bind(Bindings.multiply(imgBackground.fitHeightProperty(), 0.9));
        imgMainPicture.setPreserveRatio(true);

    }


    /**
     * Method to open multiple files into the program
     * It adds them to the loadedImages list. so we can go through it.
     */
    @FXML
    private void onLoadImages() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Images");

        // Set file filters to show only image files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(extFilter);

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            loadedImages.clear(); // Clear previous images
            for (File file : selectedFiles) {
                // Load image and add it to loadedImages list
                Image image = new Image(file.toURI().toString());
                loadedImages.add(image);
            }
        }
        // Reset currentImageIndex if no images loaded
        if (!loadedImages.isEmpty()) {
            currentImageIndex = 0;
            displayCurrentImage();
        }
    }


    // Method to display next image in loadedImages list
    @FXML
    public void onDisplayNextImage() {
        if (!loadedImages.isEmpty()) {
            // Increment currentImageIndex and cycle back to 0 if end is reached
            currentImageIndex = (currentImageIndex + 1) % loadedImages.size();
            // Display image in ImageView
            displayCurrentImage();
        }
    }

    @FXML
    private void onDisplayPreviousImage() {
        if (!loadedImages.isEmpty()) {
            // Increment currentImageIndex and cycle back to 0 if end is reached
            currentImageIndex = (currentImageIndex - 1) % loadedImages.size();
            // Display image in ImageView
            displayCurrentImage();
        }
    }

    public void displayCurrentImage() {
        // Display first image if images loaded
        if (!loadedImages.isEmpty() && currentImageIndex >= 0) {
            Image currentImage = loadedImages.get(currentImageIndex);
            String imagePath = currentImage.getUrl();
            String fileName = new File(imagePath).getName();

            Platform.runLater(() -> {
                imgMainPicture.setImage(currentImage);
                imgBackground.setImage(currentImage);
                label.setText(fileName);
            });
        }
    }

    public void onStartSlideshow() {
        if (loadedImages.isEmpty()) {
            System.out.println("(!) No images loaded, please load images to start the slideshow");
            return;
        }
        if (slideshowExecutor != null && !slideshowExecutor.isShutdown()) {
            slideshowExecutor.shutdownNow(); // Ensure previous slideshow is stopped
        }

        slideshowExecutor = Executors.newSingleThreadScheduledExecutor();
        slideshowExecutor.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                currentImageIndex = (currentImageIndex + 1) % loadedImages.size();
                Image currentImage = loadedImages.get(currentImageIndex);
                imgMainPicture.setImage(currentImage);
                imgBackground.setImage(currentImage);
                // Update the label with the filename of the currently displayed image
                String fileName = extractFileNameFromImage(currentImage);
                label.setText(fileName); // Assuming you have a method to extract the file name
            });
        }, 1, 1, TimeUnit.SECONDS); // Change images every 1 second
    }

    private String extractFileNameFromImage(Image image) {
        String path = image.getUrl();
        return path.substring(path.lastIndexOf('/') + 1); // Extract the file name
    }

    public void onStopSlideshow() {
        if (slideshowExecutor != null && !slideshowExecutor.isShutdown()) {
            slideshowExecutor.shutdownNow(); // Properly shut down the scheduler
            slideshowExecutor = null; // Help with garbage collection
        }
    }
}

