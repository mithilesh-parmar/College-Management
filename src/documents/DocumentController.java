package documents;

import com.google.cloud.firestore.QuerySnapshot;
import custom_view.SearchTextFieldController;
import documents.add_document.AddDocumentController;
import documents.add_document.AddDocumentListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import model.Student;
import model.StudentDocument;
import students.detail_view.StudentDetailsController;
import utility.CloudStorageStudentDocumentListener;
import utility.StudentDocumentCloudStorageUtility;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DocumentController implements Initializable, DataChangeListener, CloudStorageStudentDocumentListener {

    public TableView<StudentDocument> documentTable;
    public SearchTextFieldController searchTextField;
    public ProgressIndicator progressIndicator;
    public Button addButton;
    public Button refreshButton;
    private StudentDocumentCloudStorageUtility cloudStorageUtility = StudentDocumentCloudStorageUtility.getInstance();
    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        addButton.setPadding(new Insets(14));
        addButton.setOnAction(actionEvent -> loadAddView());
        refreshButton.setOnAction(actionEvent -> refreshData());
        cloudStorageUtility.setListener(this);
        cloudStorageUtility.setStudentDocumentListener(this);
        progressIndicator.visibleProperty().bind(dataLoading);
        cloudStorageUtility.getAllDocuments();
        refreshButton.visibleProperty().bind(dataLoading.not());
        documentTable.itemsProperty().bind(cloudStorageUtility.studentDocuments);
        documentTable.setOnKeyPressed(keyEvent -> handleOnTableClick(keyEvent, documentTable.getSelectionModel().getSelectedItem()));
        documentTable.setOnContextMenuRequested(event -> {
            showContextMenu(event, documentTable.getSelectionModel().getSelectedItem());
        });
    }

    private void handleOnTableClick(KeyEvent keyEvent, StudentDocument selectedItem) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            downloadDocument(selectedItem);
        } else if (keyEvent.getCode() == KeyCode.DELETE) {
            showConfirmationAlert(selectedItem);
        }
    }

    private void showContextMenu(ContextMenuEvent event, StudentDocument selectedItem) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setAutoHide(true);
        contextMenu.setHideOnEscape(true);

        MenuItem deleteButton = new MenuItem("Delete");
        deleteButton.setOnAction(actionEvent -> showConfirmationAlert(selectedItem));

        MenuItem cancelButton = new MenuItem("Cancel");
        cancelButton.setOnAction(actionEvent -> contextMenu.hide());

        MenuItem downloadButton = new MenuItem("Download");
        downloadButton.setOnAction(actionEvent -> downloadDocument(selectedItem));

        contextMenu.getItems().addAll(downloadButton, deleteButton, cancelButton);

        contextMenu.show(documentTable, event.getScreenX(), event.getScreenY());


    }

    private void downloadDocument(StudentDocument studentDocument) {
        cloudStorageUtility.downloadDocument(studentDocument);
    }


    private void refreshData() {
        cloudStorageUtility.getAllDocuments();
    }


    private void showConfirmationAlert(StudentDocument studentDocument) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Delete User");
        alert.setHeaderText(
                "Are you sure that you want to delete\n Name: "
                        + studentDocument.getFileName()
                        + " registered to: "
                        + studentDocument.getStudentAdmissionNumber()
                        + " ?"
        );

        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            cloudStorageUtility.deleteDocument(studentDocument);
        }


    }

    private void loadAddView() {


        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/documents/add_document/AddDocumentView.fxml"));
        final Stage stage = new Stage();
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Document");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            AddDocumentController controller = loader.getController();

            controller.setListener(new AddDocumentListener() {
                @Override
                public void onDocumentSubmit(String reregistrationNumber, List<File> documents) {
                    documents.forEach(file -> cloudStorageUtility.uploadDocument(reregistrationNumber, file));
                    close(stage);
                }
            });

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDataLoaded(ObservableList data) {
        dataLoading.set(false);
    }

    @Override
    public void onDataChange(QuerySnapshot data) {
        dataLoading.set(true);
    }

    @Override
    public void onError(Exception e) {
        dataLoading.set(false);
    }

    @Override
    public void onFinish() {
        dataLoading.set(false);
    }

    @Override
    public void onStart() {
        dataLoading.set(true);
    }
}
