package teachers;

import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.storage.Blob;
import custom_view.SearchTextFieldController;
import custom_view.card_view.Card;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import custom_view.notification_view.NotificationDialogListener;
import model.Notification;
import model.Teacher;
import teachers.add_teacher.AddTeacherController;
import custom_view.notification_view.NotificationsController;
import utility.DocumentUploadListener;
import utility.ScreenUtility;
import utility.SearchCallback;
import utility.TeacherFirestoreUtility;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TeacherController implements Initializable,
        DataChangeListener,
        SearchCallback,
        DocumentUploadListener,
        TeacherCardListener {


    public ProgressIndicator progressIndicator;
    public SearchTextFieldController searchField;
    public FlowPane teacherFlowPane;
    public ScrollPane scroll;

    private ContextMenu tableContextMenu = new ContextMenu();
    private MenuItem pushNotificationMenuButton = new MenuItem("Push Notification");
    private MenuItem deleteMenuButton = new MenuItem("Delete");
    private MenuItem editMenuButton = new MenuItem("Edit");
    private MenuItem cancelMenuButton = new MenuItem("Cancel");


    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);
    private TeacherFirestoreUtility firestoreUtility = TeacherFirestoreUtility.getInstance();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        teacherFlowPane.setHgap(10);
        teacherFlowPane.setVgap(10);
        teacherFlowPane.setAlignment(Pos.TOP_LEFT);
        tableContextMenu.setHideOnEscape(true);
        tableContextMenu.setAutoHide(true);
        teacherFlowPane.setPadding(new Insets(10));

        scroll.setContent(teacherFlowPane);
        scroll.viewportBoundsProperty().addListener((ov, oldBounds, bounds) -> {
            teacherFlowPane.setPrefWidth(bounds.getWidth());
            teacherFlowPane.setPrefHeight(bounds.getHeight());
        });

        teacherFlowPane.getChildren().addAll(firestoreUtility.teacherCards);

        teacherFlowPane.setOnMouseClicked(mouseEvent -> {
            if (tableContextMenu.isShowing()) tableContextMenu.hide();
        });

        tableContextMenu.getItems().addAll(
                pushNotificationMenuButton,
                editMenuButton,
                deleteMenuButton,
                cancelMenuButton
        );

        searchField.setCallback(this);
        firestoreUtility.setListener(this);
        firestoreUtility.setDocumentUploadListener(this);
        firestoreUtility.setCardListener(this);
        firestoreUtility.getTeachers();


        progressIndicator.visibleProperty().bind(dataLoading);
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
        teacherFlowPane.getChildren().setAll(firestoreUtility.teacherCards);
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

    @Override
    public void onStart() {

    }

    private void loadNotificationsView(Teacher teacher) {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/custom_view/notification_view/NotificationsView.fxml"));
        final Stage stage = new Stage();
        stage.setHeight(ScreenUtility.getScreenHeight() * 0.70);
        stage.setWidth(ScreenUtility.getScreenThreeFourthWidth());
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
        stage.setHeight(ScreenUtility.getScreenHeight() * 0.70);
        stage.setWidth(ScreenUtility.getScreenThreeFourthWidth());
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Teacher Details");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            AddTeacherController controller = loader.getController();

            if (teacher != null) controller.setTeacher(teacher);

            controller.setListener(new TeacherDetailViewListener() {
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

            teacherFlowPane.getChildren().setAll(firestoreUtility.teacherCards);
        }

        // convert the searched text to uppercase
        String searchtext = newValue.toUpperCase();

        ObservableList<Card> subList = FXCollections.observableArrayList();
        for (Teacher p : firestoreUtility.teachers) {
            StringBuilder text = new StringBuilder();
            text.append((p.getName() == null || !p.getName().isEmpty()) ? "" : p.getName().toUpperCase())
                    .append((p.getEmail() == null || !p.getEmail().isEmpty()) ? "" : p.getEmail().toUpperCase());
            // if the search text contains the manufacturer then add it to sublist
            if (text.toString().contains(searchtext)) {
                subList.add(firestoreUtility.teacherCardMapProperty.get(p));
            }

        }
        teacherFlowPane.getChildren().setAll(subList);
    }

    @Override
    public void onCardClick(Teacher teacher) {
        loadAddView(teacher);
    }

    @Override
    public void onDeleteButtonClick(Teacher teacher) {
        showConfirmationAlert(teacher);
    }

    @Override
    public void onEditButtonClick(Teacher teacher) {
        loadAddView(teacher);
    }

    @Override
    public void onNotificationButtonClick(Teacher teacher) {
        loadNotificationsView(teacher);
    }

    @Override
    public void onContextMenuRequested(Teacher teacher, MouseEvent event) {

        System.out.println("event: " + event);
        pushNotificationMenuButton.setOnAction(actionEvent -> loadNotificationsView(teacher));
        editMenuButton.setOnAction(actionEvent -> loadAddView(teacher));
        cancelMenuButton.setOnAction(actionEvent -> tableContextMenu.hide());
        deleteMenuButton.setOnAction(actionEvent -> showConfirmationAlert(teacher));

        tableContextMenu.show(teacherFlowPane, event.getScreenX(), event.getScreenY());
        tableContextMenu.requestFocus();
    }


}
