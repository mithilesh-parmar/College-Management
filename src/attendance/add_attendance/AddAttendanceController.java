package attendance.add_attendance;

import custom_view.loading_combobox.course.CourseLoadingComboBox;
import custom_view.loading_combobox.subject.SubjectLoadingComboBox;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import model.Course;
import model.Subject;
import utility.AttendanceListener;
import utility.ExcelSheetUtility;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AddAttendanceController implements Initializable, AttendanceListener {


    public Button addExcelSheet;
    public Button submitButton;
    public DatePicker attendanceDate;
    public CourseLoadingComboBox courseLoadingComboBox;
    public SubjectLoadingComboBox subjectLoadingComboBox;
    public ComboBox<Integer> yearComboBox;

    private AddAttendanceListener listener;

    private StringProperty buttonTitle = new SimpleStringProperty("Upload Excel Sheet");

    private BooleanProperty dataLoading = new SimpleBooleanProperty(false);
    private ObjectProperty<Course> selectedCourse = new SimpleObjectProperty<>();
    private ObjectProperty<Integer> selectedYear = new SimpleObjectProperty<>();
    private ObjectProperty<Subject> selectedLecture = new SimpleObjectProperty<>();
    private ObjectProperty<File> selectedFile = new SimpleObjectProperty<>();

    private ListProperty<Integer> yearList = new SimpleListProperty<>(FXCollections.observableArrayList());

    private BooleanProperty valid = new SimpleBooleanProperty(false);

    public void setListener(AddAttendanceListener listener) {
        this.listener = listener;
    }

    @Override

    public void initialize(URL url, ResourceBundle resourceBundle) {

        yearComboBox.itemsProperty().bind(yearList);

        selectedCourse.addListener((observableValue, course, t1) -> {
            yearList.clear();
            for (int i = 1; i <= t1.getYears(); i++) {
                yearList.get().add(i);
            }
            checkReadyToSubmit();
        });

        yearComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, integer, t1) -> {
            selectedYear.set(t1);
            checkReadyToSubmit();
        });
        courseLoadingComboBox.setListener(selectedItem -> {
            selectedCourse.set((Course) selectedItem);
            checkReadyToSubmit();
        });

        subjectLoadingComboBox.setListener(selectedItem -> {
            selectedLecture.set((Subject) selectedItem);
            checkReadyToSubmit();
        });

        addExcelSheet.textProperty().bind(buttonTitle);


        addExcelSheet.setOnAction(actionEvent -> chooseExcelSheet());
        submitButton.setOnAction(actionEvent -> uploadAttendance());

        submitButton.visibleProperty().bind(valid);
    }

    private void uploadAttendance() {
        ExcelSheetUtility sheetUtility = new ExcelSheetUtility();
        sheetUtility.setListener(this);
//        Thread thread = new Thread(() -> sheetUtility.processAttendanceSheet(selectedFile.get(), selectedBatch.get(), selectedClass.get(), selectedSection.get()));


//        thread.start();
        if (listener != null) listener.onUploadStart();

    }

    private void chooseExcelSheet() {
        FileChooser fileChooser = new FileChooser();
        File excelFile = fileChooser.showOpenDialog(addExcelSheet.getScene().getWindow());
        buttonTitle.set(excelFile.getName());
        selectedFile.set(excelFile);
        checkReadyToSubmit();
    }


    private void checkReadyToSubmit() {
        valid.set(
                (selectedYear.get() != null
                        && selectedLecture.get() != null
                        && selectedCourse.get() != null
                        && selectedFile.get() != null
                )
        );
    }

    @Override
    public void onAttendanceUploadFinish() {
        System.out.println("Uploaded");
        dataLoading.set(false);
    }

    @Override
    public void onAttendanceUploadStart() {
        System.out.println("Uploading Start");
        dataLoading.set(true);
    }

    @Override
    public void onAttendanceUploadError() {
        System.out.println("Upload Error");
        dataLoading.set(false);
    }


}
