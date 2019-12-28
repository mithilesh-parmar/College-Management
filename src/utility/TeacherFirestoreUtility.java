package utility;

import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import listeners.DataChangeListener;
import model.Teacher;

import java.util.List;

public class TeacherFirestoreUtility {


    private static TeacherFirestoreUtility instance;
    public ObservableList<Teacher> teachers;
    private DataChangeListener listener;

    private EventListener<QuerySnapshot> teacherDataListener = (snapshot, e) -> {
        if (e != null) {
            System.err.println("Listen failed: " + e);
            if (listener != null) listener.onError(new Exception("Listen failed: " + e));
            return;
        }
        if (snapshot != null && !snapshot.isEmpty()) {
            Platform.runLater(() -> {
                if (listener != null) listener.onDataChange(snapshot);
                System.out.println("Encountered Changes");
                parseTeachersData(snapshot.getDocuments());
                if (listener != null) listener.onDataLoaded(teachers);
            });
        } else {
            System.out.print("Current data: null");
            if (listener != null) listener.onError(new Exception("Current data is null"));
        }
    };


    private TeacherFirestoreUtility() {
        teachers = FXCollections.observableArrayList();
    }

    public static TeacherFirestoreUtility getInstance() {
        if (instance == null) {
            synchronized (StudentFirestoreUtility.class) {
                instance = new TeacherFirestoreUtility();
            }
        }
        return instance;
    }

    public void getTeachers() {

        if (teachers.size() > 0) listener.onDataLoaded(teachers);
        else
            FirestoreConstants.teacherCollectionReference.addSnapshotListener(teacherDataListener);

    }

    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }


    public ObservableList<Teacher> parseTeachersData(List<QueryDocumentSnapshot> data) {
        for (QueryDocumentSnapshot document : data) teachers.add(Teacher.fromJSON(document.getData()));
        return teachers;
    }
}

