package utility;

import com.google.cloud.firestore.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import listeners.DataChangeListener;
import model.Event;

import java.util.List;

public class EventFirestoreUtility {

    private static EventFirestoreUtility instance;
    public ObservableList<Event> events;

    private DataChangeListener listener;

    private EventListener<QuerySnapshot> eventDataListener = (snapshot, e) -> {
        if (e != null) {
            System.err.println("Listen failed: " + e);
            if (listener != null) listener.onError(new Exception("Listen failed: " + e));
            return;
        }
        if (snapshot != null && !snapshot.isEmpty()) {
            Platform.runLater(() -> {
                if (listener != null) listener.onDataChange(snapshot);
                System.out.println("Encountered Changes");
                parseEventsData(snapshot.getDocuments());
                if (listener != null) listener.onDataLoaded(events);
            });
        } else {
            System.out.print("Current data: null");
            if (listener != null) listener.onError(new Exception("Current data is null"));
        }
    };


    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }

    private EventFirestoreUtility() {
        events = FXCollections.observableArrayList();
    }

    public static EventFirestoreUtility getInstance() {
        if (instance == null) {
            synchronized (EventFirestoreUtility.class) {
                instance = new EventFirestoreUtility();
            }
        }
        return instance;
    }

    public void getEvents() {
        if (events.size() > 0) listener.onDataLoaded(events);
        else FirestoreConstants.eventCollectionReference.addSnapshotListener(eventDataListener);

    }

    private void parseEventsData(List<QueryDocumentSnapshot> data) {
        events.clear();
        for (QueryDocumentSnapshot document : data) events.add(Event.fromJSON(document.getData()));

    }
}
