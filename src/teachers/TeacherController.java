package teachers;

import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.storage.Blob;
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
import custom_view.notification_view.NotificationDialogListener;
import model.Notification;
import model.Teacher;
import teachers.add_teacher.AddTeacherController;
import custom_view.notification_view.NotificationsController;
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
    private ContextMenu tableContextMenu = new ContextMenu();
    private MenuItem notificationsMenuButton = new MenuItem("Notifications");
    private MenuItem deleteMenuButton = new MenuItem("Delete");
    private MenuItem editMenuButton = new MenuItem("Edit");
    private MenuItem cancelMenuButton = new MenuItem("Cancel");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        tableContextMenu.getItems().addAll(notificationsMenuButton, deleteMenuButton, editMenuButton, cancelMenuButton);

        tableContextMenu.setHideOnEscape(true);
        tableContextMenu.setAutoHide(true);


        cancelMenuButton.setOnAction(actionEvent -> tableContextMenu.hide());
        notificationsMenuButton.setOnAction(actionEvent -> {
            loadNotificationsView(teacherTable.getSelectionModel().getSelectedItem());
        });

        deleteMenuButton.setOnAction(actionEvent -> {
            showConfirmationAlert(teacherTable.getSelectionModel().getSelectedItem());
        });

        editMenuButton.setOnAction(actionEvent -> {
            loadAddView(teacherTable.getSelectionModel().getSelectedItem());
        });


        searchField.setCallback(this);
        firestoreUtility.setListener(this);
        firestoreUtility.setDocumentUploadListener(this);
        firestoreUtility.getTeachers();


        progressIndicator.visibleProperty().bind(dataLoading);

        teacherTable.setOnKeyPressed(this::handleOnKeyPressed);

        teacherTable.setOnMouseClicked(this::handleOnMouseClicked);
        teacherTable.setOnContextMenuRequested(event -> {
            tableContextMenu.show(teacherTable, event.getScreenX(), event.getScreenY());
        });
    }


    private void handleOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            loadAddView(teacherTable.getSelectionModel().getSelectedItem());
        } else if (keyEvent.getCode().equals(KeyCode.BACK_SPACE)) {
            showConfirmationAlert(teacherTable.getSelectionModel().getSelectedItem());
        }
    }

    private void handleOnMouseClicked(MouseEvent event) {
        if (tableContextMenu.isShowing() && !tableContextMenu.isFocused()) tableContextMenu.hide();
        if (event.getClickCount() == 2) {
            loadAddView(teacherTable.getSelectionModel().getSelectedItem());

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

    private void loadNotificationsView(Teacher teacher) {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/custom_view/notification_view/NotificationsView.fxml"));
        final Stage stage = new Stage();
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Notifications");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            NotificationsController controller = loader.getController();

            controller.setListener(new NotificationDialogListener() {
                @Override
                public void sendNotification(Notification notification) {
                    close(stage);
                    firestoreUtility.publishNotification(teacher, notification);
                }
            });


            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadAddView(Teacher teacher) {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/teachers/add_teacher/AddTeacherView.fxml"));
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
                    Platform.runLater(() -> firestoreUtility.updateTeacherDetails(teacher, profileImage));
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
