package fee.add_fee;

import com.google.cloud.Timestamp;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.Fee;
import model.Student;
import utility.DateUtility;
import utility.FirestoreConstants;
import utility.StudentVerificationUtility;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class AddFeeController implements Initializable {

    public Button submitButton;
    public TextField studentIDField;
    public TextField amountField;
    public ComboBox<Fee.Type> typeComboBox;
    public ProgressIndicator progressIndicator;
    public DatePicker datePicker;

    private AddFeeListener listener;

    private BooleanProperty canSubmit = new SimpleBooleanProperty(false),
            isValid = new SimpleBooleanProperty(false),
            dataLoading = new SimpleBooleanProperty(false);
    private StringProperty selectedStudentAdmissionId = new SimpleStringProperty();
    private LongProperty selectedAmount = new SimpleLongProperty();
    private ObjectProperty<Fee.Type> selectedFeeType = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> selectedDate = new SimpleObjectProperty<>();

    private ObjectProperty<Student> student = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        submitButton.visibleProperty().bind(canSubmit);
        progressIndicator.visibleProperty().bind(dataLoading);
        typeComboBox.getItems().addAll(Fee.Type.ADMISSION_FEE, Fee.Type.FINE);
        studentIDField.textProperty().addListener((observableValue, s, t1) -> {
            selectedStudentAdmissionId.set(t1);
            checkCanSubmit();
        });
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                amountField.setText(oldValue);
            } else {
                selectedAmount.set(Long.parseLong(newValue));
                checkCanSubmit();
            }
        });

        datePicker.valueProperty().addListener((observableValue, localDate, t1) -> {
            selectedDate.set(t1);
            checkCanSubmit();
        });
        typeComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, type, t1) -> {
            if (t1 == null) return;
            selectedFeeType.set(t1);
            checkCanSubmit();
        });
        student.addListener((observableValue, student1, t1) -> {
            if (t1 != null) {
                isValid.set(true);
            }
        });

        isValid.addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                showSuccessDialog(student.get());
            } else {
                showErrorDialog();
            }
        });

        datePicker.setValue(LocalDate.now());

        submitButton.setOnAction(actionEvent -> submitFee());
    }

    private void submitFee() {
        if (listener == null) return;

        StudentVerificationUtility.Callback callback = new StudentVerificationUtility.Callback() {
            @Override
            public void onSuccess(Student student) {
                dataLoading.set(false);
//                if reg is valid then upload the attendance
                Timestamp timestamp = DateUtility.localDateToTimestamp(selectedDate.get());
                listener.onFeeSubmit(
                        new Fee("",
                                selectedStudentAdmissionId.get(),
                                selectedAmount.get(),
                                timestamp,
                                selectedFeeType.get(),
                                DateUtility.timeStampToReadable(timestamp))
                );
            }

            @Override
            public void onFailure() {
//                does not exist show alert
                dataLoading.set(false);
                Alert alert = new Alert(Alert.AlertType.WARNING);

                alert.setTitle("Alert");
                alert.setHeaderText(
                        "No Student with id " + selectedStudentAdmissionId.get() + " found."
                );

                alert.showAndWait();
            }

            @Override
            public void onStart() {
//                show progress indicator
                dataLoading.set(true);
            }
        };

        //        check if reg is valid
        isRegistrationNumberValid(selectedStudentAdmissionId.get(), callback);


    }

    private void isRegistrationNumberValid(String admissionId, StudentVerificationUtility.Callback callback) {
        try {
            StudentVerificationUtility.exist(admissionId, callback);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setListener(AddFeeListener listener) {
        this.listener = listener;
    }

    private void checkCanSubmit() {
        canSubmit.set(
                selectedAmount.get() > 0L
                        && selectedStudentAdmissionId.get() != null && !selectedStudentAdmissionId.get().isEmpty()
                        && selectedFeeType.get() != null
                        && selectedDate.get() != null
        );
    }

    public void setStudent(Student student) {
        this.student.set(student);
        selectedStudentAdmissionId.set(student.getAdmissionID());
        studentIDField.setText(student.getAdmissionID());
        studentIDField.disableProperty().set(true);
    }

    private void showSuccessDialog(Student student) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alert");
        alert.setContentText("Name: " + student.getName() +
                "\nAdmission ID: " + student.getAdmissionID() +
                "\nClass: " + student.getClassName() + "-" + student.getSection());
        alert.showAndWait();
    }

    private void showErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Alert");
        alert.setContentText("User Not Found");
        alert.showAndWait();
    }

    private void checkAdmissionNumber() {
        if (selectedStudentAdmissionId.get() == null) return;
        try {
            dataLoading.set(true);
            System.out.println("Checking for " + selectedStudentAdmissionId.get());
            FirestoreConstants
                    .studentCollectionReference
                    .whereEqualTo("admission_id", selectedStudentAdmissionId.get())
                    .get()
                    .get()
                    .forEach(queryDocumentSnapshot -> student.set(Student.fromJSON(queryDocumentSnapshot.getData())));
            dataLoading.set(false);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
