package students.profile;

import javafx.stage.Stage;
import model.Fee;
import students.profile.detail_view.StudentListener;
import students.profile.fee_view.StudentFeeCallback;

public interface StudentProfileCallback extends StudentListener, StudentFeeCallback {


    default void close(Stage stage) {
        stage.close();
    }
}
