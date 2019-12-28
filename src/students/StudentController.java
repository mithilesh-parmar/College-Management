package students;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import custom_view.SearchTextFieldController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import listeners.DataChangeListener;
import model.Event;
import model.Student;
import utility.FirestoreConstants;
import utility.SearchCallback;
import utility.StudentFirestoreUtility;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StudentController implements Initializable, DataChangeListener, SearchCallback {


    public TableView<Student> studentTable;
    public ProgressIndicator progressIndicator;
    public SearchTextFieldController searchTextField;
    private StudentFirestoreUtility firestoreUtility = StudentFirestoreUtility.getInstance();
    private BooleanProperty loadingData = new SimpleBooleanProperty(true);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        firestoreUtility.setListener(this);
        firestoreUtility.getStudents();
        searchTextField.setCallback(this);
        progressIndicator.visibleProperty().bind(loadingData);
    }

    public void onTableKeyPressed(KeyEvent keyEvent) {
    }


    @Override
    public void onDataLoaded(ObservableList data) {
        loadingData.set(false);
        studentTable.setItems(firestoreUtility.students);
    }

    @Override
    public void onDataChange(QuerySnapshot data) {
        loadingData.set(true);
    }

    @Override
    public void onError(Exception e) {

        System.out.println(e);
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


