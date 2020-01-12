package documents.add_document;

import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public interface AddDocumentListener {
    void onDocumentSubmit(String reregistrationNumber, List<File> documents);

    default void close(Stage stage) {
        stage.close();
    }
}
