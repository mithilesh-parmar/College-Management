package custom_view.fees_notification_view;

import custom_view.notification_view.NotificationDialogListener;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.FeeNotification;

import java.net.URL;
import java.util.ResourceBundle;

public class FeesNotificationController implements Initializable {
    public TextField amountTextField;
    public TextField messageTextField;
    public TextField deadLineTextField;
    public Button sendButton;

    private NotificationDialogListener listener;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        sendButton.setOnAction(actionEvent -> {
            if (listener != null) listener.sendNotification(new FeeNotification(
                    null,
                    messageTextField.getText(),
                    amountTextField.getText(),
                    deadLineTextField.getText()
            ));
        });

    }


    public void setListener(NotificationDialogListener listener) {
        this.listener = listener;
    }
}
