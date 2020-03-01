package students.profile.detail_view;

import javafx.stage.Stage;
import model.Student;

import java.io.File;

public interface StudentListener {


    void onStudentSubmit(Student student, File profileImage);

    void onStudentEdit(Student student);

    void onCancel();

    default void close(Stage stage) {
        stage.close();
    }
}