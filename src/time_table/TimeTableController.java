package time_table;

import com.google.cloud.firestore.QuerySnapshot;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import listeners.DataChangeListener;
import model.Event;
import model.Lecture;
import model.Section;
import utility.SectionsFirestoreUtility;

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

    private ToggleGroup dayChooser;

    private SectionsFirestoreUtility firestoreUtility = SectionsFirestoreUtility.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

       setupToggleButtons();
       mainView.getCenter().setVisible(false);
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> performSearch(oldValue, newValue));

        dayChooser.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            System.out.println(observableValue.getValue().getUserData().toString());
            timeTableForSection.setItems(
                    sectionsListView
                            .getSelectionModel()
                            .getSelectedItem()
                            .getClassSchedules()
                            .get(observableValue.getValue().getUserData().toString())
            );
        });

        firestoreUtility.setListener(this);
        firestoreUtility.getSections();


        sectionsListView.getSelectionModel().selectedItemProperty().addListener((observableValue, section, t1) -> {
            timeTableForSection.setItems(observableValue.getValue().getClassSchedules().get("1"));
            dayChooser.getToggles().get(0).setSelected(true);
            mainView.getCenter().setVisible(true);
        });


    }


    private void setupToggleButtons(){
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
    public void onDataLoaded(ObservableList data) {
        sectionsListView.setItems(firestoreUtility.sections);
        progressIndicator.setVisible(false);
    }

    @Override
    public void onDataChange(QuerySnapshot data) {
        progressIndicator.setVisible(true);
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
}
