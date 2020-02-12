package backlog;

import com.google.cloud.firestore.QuerySnapshot;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import listeners.DataChangeListener;
import model.BackLog;
import utility.BackLogFirestoreUtility;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class BackLogController implements Initializable, DataChangeListener {

    public ProgressIndicator progressIndicator;
    public TableView<BackLog> backLogTable;
    public Button studentBackLogButton;
    public Button subjectBackLogButton;
    public Button sectionBackLogButton;
    public Button examBackLogButton;
    public Button clearButton;

    private BackLogFirestoreUtility firestoreUtility = BackLogFirestoreUtility.getInstance();
    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        Set action listeners
        studentBackLogButton.setOnAction(actionEvent -> showStudentBackLogs());
        subjectBackLogButton.setOnAction(actionEvent -> showSubjectBackLogs());
        sectionBackLogButton.setOnAction(actionEvent -> showSectionBackLogs());
        examBackLogButton.setOnAction(actionEvent -> showExamBackLogs());

        clearButton.setOnAction(actionEvent -> backLogTable.setItems(firestoreUtility.backLogs));

        backLogTable.setItems(firestoreUtility.backLogs);
        backLogTable.setOnKeyPressed(keyEvent -> handleOnTableClick(keyEvent, backLogTable.getSelectionModel().getSelectedItem()));
        backLogTable.setOnContextMenuRequested(event -> showContextMenu(event, backLogTable.getSelectionModel().getSelectedItem()));
        progressIndicator.visibleProperty().bind(dataLoading);
        firestoreUtility.setListener(this);
        firestoreUtility.getBackLogs();

    }

    private void showExamBackLogs() {
        Optional<String> result = showInputDialog("Exam Details", "Exam Id");
        result.ifPresent(s -> backLogTable.setItems(FXCollections.observableArrayList(firestoreUtility.getBackLogsForExam(s))));
    }

    private void showSectionBackLogs() {
        Optional<String> result = showInputDialog("Section Details", "Section Id");
        result.ifPresent(s -> backLogTable.setItems(FXCollections.observableArrayList(firestoreUtility.getBackLogsForSection(s))));
    }

    private void showSubjectBackLogs() {

    }

    private void showStudentBackLogs() {
        Optional<String> result = showInputDialog("Student Details", "Admission Number");
        result.ifPresent(s -> backLogTable.setItems(FXCollections.observableArrayList(firestoreUtility.getBackLogsForStudent(s))));
    }

    private Optional<String> showInputDialog(String title, String promptedText) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField studentAdmissionField = new TextField();
        studentAdmissionField.setPromptText(promptedText);

        gridPane.add(studentAdmissionField, 1, 0);
        gridPane.add(new Label(promptedText), 0, 0);

        dialog.getDialogPane().setContent(gridPane);

        // Request focus on the username field by default.
        Platform.runLater(studentAdmissionField::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return studentAdmissionField.getText();
            }
            return null;
        });


        return dialog.showAndWait();
    }

    private void showContextMenu(ContextMenuEvent event, BackLog selectedItem) {

    }

    private void handleOnTableClick(KeyEvent keyEvent, BackLog selectedItem) {

    }

    @Override
    public void onDataLoaded(ObservableList data) {
        dataLoading.set(false);
        backLogTable.setItems(firestoreUtility.backLogs);
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
