package teacher_leaves.edit_view;

import javafx.stage.Stage;
import model.Leave;

public interface LeaveEditListener {

    void onEdit(Leave leave);

    default void close(Stage stage) {
        stage.close();
    }


}
