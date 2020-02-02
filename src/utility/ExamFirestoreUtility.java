package utility;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import custom_view.card_view.CardListener;
import custom_view.card_view.CourseCard;
import custom_view.card_view.ExamCard;
import custom_view.card_view.ExamCardListener;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import listeners.DataChangeListener;
import model.Course;
import model.Exam;

import javax.swing.event.DocumentListener;
import java.util.List;

public class ExamFirestoreUtility {

    private static ExamFirestoreUtility instance;

    public ObservableList<Exam> exams;
    public ListProperty<ExamCard> examCards;
    public MapProperty<Exam, ExamCard> examCardMapProperty;

    private ExamCardListener examCardListener;
    private DataChangeListener listener;

    private EventListener<QuerySnapshot> examDataListener = (snapshot, e) -> {
        if (e != null) {
            System.err.println("Listen failed: " + e);
            if (listener != null) listener.onError(new Exception("Listen failed: " + e));
            return;
        }
        if (snapshot != null && !snapshot.isEmpty()) {
            Platform.runLater(() -> {
                if (listener != null) listener.onDataChange(snapshot);
                System.out.println("Encountered Changes");
                parseExamData(snapshot.getDocuments());
                if (listener != null) listener.onDataLoaded(exams);
            });
        } else {
            System.out.print("Current data: null");
            if (listener != null) listener.onError(new Exception("Current data is null"));
        }
    };


    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }

    private ExamFirestoreUtility() {
        exams = FXCollections.observableArrayList();
        examCards = new SimpleListProperty<>(FXCollections.observableArrayList());
        examCardMapProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
    }

    public void getExams() {
        if (exams.size() > 0) listener.onDataLoaded(exams);
        FirestoreConstants.examsCollectionReference.addSnapshotListener(examDataListener);
    }

    public static ExamFirestoreUtility getInstance() {
        if (instance == null) synchronized (ExamFirestoreUtility.class) {
            instance = new ExamFirestoreUtility();
        }
        return instance;
    }

    public void setExamCardListener(ExamCardListener examCardListener) {
        this.examCardListener = examCardListener;
    }

    private void parseExamData(List<QueryDocumentSnapshot> data) {
        exams.clear();
        examCards.clear();
        for (QueryDocumentSnapshot document : data) {
            Exam exam = Exam.fromJSON(document.getData());


            ExamCard card = new ExamCard(exam);

            if (examCardListener != null)
                card.setListener(examCardListener);

            exams.add(exam);
            examCards.add(card);
            examCardMapProperty.put(exam, card);

        }
    }

    public void addExam(Exam exam) {
        DocumentReference document = FirestoreConstants.examsCollectionReference.document();
        exam.setId(document.getId());
        document.set(exam.toJSON());
    }
}
