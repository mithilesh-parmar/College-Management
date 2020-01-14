package course;

import com.google.cloud.firestore.QuerySnapshot;
import course.add_course.AddCourseController;
import custom_view.SearchTextFieldController;
import custom_view.chip_view.Chip;
import custom_view.chip_view.ChipView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import model.Course;
import model.Student;
import students.detail_view.StudentDetailsController;
import utility.CourseFirestoreUtility;

import java.io.IOException;
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
        addCourse.setOnAction(actionEvent -> loadAddVew(null));
    }

    private void loadAddVew(Course course) {


        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/course/add_course/AddCourseView.fxml"));
        final Stage stage = new Stage();
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Course Details");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
//            AddCourseController controller = loader.getController();


            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }

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
