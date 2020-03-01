package students.profile.back_logs;

import com.google.cloud.firestore.QuerySnapshot;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;
import listeners.DataChangeListener;
import model.BackLog;
import model.Student;
import utility.BackLogFirestoreUtility;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static custom_view.dialog_helper.CustomDialog.showInputDialogWithOneParameter;
import static custom_view.dialog_helper.CustomDialog.showInputDialogWithTwoParameter;

public class StudentBackLogController implements Initializable, DataChangeListener {

    public Button subjectBackLogButton;
    public Button sectionBackLogButton;
    public Button examBackLogButton;
    public Button clearButton;
    public TableView<BackLog> backLogTable;
    public ProgressIndicator progressIndicator;

    private BackLogFirestoreUtility firestoreUtility = BackLogFirestoreUtility.getInstance();
    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);
    private ObjectProperty<Button> selectedButton = new SimpleObjectProperty<>();
    private List<Button> buttonList = new ArrayList<>();

    private ObjectProperty<Student> student = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        subjectBackLogButton.setUserData("Subject BackLog");
        sectionBackLogButton.setUserData("Section BackLog");
        examBackLogButton.setUserData("Exam BackLog");
        clearButton.setUserData("Clear BackLog");

        buttonList.add(subjectBackLogButton);
        buttonList.add(sectionBackLogButton);
        buttonList.add(examBackLogButton);
        buttonList.add(clearButton);

//        Set action listeners
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

        student.addListener((observableValue, student1, t1) -> {
            if (t1 == null) return;
            showStudentBackLogs(t1);
        });
    }

    private void showStudentBackLogs(Student student) {
        dataLoading.set(true);
        if (student != null) {
            backLogTable.setItems(FXCollections.observableArrayList(firestoreUtility.getBackLogsForStudent(student.getAdmissionID())));
        } else {
            System.out.println("No Student Specified");
        }
        dataLoading.set(false);
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
        Optional<Pair<String, String>> result = showInputDialogWithTwoParameter(
                "Exam Details",
                "Exam Id",
                "Exam Name",
                "OR");


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
        Optional<Pair<String, String>> result = showInputDialogWithTwoParameter(
                "Details",
                "Section id",
                "Subject Name",
                "AND");
        result.ifPresent(sectionIdSubjectNamePair -> {
            dataLoading.set(true);
            backLogTable.setItems(FXCollections.observableArrayList(firestoreUtility.getBackLogsForSubject(sectionIdSubjectNamePair)));
            dataLoading.set(false);
        });
    }

    private void showStudentBackLogs() {
        dataLoading.set(true);
        if (student.get() != null) {
            backLogTable.setItems(FXCollections.observableArrayList(firestoreUtility.getBackLogsForStudent(student.get().getAdmissionID())));
        } else {
            System.out.println("No Student Specified");
        }
        dataLoading.set(false);

    }


    private void showContextMenu(ContextMenuEvent event, BackLog selectedItem) {

    }

    private void handleOnTableClick(KeyEvent keyEvent, BackLog selectedItem) {

    }

    @Override
    public void onDataLoaded(ObservableList data) {
        dataLoading.set(false);
//        backLogTable.setItems(firestoreUtility.backLogs);
        showStudentBackLogs();
    }

    @Override
    public void onDataChange(QuerySnapshot data) {
        dataLoading.set(true);
    }

    @Override
    public void onError(Exception e) {
        dataLoading.set(false);
    }


    public void setStudent(Student student) {
        this.student.set(student);
        showStudentBackLogs();
    }
}
