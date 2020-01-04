package teacher_leaves;

import javafx.scene.input.MouseEvent;
import model.Teacher;
import model.TeacherLeave;

public interface TeacherLeavesCardListener {

    void onContextMenuRequested(TeacherLeave leave, MouseEvent event);


}
