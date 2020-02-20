package events;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import custom_view.ImageCarousel;
import custom_view.card_view.ImageCard;
import custom_view.image_gallery_view.Gallery;
import events.add_event.AddEventController;
import events.add_event.AddEventListener;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import model.Event;
import model.Student;
import students.detail_view.StudentDetailsController;
import utility.EventFirestoreUtility;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class EventsController implements Initializable, DataChangeListener {

    public ListView<Event> eventsList;

    public Label eventTitle;
    public Label eventDescription;

//    public ImageView eventImage;

    public ProgressIndicator mainProgressIndicator;
    public ProgressIndicator detailProgressIndicator;

    public Label eventTime;
    public Label createdAt;
    public Label eventDate;


    public TextField titleTextField;
    public TextField descriptionTextField;
    public TextField eventTimeTextField;
    public TextField createdAtTextField;
    public TextField eventDateTextField;
    public Button addButton;
    public VBox eventDetailPane;
    public Button editButton;
    public TextField searchTextField;
    public Gallery galleryView;
    public ListView testList;
    public HBox imageBox;


    private BooleanProperty loadingData = new SimpleBooleanProperty(true);
    private EventFirestoreUtility firestoreUtility = EventFirestoreUtility.getInstance();
    private ListProperty<ImageCard> images = new SimpleListProperty<>(FXCollections.observableArrayList());

    private ListProperty<String> imagesTest = new SimpleListProperty<>(FXCollections.observableArrayList());


    private ObjectProperty<Event> selectedEvent = new SimpleObjectProperty<>();


    public static class GalleryItem extends ListCell<String> {
        private final Button button = new Button();

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                ImageView imageView = new ImageView(new Image(item, false));
                imageView.setFitWidth(20);
                imageView.setFitHeight(20);
                imageView.setCache(false);
                button.setGraphic(imageView);
                setGraphic(button);

            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        galleryView.imageViewsProperty().bind(images);

        galleryView.setShowAddButton(false);

//        testList.itemsProperty().bind(imagesTest);
//        testList.setCellFactory(listView -> new GalleryItem());

        setfieldsvisiblity(false);

        firestoreUtility.setListener(this);
        firestoreUtility.getEvents();

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> performSearch(oldValue, newValue));

        mainProgressIndicator.visibleProperty().bind(loadingData);

        eventsList.getSelectionModel().selectedItemProperty().addListener((observableValue, a, t1) -> selectedEvent.set(t1));

        selectedEvent.addListener((observableValue, event, t1) -> {
            if (t1 == null) return;
            detailProgressIndicator.setVisible(true);
            setfieldsvisiblity(true);
            fillDetailsForEvent(t1);
        });

        editButton.setOnAction(actionEvent -> loadAddView(eventsList.getSelectionModel().getSelectedItem()));


        addButton.setPadding(new Insets(15));
        addButton.setOnAction(actionEvent -> loadAddView(null));

    }

    private void loadAddView(@Nullable Event event) {


        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/events/add_event/AddEventView.fxml"));
        final Stage stage = new Stage();
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Event Details");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);

            AddEventController controller = loader.getController();
            if (event != null) controller.setEvent(event);
            controller.setListener(new AddEventListener() {
                @Override
                public void onEventAdded(Event event) {
                    System.out.println("Adding new event");
                    close(stage);
                    loadingData.set(true);
                    firestoreUtility.addEvent(event);
                }

                @Override
                public void onEventUpdated(Event updatedEvent) {
                    close(stage);
                    loadingData.set(true);
                    firestoreUtility.updateEvent(updatedEvent);
                }
            });

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDataLoaded(ObservableList data) {
        loadingData.set(false);
        detailProgressIndicator.setVisible(false);
        eventsList.setItems(firestoreUtility.events);
        eventsList.getSelectionModel().selectFirst();
    }

    @Override
    public void onDataChange(QuerySnapshot data) {
        loadingData.set(true);
    }

    private void setFieldsData(Event event) {
        titleTextField.setText(event.getTitle());
        descriptionTextField.setText(event.getDescription());
        eventTimeTextField.setText(event.getTime());
        createdAtTextField.setText(event.getCreatedAt().toString());
        eventDateTextField.setText(event.getEventDate().toString());
    }

    private void setfieldsvisiblity(boolean value) {
        eventDetailPane.setVisible(value);
    }

    private void fillDetailsForEvent(Event event) {

        eventTitle.setText(event.getTitle());
        eventDescription.setText(event.getDescription());
        eventTime.setText(event.getTime());
        createdAt.setText(event.getCreatedAt().toString());
        eventDate.setText(event.getEventDate().toString());
        if (event.getImages() != null && event.getImages().size() > 0) {
            images.clear();
            imagesTest.clear();


            event.getImages().forEach(url -> {
                        images.get().add(new ImageCard(url, false));
                        System.out.println(url);
//                        imagesTest.get().add(url);
//                        imageBox.getChildren().add(createImageView(url));
                    }
            );


            System.out.println(imagesTest);
        } else {
            imagesTest.clear();
            images.clear();
            images.get().add(new ImageCard(new Image("/assets/add.png", true), false));
        }

        detailProgressIndicator.setVisible(false);
    }

    private ImageView createImageView(final File imageFile) {
        // DEFAULT_THUMBNAIL_WIDTH is a constant you need to define
        // The last two arguments are: preserveRatio, and use smooth (slower)
        // resizing

        ImageView imageView = null;
        try {
            final Image image = new Image(new FileInputStream(imageFile), 150, 0, true,
                    true);
            imageView = new ImageView(image);
            imageView.setFitWidth(150);
//            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
//
//                @Override
//                public void handle(MouseEvent mouseEvent) {
//
//                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
//
//                        if (mouseEvent.getClickCount() == 2) {
//                            try {
//                                BorderPane borderPane = new BorderPane();
//                                ImageView imageView = new ImageView();
//                                Image image = new Image(new FileInputStream(imageFile));
//                                imageView.setImage(image);
//                                imageView.setStyle("-fx-background-color: BLACK");
////                                imageView.setFitHeight(stage.getHeight() - 10);
//                                imageView.setPreserveRatio(true);
//                                imageView.setSmooth(true);
//                                imageView.setCache(true);
//                                borderPane.setCenter(imageView);
//                                borderPane.setStyle("-fx-background-color: BLACK");
//                                Stage newStage = new Stage();
////                                newStage.setWidth(stage.getWidth());
////                                newStage.setHeight(stage.getHeight());
//                                newStage.setTitle(imageFile.getName());
//                                Scene scene = new Scene(borderPane, Color.BLACK);
//                                newStage.setScene(scene);
//                                newStage.show();
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }
//                }
//            });
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return imageView;
    }

    private ImageView createImageView(final String imageUrl) {
        // DEFAULT_THUMBNAIL_WIDTH is a constant you need to define
        // The last two arguments are: preserveRatio, and use smooth (slower)
        // resizing

        ImageView imageView = null;

        final Image image = new Image(imageUrl, 150, 0, true,
                true);
        imageView = new ImageView(image);
        imageView.setUserData(imageUrl);
        imageView.setFitWidth(150);
//            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
//
//                @Override
//                public void handle(MouseEvent mouseEvent) {
//
//                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
//
//                        if (mouseEvent.getClickCount() == 2) {
//                            try {
//                                BorderPane borderPane = new BorderPane();
//                                ImageView imageView = new ImageView();
//                                Image image = new Image(new FileInputStream(imageFile));
//                                imageView.setImage(image);
//                                imageView.setStyle("-fx-background-color: BLACK");
////                                imageView.setFitHeight(stage.getHeight() - 10);
//                                imageView.setPreserveRatio(true);
//                                imageView.setSmooth(true);
//                                imageView.setCache(true);
//                                borderPane.setCenter(imageView);
//                                borderPane.setStyle("-fx-background-color: BLACK");
//                                Stage newStage = new Stage();
////                                newStage.setWidth(stage.getWidth());
////                                newStage.setHeight(stage.getHeight());
//                                newStage.setTitle(imageFile.getName());
//                                Scene scene = new Scene(borderPane, Color.BLACK);
//                                newStage.setScene(scene);
//                                newStage.show();
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }
//                }
//            });
        return imageView;
    }


    @Override
    public void onError(Exception e) {
        loadingData.set(false);
        detailProgressIndicator.setVisible(false);
        e.printStackTrace();
    }

    public void onSearchTextEntered(KeyEvent keyEvent) {
        if (!eventsList.isFocused()
                && keyEvent.getCode() == KeyCode.ENTER)
            eventsList.requestFocus();
    }


    /**
     * called for filtering the observable list to show only those  that
     * matches the search text criteria
     *
     * @param oldValue
     * @param newValue
     */
    private void performSearch(String oldValue, String newValue) {
        if (loadingData.get()) return;
        // if pressing backspace then set initial values to list
        if (oldValue != null && (newValue.length() < oldValue.length())) {
            eventsList.setItems(firestoreUtility.events);
        }

        // convert the searched text to uppercase
        String searchtext = newValue.toUpperCase();

        ObservableList<Event> subList = FXCollections.observableArrayList();
        for (Event p : eventsList.getItems()) {
            String text = p.getTitle().toUpperCase() + " " + p.getDescription().toUpperCase();
            // if the search text contains the manufacturer then add it to sublist
            if (text.contains(searchtext)) {
                subList.add(p);
            }

        }
        // set the items to listview that matches
        eventsList.setItems(subList);
    }


}

