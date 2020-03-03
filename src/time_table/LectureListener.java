package time_table;

import javafx.stage.Stage;
import model.Lecture;

public interface LectureListener {

    void onLectureAdded(Lecture lecture);

    default void close(Stage stage){
        stage.close();
    }

    void onLectureUpdated(Lecture lecture);
}
