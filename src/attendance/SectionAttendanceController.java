package attendance;

import attendance.add_attendance.AddAttendanceController;
import attendance.add_attendance.AddAttendanceListener;
import com.google.cloud.firestore.QuerySnapshot;
import custom_view.SearchTextFieldController;
import custom_view.card_view.AttendanceCard;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import model.SectionAttendance;
import utility.AttendanceFirestoreUtility;
import utility.AttendanceListener;
import utility.SearchCallback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SectionAttendanceController implements Initializable, DataChangeListener, AttendanceListener, SearchCallback, AttendanceViewCardListener {


    public SearchTextFieldController searchTextField;
    public FlowPane attendanceFlowPane;
    public ProgressIndicator progressIndicator;
    public Button addAttendance;
    public ScrollPane scrollPane;

    private AttendanceFirestoreUtility firestoreUtility = AttendanceFirestoreUtility.getInstance();
    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);


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
    }

    private void loadEditView() {


        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/attendance/add_attendance/AddAttendanceView.fxml"));
        final Stage stage = new Stage();
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
        loadEditView();
    }
}
