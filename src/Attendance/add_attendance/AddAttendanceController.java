package Attendance.add_attendance;

import custom_view.loading_combobox.batches.BatchLoadingComboBox;
import custom_view.loading_combobox.classes.ClassLoadingComboBox;
import custom_view.loading_combobox.section.SectionLoadingComboBox;
import javafx.beans.property.*;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.FileChooser;
import model.Batch;
import model.Section;
import model.StudentClass;
import utility.AttendanceListener;
import utility.ExcelSheetUtility;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AddAttendanceController implements Initializable, AttendanceListener {



    public Button addExcelSheet;
    public ProgressIndicator progressIndicator;
    public BatchLoadingComboBox batchComboBox;
    public SectionLoadingComboBox sectionComboBox;
    public ClassLoadingComboBox classComboBox;
    public Button submitButton;
    private AddAttendanceListener listener;

    private StringProperty buttonTitle = new SimpleStringProperty("Upload Excel Sheet");

    private BooleanProperty dataLoading = new SimpleBooleanProperty(false);
    private ObjectProperty<Section> selectedSection = new SimpleObjectProperty<>();
    private ObjectProperty<StudentClass> selectedClass = new SimpleObjectProperty<>();
    private ObjectProperty<Batch> selectedBatch = new SimpleObjectProperty<>();
    private ObjectProperty<File> selectedFile = new SimpleObjectProperty<>();


    private BooleanProperty valid = new SimpleBooleanProperty(false);

    public void setListener(AddAttendanceListener listener) {
        this.listener = listener;
    }

    @Override

    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressIndicator.visibleProperty().bind(dataLoading);
        addExcelSheet.textProperty().bind(buttonTitle);

        classComboBox.setListener(selctedItem -> {
            selectedClass.set((StudentClass) selctedItem);
            checkReadyToSubmit();
        });

        batchComboBox.setListener(selctedItem -> {
            selectedBatch.set((Batch) selctedItem);
            checkReadyToSubmit();
        });

        sectionComboBox.setListener(selctedItem -> {
            selectedSection.set((Section) selctedItem);
            checkReadyToSubmit();
        });

        addExcelSheet.setOnAction(actionEvent -> chooseExcelSheet());
        submitButton.setOnAction(actionEvent -> uploadAttendance());

        submitButton.visibleProperty().bind(valid);
    }

    private void uploadAttendance() {
        ExcelSheetUtility sheetUtility = new ExcelSheetUtility();
        sheetUtility.setListener(this);
        Thread thread = new Thread(() -> sheetUtility.processAttendanceSheet(selectedFile.get(), selectedBatch.get(), selectedClass.get(), selectedSection.get()));


        thread.start();
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
                (selectedBatch.get() != null
                        && selectedClass.get() != null
                        && selectedSection.get() != null
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
