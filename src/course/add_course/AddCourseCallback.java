package course.add_course;

import javafx.stage.Stage;
import model.Course;

public interface AddCourseCallback {
    void onCourseSubmit(Course course);

    void onCourseUpdate(Course course);

    default void close(Stage stage) {
        stage.close();
    }

    void onCourseDelete(Course course);
}
