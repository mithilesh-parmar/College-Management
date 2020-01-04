package teacher_leaves.edit_view;

import com.google.cloud.Timestamp;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.Leave;

import java.net.URL;
import java.util.ResourceBundle;

public class LeaveEditController implements Initializable {

    public TextField teacherNameTextField;
    public TextField startDateTextField;
    public TextField endDateTextField;
    public TextField statusTextField;
    public TextField reasonTextField;
    public Button submitButton;
    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);
    private Leave leave;
    private LeaveEditListener listener;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        submitButton.setOnAction(actionEvent -> handleSubmitAction());
    }

    private void handleSubmitAction() {
        if (listener == null) return;
        Leave leave = new Leave(
                this.leave.getId(),
                startDateTextField.getText().isEmpty() ? this.leave.getStartDate() : Timestamp.parseTimestamp(startDateTextField.getText()),
                endDateTextField.getText().isEmpty() ? this.leave.getEndDate() : Timestamp.parseTimestamp(endDateTextField.getText()),
                this.leave.getTeacherId(),
                teacherNameTextField.getText().isEmpty() ? this.leave.getTeacherName() : teacherNameTextField.getText(),
                reasonTextField.getText().isEmpty() ? this.leave.getReason() : reasonTextField.getText(),
                statusTextField.getText().isEmpty() ? this.leave.getStatus() : Integer.parseInt(statusTextField.getText())
        );
        listener.onEdit(leave);

    }


    public void setLeave(Leave leave) {
        this.leave = leave;
        loadData(leave);
    }

    private void loadData(Leave leave) {
        dataLoading.set(true);
        teacherNameTextField.setText(leave.getTeacherName());
        startDateTextField.setText(leave.getStartDate().toString());
        endDateTextField.setText(leave.getEndDate().toString());
        statusTextField.setText(String.valueOf(leave.getStatus()));
        reasonTextField.setText(leave.getReason());
        dataLoading.set(false);
    }

    public void setListener(LeaveEditListener listener) {
        this.listener = listener;
    }


}
