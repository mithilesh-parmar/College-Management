package utility;

import com.google.cloud.firestore.*;
import custom_view.card_view.Card;
import custom_view.card_view.CardListener;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import listeners.DataChangeListener;
import model.Notification;
import model.Student;
import students.StudentCardListener;

import java.util.List;

public class StudentFirestoreUtility {


    private DataChangeListener listener;
    private static StudentFirestoreUtility instance;
    public ObservableList<Student> students;
    public MapProperty<Student, Card> studentCardMapProperty;
    public ListProperty<Card> studentCards;
    private StudentCardListener cardListener;


    private EventListener<QuerySnapshot> studentDataListener = (snapshot, e) -> {
        if (e != null) {
            System.err.println("Listen failed: " + e);
            if (listener != null) listener.onError(new Exception("Listen failed: " + e));
            return;
        }
        if (snapshot != null && !snapshot.isEmpty()) {
            Platform.runLater(() -> {
                if (listener != null) listener.onDataChange(snapshot);
                System.out.println("Encountered Changes");
                parseStudentsData(snapshot.getDocuments());
                if (listener != null) listener.onDataLoaded(students);
            });
        } else {
            System.out.print("Current data: null");
            if (listener != null) listener.onError(new Exception("Current data is null"));
        }
    };


    private StudentFirestoreUtility() {
        students = FXCollections.observableArrayList();
        studentCards = new SimpleListProperty<>(FXCollections.observableArrayList());
        studentCardMapProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    }

    public static StudentFirestoreUtility getInstance() {
        if (instance == null) {
            synchronized (StudentFirestoreUtility.class) {
                instance = new StudentFirestoreUtility();
            }
        }
        return instance;
    }

    public void getAttendance(Student student, EventListener<QuerySnapshot> studentAttendanceListener) {
        FirestoreConstants
                .studentCollectionReference
                .document(student.getID())
                .collection("attendance")
                .addSnapshotListener(studentAttendanceListener);
    }

    public void publishNotification(Student student, Notification notification) {
        FirestoreConstants.studentCollectionReference.document(student.getID()).collection("push_notification").add(notification.toJSON());
    }

    public void publishFeeNotification(Student student, Notification notification) {
        FirestoreConstants.studentCollectionReference.document(student.getID()).collection("fees_notification").add(notification.toJSON());

    }

    public void getStudents() {

        if (students.size() > 0) listener.onDataLoaded(students);
        else FirestoreConstants.studentCollectionReference.addSnapshotListener(studentDataListener);

    }

    public void setCardListener(StudentCardListener cardListener) {
        this.cardListener = cardListener;
    }

    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }


    private void parseStudentsData(List<QueryDocumentSnapshot> data) {
        students.clear();
        studentCards.clear();
        for (QueryDocumentSnapshot document : data) {
            Student student = Student.fromJSON(document.getData());

            Card card = new Card(student.getName(), student.getEmail(), student.getProfilePictureURL(),true);


            if (cardListener != null) {
                card.setListener(new CardListener() {
                    @Override
                    public void onCardClick() {
                        cardListener.onCardClick(student);
                    }

                    @Override
                    public void onDeleteButtonClick() {
                        cardListener.onDeleteButtonClick(student);
                    }

                    @Override
                    public void onEditButtonClick() {
                        cardListener.onEditButtonClick(student);
                    }

                    @Override
                    public void onNotificationButtonClick() {
                        cardListener.onNotificationButtonClick(student);
                    }

                    @Override
                    public void onContextMenuRequested(MouseEvent event) {
                        cardListener.onContextMenuRequested(student, event);
                    }
                });

            }


            students.add(student);
            studentCards.add(card);
            studentCardMapProperty.put(student, card);

        }
    }


    public void deleteStudent(Student student) {
        FirestoreConstants.studentCollectionReference.document(student.getID()).delete();
    }
}
