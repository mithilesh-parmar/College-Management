package time_table;

import com.google.cloud.firestore.QuerySnapshot;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import listeners.DataChangeListener;
import model.Lecture;
import model.Section;
import utility.SectionsFirestoreUtility;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Flow;

public class TimeTableController implements Initializable, DataChangeListener {


    public ListView<Section> sectionsListView;
    public ProgressIndicator progressIndicator;
    public TextField searchTextField;

    public TableView<Lecture> mondayTableView;
    public TableView<Lecture> tuesdayTableView;
    public TableView<Lecture> wednesdayTableView;
    public TableView<Lecture> thursdayTableView;
    public TableView<Lecture> fridayTableView;
    public TableView<Lecture> saturdayTableView;

    public Button addButton;
    public ListView scheduleView;


    private BooleanProperty loadingData = new SimpleBooleanProperty(true);
    private SectionsFirestoreUtility firestoreUtility = SectionsFirestoreUtility.getInstance();


    private ObjectProperty<Section> selectedSection = new SimpleObjectProperty<>();
    private BooleanProperty canViewSchedule = new SimpleBooleanProperty(false);
    private IntegerProperty selectedDay = new SimpleIntegerProperty(0);

    private ListProperty<Lecture> mondaySchedule = new SimpleListProperty<>(FXCollections.observableArrayList()),
            tuesdaySchedule = new SimpleListProperty<>(FXCollections.observableArrayList()),
            wednesdaySchedule = new SimpleListProperty<>(FXCollections.observableArrayList()),
            thursdaySchedule = new SimpleListProperty<>(FXCollections.observableArrayList()),
            fridaySchedule = new SimpleListProperty<>(FXCollections.observableArrayList()),
            saturdaySchedule = new SimpleListProperty<>(FXCollections.observableArrayList());

    public TimeTableController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        sectionsListView.setCellFactory(sectionListView -> new SectionListCell());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        scrollPane.setPannable(true);


        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> performSearch(oldValue, newValue));
        progressIndicator.visibleProperty().bind(loadingData);

        addButton.visibleProperty().bind(canViewSchedule);

        mondayTableView.itemsProperty().bind(mondaySchedule);
        tuesdayTableView.itemsProperty().bind(tuesdaySchedule);
        wednesdayTableView.itemsProperty().bind(wednesdaySchedule);
        thursdayTableView.itemsProperty().bind(thursdaySchedule);
        fridayTableView.itemsProperty().bind(fridaySchedule);
        saturdayTableView.itemsProperty().bind(saturdaySchedule);


        firestoreUtility.setListener(this);
        firestoreUtility.getSections();

        addButton.setOnAction(actionEvent -> loadAddView());


        sectionsListView.getSelectionModel().selectedItemProperty().addListener((observableValue, section, t1) -> {
            selectedSection.set(t1);
            checkCanViewSchedule();

            mondaySchedule.setValue(t1.getClassSchedules().get("1"));
            tuesdaySchedule.setValue(t1.getClassSchedules().get("2"));
            wednesdaySchedule.setValue(t1.getClassSchedules().get("3"));
            thursdaySchedule.setValue(t1.getClassSchedules().get("4"));
            fridaySchedule.setValue(t1.getClassSchedules().get("5"));
            saturdaySchedule.setValue(t1.getClassSchedules().get("6"));

        });
        sectionsListView.getSelectionModel().selectFirst();


        scheduleView.visibleProperty().bind(canViewSchedule);

        canViewSchedule.addListener((observableValue, aBoolean, t1) -> {
            if (t1) scrollPane.setContent(scheduleView);
        });
    }


    private void checkCanViewSchedule() {
        canViewSchedule.set(selectedSection.get() != null);
    }


    private void loadAddView() {


        FXMLLoader loader;
        loader = new FXMLLoader((getClass().getResource("AddLectureView.fxml")));
        final Stage stage = new Stage();
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Student Details");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            AddLectureController controller = loader.getController();
            controller.setSection(selectedSection.get());
            controller.setListener(new LectureListener() {
                @Override
                public void onLectureAdded(Lecture lecture) {
                    close(stage);
                    loadingData.set(true);
                    firestoreUtility.addLecture(
                            lecture,
                            selectedSection.get()
                    );
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
        sectionsListView.setItems(firestoreUtility.sections);

    }

    @Override
    public void onDataChange(QuerySnapshot data) {
        loadingData.set(true);
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
            String text = p.getSectionName().toUpperCase() + " " + p.getClassName();
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

    private static class SectionListCell extends ListCell<Section> {

        private final Label classNameLabel = new Label();
        private final Label sectionNameLabel = new Label();
        private VBox vBox = new VBox(5);

        SectionListCell() {
//            setStyle("/styles/dark_metro_style.css");
            // Add listeners here...
        }

        @Override
        public void updateItem(Section obj, boolean empty) {
            super.updateItem(obj, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                classNameLabel.setText("Class: " + obj.getClassName());

                sectionNameLabel.setText("Section: " + obj.getSectionName());
                vBox.getChildren().setAll(classNameLabel, sectionNameLabel);
                setGraphic(vBox);
            }
        }
    }
}
