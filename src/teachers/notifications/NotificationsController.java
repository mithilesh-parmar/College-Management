package teachers.notifications;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import listeners.NotificationDialogListener;
import model.Notification;

import java.net.URL;
import java.util.ResourceBundle;

public class NotificationsController implements Initializable {


    public TextField titleTextField;
    public TextField messageTextField;
    public Button sendButton;
    private NotificationDialogListener listener;

    public void setListener(NotificationDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sendButton.setOnAction(actionEvent -> {
            if (listener != null) listener.sendNotification(new Notification(
                    titleTextField.getText().toString(),
                    messageTextField.getText().toString()
            ));
        });
    }


}
