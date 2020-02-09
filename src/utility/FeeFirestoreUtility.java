package utility;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import listeners.DataChangeListener;
import model.Fee;
import model.Student;

import java.util.List;
import java.util.stream.Collectors;

public class FeeFirestoreUtility {

    private static FeeFirestoreUtility instance;
    public ObservableList<Fee> fees;

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
                parseFeesData(snapshot.getDocuments());
                if (listener != null) listener.onDataLoaded(fees);
            });
        } else {
            System.out.print("Current data: null");
            if (listener != null) listener.onError(new Exception("Current data is null"));
        }
    };


    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }

    private FeeFirestoreUtility() {
        fees = FXCollections.observableArrayList();
    }

    public static FeeFirestoreUtility getInstance() {
        if (instance == null) {
            synchronized (FeeFirestoreUtility.class) {
                instance = new FeeFirestoreUtility();
            }
        }
        return instance;
    }

    public void getFees() {
        if (fees.size() > 0) listener.onDataLoaded(fees);
        else FirestoreConstants.feeCollectionReference.addSnapshotListener(eventDataListener);

    }

    private void parseFeesData(List<QueryDocumentSnapshot> data) {
        fees.clear();
        for (QueryDocumentSnapshot document : data) fees.add(Fee.fromJSON(document.getData()));

    }

    public ObservableList<Fee> getFeesForStudent(String studentID) {
        System.out.println(fees);
        System.out.println("Filtering for: " + studentID);
        return
                FXCollections.observableArrayList(
                        fees.stream()
                                .filter(fee -> fee.getStudentID().matches(studentID))
                                .collect(Collectors.toList())
                );
    }


    public ObservableList<Fee> getFeesForDate(Timestamp date) {
        return
                FXCollections.observableArrayList(
                        fees.stream()
                                .filter(fee -> fee.getDate().equals(date))
                                .collect(Collectors.toList())
                );
    }


    public void addFee(Fee fee) {
        DocumentReference document = FirestoreConstants.feeCollectionReference.document();
        fee.setId(document.getId());
        document.set(fee.toJSON());
    }

    public void updateEvent(Fee updatedEvent) {

    }
}
