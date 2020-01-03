package students;

import javafx.scene.input.MouseEvent;
import model.Student;

public interface StudentCardListener {

    void onCardClick(Student student);

    void onDeleteButtonClick(Student student);

    void onEditButtonClick(Student student);

    void onNotificationButtonClick(Student student);

    void onContextMenuRequested(Student student, MouseEvent event);
}
