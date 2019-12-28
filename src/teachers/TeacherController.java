package teachers;

import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import custom_view.SearchTextFieldController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import listeners.DataChangeListener;
import model.Student;
import model.Teacher;
import utility.FirestoreConstants;
import utility.SearchCallback;
import utility.TeacherFirestoreUtility;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TeacherController implements Initializable, DataChangeListener, SearchCallback {


    public TableView<Teacher> teacherTable;
    public ProgressIndicator progressIndicator;
    public SearchTextFieldController searchField;

    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);
    private TeacherFirestoreUtility firestoreUtility = TeacherFirestoreUtility.getInstance();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchField.setCallback(this);
        firestoreUtility.setListener(this);
        firestoreUtility.getTeachers();
        progressIndicator.visibleProperty().bind(dataLoading);
    }

    public void onTableKeyPressed(KeyEvent keyEvent) {

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

    public void onSearchTextEntered(KeyEvent keyEvent) {
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
