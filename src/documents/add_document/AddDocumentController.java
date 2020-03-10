package documents.add_document;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import model.Student;
import org.apache.poi.ss.formula.ptg.StringPtg;
import utility.ScreenUtility;
import utility.StudentVerificationUtility;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class AddDocumentController implements Initializable {
    public TextField regNumberTextField;
    public Button fileChooserButton;
    public Button submitButton;
    public ProgressIndicator progressIndicator;

    private AddDocumentListener listener;
    private ObjectProperty<List<File>> selectedDocument = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private BooleanProperty canSubmit = new SimpleBooleanProperty(false);
    private StringProperty selectedRegNumber = new SimpleStringProperty();
    private StringProperty documentName = new SimpleStringProperty("Choose Document");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        progressIndicator.setVisible(false);
        regNumberTextField.textProperty().addListener((observableValue, s, t1) -> {
            if (t1 == null) return;
            selectedRegNumber.set(t1);
            checkCanSubmit();
        });
        fileChooserButton.setOnAction(actionEvent -> handleOnFileChooserAction());
        fileChooserButton.textProperty().bind(documentName);
        submitButton.setOnAction(actionEvent -> handleOnSubmitAction());
        fileChooserButton.setMaxWidth(ScreenUtility.getScreenHalfWidth());
        fileChooserButton.setMaxHeight(150);
        submitButton.visibleProperty().bind(canSubmit);

    }


    public void setListener(AddDocumentListener listener) {
        this.listener = listener;
    }

    private void handleOnSubmitAction() {
        if (!canSubmit.get() || listener == null) return;
        progressIndicator.setVisible(true);

        try {
            StudentVerificationUtility.exist(selectedRegNumber.get(), new StudentVerificationUtility.Callback() {
                @Override
                public void onSuccess(Student student) {
//                    reg exist so can upload document
                    progressIndicator.setVisible(false);
                    listener.onDocumentSubmit(regNumberTextField.getText(), selectedDocument.get());
                }

                @Override
                public void onFailure() {
//                    this reg does not exist so cannot upload document
                    progressIndicator.setVisible(false);
//                    show alert
                    Alert alert = new Alert(Alert.AlertType.WARNING);

                    alert.setTitle("Alert");
                    alert.setHeaderText(
                            "No Student Found"
                    );

                    alert.showAndWait();
                }

                @Override
                public void onStart() {
                    progressIndicator.setVisible(true);
                }
            });
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


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
                selectedRegNumber.get() != null
                        && !selectedRegNumber.get().isEmpty()
                        && selectedDocument.get() != null
        );
    }
}

