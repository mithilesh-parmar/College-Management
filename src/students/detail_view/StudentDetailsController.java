package students.detail_view;

import custom_view.ImageButton;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.Student;

import java.net.URL;
import java.util.ResourceBundle;

public class StudentDetailsController implements Initializable {


    public ImageButton profileImageView;
    public TextField nameField;
    public TextField emailField;
    public Button submitButton;
//    public TextField sectionTextField;
    public TextField parentNumberTextField;
    public TextField requestedRemarkTextField;
    public TextField classNameTextField;

    private Student student;
    private StudentListener listener;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        submitButton.setOnAction(actionEvent -> {
            if (listener != null) listener.onStudentSubmit(student, null);
        });

    }


    public void setListener(StudentListener listener) {
        this.listener = listener;
    }


    private void updateStudentDetails(Student student) {
        if (student.getProfilePictureURL() != null)
            profileImageView.setImage(student.getProfilePictureURL());
        nameField.setText(student.getName());
        emailField.setText(student.getEmail());
//        sectionTextField.setText(student.getSection());
        parentNumberTextField.setText(student.getParentNumber());
        requestedRemarkTextField.setText(student.getRequestedRemark());
        classNameTextField.setText(student.getClassName());

    }

    public void setStudent(Student student) {
        this.student = student;
        updateStudentDetails(student);
    }
}
