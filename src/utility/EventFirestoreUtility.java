package utility;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.Blob;
import constants.Constants;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import listeners.DataChangeListener;
import model.Event;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EventFirestoreUtility {


// TODO on event add and edit upload the images first then upload the object

    private static EventFirestoreUtility instance;
    public ObservableList<Event> events;

    private DataChangeListener listener;

    private BooleanProperty canUploadDocument = new SimpleBooleanProperty(false);

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

//        upload pictures of event
        ListProperty<String> uploadURLS = new SimpleListProperty<>(FXCollections.observableArrayList());

        IntegerProperty sizeProperty = new SimpleIntegerProperty(0);

        CloudStorageUtility cloudStorageUtility = CloudStorageUtility.getInstance();


//        sizeProperty.addListener((observableValue, number, t1) -> {
//
//        });

        uploadURLS.sizeProperty().addListener((observableValue, number, t1) -> {
            System.out.println("Size Changed" + t1);
            if (t1 == null) return;
            if (uploadURLS.get().size() == event.getImages().size()) {
                System.out.println("Can upload Now");
                canUploadDocument.set(true);
            }
        });

        canUploadDocument.addListener((observableValue, aBoolean, t1) -> {
            if (t1 == null) return;
            if (t1) {
                //        upload event

                DocumentReference document = FirestoreConstants.eventCollectionReference.document();
                event.setId(document.getId());
                event.setImages(uploadURLS.get());
                System.out.println("Uploading now ");
                System.out.println(event.toJSON());
                document.set(event.toJSON());
            }
        });
        for (String s : event.getImages()) {

            System.out.println("Image: " + s);
            cloudStorageUtility.setListener(new DocumentUploadListener() {
                @Override
                public void onSuccess(Blob blob) {
                    uploadURLS.get().add(blob.getMediaLink());
                    System.out.println("Uploaded " + blob);
                }

                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                }
            });

            cloudStorageUtility.uploadDocument(
                    Constants.eventImageFolder + event.getNameWithoutSpaces(),
                    event.getNameWithoutSpaces(),
                    s,
                    DocumentType.IMAGE.toString());
        }

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
