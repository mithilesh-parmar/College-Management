package custom_view.image_gallery_view;

import custom_view.card_view.ImageCard;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.util.Duration;
import utility.ScreenUtility;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static utility.ScreenUtility.*;


public class
Gallery extends BorderPane implements Initializable {

    private ListProperty<ImageCard> imageViews = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListView<ImageCard> listView = new ListView<>();
    private Button addButton;

    private BooleanProperty showAddButton = new SimpleBooleanProperty(false);

    public Gallery() {
        setPadding(new Insets(4));


        addButton = new Button("Add Image");
        setAlignment(addButton, Pos.CENTER);

        addButton.visibleProperty().bind(showAddButton);
        addButton.setDefaultButton(true);

        listView.setOrientation(Orientation.HORIZONTAL);
        listView.setPlaceholder(new ProgressIndicator());

        listView.setMaxWidth(getScreenHalfWidth());
        listView.setMinWidth(getScreenHalfWidth());
        listView.setMaxHeight(150);
        listView.itemsProperty().bind(imageViews);
        listView.setCache(true);
        addButton.setOnAction(actionEvent -> addImage());

        setMaxWidth(getScreenHalfWidth());
        setCenter(listView);
        setRight(addButton);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setShowAddButton(boolean value) {
        showAddButton.set(value);
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
        if (image != null) {
            imageViews.get().add(new ImageCard(new Image(image.toURI().toString()), true));

        }

    }

    public void setImageView(List<String> imageUrls) {
        if (imageUrls != null)
            imageUrls.forEach(url -> imageViews.get().add(new ImageCard(new Image(url, true), true)));

    }


    public List<String> getImageUrls() {
        List<String> urls = new ArrayList<>();

        imageViews.forEach(card -> urls.add(card.getUrl()));
        return urls;
    }


    public ObservableList<ImageCard> getImageViews() {
        return imageViews.get();
    }

    public ListProperty<ImageCard> imageViewsProperty() {
        return imageViews;
    }
}
