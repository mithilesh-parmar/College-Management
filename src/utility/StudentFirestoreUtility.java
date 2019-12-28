package utility;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import listeners.DataChangeListener;
import model.Student;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class StudentFirestoreUtility {


    private DataChangeListener listener;
    private static StudentFirestoreUtility instance;
    public ObservableList<Student> students;


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
    }

    public static StudentFirestoreUtility getInstance() {
        if (instance == null) {
            synchronized (StudentFirestoreUtility.class) {
                instance = new StudentFirestoreUtility();
            }
        }
        return instance;
    }

    public void getStudents() {
        if (students.size() > 0) listener.onDataLoaded(students);
        else FirestoreConstants.studentCollectionReference.addSnapshotListener(studentDataListener);

    }

    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }


    private void parseStudentsData(List<QueryDocumentSnapshot> data) {
        students.clear();
        for (QueryDocumentSnapshot document : data) students.add(Student.fromJSON(document.getData()));
    }
}
