package teachers;

import model.Teacher;

public interface TeacherCardListener {

    void onCardClick(Teacher teacher);

    void onDeleteButtonClick(Teacher teacher);

    void onEditButtonClick(Teacher teacher);

    void onNotificationButtonClick(Teacher teacher);
}
