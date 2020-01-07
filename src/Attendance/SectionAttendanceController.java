package Attendance;

import com.google.cloud.firestore.QuerySnapshot;
import custom_view.SearchTextFieldController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import listeners.DataChangeListener;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import utility.AttendanceFirestoreUtility;
import utility.AttendanceListener;
import utility.ExcelSheetUtility;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class SectionAttendanceController implements Initializable, DataChangeListener, AttendanceListener {
    public SearchTextFieldController searchTextField;
    public FlowPane attendanceFlowPane;
    public ProgressIndicator progressIndicator;
    public Button addAttendance;
    private AttendanceFirestoreUtility firestoreUtility = AttendanceFirestoreUtility.getInstance();
    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressIndicator.visibleProperty().bind(dataLoading);
        attendanceFlowPane.setHgap(10);
        attendanceFlowPane.setVgap(10);
        attendanceFlowPane.setAlignment(Pos.CENTER_LEFT);
        attendanceFlowPane.setPadding(new Insets(10));
        firestoreUtility.setListener(this);
        firestoreUtility.getAttendance();
        addAttendance.setOnAction(actionEvent -> {
            handleAddAttendanceAction();
        });
    }

    private void handleAddAttendanceAction() {

        FileChooser fileChooser = new FileChooser();
        File excelFile = fileChooser.showOpenDialog(attendanceFlowPane.getScene().getWindow());

        ExcelSheetUtility sheetUtility = new ExcelSheetUtility();
        sheetUtility.setListener(this);
        new Thread(() -> {
            try {
                sheetUtility.processAttendanceSheet(excelFile);
            } catch (IOException | InvalidFormatException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

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
}
