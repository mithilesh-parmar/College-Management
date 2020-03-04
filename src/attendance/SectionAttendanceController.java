package attendance;

import attendance.add_attendance.AddAttendanceController;
import attendance.add_attendance.AddAttendanceListener;
import attendance.details.SectionAttendanceDetails;
import com.google.cloud.firestore.QuerySnapshot;
import custom_view.SearchTextFieldController;
import custom_view.card_view.AttendanceCard;
import custom_view.dialog_helper.CustomDialog;
import custom_view.loading_combobox.class_section_combobox.ClassSectionComboBox;
import custom_view.loading_combobox.section.SectionLoadingComboBox;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import listeners.DataChangeListener;
import model.Batch;
import model.ClassItem;
import model.Section;
import model.SectionAttendance;
import utility.AttendanceFirestoreUtility;
import utility.AttendanceListener;
import utility.ScreenUtility;
import utility.SearchCallback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static custom_view.dialog_helper.CustomDialog.showInputDialogWithOneParameter;
import static custom_view.dialog_helper.CustomDialog.showInputDialogWithTwoParameter;

public class SectionAttendanceController implements Initializable, DataChangeListener, AttendanceListener, SearchCallback, AttendanceViewCardListener {


    public SearchTextFieldController searchTextField;
    public FlowPane attendanceFlowPane;
    public ProgressIndicator progressIndicator;
    public Button addAttendance;
    public ScrollPane scrollPane;
    public Button batchFilterButton;
    public Button subjectFilterButton;
    public Button sectionFilterButton;
    public Button clearButton;

    private AttendanceFirestoreUtility firestoreUtility = AttendanceFirestoreUtility.getInstance();
    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);

    private ObjectProperty<Button> selectedButton = new SimpleObjectProperty<>();
    private List<Button> buttonList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        scrollPane.setContent(attendanceFlowPane);
        scrollPane.viewportBoundsProperty().addListener((ov, oldBounds, bounds) -> {
            attendanceFlowPane.setPrefWidth(bounds.getWidth());
            attendanceFlowPane.setPrefHeight(bounds.getHeight());
        });


        progressIndicator.visibleProperty().bind(dataLoading);
        attendanceFlowPane.setHgap(10);
        attendanceFlowPane.setVgap(10);
        attendanceFlowPane.setPadding(new Insets(16));
        attendanceFlowPane.setAlignment(Pos.TOP_LEFT);
        firestoreUtility.setListener(this);
        firestoreUtility.setCardListener(this);
        firestoreUtility.getAttendance();
        searchTextField.setCallback(this);
        addAttendance.setId("menubutton");
        addAttendance.setOnAction(actionEvent -> loadEditView());


        batchFilterButton.setUserData("Batch Filter");
        sectionFilterButton.setUserData("Section Filter");
        subjectFilterButton.setUserData("Subject Filter");
        clearButton.setUserData("Clear Filter");

        buttonList.add(batchFilterButton);
        buttonList.add(sectionFilterButton);
        buttonList.add(subjectFilterButton);
        buttonList.add(clearButton);

