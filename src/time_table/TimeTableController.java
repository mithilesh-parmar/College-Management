package time_table;

import com.google.cloud.firestore.QuerySnapshot;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import listeners.DataChangeListener;
import model.Lecture;
import model.Section;
import utility.SectionsFirestoreUtility;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TimeTableController implements Initializable, DataChangeListener {


    public TableView<Lecture> timeTableForSection;
    public ListView<Section> sectionsListView;

    public ToggleButton mondayToggleButton;
    public ToggleButton tuesdayToggleButton;
    public ToggleButton wednesdayToggleButton;
    public ToggleButton thursdayToggleButton;
    public ToggleButton saturdayToggleButton;
    public ToggleButton fridayToggleButton;

    public BorderPane mainView;
    public ProgressIndicator progressIndicator;
    public TextField searchTextField;
    public Button addLectureButton;

    private ToggleGroup dayChooser;
    private BooleanProperty loadingData = new SimpleBooleanProperty(true);
    private SectionsFirestoreUtility firestoreUtility = SectionsFirestoreUtility.getInstance();

    private Section previouslySelectedSection;
    private Toggle previouslySelectedDayOfWeek;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setupToggleButtons();
        mainView.getCenter().visibleProperty().bind(loadingData.not());
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> performSearch(oldValue, newValue));
        progressIndicator.visibleProperty().bind(loadingData);

        dayChooser.selectedToggleProperty().addListener(this::changedDayForSection);

        firestoreUtility.setListener(this);
        firestoreUtility.getSections();


        sectionsListView.getSelectionModel().selectedItemProperty().addListener(this::changedSelectedSection);
        sectionsListView.getSelectionModel().selectFirst();


        addLectureButton.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {


                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddLectureView.fxml"));
                Parent parent = null;
                try {
                    parent = fxmlLoader.load();
                    AddLectureController dialogController = fxmlLoader.getController();
                    Dialog<Boolean> dialog = new Dialog<>();
                    dialog.setGraphic(parent);

                    dialog.initModality(Modality.APPLICATION_MODAL);
                    dialog.initOwner(sectionsListView.getScene().getWindow());


                    dialogController.button.setOnAction(actionEvent1 -> onAddButtonClick(dialogController, dialog));

                    dialog.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


    }

//    TODO after adding a lecture select previous section and dayofweek

    @Override
    public void onDataLoaded(ObservableList data) {
        loadingData.set(false);
        sectionsListView.setItems(firestoreUtility.sections);

        if (previouslySelectedSection != null) {
            System.out.println("Selecting : " + previouslySelectedSection);
            sectionsListView.getSelectionModel().select(previouslySelectedSection);
            dayChooser.selectToggle(previouslySelectedDayOfWeek);
            sectionsListView.getFocusModel().focusPrevious();
//            sectionsListView.getFocusModel().focus(sectionsListView.getItems().indexOf(previouslySelectedSection));

        }

    }

    @Override
    public void onDataChange(QuerySnapshot data) {
        System.out.println(getClass() + " onDataChange: selected section value: " + previouslySelectedSection);
        previouslySelectedSection = sectionsListView.getSelectionModel().getSelectedItem();
        previouslySelectedDayOfWeek = dayChooser.getSelectedToggle();
        System.out.println(getClass() + " onDataChange: Setting selected section to: " + previouslySelectedSection);
        loadingData.set(true);
    }

    private void changedDayForSection(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle t1) {
        if (sectionsListView.getSelectionModel().getSelectedItem().getClassSchedules() != null)
            timeTableForSection.setItems(
                    sectionsListView
                            .getSelectionModel()
                            .getSelectedItem()
                            .getClassSchedules()
                            .get(observableValue.getValue().getUserData().toString())
            );
        else timeTableForSection.setItems(FXCollections.observableArrayList());
    }

    private void onAddButtonClick(AddLectureController dialogController, Dialog dialog) {
        String subjectName = dialogController.subjectTextField.getText();
        String startTime = dialogController.startTimeTextField.getText();
        String endTime = dialogController.endTimeTextField.getText();

        Lecture lecture = new Lecture(subjectName, endTime, startTime);
        firestoreUtility.addLecture(
                lecture,
                dayChooser.getSelectedToggle().getUserData().toString(),
                sectionsListView.getSelectionModel().getSelectedItem()
        );


        dialog.setResult(Boolean.TRUE);
        dialog.close();

    }


    private void setupToggleButtons() {
        mondayToggleButton.setUserData("1");
        tuesdayToggleButton.setUserData("2");
        wednesdayToggleButton.setUserData("3");
        thursdayToggleButton.setUserData("4");
        fridayToggleButton.setUserData("5");
        saturdayToggleButton.setUserData("6");

        dayChooser = new ToggleGroup();
        dayChooser.getToggles().addAll(
                mondayToggleButton,
                tuesdayToggleButton,
                wednesdayToggleButton,
                thursdayToggleButton,
                fridayToggleButton,
                saturdayToggleButton
        );
    }

    @Override
    public void onError(Exception e) {

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
            sectionsListView.setItems(firestoreUtility.sections);
        }

        // convert the searched text to uppercase
        String searchtext = newValue.toUpperCase();

        ObservableList<Section> subList = FXCollections.observableArrayList();
        for (Section p : sectionsListView.getItems()) {
            String text = p.getName().toUpperCase() + " " + p.getClassId().toUpperCase();
            // if the search text contains the manufacturer then add it to sublist
            if (text.contains(searchtext)) {
                subList.add(p);
            }

        }
        // set the items to listview that matches
        sectionsListView.setItems(subList);
    }

    public void onSearchTextEntered(KeyEvent keyEvent) {
        if (!sectionsListView.isFocused()
                && keyEvent.getCode() == KeyCode.ENTER)
            sectionsListView.requestFocus();
    }


    private void changedSelectedSection(ObservableValue<? extends Section> observableValue, Section section, Section t1) {


        if (t1 != null && t1.getClassSchedules() != null && t1.getClassSchedules().containsKey("1")) {
            timeTableForSection.setItems(t1.getClassSchedules().get("1"));
            dayChooser.getToggles().get(0).setSelected(true);
        }

    }
}
