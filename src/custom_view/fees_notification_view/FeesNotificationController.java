package custom_view.fees_notification_view;

import custom_view.notification_view.NotificationDialogListener;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import model.FeeNotification;
import utility.DateUtility;

import java.net.URL;
import java.util.ResourceBundle;

public class FeesNotificationController implements Initializable {
    public TextField amountTextField;
    public TextField messageTextField;
    public Button sendButton;
    public DatePicker deadLineDate;

    private NotificationDialogListener listener;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        sendButton.setOnAction(actionEvent -> {
            if (listener != null) listener.sendNotification(new FeeNotification(
                    null,
                    messageTextField.getText(),
                    amountTextField.getText(),
                    DateUtility.localDateToTimestamp(deadLineDate.getValue())
            ));
        });

    }


    public void setListener(NotificationDialogListener listener) {
        this.listener = listener;
    }
}
