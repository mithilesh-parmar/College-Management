package utility;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import listeners.DataChangeListener;
import model.Event;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class EventFirestoreUtility {


// TODO on event add and edit upload the images first then upload the object

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

    public void addEvent(Event event) {
        DocumentReference document = FirestoreConstants.eventCollectionReference.document();
        event.setId(document.getId());
        document.set(event.toJSON());
    }

    public void updateEvent(Event updatedEvent) {
        System.out.println("Updating event with data: " + updatedEvent.toJSON());
        try {
            ApiFuture<QuerySnapshot> future = FirestoreConstants.eventCollectionReference.whereEqualTo("id", updatedEvent.getId()).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot document : documents) {
                Event previousEvent = Event.fromJSON(document.getData());
                System.out.println("Previous data: " + previousEvent.toJSON());
                if (previousEvent.getId().equals(updatedEvent.getId())) {
                    System.out.println("Updating now.....");
                    new Thread(() -> document.getReference().set(updatedEvent.toJSON())).start();
                }
            }
            System.out.println("Quitting noew");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
