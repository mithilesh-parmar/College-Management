package teachers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import model.Teacher;

import java.net.URL;
import java.util.ResourceBundle;

public class AddTeacherController implements Initializable {


    public Button submitButton;
    public TextField nameField;
    public TextField emailField;



    private ToggleGroup profileCompleted;
    public RadioButton profileCompletedTrueRadioButton;
    public RadioButton profileCompletedFalseRadioButton;

    private ToggleGroup verified;
    public RadioButton verifiedTrueRadioButton;
    public RadioButton verifiedFalseRadioButton;
    public TextField verificationCodeTextField;

    private TeacherListener listener;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        profileCompleted = new ToggleGroup();
        verified = new ToggleGroup();

        verified.getToggles().addAll(verifiedFalseRadioButton, verifiedTrueRadioButton);
        profileCompleted.getToggles().addAll(profileCompletedFalseRadioButton, profileCompletedTrueRadioButton);

//        profileImageView.setOwner(submitButton.getScene().getWindow());

        submitButton.setOnAction(actionEvent -> {

            String name = nameField.getText().toString();
            String email = emailField.getText().toString();
            String verificationCode = verificationCodeTextField.getText().toString();
            boolean isVerified = Boolean.parseBoolean(verified.getSelectedToggle().getUserData().toString());
            boolean isProfileCompleted = Boolean.parseBoolean(profileCompleted.getSelectedToggle().getUserData().toString());

            Teacher teacher = new Teacher(
                    name, email, "", "", verificationCode, isVerified, isProfileCompleted
            );

            System.out.println(teacher);
            if (listener != null) {

                listener.onTeacherSubmit(teacher);
            }


        });
    }


    public void setListener(TeacherListener listener) {
        this.listener = listener;
    }
}
