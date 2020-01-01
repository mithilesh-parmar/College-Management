package custom_view;

import com.google.cloud.storage.Blob;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;


public class ImageButton extends Button {

    private final String STYLE_NORMAL = "-fx-background-color: transparent; -fx-padding: 15, 15, 15, 15;";
    private final String STYLE_PRESSED = "-fx-background-color: transparent; -fx-padding: 16 14 14 16;";

    private ImageView imageView;
    private ImageButtonListener listener;

    private BooleanProperty imageLoading = new SimpleBooleanProperty(false);

    public ImageButton() {

        imageView = new ImageView();
        StringProperty path = new SimpleStringProperty("/assets/add-user.png");
        imageView.setImage(new Image(getClass().getResourceAsStream(path.get())));


        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.visibleProperty().bind(imageLoading);
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(imageView, progressIndicator);
        setGraphic(stackPane);

        setStyle(STYLE_NORMAL);

        setOnMousePressed(event -> setStyle(STYLE_PRESSED));

        setOnMouseReleased(event -> setStyle(STYLE_NORMAL));

        setOnMouseClicked(event -> showFileChooser());
    }

    public void setListener(ImageButtonListener listener) {
        this.listener = listener;
    }

    public void setImage(String imageURL) {

        if (imageURL.isEmpty() || imageURL == null) return;
        imageLoading.set(true);
        Image image = new Image(imageURL, true);

        image.progressProperty().addListener((observableValue, number, t1) -> {
            if (t1.doubleValue() == 1.0) imageLoading.set(false);
        });

        image.errorProperty().addListener((observableValue, aBoolean, t1) -> {
            imageLoading.set(false);
            System.out.println("error occured while loading image");
        });

        image.exceptionProperty().addListener((observableValue, e, t1) -> {
            System.out.println(t1);
        });

        imageView.setImage(new Image(imageURL, true));


    }


    private void showFileChooser() {
        File file;
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        file = fileChooser.showOpenDialog(getScene().getWindow());
        imageView.setImage(new Image(file.toURI().toString()));
        if (listener != null) listener.onImageSelected(file);

    }


}
