package custom_view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class ImageButton extends Button {
    private final String STYLE_NORMAL = "-fx-background-color: transparent; -fx-padding: 15, 15, 15, 15;";
    private final String STYLE_PRESSED = "-fx-background-color: transparent; -fx-padding: 16 14 14 16;";

    private StringProperty path = new SimpleStringProperty("/assets/add-user.png");
    private ImageView imageView;
    private ImageButtonListener listener;

    public ImageButton() {
        imageView = new ImageView();
        imageView.setImage(new Image(getClass().getResourceAsStream(path.get())));
        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);
        setGraphic(imageView);

        setStyle(STYLE_NORMAL);

        setOnMousePressed(event -> setStyle(STYLE_PRESSED));

        setOnMouseReleased(event -> setStyle(STYLE_NORMAL));

        setOnMouseClicked(event -> showFileChooser());
    }

    public void setListener(ImageButtonListener listener) {
        this.listener = listener;
    }

    private void showFileChooser() {
        File file;
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        FileChooser fileChooser = new FileChooser();
//        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        file = fileChooser.showOpenDialog(getScene().getWindow());


        imageView.setImage(new Image(file.toURI().toString()));
        if (listener != null) listener.onImageSelected(file);

    }

}
