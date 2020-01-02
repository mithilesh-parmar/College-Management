package teachers.add_teacher;

import custom_view.ImageButton;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.Teacher;
import teachers.TeacherDetailViewListener;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AddTeacherController implements Initializable {


    public Button submitButton;
    public TextField nameField;
    public TextField emailField;
    public ImageButton profileImageView;


    private TeacherDetailViewListener listener;
    private File profileImageFile;

    private Teacher teacher;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        profileImageView.setListener(file -> profileImageFile = file);


        submitButton.setOnAction(actionEvent -> {

            String name = nameField.getText().toString();
            String email = emailField.getText().toString();


            Teacher updatedTeacher = new Teacher("", name, email, "", "", "", false, false);


            if (listener != null) {

                System.out.println(updatedTeacher);
                System.out.println(profileImageFile);


                if (teacher != null) {
                    updatedTeacher.setVerified(teacher.isVerified());
                    updatedTeacher.setProfileCompleted(teacher.isProfileCompleted());
                    updatedTeacher.setVerificationCode(teacher.getVerificationCode());
                    updatedTeacher.setToken(teacher.getToken());
                    updatedTeacher.setID(teacher.getID());
                    updatedTeacher.setProfilePictureUrl(teacher.getProfilePictureUrl());
                }
                listener.onTeacherSubmit(updatedTeacher, profileImageFile);
            }


        });
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        fillTeacherData();
    }

    private void fillTeacherData() {


        nameField.setText(teacher.getName());

        emailField.setText(teacher.getEmail());


        if (teacher.getProfilePictureUrl() != null)
            profileImageView.setImage(teacher.getProfilePictureUrl());

    }

    public void setListener(TeacherDetailViewListener listener) {
        this.listener = listener;
    }
}
