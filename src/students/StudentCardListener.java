package students;

import model.Student;
import model.Teacher;

public interface StudentCardListener {

    void onCardClick(Student student);

    void onDeleteButtonClick(Student student);

    void onEditButtonClick(Student student);

    void onNotificationButtonClick(Student student);
}
