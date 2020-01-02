package students;

import com.google.cloud.firestore.*;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import custom_view.notification_view.NotificationDialogListener;
import model.Notification;
import model.Student;
import custom_view.notification_view.NotificationsController;
import students.attendance.AttendanceController;
import students.detail_view.StudentDetailsController;
import utility.SearchCallback;
import utility.StudentFirestoreUtility;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StudentController implements Initializable, DataChangeListener, SearchCallback {


    public TableView<Student> studentTable;
    public ProgressIndicator progressIndicator;
    public SearchTextFieldController searchTextField;
    public FlowPane studentFlowPane;
    private StudentFirestoreUtility firestoreUtility = StudentFirestoreUtility.getInstance();
    private BooleanProperty loadingData = new SimpleBooleanProperty(true);
    private ContextMenu tableContextMenu = new ContextMenu();
    private MenuItem attendanceMenuButton = new MenuItem("Attendance");
    private MenuItem feesMenuButton = new MenuItem("Fees");
    private MenuItem feesNotificationMenuButton = new MenuItem("Fees Notification");
    private MenuItem pushNotificationMenuButton = new MenuItem("Push Notification");
    //    private MenuItem notificationsMenuButton = new MenuItem("Notifications");
    private MenuItem deleteMenuButton = new MenuItem("Delete");
    private MenuItem editMenuButton = new MenuItem("Edit");
    private MenuItem cancelMenuButton = new MenuItem("Cancel");
    private ObservableList<Card> cardControllers = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        Menu menu = new Menu("Notifications");


        studentFlowPane.setHgap(10);
        studentFlowPane.setVgap(10);
        studentFlowPane.setAlignment(Pos.CENTER_LEFT);


        studentFlowPane.setPadding(new Insets(10));


        studentFlowPane.getChildren().addAll(cardControllers);

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

        cancelMenuButton.setOnAction(actionEvent -> tableContextMenu.hide());
        attendanceMenuButton.setOnAction(actionEvent -> loadAttendanceView(studentTable.getSelectionModel().getSelectedItem()));
        feesNotificationMenuButton.setOnAction(actionEvent -> loadFeeNotificationsView(studentTable.getSelectionModel().getSelectedItem()));
        pushNotificationMenuButton.setOnAction(actionEvent -> loadNotificationsView(studentTable.getSelectionModel().getSelectedItem()));
        cancelMenuButton.setOnAction(actionEvent -> tableContextMenu.hide());
        deleteMenuButton.setOnAction(actionEvent -> showConfirmationAlert(studentTable.getSelectionModel().getSelectedItem()));

        feesMenuButton.setOnAction(actionEvent -> {
        });
        editMenuButton.setOnAction(actionEvent -> loadEditView(studentTable.getSelectionModel().getSelectedItem()));

        firestoreUtility.setListener(this);
        firestoreUtility.getStudents();
        searchTextField.setCallback(this);
        progressIndicator.visibleProperty().bind(loadingData);

        studentTable.setOnMouseClicked(this::handleOnMouseClicked);
        studentTable.setOnKeyPressed(this::handleOnKeyPressed);
        studentTable.setOnContextMenuRequested(event -> tableContextMenu.show(studentTable, event.getScreenX(), event.getScreenY()));
    }

    private void handleOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {


        } else if (keyEvent.getCode().equals(KeyCode.BACK_SPACE)) {
            showConfirmationAlert(studentTable.getSelectionModel().getSelectedItem());
        }
    }

    private void handleOnMouseClicked(MouseEvent event) {
        if (tableContextMenu.isShowing() && !tableContextMenu.isFocused()) tableContextMenu.hide();
        if (event.getClickCount() == 2) {


        }
    }

    @Override
    public void onDataLoaded(ObservableList data) {
        loadingData.set(false);
        studentTable.setItems(firestoreUtility.students);

        for (Student student : firestoreUtility.students) {

            cardControllers.add(new Card(student.getEmail(), student.getName(), student.getProfilePictureURL()));

        }

        studentFlowPane.getChildren().addAll(cardControllers);
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
        loader = new FXMLLoader(getClass().getResource("/students/detail_view/StudentDetailView.fxml"));
        final Stage stage = new Stage();
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Student Details");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            StudentDetailsController controller = loader.getController();

            if (student != null) controller.setStudent(student);


//
//            controller.setListener(new TeacherListener() {
//                @Override
//                public void onTeacherSubmit(Teacher teacher, File profileImage) {
//                    close(stage);
//                    dataLoading.set(true);
//                    Platform.runLater(() -> firestoreUtility.updateTeacherDetails(teacher, profileImage));
//                }
//
//                @Override
//                public void onTeacherEdit(Teacher teacher) {
//                    close(stage);
//                }
//
//                @Override
//                public void onCancel() {
//                    close(stage);
//                }
//            });

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadAttendanceView(Student student) {
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/students/attendance/AttendanceView.fxml"));
        final Stage stage = new Stage();
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Attendance");

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
            studentTable.setItems(firestoreUtility.students);
        }

        // convert the searched text to uppercase
        String searchtext = newValue.toUpperCase();

        ObservableList<Student> subList = FXCollections.observableArrayList();
        for (Student p : studentTable.getItems()) {
            String text =
                    p.getName().toUpperCase() + " "
                            + p.getClassName().toUpperCase() + " "
                            + p.getAddress().toUpperCase() + " "
                            + p.getEmail().toUpperCase() + " "
                            + p.getParentNumber().toUpperCase() + " "
                            + p.getSection().toUpperCase() + " ";
            // if the search text contains the manufacturer then add it to sublist
            if (text.contains(searchtext)) {
                subList.add(p);
            }

        }
        // set the items to listview that matches
        studentTable.setItems(subList);
    }
}


