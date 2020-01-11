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
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import model.Student;
import model.StudentDocument;
import students.detail_view.StudentDetailsController;
import utility.StudentDocumentCloudStorageUtility;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DocumentController implements Initializable, DataChangeListener {

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
        progressIndicator.visibleProperty().bind(dataLoading);
        cloudStorageUtility.getAllDocuments();
        refreshButton.visibleProperty().bind(dataLoading.not());
        documentTable.itemsProperty().bind(cloudStorageUtility.studentDocuments);
    }

    private void refreshData() {

        cloudStorageUtility.getAllDocuments();
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
                public void onDocumentSubmit(String reregistrationNumber, File document) {
                    close(stage);
                    cloudStorageUtility.uploadDocument(reregistrationNumber, document);
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
}
