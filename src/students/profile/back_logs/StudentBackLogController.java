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

    public TableView<BackLog> backLogTable;
    public ProgressIndicator progressIndicator;

    private BackLogFirestoreUtility firestoreUtility = BackLogFirestoreUtility.getInstance();
    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);

    private ObjectProperty<Student> student = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        backLogTable.setItems(firestoreUtility.backLogs);
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


    public void setStudent(Student student) {
        this.student.set(student);
        showStudentBackLogs(student);
    }
}
