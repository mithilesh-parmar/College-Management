package documents.add_document;

import javafx.beans.property.*;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AddDocumentController implements Initializable {
    public TextField regNumberTextField;
    public Button fileChooserButton;
    public Button submitButton;

    private AddDocumentListener listener;
    private ObjectProperty<File> selectedDocument = new SimpleObjectProperty<>();
    private BooleanProperty canSubmit = new SimpleBooleanProperty(false);

    private StringProperty documentName = new SimpleStringProperty("Choose Document");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        fileChooserButton.setOnAction(actionEvent -> handleOnFileChooserAction());
        fileChooserButton.textProperty().bind(documentName);
        submitButton.setOnAction(actionEvent -> handleOnSubmitAction());
        submitButton.visibleProperty().bind(canSubmit);

    }

    public void setListener(AddDocumentListener listener) {
        this.listener = listener;
    }

    private void handleOnSubmitAction() {
        if (!canSubmit.get() || listener == null) return;
        listener.onDocumentSubmit(regNumberTextField.getText(), selectedDocument.get());
    }

    private void handleOnFileChooserAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Document");
        File file = fileChooser.showOpenDialog(fileChooserButton.getScene().getWindow());
        if (file != null) {
            selectedDocument.set(file);
            documentName.set(file.getName());
        }

        checkCanSubmit();
    }

    private void checkCanSubmit() {
        canSubmit.set(
                !regNumberTextField.getText().isEmpty()
                        &&
                        selectedDocument.get() != null
        );
    }
}

