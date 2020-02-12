package backlog;

import com.google.cloud.firestore.QuerySnapshot;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import listeners.DataChangeListener;
import model.BackLog;
import utility.BackLogFirestoreUtility;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    private ObjectProperty<Button> selectedButton = new SimpleObjectProperty<>();
    private List<Button> buttonList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        studentBackLogButton.setUserData("Student BackLog");
        subjectBackLogButton.setUserData("Subject BackLog");
        sectionBackLogButton.setUserData("Section BackLog");
        examBackLogButton.setUserData("Exam BackLog");
        clearButton.setUserData("Clear BackLog");

        buttonList.add(studentBackLogButton);
        buttonList.add(subjectBackLogButton);
        buttonList.add(sectionBackLogButton);
        buttonList.add(examBackLogButton);
        buttonList.add(clearButton);

//        Set action listeners
        studentBackLogButton.setOnAction(actionEvent -> {
            showStudentBackLogs();
            selectedButton.set(studentBackLogButton);
        });
        subjectBackLogButton.setOnAction(actionEvent -> {
            showSubjectBackLogs();
            selectedButton.set(subjectBackLogButton);
        });
        sectionBackLogButton.setOnAction(actionEvent -> {
            showSectionBackLogs();
            selectedButton.set(sectionBackLogButton);
        });
        examBackLogButton.setOnAction(actionEvent -> {
            showExamBackLogs();
            selectedButton.set(examBackLogButton);
        });
        clearButton.setOnAction(actionEvent -> {
            backLogTable.setItems(firestoreUtility.backLogs);
            selectedButton.set(clearButton);
            highlightAll();
        });

        selectedButton.addListener((observableValue, button, t1) -> {
            if (t1 == null || t1.getUserData().toString().matches(clearButton.getUserData().toString())) return;
            highlightButton(t1);
        });


        backLogTable.setItems(firestoreUtility.backLogs);
        backLogTable.setOnKeyPressed(keyEvent -> handleOnTableClick(keyEvent, backLogTable.getSelectionModel().getSelectedItem()));
        backLogTable.setOnContextMenuRequested(event -> showContextMenu(event, backLogTable.getSelectionModel().getSelectedItem()));
        progressIndicator.visibleProperty().bind(dataLoading);
        firestoreUtility.setListener(this);
        firestoreUtility.getBackLogs();

    }

    private void highlightButton(Button selectedButton) {
        buttonList.forEach(button -> {
            if (button.getUserData().toString().matches(selectedButton.getUserData().toString())
                    || button.getUserData().toString().matches(clearButton.getUserData().toString())) {
                button.setDefaultButton(true);
                button.disableProperty().set(false);
            } else {
                button.disableProperty().set(true);
            }
        });
    }

    private void highlightAll() {
        buttonList.forEach(button -> {
            button.setDefaultButton(false);
            button.disableProperty().set(false);
        });
    }

    private void showExamBackLogs() {
        Optional<Pair<String, String>> result = showInputDialogWithTwoParameter("Exam Details", "Exam Id", "Exam Name");
        result.ifPresent(stringStringPair -> {
            dataLoading.set(true);
            backLogTable.setItems(FXCollections.observableArrayList(firestoreUtility.getBackLogsForExam(stringStringPair)));
            dataLoading.set(false);
        });
    }

    private void showSectionBackLogs() {
        Optional<String> result = showInputDialogWithOneParameter("Section Details", "Section Id");
        result.ifPresent(s -> {
            dataLoading.set(true);
            backLogTable.setItems(FXCollections.observableArrayList(firestoreUtility.getBackLogsForSection(s)));
            dataLoading.set(false);
        });
    }

    private void showSubjectBackLogs() {
        Optional<Pair<String, String>> result = showInputDialogWithTwoParameter("Details", "Section id", "Subject Name");
        result.ifPresent(sectionIdSubjectNamePair -> {
            dataLoading.set(true);
            backLogTable.setItems(FXCollections.observableArrayList(firestoreUtility.getBackLogsForSubject(sectionIdSubjectNamePair)));
            dataLoading.set(false);
        });
    }

    private void showStudentBackLogs() {
        Optional<String> result = showInputDialogWithOneParameter("Student Details", "Admission Number");
        result.ifPresent(s -> {
            dataLoading.set(true);
            backLogTable.setItems(FXCollections.observableArrayList(firestoreUtility.getBackLogsForStudent(s)));
            dataLoading.set(false);
        });
    }


    private Optional<Pair<String, String>> showInputDialogWithTwoParameter(String title, String promptedTextOne, String promptedTextTwo) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(title);

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField from = new TextField();
        from.setPromptText(promptedTextOne);
        TextField to = new TextField();
        to.setPromptText(promptedTextTwo);

        gridPane.add(from, 0, 0);
        gridPane.add(new Label(" OR "), 1, 0);
        gridPane.add(to, 2, 0);

        dialog.getDialogPane().setContent(gridPane);

        // Request focus on the username field by default.
        Platform.runLater(() -> from.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(from.getText(), to.getText());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private Optional<String> showInputDialogWithOneParameter(String title, String promptedText) {
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
