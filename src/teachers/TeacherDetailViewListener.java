package teachers;

import javafx.stage.Stage;
import model.Teacher;

import java.io.File;

public interface TeacherDetailViewListener {
    void onTeacherSubmit(Teacher teacher, File profileImage);

    void onTeacherEdit(Teacher teacher);

    void onCancel();

    default void close(Stage stage) {
        stage.close();
    }

}
