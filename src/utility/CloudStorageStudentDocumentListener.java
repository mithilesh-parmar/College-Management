package utility;

import javafx.stage.Stage;

public interface CloudStorageStudentDocumentListener {
    void onFinish();

    void onStart();

    default void close(Stage stage) {
        stage.close();
    }
}
