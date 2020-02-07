package utility;

import com.google.cloud.firestore.*;
import custom_view.card_view.*;
import javafx.animation.FadeTransition;
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
import model.ClassItem;
import model.Course;
import model.Section;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CourseFirestoreUtility {

    private static CourseFirestoreUtility instance;

    private DataChangeListener listener;

    public ObservableList<ClassItem> courses;
    public ListProperty<CourseCard> courseCards;
    public MapProperty<ClassItem, CourseCard> courseCourseCardMapProperty;
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
            ClassItem classItem = ClassItem.fromJSON(document.getData());

            CourseCard card = new CourseCard(
                    classItem
            );

            card.setCardListener(new CardListener() {
                @Override
                public void onCardClick() {
                    cardListener.onCardClick(classItem);
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

            courses.add(classItem);
            courseCards.add(card);
            courseCourseCardMapProperty.put(classItem, card);

        }
    }

    private CourseFirestoreUtility() {
        courses = FXCollections.observableArrayList();
        courseCards = new SimpleListProperty<>(FXCollections.observableArrayList());
        courseCourseCardMapProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    }

    public void getCourses() {
        if (courses.size() > 0) listener.onDataLoaded(courses);
        FirestoreConstants.classCollectionReference.addSnapshotListener(courseDataListener);
    }

    public static CourseFirestoreUtility getInstance() {
        if (instance == null) synchronized (CourseFirestoreUtility.class) {
            instance = new CourseFirestoreUtility();
        }
        return instance;
    }

    public void addCourse(Course course) {
        DocumentReference document = FirestoreConstants.classCollectionReference.document();
        List<Section> sections = course.getSections();

        System.out.println(sections);

        sections.forEach(section -> {
            addSection(section, document.getId());
        });

        course.getClassItem().setId(document.getId());
        document.set(course.getClassItem().toJSON());
    }

    private void addSection(Section section, String classID) {
        DocumentReference document = FirestoreConstants.sectionsCollectionReference.document();
        section.setId(document.getId());
        section.setClassID(classID);
        document.set(section.toJSON());
    }

    public void updateCourse(Course course) {
        ClassItem classItem = course.getClassItem();
        List<Section> sections = course.getSections();

//        Update The Class
        FirestoreConstants
                .classCollectionReference
                .document(classItem.getId())
                .set(classItem.toJSON());

//        Update The Sections
        sections.forEach(section ->
                FirestoreConstants
                        .sectionsCollectionReference
                        .document(section.getId())
                        .set(section.toJSON()));

    }


    public void deleteCourse(Course course) {
        String classID = course.getClassItem().getId();
        FirestoreConstants.classCollectionReference.document(course.getClassItem().getId()).delete();
        Query sectionsQuery = FirestoreConstants
                .sectionsCollectionReference
                .whereEqualTo("class_id", classID);
        try {

            sectionsQuery.get().get().forEach(queryDocumentSnapshot -> {
                System.out.println("Deleting: " + queryDocumentSnapshot.getData());
                queryDocumentSnapshot.getReference().delete();
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }
}
