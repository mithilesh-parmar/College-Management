package students;

import com.google.cloud.firestore.*;
import com.google.cloud.storage.Blob;
import custom_view.SearchTextFieldController;
import custom_view.fees_notification_view.FeesNotificationController;
import custom_view.card_view.Card;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import model.Fee;
import model.Notification;
import model.Student;
import custom_view.notification_view.NotificationsController;
import students.profile.StudentProfileCallback;
import students.profile.attendance.AttendanceController;
import students.profile.fee_view.StudentFeeController;
import students.profile.StudentProfile;
import utility.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StudentController implements Initializable, DataChangeListener, SearchCallback, StudentCardListener, DocumentUploadListener {


    public ProgressIndicator progressIndicator;
    public SearchTextFieldController searchTextField;
    public FlowPane studentFlowPane;
    public ScrollPane scroll;
    public Button addStudent;

    private StudentFirestoreUtility firestoreUtility = StudentFirestoreUtility.getInstance();
    private FeeFirestoreUtility feeFirestoreUtility = FeeFirestoreUtility.getInstance();
    private BooleanProperty loadingData = new SimpleBooleanProperty(true);
    private BooleanProperty loadingFeeData = new SimpleBooleanProperty(false);

    private ContextMenu tableContextMenu = new ContextMenu();
    private MenuItem attendanceMenuButton = new MenuItem("Attendance");
    private MenuItem feesMenuButton = new MenuItem("Fees");
    private MenuItem feesNotificationMenuButton = new MenuItem("Fees Notification");
    private MenuItem pushNotificationMenuButton = new MenuItem("Push Notification");
    private MenuItem deleteMenuButton = new MenuItem("Delete");
    private MenuItem editMenuButton = new MenuItem("Edit");
    private MenuItem cancelMenuButton = new MenuItem("Cancel");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        Menu menu = new Menu("Notifications");

        addStudent.setOnAction(actionEvent -> loadEditView(null));

        scroll.setContent(studentFlowPane);
        scroll.viewportBoundsProperty().addListener((ov, oldBounds, bounds) -> {
            studentFlowPane.setPrefWidth(bounds.getWidth());
            studentFlowPane.setPrefHeight(bounds.getHeight());
        });
        studentFlowPane.setHgap(10);
        studentFlowPane.setVgap(10);
        studentFlowPane.setAlignment(Pos.TOP_LEFT);

        studentFlowPane.setOnMouseClicked(mouseEvent -> {
            if (tableContextMenu.isShowing()) {
                tableContextMenu.hide();
            }
        });

        studentFlowPane.setPadding(new Insets(10));


        studentFlowPane.getChildren().addAll(firestoreUtility.studentCards);

        menu.getItems().addAll(feesNotificationMenuButton, pushNotificationMenuButton);


        tableContextMenu.getItems().addAll(
                attendanceMenuButton,
                menu,
                feesMenuButton,
                deleteMenuButton,
                editMenuButton,
                cancelMenuButton
        );


        tableContextMenu.setHideOnEscape(true);
        tableContextMenu.setAutoHide(true);

        feeFirestoreUtility.setListener(new DataChangeListener() {
            @Override
            public void onDataLoaded(ObservableList data) {
                loadingFeeData.set(false);
            }

            @Override
            public void onDataChange(QuerySnapshot data) {
                loadingFeeData.set(true);
            }

            @Override
            public void onError(Exception e) {
                loadingFeeData.set(false);
            }
        });

        firestoreUtility.setListener(this);
        firestoreUtility.setCardListener(this);
        firestoreUtility.setDocumentUploadListener(this);
        firestoreUtility.getStudents();
        searchTextField.setCallback(this);
        progressIndicator.visibleProperty().bind(loadingData.or(loadingFeeData));

    }

    private void loadFeeView(Student student) {

        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/students/profile/fee_view/StudentFeeView.fxml"));
        final Stage stage = new Stage();
        Parent parent = null;
        try {

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Student Details");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            StudentFeeController controller = loader.getController();
            if (student != null)
                controller.setStudent(student);
            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDataLoaded(ObservableList data) {
        loadingData.set(false);
        studentFlowPane.getChildren().setAll(firestoreUtility.studentCards);
    }


    @Override
    public void onDataChange(QuerySnapshot data) {
        loadingData.set(true);
    }

    @Override
    public void onError(Exception e) {

        System.out.println(e);
    }

    private void loadEditView(Student student) {

        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/students/profile/StudentProfile.fxml"));
        final Stage stage = new Stage();
        stage.setHeight(ScreenUtility.getScreenHeight() * 0.70);
        stage.setWidth(ScreenUtility.getScreenThreeFourthWidth());
        Parent parent = null;
        try {

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Student Details");
            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            StudentProfile controller = loader.getController();
            controller.setStudent(student);
            controller.setCallback(new StudentProfileCallback() {
                @Override
                public void onStudentSubmit(Student student, File profileImage) {
                    loadingData.set(true);
                    close(stage);
                    firestoreUtility.addStudent(student, profileImage);
                }

                @Override
                public void onStudentEdit(Student student, File profileImage) {
                    loadingData.set(true);
                    close(stage);
                    firestoreUtility.updateStudent(student, profileImage);
                }

                @Override
                public void onStudentFeeAdded(Fee fee) {
                    loadingFeeData.set(true);
                    close(stage);
                    feeFirestoreUtility.addFee(fee);
                }
            });
            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void loadAttendanceView(Student student) {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/students/profile/attendance/AttendanceView.fxml"));
        final Stage stage = new Stage();
        stage.setHeight(ScreenUtility.getScreenHeight() * 0.70);
        stage.setWidth(ScreenUtility.getScreenThreeFourthWidth());
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("attendance");

            parent = loader.load();
            Scene scene = new Scene(parent, 500, 600);
            stage.setScene(scene);
            AttendanceController controller = loader.getController();

            controller.setStudent(student);

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadFeeNotificationsView(Student student) {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/custom_view/fees_notification_view/FeesNotificationView.fxml"));
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
            FeesNotificationController controller = loader.getController();

            controller.setListener(new NotificationDialogListener() {
                @Override
                public void sendNotification(Notification notification) {
                    close(stage);
                    firestoreUtility.publishFeeNotification(student, notification);

                }
            });


            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadNotificationsView(Student student) {
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
                    firestoreUtility.publishNotification(student, notification);
                }
            });


            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void showConfirmationAlert(Student student) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Delete User");
        alert.setHeaderText("Are you sure that you want to delete\n Name: " + student.getName() + " with email: " + student.getEmail() + " ?");

        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            firestoreUtility.deleteStudent(student);
        }


    }

    @Override
    public void performSearch(String oldValue, String newValue) {
        if (loadingData.get()) return;
        // if pressing backspace then set initial values to list
        if (oldValue != null && (newValue.length() < oldValue.length())) {
            studentFlowPane.getChildren().setAll(firestoreUtility.studentCards);
        }

        // convert the searched text to uppercase
        String searchtext = newValue.toUpperCase();

        ObservableList<Card> subList = FXCollections.observableArrayList();
        for (Student p : firestoreUtility.students) {
            String text =
                    p.getName().toUpperCase() + " "
                            + p.getClassName().toUpperCase() + " "
                            + p.getAddress().toUpperCase() + " "
                            + p.getEmail().toUpperCase() + " "
                            + p.getParentNumber().toUpperCase() + " "
                            + p.getSection().toUpperCase() + " ";
            // if the search text contains the manufacturer then add it to sublist
            if (text.contains(searchtext)) {
                subList.add(firestoreUtility.studentCardMapProperty.get(p));
            }

        }
        // set the items to listview that matches
        studentFlowPane.getChildren().setAll(subList);
    }

    @Override
    public void onCardClick(Student student) {
        loadEditView(student);
//        loadEditView();
    }

    @Override
    public void onDeleteButtonClick(Student student) {
        showConfirmationAlert(student);
    }

    @Override
    public void onEditButtonClick(Student student) {
        loadEditView(student);
    }

    @Override
    public void onNotificationButtonClick(Student student) {
        loadNotificationsView(student);
    }

    @Override
    public void onContextMenuRequested(Student student, MouseEvent event) {

        attendanceMenuButton.setOnAction(actionEvent -> loadAttendanceView(student));
        feesMenuButton.setOnAction(actionEvent -> loadFeeView(student));
        feesNotificationMenuButton.setOnAction(actionEvent -> loadFeeNotificationsView(student));
        pushNotificationMenuButton.setOnAction(actionEvent -> loadNotificationsView(student));
        deleteMenuButton.setOnAction(actionEvent -> showConfirmationAlert(student));
        editMenuButton.setOnAction(actionEvent -> loadEditView(student));
        cancelMenuButton.setOnAction(action -> tableContextMenu.hide());
        tableContextMenu.show(studentFlowPane, event.getScreenX(), event.getScreenY());
    }

    @Override
    public void onSuccess(Blob blob) {
        loadingData.set(false);
    }

    @Override
    public void onFailure(Exception e) {
        loadingData.set(false);
    }


}


