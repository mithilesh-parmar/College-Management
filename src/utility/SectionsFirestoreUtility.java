package utility;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import listeners.DataChangeListener;
import model.Lecture;
import model.Section;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SectionsFirestoreUtility {

    private DataChangeListener listener;
    private static SectionsFirestoreUtility instance;
    public ObservableList<Section> sections = FXCollections.observableArrayList();

    public ObservableList<String> days = FXCollections.observableArrayList();


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

    public void addLecture(Lecture lecture, Section section) {
//        System.out.println(lecture + "" + lecture.getDayOfWeek() + "" + section);
        section.addLecture(lecture);
//        System.out.println(section.toJSON());

        Query query = FirestoreConstants
                .sectionsCollectionReference
                .whereEqualTo("name", section.getSectionName())
                .whereEqualTo("class_id", section.getClassName());
        System.out.println("Section name " + section.getSectionName() + " class id " + section.getClassName());
        new Thread(() -> {
            try {
                query.get().get().forEach(queryDocumentSnapshot -> {
                    System.out.println("Found: " + queryDocumentSnapshot.getData());


//            write data to firestore
                    ApiFuture<WriteResult> attendance = queryDocumentSnapshot
                            .getReference()
                            .set(section.toJSON());


                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();

//        new Thread(() -> FirestoreConstants.sectionsClassScheduleCollectionReference.document(section.getName()).set(section.toJSON())).start();

    }

    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }

    private void parseSectionsData(List<QueryDocumentSnapshot> data) {
        if (sections != null) sections.clear();
        for (QueryDocumentSnapshot document : data) sections.add(Section.fromJSON(document.getData()));
    }
}
