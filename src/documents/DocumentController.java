package documents;

import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.storage.Blob;
import custom_view.SearchTextFieldController;
import documents.add_document.AddDocumentController;
import documents.add_document.AddDocumentListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import listeners.DataChangeListener;
import model.StudentDocument;
import utility.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class DocumentController implements Initializable, DataChangeListener, CloudStorageStudentDocumentListener, SearchCallback {

    public TableView<StudentDocument> documentTable;
    public SearchTextFieldController searchTextField;
    public ProgressIndicator progressIndicator;
    public Button addButton;
    public Button refreshButton;
    private StudentDocumentCloudStorageUtility cloudStorageUtility = StudentDocumentCloudStorageUtility.getInstance();
    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchTextField.setCallback(this);
        addButton.setOnAction(actionEvent -> loadAddView());
        refreshButton.setOnAction(actionEvent -> refreshData());
        cloudStorageUtility.setListener(this);
        cloudStorageUtility.setStudentDocumentListener(this);
        progressIndicator.visibleProperty().bind(dataLoading);
        cloudStorageUtility.getAllDocuments();
        refreshButton.visibleProperty().bind(dataLoading.not());
        documentTable.setItems(cloudStorageUtility.studentDocuments);
        documentTable.setOnKeyPressed(keyEvent -> handleOnTableClick(keyEvent, documentTable.getSelectionModel().getSelectedItem()));

        addButtonToTable();
    }


    private void handleOnTableClick(KeyEvent keyEvent, StudentDocument selectedItem) {

        if (keyEvent.getCode() == KeyCode.ENTER) {
            downloadDocument(selectedItem);
        } else if (keyEvent.getCode() == KeyCode.DELETE) {
            showConfirmationAlert(selectedItem);
        }
    }

//    private void showContextMenu(ContextMenuEvent event, StudentDocument selectedItem) {
//
//        contextMenu.setAutoHide(true);
//        contextMenu.setHideOnEscape(true);
//
//        MenuItem deleteButton = new MenuItem("Delete");
//
//        deleteButton.setOnAction(actionEvent -> showConfirmationAlert(selectedItem));
//
//        MenuItem cancelButton = new MenuItem("Cancel");
//        cancelButton.setOnAction(actionEvent -> contextMenu.hide());
//
//        MenuItem downloadButton = new MenuItem("Download");
//        downloadButton.setOnAction(actionEvent -> downloadDocument(selectedItem));
//
//        contextMenu.getItems().clear();
//        contextMenu.getItems().addAll(downloadButton, deleteButton, cancelButton);
//
//        contextMenu.show(documentTable, event.getScreenX(), event.getScreenY());
//
//
//    }

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
            cloudStorageUtility.deleteDocument(studentDocument, new DocumentUploadListener() {
                @Override
                public void onSuccess(Blob blob) {
                    dataLoading.set(false);
                }

                @Override
                public void onFailure(Exception e) {
                    dataLoading.set(false);
                }

                @Override
                public void onStart() {
                    dataLoading.set(true);
                }
            });
        }


    }

    private void loadAddView() {


        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/documents/add_document/AddDocumentView.fxml"));
        final Stage stage = new Stage();
        stage.setHeight(ScreenUtility.getScreenHeight() * 0.70);
        stage.setWidth(ScreenUtility.getScreenThreeFourthWidth());
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
                    dataLoading.set(true);


                    documents.forEach(file -> cloudStorageUtility.uploadDocument(reregistrationNumber, file, new DocumentUploadListener() {
                        @Override
                        public void onSuccess(Blob blob) {
                            dataLoading.set(false);
                            refreshData();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            dataLoading.set(false);
                            System.out.println(e);
                        }

                        @Override
                        public void onStart() {
                            dataLoading.set(true);
                        }
                    }));
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
        refreshData();
    }

    @Override
    public void onStart() {
        dataLoading.set(true);
    }

    private void addButtonToTable() {
        TableColumn<StudentDocument, Void> colBtn = new TableColumn("Actions");

        Callback<TableColumn<StudentDocument, Void>, TableCell<StudentDocument, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<StudentDocument, Void> call(final TableColumn<StudentDocument, Void> param) {
                final TableCell<StudentDocument, Void> cell = new TableCell<>() {

                    private final HBox hBox = new HBox(15);

                    private final Button downloadButton = new Button("Download");

                    {
                        downloadButton.setOnAction((ActionEvent event) -> {
                            StudentDocument data = getTableView().getItems().get(getIndex());
                            downloadDocument(data);
                        });
                    }

                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setOnAction((ActionEvent event) -> {
                            StudentDocument data = getTableView().getItems().get(getIndex());
                            showConfirmationAlert(data);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            hBox.getChildren().setAll(downloadButton, deleteButton);
                            setGraphic(hBox);
                        }
                    }
                };

                return cell;
            }
        };

        colBtn.setCellFactory(cellFactory);

        documentTable.getColumns().add(colBtn);

    }

    @Override
    public void performSearch(String oldValue, String newValue) {
        if (dataLoading.get()) return;
        // if pressing backspace then set initial values to list
        if (oldValue != null && (newValue.length() < oldValue.length())) {
            documentTable.itemsProperty().set(cloudStorageUtility.studentDocuments);
        }

        // convert the searched text to uppercase
        String searchtext = newValue.toUpperCase();


        ListProperty<StudentDocument> subList = new SimpleListProperty<>(FXCollections.observableArrayList());
        for (StudentDocument p : cloudStorageUtility.studentDocuments) {
            String text =
                    p.getFileName().toUpperCase() + " "
                            + p.getStudentAdmissionNumber().toUpperCase();
            // if the search text contains the manufacturer then add it to sublist
            if (text.contains(searchtext)) {
                subList.get().add(p);
            }

        }
        // set the items to tableView that matches
        documentTable.itemsProperty().setValue(subList);
    }
}
