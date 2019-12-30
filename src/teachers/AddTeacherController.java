package teachers;

import custom_view.ImageButton;
import custom_view.ImageButtonListener;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import model.Teacher;

import javax.annotation.security.RunAs;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AddTeacherController implements Initializable {


    public Button submitButton;
    public TextField nameField;
    public TextField emailField;
    public ImageButton profileImageView;


    private TeacherListener listener;
    private File profileImageFile;

    private Teacher teacher;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        profileImageView.setListener(file -> profileImageFile = file);


        submitButton.setOnAction(actionEvent -> {

            String name = nameField.getText().toString();
            String email = emailField.getText().toString();


            Teacher teacher = new Teacher(
                    name, email, "", "", "", false, false
            );


            if (listener != null) {

                System.out.println(teacher);
                System.out.println(profileImageFile);
                listener.onTeacherSubmit(teacher, profileImageFile);
            }


        });
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        fillTeacherData();
    }

    private void fillTeacherData() {

        System.out.println(teacher.toJSON());

        nameField.setText(teacher.getName());

        emailField.setText(teacher.getEmail());


        if (teacher.getProfilePictureUrl() != null)
            profileImageView.setImage(teacher.getProfilePictureUrl());

    }

    public void setListener(TeacherListener listener) {
        this.listener = listener;
    }
}
