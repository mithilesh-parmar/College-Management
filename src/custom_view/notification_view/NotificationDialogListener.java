package custom_view.notification_view;

import javafx.stage.Stage;
import model.Notification;

public interface NotificationDialogListener {
    void sendNotification(Notification notification);

    default void close(Stage stage){
        stage.close();
    }
}
