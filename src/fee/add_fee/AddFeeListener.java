package fee.add_fee;

import javafx.stage.Stage;
import model.Fee;

public interface AddFeeListener {
    void onFeeSubmit(Fee fee);

    default void close(Stage stage) {
        stage.close();
    }
}
