package utility;

import com.google.cloud.firestore.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import listeners.DataChangeListener;
import model.ClassItem;
import model.Lecture;
import model.Section;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ClassFirestoreUtility {

    private DataChangeListener listener;
    private static ClassFirestoreUtility instance;
    public ObservableList<ClassItem> classes = FXCollections.observableArrayList();

    private ClassFirestoreUtility() {
    }

    private EventListener<QuerySnapshot> classDataListener = (snapshot, e) -> {
        if (e != null) {
            System.err.println("Listen failed: " + e);
            if (listener != null) listener.onError(new Exception("Listen failed: " + e));
            return;
        }
        if (snapshot != null && !snapshot.isEmpty()) {
            Platform.runLater(() -> {
                if (listener != null) listener.onDataChange(snapshot);
                System.out.println("Encountered Changes");
                parseClassData(snapshot.getDocuments());
                if (listener != null) listener.onDataLoaded(classes);
            });
        } else {
            System.out.print("Current data: null");
            if (listener != null) listener.onError(new Exception("Current data is null"));
        }
    };


    public static ClassFirestoreUtility getInstance() {
        if (instance == null) {
            synchronized (ClassFirestoreUtility.class) {
                instance = new ClassFirestoreUtility();
            }
        }
        return instance;
    }


    public void getClasses() {
        if (classes.size() > 0) listener.onDataLoaded(classes);
        else {
            FirestoreConstants.classCollectionReference.addSnapshotListener(classDataListener);
        }

    }

    public ClassItem getClassItem(String className) {
        List<ClassItem> collect = classes
                .stream()
                .filter(classItem -> classItem.getName().matches(className))
                .collect(Collectors.toList());
        if (collect.size() > 0) return collect.get(0);
        return null;
    }

    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }

    private void parseClassData(List<QueryDocumentSnapshot> data) {
        if (classes != null) classes.clear();
        for (QueryDocumentSnapshot document : data) classes.add(ClassItem.fromJSON(document.getData()));
    }

    public void addLecture(Lecture lecture, Section section) {
        section.addLecture(lecture);
        FirestoreConstants.sectionsCollectionReference.document(section.getId()).set(section.toJSON());
    }
}
