package documents.add_document;

import javafx.stage.Stage;

import java.io.File;

public interface AddDocumentListener {
    void onDocumentSubmit(String reregistrationNumber, File document);
    default void close(Stage stage){
        stage.close();
    }
}
