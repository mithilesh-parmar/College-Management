package utility;

import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import listeners.DataChangeListener;
import model.Course;
import model.Exam;
import model.Result;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ResultFirestoreUtility {

    private static ResultFirestoreUtility instance;
    private DataChangeListener listener;
    public ObservableList<Result> results;

    private EventListener<QuerySnapshot> resultDataListener = (snapshot, e) -> {
        if (e != null) {
            System.err.println("Listen failed: " + e);
            if (listener != null) listener.onError(new Exception("Listen failed: " + e));
            return;
        }
        if (snapshot != null && !snapshot.isEmpty()) {
            Platform.runLater(() -> {
                if (listener != null) listener.onDataChange(snapshot);
                System.out.println("Encountered Changes");
                parseResultData(snapshot.getDocuments());
                if (listener != null) listener.onDataLoaded(results);
            });
        } else {
            System.out.print("Current data: null");
            if (listener != null) listener.onError(new Exception("Current data is null"));
        }
    };

    public static ResultFirestoreUtility getInstance() {
        if (instance == null) {
            synchronized (Result.class) {
                instance = new ResultFirestoreUtility();
            }
        }
        return instance;
    }

    public ResultFirestoreUtility() {
        results = FXCollections.observableArrayList();
    }

    public void getResults() {
        if (results.size() > 0) listener.onDataLoaded(results);
        FirestoreConstants.resultCollectionReference.addSnapshotListener(resultDataListener);
    }

    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }

    public Result getResultForStudent() {
        return null;
    }

    public ObservableList<Result> getResultForExam(Exam exam) {
        Query query = FirestoreConstants.resultCollectionReference
                .whereEqualTo("batch", exam.getBatch())
                .whereEqualTo("class_name", exam.getClassName())
                .whereEqualTo("exam_name", exam.getName())
                .whereEqualTo("section", exam.getSection());
        ObservableList<Result> list = FXCollections.observableArrayList();
        try {
            query.get().get().forEach(queryDocumentSnapshot -> {
                list.add(Result.fromJSON(queryDocumentSnapshot.getData()));
            });
            return list;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseResultData(List<QueryDocumentSnapshot> data) {
        results.clear();
        for (QueryDocumentSnapshot document : data) {
            Result result = Result.fromJSON(document.getData());
            results.add(result);
        }
    }
}
