package students.attendance;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import custom_view.dialog_helper.CustomDialog;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import model.StudentAttendance;
import model.Student;
import teacher_leaves.LeavesController;
import utility.DateUtility;
import utility.StudentFirestoreUtility;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static custom_view.dialog_helper.CustomDialog.*;
import static students.attendance.AttendanceController.Filter.*;

public class AttendanceController implements Initializable {


    enum Filter {

        ALL("All"),
        DATE("Date"),
        LECTURE("Lecture"),
        PRESENT("Present");

        private String title;

        Filter(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    public TableView<StudentAttendance> attendanceTable;
    public ProgressIndicator progressIndicator;
    public ComboBox<Filter> filterComboBox;

    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);
    private StudentFirestoreUtility firestoreUtility = StudentFirestoreUtility.getInstance();

    private ObservableList<StudentAttendance> attendances = FXCollections.observableArrayList();

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


        filterComboBox.setPadding(new Insets(25));


        filterComboBox.getItems().addAll(ALL, LECTURE, DATE, PRESENT);

        filterComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, filter, t1) -> {

            if (t1 == null) return;

            if (t1 == ALL) {
                filterALL();
            } else if (t1 == LECTURE) {
                filterLecture();
            } else if (t1 == DATE) {
                filterDate();
            } else if (t1 == PRESENT) {
                filterPresent();
            }
        });

        filterComboBox.getSelectionModel().selectFirst();

    }

    private void filterPresent() {
        Optional<Boolean> result = showInputDialogWithRadioButton("Select Present ", "Present");
        result.ifPresent(x -> {
            List<StudentAttendance> collect =
                    attendances
                            .stream()
                            .filter(studentAttendance -> studentAttendance.isPresent() == x)
                            .collect(Collectors.toList());
            attendanceTable.setItems(FXCollections.observableArrayList(collect));
        });
    }

    private void filterDate() {
        Optional<LocalDate> localDate = showInputDialogWithDatePicker("Select Date", "Date");
        localDate.ifPresent(localDate1 -> {
            System.out.println("Date: " + localDate1);
            List<StudentAttendance> collect =
                    attendances
                            .stream()
                            .filter(studentAttendance -> studentAttendance.getDate().contains(DateUtility.timeStampToReadable(Timestamp.of(DateUtility.localDateToDate(localDate1)))))
                            .collect(Collectors.toList());
            attendanceTable.setItems(FXCollections.observableArrayList(collect));
        });
    }

    private void filterLecture() {
        Optional<String> lectureName = showInputDialogWithOneParameter("Lecture", "Lecture Name");
        lectureName.ifPresent(s -> {
            List<StudentAttendance> collect =
                    attendances
                            .stream()
                            .filter(studentAttendance -> studentAttendance.getLecture().toUpperCase().contains(s.toUpperCase()))
                            .collect(Collectors.toList());
            attendanceTable.setItems(FXCollections.observableArrayList(collect));
        });
    }

    private void filterALL() {
        attendanceTable.setItems(attendances);
    }

    public void setStudent(Student student) {
        firestoreUtility.getAttendance(student, studentAttendanceListener);
    }

    private void parseAttendanceData(List<QueryDocumentSnapshot> data) {
        dataLoading.set(true);
        attendances.clear();
        for (QueryDocumentSnapshot document : data) attendances.add(StudentAttendance.fromJSON(document.getData()));
        attendanceTable.setItems(attendances);
        dataLoading.set(false);
    }

}
