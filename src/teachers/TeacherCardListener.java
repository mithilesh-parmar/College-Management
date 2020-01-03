package teachers;

import javafx.scene.input.MouseEvent;
import model.Teacher;

public interface TeacherCardListener {

    void onCardClick(Teacher teacher);

    void onDeleteButtonClick(Teacher teacher);

    void onEditButtonClick(Teacher teacher);

    void onNotificationButtonClick(Teacher teacher);

    void onContextMenuRequested(Teacher teacher, MouseEvent event);
}
