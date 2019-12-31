package teachers;

import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.storage.Blob;
import custom_view.AlertWindow;
import custom_view.SearchTextFieldController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import model.Teacher;
import utility.DocumentUploadListener;
import utility.SearchCallback;
import utility.TeacherFirestoreUtility;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TeacherController implements Initializable, DataChangeListener, SearchCallback, DocumentUploadListener {


    public TableView<Teacher> teacherTable;
    public ProgressIndicator progressIndicator;
    public SearchTextFieldController searchField;
//    public Button addButton;

    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);
    private TeacherFirestoreUtility firestoreUtility = TeacherFirestoreUtility.getInstance();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchField.setCallback(this);
        firestoreUtility.setListener(this);
        firestoreUtility.setDocumentUploadListener(this);
        firestoreUtility.getTeachers();

//        addButton.setOnAction(actionEvent -> loadAddView(null));

        progressIndicator.visibleProperty().bind(dataLoading);

        teacherTable.setOnKeyPressed(event -> handleOnKeyPressed(event));

        teacherTable.setOnMouseClicked(event -> handleOnMouseClicked(event));
    }


    private void handleOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            loadAddView(teacherTable.getSelectionModel().getSelectedItem());
        } else if (keyEvent.getCode().equals(KeyCode.BACK_SPACE)) {
            showConfirmationAlert(teacherTable.getSelectionModel().getSelectedItem());
        }
    }

    private void handleOnMouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            System.out.println("Clicked ");
        }
    }

    private void showConfirmationAlert(Teacher teacher) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Delete User");
        alert.setHeaderText("Are you sure that you want to delete\n Name: " + teacher.getName() + " with email: " + teacher.getEmail() + " ?");

        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            firestoreUtility.deleteTeacher(teacher);
        }


    }


    @Override
    public void onDataLoaded(ObservableList data) {
        dataLoading.set(false);
        teacherTable.setItems(firestoreUtility.teachers);
    }

    @Override
    public void onDataChange(QuerySnapshot data) {
        dataLoading.set(true);
    }

    @Override
    public void onError(Exception e) {

    }


    @Override
    public void onSuccess(Blob blob) {
        dataLoading.set(false);
    }

    @Override
    public void onFailure(Exception e) {
        dataLoading.set(false);
    }

    private void loadAddView(Teacher teacher) {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/teachers/AddTeacherView.fxml"));
        final Stage stage = new Stage();
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Teacher Details");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            AddTeacherController controller = loader.getController();

            if (teacher != null) controller.setTeacher(teacher);

            controller.setListener(new TeacherListener() {
                @Override
                public void onTeacherSubmit(Teacher teacher, File profileImage) {
                    close(stage);
                    dataLoading.set(true);
                    Platform.runLater(() -> {

                        firestoreUtility.updateTeacherDetails(teacher, profileImage);

                    });
                }

                @Override
                public void onTeacherEdit(Teacher teacher) {
                    close(stage);
                }

                @Override
                public void onCancel() {
                    close(stage);
                }
            });

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void performSearch(String oldValue, String newValue) {
        if (dataLoading.get()) return;
        // if pressing backspace then set initial values to list
        if (oldValue != null && (newValue.length() < oldValue.length())) {
            teacherTable.setItems(firestoreUtility.teachers);
        }

        // convert the searched text to uppercase
        String searchtext = newValue.toUpperCase();

        ObservableList<Teacher> subList = FXCollections.observableArrayList();
        for (Teacher p : teacherTable.getItems()) {
            String text =
                    p.getName().toUpperCase() + " "
                            + p.getVerificationCode().toUpperCase() + " ";
            // if the search text contains the manufacturer then add it to sublist
            if (text.contains(searchtext)) {
                subList.add(p);
            }

        }
        // set the items to listview that matches
        teacherTable.setItems(subList);
    }
}
