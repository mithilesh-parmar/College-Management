package custom_view.image_gallery_view;

import custom_view.card_view.ImageCard;
import custom_view.card_view.ImageCardListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import static utility.ScreenUtility.*;


public class Gallery extends BorderPane implements Initializable {

    public interface GalleryListener {
        void onDeleteImage(String url);

    }

    private ListProperty<ImageCard> imageViews = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListView<ImageCard> listView = new ListView<>();


    private GalleryListener listener;

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

        listView.setMaxWidth(getScreenThreeFourthWidth());
        listView.setMinWidth(getScreenThreeFourthWidth());
        listView.setMaxHeight(150);
        listView.itemsProperty().bind(imageViews);
        listView.setCache(true);

        addButton.setPadding(new Insets(4));
        setMargin(addButton, new Insets(8));
        addButton.setOnAction(actionEvent -> addImage());

        setMaxWidth(getScreenThreeFourthWidth());
        setCenter(listView);
        setBottom(addButton);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setShowAddButton(boolean value) {
        showAddButton.set(value);
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
        File file = showFileChooser("Choose Image");
//        System.out.println(file);
        if (file != null) {
            ImageCard imageCard = new ImageCard(file, true);
            imageCard.setListener(new ImageCardListener() {

                @Override
                public void onDeleteAction(ImageCard card, String url) {
                    imageViews.remove(card);
                }

                @Override
                public void onClickAction(ImageCard card) {

                }
            });

            imageViews.get().add(imageCard);

        }

    }

    public void setImageView(List<String> imageUrls) {
        System.out.println("Setting image urls " + imageUrls);
        if (imageUrls != null) {

            imageUrls.forEach(url -> {
                ImageCard imageCard = new ImageCard(url, true);
                imageCard.setUserData(url + UUID.randomUUID());
                imageCard.setListener(new ImageCardListener() {
                    @Override
                    public void onDeleteAction(ImageCard card, String url) {
                        imageViews.remove(card);
                        if (listener != null)
                            listener.onDeleteImage(url);
                    }

                    @Override
                    public void onClickAction(ImageCard card) {

                    }
                });
                imageViews.get().add(imageCard);
            });
        }


    }

    public void setListener(GalleryListener listener) {
        this.listener = listener;
    }

    public List<String> getImageUrls() {
        List<String> urls = new ArrayList<>();
        imageViews.forEach(card -> urls.add(card.getUrl()));
        return urls;
    }

    public ListProperty<ImageCard> imageViewsProperty() {
        return imageViews;
    }

}
