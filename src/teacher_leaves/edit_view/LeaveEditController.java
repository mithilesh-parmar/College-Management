package teacher_leaves.edit_view;

import com.google.cloud.Timestamp;
import javafx.beans.property.*;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import model.Leave;
import utility.DateUtility;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class LeaveEditController implements Initializable {

    public TextField teacherNameTextField;
    public TextField statusTextField;
    public TextField reasonTextField;
    public Button submitButton;
    public DatePicker endDatePicker;
    public DatePicker startDatePicker;

    private StringProperty teacherName = new SimpleStringProperty(),
            reason = new SimpleStringProperty();
    private IntegerProperty status = new SimpleIntegerProperty();
    private ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>(),
            endDate = new SimpleObjectProperty<>();

    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);
    private Leave leave;
    private LeaveEditListener listener;
    private BooleanProperty canSubmit = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        submitButton.visibleProperty().bind(canSubmit);
        submitButton.setOnAction(actionEvent -> handleSubmitAction());

        teacherNameTextField.textProperty().addListener((observableValue, s, t1) -> {
            teacherName.set(t1);
            checkCanSubmit();
        });

        statusTextField.textProperty().addListener((observableValue, s, t1) -> {
            status.set(Integer.parseInt(t1));
            checkCanSubmit();
        });

        reasonTextField.textProperty().addListener((observableValue, s, t1) -> {
            reason.set(t1);
            checkCanSubmit();
        });

        endDatePicker.valueProperty().addListener((observableValue, localDate, t1) -> {
            endDate.set(t1);
            checkCanSubmit();
        });
        startDatePicker.valueProperty().addListener((observableValue, localDate, t1) -> {
            startDate.set(t1);
            checkCanSubmit();
        });
    }

    private void checkCanSubmit() {
        canSubmit.set(
                teacherName.get() != null && !teacherName.get().isEmpty()
                        && reason.get() != null && !reason.get().isEmpty()
                        && status.get() > 0
                        && startDate.get() != null
                        && endDate.get() != null
        );
    }

    private void handleSubmitAction() {
        if (listener == null) return;
        Leave updatedLeave = new Leave(
                leave.getId(),
                DateUtility.localDateToTimestamp(startDate.get()),
                DateUtility.localDateToTimestamp(endDate.get()),
                leave.getTeacherId(),
                teacherName.get(),
                reason.get(),
                status.get()
        );
        listener.onEdit(updatedLeave);

    }


    public void setLeave(Leave leave) {
        this.leave = leave;
        loadData(leave);
    }

    private void loadData(Leave leave) {
        dataLoading.set(true);
        teacherNameTextField.setText(leave.getTeacherName());
        startDatePicker.setValue(DateUtility.dateToLocalDate(leave.getStartDate()));
        endDatePicker.setValue(DateUtility.dateToLocalDate(leave.getEndDate()));
        statusTextField.setText(String.valueOf(leave.getStatus()));
        reasonTextField.setText(leave.getReason());
        dataLoading.set(false);
    }

    public void setListener(LeaveEditListener listener) {
        this.listener = listener;
    }


}
