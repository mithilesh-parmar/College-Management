package attendance.add_attendance;

import custom_view.loading_combobox.batches.BatchLoadingComboBox;
import custom_view.loading_combobox.course.ClassLoadingComboBox;
import custom_view.loading_combobox.section.SectionLoadingComboBox;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import model.ClassItem;
import model.Course;
import model.Section;
import utility.AttendanceListener;
import utility.ExcelSheetUtility;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class AddAttendanceController implements Initializable, AttendanceListener {


    public Button addExcelSheet;
    public Button submitButton;
    public DatePicker attendanceDate;
    public ClassLoadingComboBox classComboBox;

    public ComboBox<String> subjectComboBox;
    public SectionLoadingComboBox sectionComboBox;
    public BatchLoadingComboBox batchComboBox;

    private AddAttendanceListener listener;

    private StringProperty buttonTitle = new SimpleStringProperty("Upload Excel Sheet");

    private BooleanProperty dataLoading = new SimpleBooleanProperty(false);
    private StringProperty selectedClassName = new SimpleStringProperty();
    private ObjectProperty<Section> selectedSection = new SimpleObjectProperty<>();
    private StringProperty selectedLecture = new SimpleStringProperty();
    private ObjectProperty<File> selectedFile = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> selectedDate = new SimpleObjectProperty<>();
    private StringProperty selectedBatch = new SimpleStringProperty();

    private ListProperty<String> yearList = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<String> subjectList = new SimpleListProperty<>(FXCollections.observableArrayList());

    private BooleanProperty valid = new SimpleBooleanProperty(false);

    public void setListener(AddAttendanceListener listener) {
        this.listener = listener;
    }


    @Override

    public void initialize(URL url, ResourceBundle resourceBundle) {


        subjectComboBox.itemsProperty().bind(subjectList);

        batchComboBox.setListener(selctedItem -> {
            selectedBatch.set(String.valueOf(selctedItem));
            checkReadyToSubmit();
        });


        selectedClassName.addListener((observableValue, s, t1) -> {
            System.out.println("Showing Sections for: " + t1);
            sectionComboBox.showItemFor(t1);
            checkReadyToSubmit();
        });

        selectedSection.addListener((observableValue, s, t1) -> {
            subjectList.get().setAll(t1.getSubjects());
            checkReadyToSubmit();
        });

        sectionComboBox.setListener(selctedItem -> {
            selectedSection.set(((Section) selctedItem));
            checkReadyToSubmit();
        });


        classComboBox.setListener(selectedItem -> {
            selectedClassName.set(((ClassItem) selectedItem).getName());
            checkReadyToSubmit();
        });
        subjectComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            selectedLecture.set(t1);
            checkReadyToSubmit();
        });


        addExcelSheet.textProperty().bind(buttonTitle);


        addExcelSheet.setOnAction(actionEvent -> chooseExcelSheet());
        submitButton.setOnAction(actionEvent -> uploadAttendance());

        submitButton.visibleProperty().bind(valid);

        attendanceDate.valueProperty().addListener((observableValue, localDate, t1) -> {
            selectedDate.set(t1);
            checkReadyToSubmit();
        });
    }

    private void uploadAttendance() {
        ExcelSheetUtility sheetUtility = new ExcelSheetUtility();
        sheetUtility.setListener(this);

        if (listener != null) listener.onUploadStart();
        new Thread(
                () -> sheetUtility
                        .processAttendanceSheet(
                                selectedFile.get(),
                                selectedClassName.get(),
                                selectedLecture.get(),
                                Date.from(selectedDate.get().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                                selectedSection.get().getSectionName(),
                                selectedBatch.get()
                        )
        ).start();


    }

    private void chooseExcelSheet() {
        FileChooser fileChooser = new FileChooser();
        File excelFile = fileChooser.showOpenDialog(addExcelSheet.getScene().getWindow());
        buttonTitle.set(excelFile.getName());
        selectedFile.set(excelFile);
        checkReadyToSubmit();
    }


    private void checkReadyToSubmit() {

        boolean sectionSelected = selectedSection.get() != null,
                lectureSelected = selectedLecture.get() != null,
                classNameSelected = selectedClassName.get() != null,
                fileSelected = selectedFile.get() != null,
                dateSelected = selectedDate.get() != null,
                batchSelected = selectedBatch.get() != null;

        System.out.println("\nSection: " + sectionSelected + " - " + selectedSection.get()
                + "\nlecture: " + lectureSelected + " - " + selectedLecture.get()
                + "\nclass: " + classNameSelected + " - " + selectedClassName.get()
                + "\nfile: " + fileSelected + " - " + selectedFile.get()
                + "\ndate: " + dateSelected + " - " + selectedDate.get()
                + "\nbatch: " + batchSelected + " - " + selectedBatch.get()
        );


        valid.set(
                (sectionSelected && lectureSelected && classNameSelected && fileSelected && dateSelected && batchSelected)
        );
        System.out.println(valid);
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
//        if (listener != null) listener.onUploadStart();
    }

    @Override
    public void onAttendanceUploadError() {
        System.out.println("Upload Error");
        dataLoading.set(false);
    }


}
