package fee.add_fee;

import javafx.beans.property.*;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.Fee;
import model.Student;
import utility.DateUtility;
import utility.FirestoreConstants;

import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class AddFeeController implements Initializable {

    public Button submitButton;
    public TextField studentIDField;
    public TextField amountField;
    public ComboBox<Fee.Type> typeComboBox;
    public Button checkButton;
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
        checkButton.setOnAction(actionEvent -> checkAdmissionNumber());
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

        submitButton.setOnAction(actionEvent -> {
            if (listener == null) return;
            listener.onFeeSubmit(
                    new Fee("",
                            selectedStudentAdmissionId.get(),
                            selectedAmount.get(),
                            DateUtility.localDateToTimestamp(selectedDate.get()),
                            selectedFeeType.get())
            );
        });
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
