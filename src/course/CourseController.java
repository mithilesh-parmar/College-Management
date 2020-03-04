package course;

import com.google.cloud.firestore.QuerySnapshot;
import course.add_course.AddCourseCallback;
import course.add_course.AddCourseController;
import custom_view.SearchTextFieldController;
import custom_view.card_view.CourseCardListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import listeners.DataChangeListener;
import model.ClassItem;
import model.Course;
import utility.CourseFirestoreUtility;
import utility.ScreenUtility;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CourseController implements Initializable, DataChangeListener, CourseCardListener {

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
        courseFlowPane.getChildren().setAll(firestoreUtility.courseCards);
        addCourse.setOnAction(actionEvent -> loadAddVew(null));
        firestoreUtility.setCardListener(this);

    }

    private void loadAddVew(ClassItem course) {


        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/course/add_course/AddCourseView.fxml"));
        final Stage stage = new Stage();
        stage.setHeight(ScreenUtility.getScreenHeight() * 0.70);
        stage.setWidth(ScreenUtility.getScreenHalfWidth());
        Parent parent = null;
        try {


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Course Details");

            parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            AddCourseController controller = loader.getController();
            if (course != null) {

                controller.setClassItem(course);
            }
            controller.setCallback(new AddCourseCallback() {
                @Override
                public void onCourseSubmit(Course course) {
                    close(stage);
                    dataLoading.set(true);
                    firestoreUtility.addCourse(course);
                }

                @Override
                public void onCourseUpdate(Course course) {
                    close(stage);
                    dataLoading.set(true);
//                    System.out.println("Updated Course ");
//                    System.out.println("Class: " + course.getClassItem().toJSON());
//                    System.out.println("Sections: ");
//                    course.getSections().forEach(section -> {
//                        System.out.println(section.toJSON());
//                    });
                    firestoreUtility.updateCourse(course);
                }

                @Override
                public void onCourseDelete(Course course) {
                    close(stage);
                    dataLoading.set(true);
//                    firestoreUtility.deleteCourse(course);
                }
            });
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

    @Override
    public void onCardClick(ClassItem course) {
//        System.out.println(course);
        loadAddVew(course);
    }
}
