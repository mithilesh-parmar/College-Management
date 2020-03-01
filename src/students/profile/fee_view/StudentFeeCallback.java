package students.profile.fee_view;

import javafx.stage.Stage;
import model.Fee;

public interface StudentFeeCallback {
    void onStudentFeeAdded(Fee fee);

    default void close(Stage stage) {
        stage.close();
    }
}
