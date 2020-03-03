package test;

import com.google.cloud.firestore.QuerySnapshot;
import custom_view.class_section_accordion.ClassSectionTitledPane;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import model.ClassItem;
import model.Lecture;
import model.Section;
import time_table.AddLectureController;
import time_table.LectureListener;
import utility.ClassFirestoreUtility;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static utility.DAY.*;


public class TestController implements Initializable, DataChangeListener, ClassSectionTitledPane.EventCallback {


    public ListView<ClassItem> classList;
    public TextField searchTextField;
    public ProgressIndicator progressIndicator;
    public ListView scheduleView;

    public TableView<Lecture> mondayTableView;
    public TableView<Lecture> tuesdayTableView;
    public TableView<Lecture> wednesdayTableView;
    public TableView<Lecture> thursdayTableView;
    public TableView<Lecture> fridayTableView;
    public TableView<Lecture> saturdayTableView;
    public Button addButton;

    private ListProperty<Lecture> mondayLectures = new SimpleListProperty<>(FXCollections.observableArrayList()),
            tuesdayLectures = new SimpleListProperty<>(FXCollections.observableArrayList()),
            wednesdayLectures = new SimpleListProperty<>(FXCollections.observableArrayList()),
            thursdayLectures = new SimpleListProperty<>(FXCollections.observableArrayList()),
            fridayLectures = new SimpleListProperty<>(FXCollections.observableArrayList()),
            saturdayLectures = new SimpleListProperty<>(FXCollections.observableArrayList());

    private ClassFirestoreUtility firestoreUtility = ClassFirestoreUtility.getInstance();
    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);
    private ObjectProperty<Section> selectedSection = new SimpleObjectProperty<>();
    private BooleanProperty canViewSchedules = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        classList.setCellFactory(classItemListView -> new ClassSectionCell(this));

        scheduleView.visibleProperty().bind(canViewSchedules);
        addButton.setOnAction(actionEvent -> loadAddView());

//        bind tableViews to list of lectures
        mondayTableView.itemsProperty().bind(mondayLectures);
        tuesdayTableView.itemsProperty().bind(tuesdayLectures);
        wednesdayTableView.itemsProperty().bind(wednesdayLectures);
        thursdayTableView.itemsProperty().bind(thursdayLectures);
        fridayTableView.itemsProperty().bind(fridayLectures);
        saturdayTableView.itemsProperty().bind(saturdayLectures);

//        add listener to section
        selectedSection.addListener((observableValue, section, t1) -> {
            if (t1 == null) return;
            canViewSchedules.set(true);
            dataLoading.set(true);
            System.out.println("Showing lectures for : " + t1);
            mondayLectures.set(t1.getLectures(MONDAY));
            tuesdayLectures.set(t1.getLectures(TUESDAY));
            wednesdayLectures.set(t1.getLectures(WEDNESDAY));
            thursdayLectures.set(t1.getLectures(THURSDAY));
            fridayLectures.set(t1.getLectures(FRIDAY));
            saturdayLectures.set(t1.getLectures(SATURDAY));
            dataLoading.set(false);
        });

        progressIndicator.visibleProperty().bind(dataLoading);
        firestoreUtility.setListener(this);
        firestoreUtility.getClasses();
    }

    @Override
    public void onDataLoaded(ObservableList data) {
        dataLoading.set(false);
        classList.setItems(firestoreUtility.classes);
    }

    @Override
    public void onDataChange(QuerySnapshot data) {
        dataLoading.set(true);
    }

    @Override
    public void onError(Exception e) {
        dataLoading.set(false);
    }

    @Override
    public void onClick(ClassItem classItem, Section s) {
//        Show timetable for this section
//        classList.getSelectionModel().select(classItem);

        selectedSection.set(s);

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
                    dataLoading.set(true);
                    firestoreUtility.addLecture(
                            lecture,
                            selectedSection.get()
                    );
                }

                @Override
                public void onLectureUpdated(Lecture lecture) {

                }
            });


            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static class ClassSectionCell
            extends ListCell<ClassItem> {

        private ClassSectionTitledPane.EventCallback eventCallback;

        public ClassSectionCell(ClassSectionTitledPane.EventCallback eventCallback) {
            this.eventCallback = eventCallback;
        }

        @Override
        public void updateItem(ClassItem obj, boolean empty) {
            super.updateItem(obj, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                ClassSectionTitledPane classSectionAccordion = new ClassSectionTitledPane(obj);
                classSectionAccordion.setCallback(eventCallback);
                setGraphic(classSectionAccordion);
            }
        }


    }
}
