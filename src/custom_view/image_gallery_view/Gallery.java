package custom_view.image_gallery_view;

import custom_view.ImageButton;
import custom_view.card_view.ImageCard;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class Gallery extends BorderPane implements Initializable {

    private ListProperty<ImageCard> imageViews = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListView<ImageCard> imageListView;
    private Button addButton;

    public Gallery() {
        System.out.println("Initializing");
        addButton = new Button("Add");
        imageListView = new ListView<>();
        imageListView.setOrientation(Orientation.HORIZONTAL);
        imageListView.itemsProperty().bind(imageViews);
        addButton.setOnAction(actionEvent -> addImage());


        setHeight(400);
        setPrefHeight(300);
        setMinWidth(100);

        setBorder(createBorder(Color.BLANCHEDALMOND, BorderStrokeStyle.SOLID, 8, 3));
        setPadding(new Insets(8));
        setCenter(imageListView);
        setRight(addButton);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    private Border createBorder(Color color, BorderStrokeStyle borderStrokeStyle, double radius, double width) {
        return new Border(new BorderStroke(color, borderStrokeStyle, new CornerRadii(radius), new BorderWidths(width)));
    }

    private File showFileChooser(String title) {
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        return fileChooser.showOpenDialog(getScene().getWindow());
    }

    private void addImage() {
        File image = showFileChooser("Choose Image");
        if (image != null)
            imageViews.get().add(new ImageCard(new Image(image.toURI().getPath())));
    }

    public void setImageView(List<String> imageUrls) {
        imageUrls.forEach(url -> {
            imageViews.get().add(new ImageCard(new Image(url, true)));
        });
    }
}
