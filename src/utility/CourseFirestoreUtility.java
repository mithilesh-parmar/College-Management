package utility;

import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import custom_view.card_view.AttendanceCard;
import custom_view.card_view.Card;
import custom_view.card_view.CardListener;
import custom_view.card_view.CourseCard;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import listeners.DataChangeListener;
import model.Course;
import model.SectionAttendance;
import model.Student;

import java.util.List;

public class CourseFirestoreUtility {

    private static CourseFirestoreUtility instance;

    private DataChangeListener listener;

    public ObservableList<Course> courses;
    public ListProperty<CourseCard> courseCards;
    public MapProperty<Course, CourseCard> courseCourseCardMapProperty;

    private EventListener<QuerySnapshot> courseDataListener = (snapshot, e) -> {
        if (e != null) {
            System.err.println("Listen failed: " + e);
            if (listener != null) listener.onError(new Exception("Listen failed: " + e));
            return;
        }
        if (snapshot != null && !snapshot.isEmpty()) {
            Platform.runLater(() -> {
                if (listener != null) listener.onDataChange(snapshot);
                System.out.println("Encountered Changes");
                parseCourseData(snapshot.getDocuments());
                if (listener != null) listener.onDataLoaded(courses);
            });
        } else {
            System.out.print("Current data: null");
            if (listener != null) listener.onError(new Exception("Current data is null"));
        }
    };

    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }


    private void parseCourseData(List<QueryDocumentSnapshot> data) {
        courses.clear();
        courseCards.clear();
        for (QueryDocumentSnapshot document : data) {
            Course course = Course.fromJSON(document.getData());

            CourseCard card = new CourseCard(
                    "Name: " + course.getName(),
                    "Years: " + course.getYears().toString()
            );


            courses.add(course);
            courseCards.add(card);
            courseCourseCardMapProperty.put(course, card);

        }
    }

    private CourseFirestoreUtility() {
        courses = FXCollections.observableArrayList();
        courseCards = new SimpleListProperty<>(FXCollections.observableArrayList());
        courseCourseCardMapProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    }

    public void getCourses() {
        if (courses.size() > 0) listener.onDataLoaded(courses);
        FirestoreConstants.courseCollectionReference.addSnapshotListener(courseDataListener);
    }

    public static CourseFirestoreUtility getInstance() {
        if (instance == null) synchronized (CourseFirestoreUtility.class) {
            instance = new CourseFirestoreUtility();
        }
        return instance;
    }
}
