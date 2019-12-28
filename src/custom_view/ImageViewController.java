package custom_view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.Key;
import java.util.ResourceBundle;

public class ImageViewController extends VBox {

    public ListView<Image> imageListView;
    public Button chooseImageButton;
    private ObservableList<Image> images = FXCollections.observableArrayList();
    private ContextMenu menu = new ContextMenu();

    public ImageViewController() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/custom_view/image_view.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);


        try {
            fxmlLoader.load();
            chooseImageButton.setOnAction(this::chooseImageButtonClicked);
            imageListView.setStyle("-fx-background-color: transparent;");
            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(actionEvent -> images.remove(imageListView.getSelectionModel().getSelectedItem()));

            imageListView.setCellFactory(param -> new ListCell<>() {
                ImageView imageView = new ImageView();

                @Override
                protected void updateItem(Image item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                        setText("");
                    } else {
                        setStyle("-fx-background-color: transparent;");

                        imageView.setFitWidth(80);
                        imageView.setFitHeight(80);
                        imageView.setImage(item);
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);
                        imageView.setOnContextMenuRequested(event -> menu.show(this, event.getScreenX(), event.getScreenY()));
                        setGraphic(imageView);

                    }
                }
            });
            imageListView.setItems(images);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }


    public void chooseImageButtonClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        fileChooser.setTitle("Choose Event Image");
        if (file != null) {
            images.add(new Image(file.toURI().toString()));
        }
    }
}
