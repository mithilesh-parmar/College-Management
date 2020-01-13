package course;

import com.google.cloud.firestore.QuerySnapshot;
import custom_view.SearchTextFieldController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.FlowPane;
import listeners.DataChangeListener;
import utility.CourseFirestoreUtility;

import java.net.URL;
import java.util.ResourceBundle;

public class CourseController implements Initializable, DataChangeListener {
    public SearchTextFieldController searchTextField;
    public Button addCourse;
    public FlowPane courseFlowPane;
    public ProgressIndicator progressIndicator;

    private CourseFirestoreUtility firestoreUtility = CourseFirestoreUtility.getInstance();
    private BooleanProperty dataLoading = new SimpleBooleanProperty(true);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        courseFlowPane.setPadding(new Insets(14));
        courseFlowPane.setVgap(10);
        courseFlowPane.setHgap(10);
        progressIndicator.visibleProperty().bind(dataLoading);
        firestoreUtility.setListener(this);
        firestoreUtility.getCourses();
        courseFlowPane.getChildren().addAll(firestoreUtility.courseCards);
    }

    @Override
    public void onDataLoaded(ObservableList data) {
        dataLoading.set(false);
        courseFlowPane.getChildren().setAll(firestoreUtility.courseCards);
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
