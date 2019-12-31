package test;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import constants.Constants;
import utility.FirestoreConstants;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class TeacherTest {
    public static void main(String[] args) throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(Constants.serviceAccount);


        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(Constants.projectID)
                .build();


        FirebaseApp.initializeApp(options);
        FirestoreConstants.teacherCollectionReference.listDocuments().forEach(documentReference -> {
            try {
                DocumentSnapshot documentSnapshot = documentReference.get().get();

                FirestoreConstants.teacherCollectionReference.document(documentSnapshot.getId()).update("id", documentSnapshot.getId());

                System.out.println(documentSnapshot.get("name") + " " + documentSnapshot.get("email") + " : " + documentSnapshot.getId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });


    }
}
