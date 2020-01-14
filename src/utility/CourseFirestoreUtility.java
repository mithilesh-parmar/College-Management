package utility;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import custom_view.card_view.*;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import listeners.DataChangeListener;
import model.Course;


import java.util.List;

public class CourseFirestoreUtility {

    private static CourseFirestoreUtility instance;

    private DataChangeListener listener;

    public ObservableList<Course> courses;
    public ListProperty<CourseCard> courseCards;
    public MapProperty<Course, CourseCard> courseCourseCardMapProperty;
    private CourseCardListener cardListener;

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

    public void setCardListener(CourseCardListener cardListener) {
        this.cardListener = cardListener;
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
            createRotator(card).play();

            card.setCardListener(new CardListener() {
                @Override
                public void onCardClick() {
                    cardListener.onCardClick(course);
                }

                @Override
                public void onDeleteButtonClick() {

                }

                @Override
                public void onEditButtonClick() {

                }

                @Override
                public void onNotificationButtonClick() {

                }

                @Override
                public void onContextMenuRequested(MouseEvent event) {

                }
            });

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

    public void addCourse(Course course) {
        DocumentReference document = FirestoreConstants.courseCollectionReference.document();
        course.setId(document.getId());
        document.set(course.toJSON());
    }

    public void updateCourse(Course course) {
        FirestoreConstants.courseCollectionReference.document(course.getId()).set(course.toJSON());
    }

    private RotateTransition createRotator(CourseCard card) {
        RotateTransition rotator = new RotateTransition(Duration.millis(1000), card);
        rotator.setAxis(Rotate.Y_AXIS);
        rotator.setFromAngle(0);
        rotator.setToAngle(180);
        rotator.setAutoReverse(true);
        rotator.statusProperty().addListener((observableValue, status, t1) -> System.out.println(t1));
        rotator.setInterpolator(Interpolator.EASE_OUT);
        rotator.setCycleCount(1);


        return rotator;
    }
}
