package teachers;

import javafx.stage.Stage;
import model.Teacher;

public interface TeacherListener {
    void onTeacherSubmit(Teacher teacher);

    void onTeacherEdit(Teacher teacher);

    void onCancel();

    default void close(Stage stage) {
        stage.close();
    }

}
