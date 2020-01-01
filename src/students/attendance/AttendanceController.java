package students.attendance;

import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import model.Attendance;
import model.Student;
import utility.StudentFirestoreUtility;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AttendanceController implements Initializable {
    public TableView<Attendance> attendanceTable;
    public ProgressIndicator progressIndicator;

    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);
    private StudentFirestoreUtility firestoreUtility = StudentFirestoreUtility.getInstance();

    private ObservableList<Attendance> attendances = FXCollections.observableArrayList();

    private EventListener<QuerySnapshot> studentAttendanceListener = (snapshot, e) -> {
        if (e != null) {
            System.err.println("Listen failed: " + e);

            dataLoading.set(false);
            return;
        }
        if (snapshot != null && !snapshot.isEmpty()) {

            Platform.runLater(() -> {
                parseAttendanceData(snapshot.getDocuments());
            });
        } else {
            System.out.print("Current data: null");

            dataLoading.set(false);
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        progressIndicator.visibleProperty().bind(dataLoading);

    }

    public void setStudent(Student student) {
        firestoreUtility.getAttendance(student, studentAttendanceListener);
    }

    private void parseAttendanceData(List<QueryDocumentSnapshot> data) {
        dataLoading.set(true);
        attendances.clear();
        for (QueryDocumentSnapshot document : data) attendances.add(Attendance.fromJSON(document.getData()));
        attendanceTable.setItems(attendances);
        dataLoading.set(false);
    }

}
