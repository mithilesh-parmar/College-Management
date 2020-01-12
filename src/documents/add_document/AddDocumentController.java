package documents.add_document;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AddDocumentController implements Initializable {
    public TextField regNumberTextField;
    public Button fileChooserButton;
    public Button submitButton;

    private AddDocumentListener listener;
    private ObjectProperty<List<File>> selectedDocument = new SimpleObjectProperty<>(FXCollections.observableArrayList());
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

        List<File> files = fileChooser.showOpenMultipleDialog(fileChooserButton.getScene().getWindow());
        if (files != null && files.size() > 0) {
            selectedDocument.set(files);
            documentName.set(files.get(0).getName() + " and " + files.size() + " selected");
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

