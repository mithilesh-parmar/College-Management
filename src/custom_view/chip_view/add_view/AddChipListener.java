package custom_view.chip_view.add_view;

import custom_view.chip_view.Chip;
import javafx.stage.Stage;

public interface AddChipListener {
    void onChipAdded(Chip chip);

    default void close(Stage stage) {
        stage.close();
    }
}