//        Set action listeners
        batchFilterButton.setOnAction(actionEvent -> {
            showBatchAttendance();
            selectedButton.set(batchFilterButton);
        });
        sectionFilterButton.setOnAction(actionEvent -> {
            showSectionAttendance();
            selectedButton.set(sectionFilterButton);
        });
        subjectFilterButton.setOnAction(actionEvent -> {
            showSubjectAttendance();
            selectedButton.set(subjectFilterButton);
        });

        clearButton.setOnAction(actionEvent -> {
            attendanceFlowPane.getChildren().setAll(firestoreUtility.attendanceCards);
            selectedButton.set(clearButton);
            highlightAll();
        });

        selectedButton.addListener((observableValue, button, t1) -> {
            if (t1 == null || t1.getUserData().toString().matches(clearButton.getUserData().toString())) return;
            highlightButton(t1);
        });
    }

    private void showSubjectAttendance() {
        Optional<Pair<Section, String>> result = CustomDialog.showDialogWithClassSectionAndSubjectComboBox("Choose Subject");

        result.ifPresentOrElse(sectionStringPair -> {
            String subject = sectionStringPair.getValue();
            Section section = sectionStringPair.getKey();
            attendanceFlowPane.getChildren().setAll(firestoreUtility.getSubjectAttendance(section, subject));
        }, () -> selectedButton.set(clearButton));

    }

    private void showSectionAttendance() {
        Optional<Pair<ClassItem, Section>> result = CustomDialog.showDialogWithClassAndSectionComboBox("Choose Section");

        result.ifPresentOrElse(classItemSectionPair -> {
            ClassItem classItem = classItemSectionPair.getKey();
            Section section = classItemSectionPair.getValue();
            attendanceFlowPane.getChildren().setAll(firestoreUtility.getSectionAttendance(classItem, section));
        }, () -> selectedButton.set(clearButton));

    }

    private void showBatchAttendance() {
        Optional<Batch> result = CustomDialog.showDialogWithBatchComboBox("Choose Batch");

        result.ifPresentOrElse(batch -> {
            attendanceFlowPane.getChildren().setAll(firestoreUtility.getBatchAttendance(batch));
        }, () -> selectedButton.set(clearButton));
    }

    private void highlightAll() {
        buttonList.forEach(button -> {
            button.setDefaultButton(false);
            button.disableProperty().set(false);
        });
    }

    private void highlightButton(Button selectedButton) {
        buttonList.forEach(button -> {
            if (button.getUserData().toString().matches(selectedButton.getUserData().toString())
                    || button.getUserData().toString().matches(clearButton.getUserData().toString())) {
                button.setDefaultButton(true);
                button.disableProperty().set(false);
            } else {
                button.disableProperty().set(true);
            }
        });
    }

    private void loadEditView() {


        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/attendance/add_attendance/AddAttendanceView.fxml"));
        final Stage stage = new Stage();
        stage.setHeight(ScreenUtility.getScreenHeight() * 0.70);
        stage.setWidth(ScreenUtility.getScreenHalfWidth());
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Attendance");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            AddAttendanceController controller = loader.getController();
            controller.setListener(new AddAttendanceListener() {
                @Override
                public void onUploadStart() {
                    close(stage);
                    dataLoading.set(true);
                    System.out.println("Upload Started .....");
                }
            });

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onDataLoaded(ObservableList data) {
        dataLoading.set(false);
        attendanceFlowPane.getChildren().setAll(firestoreUtility.attendanceCards);
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
    public void onAttendanceUploadFinish() {
        dataLoading.set(false);
    }

    @Override
    public void onAttendanceUploadStart() {
        dataLoading.set(true);
    }

    @Override
    public void onAttendanceUploadError() {

    }

    @Override
    public void performSearch(String oldValue, String newValue) {
        if (dataLoading.get()) return;
        // if pressing backspace then set initial values to list
        if (oldValue != null && (newValue.length() < oldValue.length())) {

            attendanceFlowPane.getChildren().setAll(firestoreUtility.attendanceCards);
        }

        // convert the searched text to uppercase
        String searchtext = newValue.toUpperCase();

        ObservableList<AttendanceCard> subList = FXCollections.observableArrayList();
        for (SectionAttendance p : firestoreUtility.sectionAttendances) {
            String text = p.getClassName().toUpperCase() + " " + p.getSubject() + " " + String.valueOf(p.getSectionName()).toUpperCase();
            // if the search text contains the manufacturer then add it to sublist
            if (text.contains(searchtext)) {
                subList.add(firestoreUtility.sectionAttendanceCardMapProperty.get(p));
            }

        }
        attendanceFlowPane.getChildren().setAll(subList);
    }

    @Override
    public void onCardClick(SectionAttendance attendance) {
        loadAttendanceDetails(attendance);
    }

    private void loadAttendanceDetails(SectionAttendance attendance) {

        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/attendance/details/SectionAttendanceDetails.fxml"));
        final Stage stage = new Stage();
        stage.setHeight(ScreenUtility.getScreenHeight() * 0.70);
        stage.setWidth(ScreenUtility.getScreenHalfWidth());
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Attendance");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            SectionAttendanceDetails controller = loader.getController();
            controller.setSectionAttendance(attendance);

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
