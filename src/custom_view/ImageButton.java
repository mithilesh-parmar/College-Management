package custom_view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;

public class ImageButton extends Button {
    private final String STYLE_NORMAL = "-fx-background-color: transparent; -fx-padding: 15, 15, 15, 15;";
    private final String STYLE_PRESSED = "-fx-background-color: transparent; -fx-padding: 16 14 14 16;";

    private StringProperty path = new SimpleStringProperty("/assets/cancel.png");
    private ImageView imageView;
    private ChooseImageListener listener;

    public ImageButton() {
        imageView = new ImageView();
        imageView.setImage(new Image(getClass().getResourceAsStream(path.get())));
        setGraphic(imageView);

        setStyle(STYLE_NORMAL);

        setOnMousePressed(event -> setStyle(STYLE_PRESSED));

        setOnMouseReleased(event -> setStyle(STYLE_NORMAL));

        setOnMouseClicked(event -> showFileChooser());
    }


    private void showFileChooser() {
        File file;
        FileChooser fileChooser = new FileChooser();
        file = fileChooser.showOpenDialog(getScene().getWindow());


        imageView.setImage(new Image(file.toURI().toString()));
        if (listener != null) listener.onImageSelected(file);

    }

}
