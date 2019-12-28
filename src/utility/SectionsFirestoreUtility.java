package utility;

import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import listeners.DataChangeListener;
import model.Lecture;
import model.Section;

import java.util.List;

public class SectionsFirestoreUtility {

    private DataChangeListener listener;
    private static SectionsFirestoreUtility instance;


    public ObservableList<Section> sections = FXCollections.observableArrayList();

    public ObservableList<String> days = FXCollections.observableArrayList();

    private Task uploadTask = new Task() {

        @Override
        protected Object call() throws Exception {

            return null;
        }
    };

    private SectionsFirestoreUtility() {
        getDays();
    }

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
                parseSectionsData(snapshot.getDocuments());
                if (listener != null) listener.onDataLoaded(sections);
            });
        } else {
            System.out.print("Current data: null");
            if (listener != null) listener.onError(new Exception("Current data is null"));
        }
    };


    public static SectionsFirestoreUtility getInstance() {
        if (instance == null) {
            synchronized (SectionsFirestoreUtility.class) {
                instance = new SectionsFirestoreUtility();
            }
        }
        return instance;
    }

    public void getDays() {
        days.add("MON");
        days.add("TUE");
        days.add("WED");
        days.add("THUR");
        days.add("FRI");
        days.add("SAT");

    }

    public void getSections() {
        if (sections.size() > 0) listener.onDataLoaded(sections);
        else {
            FirestoreConstants.sectionsCollectionReference.addSnapshotListener(studentDataListener);
        }

    }

    public void addLecture(Lecture lecture, String dayOfWeek, Section section) {
        section.addLecture(lecture, dayOfWeek);
        System.out.println(section.toJSON());
        new Thread(() -> FirestoreConstants.sectionsClassScheduleCollectionReference.document(section.getName()).set(section.toJSON())).start();

    }

    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }

    private void parseSectionsData(List<QueryDocumentSnapshot> data) {
        if (sections != null) sections.clear();
        for (QueryDocumentSnapshot document : data) sections.add(Section.fromJSON(document.getData()));
    }
}
