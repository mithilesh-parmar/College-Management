package test;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import constants.Constants;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import model.Leave;
import utility.DateUtility;
import utility.FirestoreConstants;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class test {

    public static void main(String[] args) throws IOException {

        GoogleCredentials credentials = GoogleCredentials.fromStream(Constants.serviceAccount);


        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(Constants.projectID)
                .build();


        FirebaseApp.initializeApp(options);

        for (int i = 0; i < 10; i++) {
            Leave leave = new Leave(
                    "",
                    Timestamp.now(),
                    Timestamp.now(),
                    String.valueOf(i),
                    String.valueOf("Name: " + i),
                    "Reason " + i,
                    i
            );
            DocumentReference document = FirestoreConstants.teacherLeavesCollectionReference.document();

            leave.setId(document.getId());
            document.set(leave.toJSON());
            System.out.println(document.listCollections());
        }

    }

}
