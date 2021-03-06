package utility;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import listeners.DataChangeListener;
import model.BackLog;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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


    public List<BackLog> getBackLogsForStudent(String studentId) {
        return backLogs
                .stream()
                .filter(backLog -> backLog.getStudentID().matches(studentId))
                .collect(Collectors.toList());
    }

    public List<BackLog> getBackLogsForSection(String sectionId) {
        return backLogs
                .stream()
                .filter(backLog -> backLog.getSectionID().matches(sectionId))
                .collect(Collectors.toList());
    }

    public List<BackLog> getBackLogsForExam(String examId) {
        return backLogs
                .stream()
                .filter(backLog -> backLog.getExamID().matches(examId))
                .collect(Collectors.toList());
    }

    public void getBackLogsForSubject(String sectionId, String subjectName) {

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

    public List<BackLog> getBackLogsForExam(Pair<String, String> result) {
//        key -> exam_id
//        value -> exam_name

        System.out.println(result);

        if (result.getValue().isEmpty() && !result.getKey().isEmpty()) {
            System.out.println("Returning for key " + result.getKey());
            return backLogs
                    .stream()
                    .filter(backLog -> backLog.getExamID().contains(result.getKey()))
                    .collect(Collectors.toList());
        } else if (result.getKey().isEmpty() && !result.getValue().isEmpty()) {
            System.out.println("Returning for value" + result.getValue());
            return backLogs
                    .stream()
                    .filter(backLog -> backLog.getExamName().toUpperCase().contains(result.getValue().toUpperCase()))
                    .collect(Collectors.toList());
        } else if (!result.getKey().isEmpty() && !result.getValue().isEmpty()) {
            System.out.println("Returning for " + result.getKey() + " and " + result.getValue());
            return backLogs
                    .stream()
                    .filter(
                            backLog ->
                                    backLog.getExamID().contains(result.getKey())
                                            ||
                                            backLog.getExamName().toUpperCase().contains(result.getValue().toUpperCase())
                    )
                    .collect(Collectors.toList());
        }
        System.out.println("Returning Empty List");
        return List.of();
    }

    public List<BackLog> getBackLogsForSubject(Pair<String, String> result) {
//        key -> section id
//        value -> subject name

        System.out.println(result);

//        if (!result.getValue().isEmpty() && !result.getKey().isEmpty()) {
//            System.out.println("Returning for key " + result.getKey());
//            return backLogs
//                    .stream()
//                    .filter(backLog -> backLog.getSectionID().matches(result.getKey()))
//                    .collect(Collectors.toList());
//        } else if (!result.getKey().isEmpty() && !result.getValue().isEmpty()) {
//            System.out.println("Returning for value" + result.getValue());
//            return backLogs
//                    .stream()
//                    .filter(backLog -> backLog.getSubjectName().toUpperCase().contains(result.getValue().toUpperCase()))
//                    .collect(Collectors.toList());
//        } else
        if (!result.getKey().isEmpty() && !result.getValue().isEmpty()) {
            System.out.println("Returning for " + result.getKey() + " and " + result.getValue());
            return backLogs
                    .stream()
                    .filter(
                            backLog ->
                                    backLog.getSectionID().matches(result.getKey())
                                            &&
                                            backLog.getSubjectName().toUpperCase().contains(result.getValue().toUpperCase())
                    )
                    .collect(Collectors.toList());
        }
        System.out.println("Returning Empty List");
        return List.of();
    }
}
