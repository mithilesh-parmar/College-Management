import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import constants.Constants;
import model.Leave;
import utility.FirestoreConstants;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Test {
    public static void main(String[] args) {
        GoogleCredentials credentials = null;
        try {
            System.out.println("Loading Credentials : " + Constants.serviceAccount);
            credentials = GoogleCredentials.fromStream(Constants.serviceAccount);
            System.out.println("Loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }


        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(Constants.projectID)
                .build();


        System.out.println("Initialing app");
        FirebaseApp.initializeApp(options);

        DocumentReference document = FirestoreConstants.teacherLeavesCollectionReference.document();
        Leave leave = new Leave(
                document.getId(),
                Timestamp.now(),
                Timestamp.now(),
                "Qb7fl2mrI3AVOohtPOc7",
                "Mithilesh",
                "Not Given",
                0L
        );
        document.set(leave.toJSON());
        FirestoreConstants.teacherLeavesCollectionReference.listDocuments().forEach(documentReference -> {
            try {
                System.out.println(documentReference.get().get().getData());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        DocumentReference document1 = FirestoreConstants.teacherLeavesCollectionReference.document();
        Leave leave1 = new Leave(
                document1.getId(),
                Timestamp.now(),
                Timestamp.now(),
                "Qb7fl2mrI3AVOohtPOc7",
                "Unknown",
                "Not Given",
                0L
        );
        document1.set(leave1.toJSON());
    }
}
