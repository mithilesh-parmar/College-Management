package exams.add_exam;

import javafx.stage.Stage;
import model.Exam;

public interface AddExamCallback {
    void onAddExam(Exam exam);

    void onExamUpdated(Exam prevValue, Exam updatedValue);

    void onExamDelete(Exam exam);

    default void close(Stage stage) {
        stage.close();
    }
}
