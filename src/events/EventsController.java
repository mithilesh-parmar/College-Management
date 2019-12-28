package events;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import listeners.DataChangeListener;
import model.Event;
import utility.EventFirestoreUtility;

import java.io.File;
import java.net.URL;
import java.util.*;

public class EventsController implements Initializable, DataChangeListener {

    public ListView<Event> eventsList;

    public Label eventTitle;
    public Label eventDescription;

    public ImageView eventImage;

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
    public GridPane eventAddPane;
    public VBox eventDetailPane;
    public Button editButton;
    public TextField searchTextField;


    private EventFirestoreUtility firestoreUtility = EventFirestoreUtility.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        setfieldsvisiblity(false);

        firestoreUtility.setListener(this);
        firestoreUtility.getEvents();

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> performSearch(oldValue, newValue));

        eventImage.setFitHeight(150);
        eventImage.setFitWidth(150);

        eventsList.getSelectionModel().selectedItemProperty().addListener((observableValue, a, t1) -> {
            detailProgressIndicator.setVisible(true);
            setfieldsvisiblity(true);
            fillDetailsForEvent(observableValue.getValue());

        });

        editButton.setOnAction(actionEvent -> setFieldsData(eventsList.getSelectionModel().getSelectedItem()));

//        addButton.setOnAction(actionEvent -> {
//
//            Event event = new Event(
//                    titleTextField.getText().toString(),
//                    descriptionTextField.getText().toString(),
//                    eventTimeTextField.getText().toString(),
//                    Timestamp.now(),
//                    Timestamp.now(),
//                    List.of()
//            );
//
//
//            Platform.runLater(() -> {
//                ApiFuture<DocumentReference> addedDocRef = FirestoreClient.getFirestore()
//                        .collection("colleges")
//                        .document("GKevzOMWBqbFtPezdLzF")
//                        .collection("events")
//                        .add(event.toJSON());
//                System.out.println("Done");
//            });
//
//
//        });
    }


    @Override
    public void onDataLoaded(ObservableList data) {

        mainProgressIndicator.setVisible(false);
        detailProgressIndicator.setVisible(false);
        eventsList.setItems(firestoreUtility.events);
        eventsList.getSelectionModel().selectFirst();
    }

    @Override
    public void onDataChange(QuerySnapshot data) {

        mainProgressIndicator.setVisible(true);
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
            eventImage.setImage(new Image(event.getImages().get(0), true));
        } else {
            eventImage.setImage(new Image("/assets/cancel.png"));
        }

        detailProgressIndicator.setVisible(false);
    }


    @Override
    public void onError(Exception e) {

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

    public void chooseImage(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            // openFile(file);


            Image image1 = new Image(file.toURI().toString());

            ImageView ip = new ImageView(image1);
            BackgroundImage backgroundImage = new BackgroundImage(image1, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, null);
        }
    }
}

