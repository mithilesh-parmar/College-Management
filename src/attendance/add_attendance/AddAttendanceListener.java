package attendance.add_attendance;


import javafx.stage.Stage;

public interface AddAttendanceListener {
    void onUploadStart();

    default void close(Stage stage) {
        stage.close();
    }
}
