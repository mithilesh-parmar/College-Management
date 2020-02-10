package utility;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import listeners.DataChangeListener;
import model.BackLog;

import java.util.List;

public class BackLogFirestoreUtility {
    private DataChangeListener listener;
    private static BackLogFirestoreUtility instance;
    public ObservableList<BackLog> backLogs = FXCollections.observableArrayList();

    private BackLogFirestoreUtility() {
    }

    private EventListener<QuerySnapshot> backLogDataListener = (snapshot, e) -> {
        if (e != null) {
            System.err.println("Listen failed: " + e);
            if (listener != null) listener.onError(new Exception("Listen failed: " + e));
            return;
        }
        if (snapshot != null && !snapshot.isEmpty()) {
            Platform.runLater(() -> {
                if (listener != null) listener.onDataChange(snapshot);
                System.out.println("Encountered Changes");
                parseBackLogData(snapshot.getDocuments());
                if (listener != null) listener.onDataLoaded(backLogs);
            });
        } else {
            System.out.print("Current data: null");
            if (listener != null) listener.onError(new Exception("Current data is null"));
        }
    };


    public static BackLogFirestoreUtility getInstance() {
        if (instance == null) {
            synchronized (BackLogFirestoreUtility.class) {
                instance = new BackLogFirestoreUtility();
            }
        }
        return instance;
    }


    public void getBackLogs() {
        if (backLogs.size() > 0) listener.onDataLoaded(backLogs);
        else {
            FirestoreConstants.backLogCollectionReference.addSnapshotListener(backLogDataListener);
        }

    }


    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }

    private void parseBackLogData(List<QueryDocumentSnapshot> data) {
        if (backLogs != null) backLogs.clear();
        for (QueryDocumentSnapshot document : data) backLogs.add(BackLog.fromJSON(document.getData()));
    }

    public void addBackLog(BackLog backLog) {
        DocumentReference document = FirestoreConstants.backLogCollectionReference.document();
        backLog.setID(document.getId());
        document.set(backLog.toJSON());
    }
}
