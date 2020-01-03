package time_table;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.Lecture;

import java.net.URL;
import java.util.ResourceBundle;

public class AddLectureController implements Initializable {
    public TextField subjectTextField;
    public TextField startTimeTextField;
    public TextField endTimeTextField;
    public Button submitButton;

    private LectureListener listener;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        submitButton.setOnAction(actionEvent -> processData());
    }

    public void setListener(LectureListener listener) {
        this.listener = listener;
    }

    private void processData() {
        if (listener == null) return;
        Lecture lecture = new Lecture(
                subjectTextField.getText(),
                endTimeTextField.getText(),
                startTimeTextField.getText()
        );

        listener.onLectureAdded(lecture);
    }
}
