package utility;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

public class FirebaseNotificationHelper {

    public static void sendNotification(String token) {
        // This registration token comes from the client FCM SDKs.


// See documentation on defining a message payload.
        Message message = Message.builder()
                .putData("Message", "Test notification message from desktop admin application")
                .setToken(token)
                .build();

// Send a message to the device corresponding to the provided
// registration token.
        String response = null;
        try {
            response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
// Response is a message ID string.
        System.out.println("Successfully sent message: " + response);
    }
}
