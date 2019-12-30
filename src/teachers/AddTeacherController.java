package teachers;

import custom_view.ImageButton;
import custom_view.ImageButtonListener;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import model.Teacher;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AddTeacherController implements Initializable {


    public Button submitButton;
    public TextField nameField;
    public TextField emailField;
    public ImageButton profileImageView;


    private ToggleGroup profileCompleted;
    public RadioButton profileCompletedTrueRadioButton;
    public RadioButton profileCompletedFalseRadioButton;

    private ToggleGroup verified;
    public RadioButton verifiedTrueRadioButton;
    public RadioButton verifiedFalseRadioButton;
    public TextField verificationCodeTextField;

    private TeacherListener listener;
    private File profileImageFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        profileCompleted = new ToggleGroup();
        verified = new ToggleGroup();

        verified.getToggles().addAll(verifiedFalseRadioButton, verifiedTrueRadioButton);
        profileCompleted.getToggles().addAll(profileCompletedFalseRadioButton, profileCompletedTrueRadioButton);

        verified.selectToggle(verifiedFalseRadioButton);
        profileCompleted.selectToggle(profileCompletedFalseRadioButton);

        profileImageView.setListener(file -> profileImageFile = file);


        submitButton.setOnAction(actionEvent -> {

            String name = nameField.getText().toString();
            String email = emailField.getText().toString();
            String verificationCode = verificationCodeTextField.getText().toString();
            boolean isVerified = Boolean.parseBoolean(verified.getSelectedToggle().getUserData().toString());
            boolean isProfileCompleted = Boolean.parseBoolean(profileCompleted.getSelectedToggle().getUserData().toString());


            Teacher teacher = new Teacher(
                    name, email, "", "", verificationCode, isVerified, isProfileCompleted
            );


            if (listener != null) {

                listener.onTeacherSubmit(teacher, profileImageFile);
            }


        });
    }


    public void setListener(TeacherListener listener) {
        this.listener = listener;
    }
}
