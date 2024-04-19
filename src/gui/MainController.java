package gui;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;

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
    @FXML
    private BarChart<String, Number> colorChart;
    private XYChart.Series<String, Number> colorXYDataRed;
    private XYChart.Series<String, Number> colorXYDataGreen;
    private XYChart.Series<String, Number> colorXYDataBlue;
    private XYChart.Series<String, Number> colorXYDataGray;
    private String fileName = "";
    @FXML
    private Button LoadImageBtn;
    @FXML
    private MFXToggleButton tglBtnShowCount;
    @FXML
    private MFXButton btnPrev;
    @FXML
    private MFXButton btnNext;
    @FXML
    private Button loadImageBtn;

    public void initialize() {


        initDynamicElements();
        initXYSeries();
        initColorChatListener();


    }

    private void initColorChatListener() {
        tglBtnShowCount.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (tglBtnShowCount.selectedProperty().getValue() == true){
                colorChart.setVisible(true);
                colorChart.setManaged(true);
            }else {
                colorChart.setVisible(false);
                colorChart.setManaged(false);
            }
        });
    }

    private void initDynamicElements() {
        imgBackground.fitWidthProperty().bind(centerStackPane.widthProperty());
        imgBackground.fitHeightProperty().bind(centerStackPane.heightProperty());
        BoxBlur blur = new BoxBlur(10, 10, 20); // Parameters: radius, iterations
        imgBackground.setEffect(blur);
        imgBackground.setOpacity(0.4);

        imgMainPicture.fitWidthProperty().bind(centerStackPane.widthProperty().multiply(0.9));
        imgMainPicture.fitHeightProperty().bind(centerStackPane.heightProperty().multiply(0.9));

        if (tglBtnShowCount.selectedProperty().getValue());
    }

    private void initXYSeries() {
        colorXYDataRed = new XYChart.Series<>();
        colorXYDataRed.setName("Red");
        colorChart.getData().add(colorXYDataRed);

        colorXYDataGreen = new XYChart.Series<>();
        colorXYDataGreen.setName("Green");
        colorChart.getData().add(colorXYDataGreen);

        colorXYDataBlue = new XYChart.Series<>();
        colorXYDataBlue.setName("Blue");
        colorChart.getData().add(colorXYDataBlue);

        colorXYDataGray = new XYChart.Series<>();
        colorXYDataGray.setName("Gray");
        colorChart.getData().add(colorXYDataGray);

        colorXYDataRed.getData().add(new XYChart.Data<>("Red", 0));
        colorXYDataGreen.getData().add(new XYChart.Data<>("Green", 0));
        colorXYDataBlue.getData().add(new XYChart.Data<>("Blue", 0));
        colorXYDataGray.getData().add(new XYChart.Data<>("Gray", 0));
    }


    public void updateChart(int redCount, int greenCount, int blueCount, int mixedCount) {
        Platform.runLater(() -> {
            colorXYDataRed.getData().get(0).setYValue(redCount);
            colorXYDataGreen.getData().get(0).setYValue(greenCount);
            colorXYDataBlue.getData().get(0).setYValue(blueCount);
            colorXYDataGray.getData().get(0).setYValue(mixedCount);

            //colorChart.setVisible(true);
            //colorChart.setManaged(true);
        });
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
        if (!loadedImages.isEmpty() && currentImageIndex >= 0) {
            Image currentImage = loadedImages.get(currentImageIndex);
            Platform.runLater(() -> {
                imgMainPicture.setImage(currentImage);
                imgBackground.setImage(currentImage);
                label.setText(extractFileNameFromImage(currentImage)); // Opdaterer label her
                showHiddenElements();
            });

            // Udfør farveanalyse udenfor Platform.runLater for at undgå UI-tråds blokering
            try {
                URI uri = new URI(currentImage.getUrl());
                File imageFile = new File(uri);
                analyzeColors(imageFile);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                System.out.println("Invalid URL for the image file.");
            }
        }
    }

    private void showHiddenElements() {
        label.setVisible(true);
        imgBackground.setVisible(true);
        imgMainPicture.setVisible(true);
        tglBtnShowCount.setVisible(true);
        btnPrev.setVisible(true);
        btnNext.setVisible(true);
        loadImageBtn.setVisible(false);
    }


    private void analyzeColors(File imageFile) {
        Task<AtomicIntegerArray> colorCountTask = createColorCount(imageFile);

        new Thread(colorCountTask).start();
        colorCountTask.setOnSucceeded(event -> {
            AtomicIntegerArray counts = colorCountTask.getValue();
            System.out.println("Updated counts: Red = " + counts.get(0) + ", Green = " + counts.get(1) + ", Blue = " + counts.get(2) + ", Mixed = " + counts.get(3));
            updateChart(counts.get(0), counts.get(1), counts.get(2), counts.get(3));
        });
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
            onDisplayNextImage();
        }, 1, 1, TimeUnit.SECONDS); // Change images every 1 second
    }



    private String extractFileNameFromImage(Image image) {
        try {
            URI uri = new URI(image.getUrl());
            File file = new File(uri);
            return file.getName();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return ""; // Returner en tom streng eller en fejlmeddelelse
        }
    }

    public void onStopSlideshow() {
        if (slideshowExecutor != null && !slideshowExecutor.isShutdown()) {
            slideshowExecutor.shutdownNow(); // Properly shut down the scheduler
            slideshowExecutor = null; // Help with garbage collection
        }
    }

    public Task<AtomicIntegerArray> createColorCount(File imageFile) {
        return new Task<>()  {
            @Override
            public AtomicIntegerArray call() throws IOException {
                BufferedImage img = ImageIO.read(imageFile);
                AtomicIntegerArray colorsToCount = new AtomicIntegerArray(4); // 0-red, 1-green, 2-blue, 3-mixed

                for (int y = 0; y < img.getHeight(); y++) {
                    for (int x = 0; x < img.getWidth(); x++) {
                        int pixel = img.getRGB(x, y);
                        Color color = new Color(pixel, true); // true indicates alpha is included

                        int red = color.getRed();
                        int green = color.getGreen();
                        int blue = color.getBlue();

                        // Determine which color is dominant
                        if (red == green && green == blue) {
                            colorsToCount.getAndIncrement(3); //gray scale
                        } else if (red > green && red > blue) {
                            colorsToCount.getAndIncrement(0); // Red
                        } else if (green > red && green > blue) {
                            colorsToCount.getAndIncrement(1); // Green
                        } else if (blue > red && blue > green) {
                            colorsToCount.getAndIncrement(2); // Blue
                        } else {
                            colorsToCount.getAndIncrement(3); // Mixed, where no single color dominates
                        }
                    }
                }

                return colorsToCount;
            }

        };
    }

    @FXML
    private void onLoadImageBtn(ActionEvent actionEvent) {
        onLoadImages();
        if (!loadedImages.isEmpty() && currentImageIndex >= 0){


        }
    }
}

