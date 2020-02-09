package test;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import constants.Constants;
import model.Fee;
import model.Student;
import utility.FirestoreConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FeeTest {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        List<Student> students = new ArrayList<>();
        GoogleCredentials credentials = GoogleCredentials.fromStream(Constants.serviceAccount);


        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(Constants.projectID)
                .build();


        FirebaseApp.initializeApp(options);

        FirestoreConstants.studentCollectionReference.get().get().forEach(queryDocumentSnapshot -> {
            students.add(Student.fromJSON(queryDocumentSnapshot.getData()));
        });
        CollectionReference feeCollectionReference = FirestoreConstants.feeCollectionReference;
        students.forEach(student -> {
            Fee fee = new Fee("", student.getAdmissionID(), 20000, Timestamp.now(), Fee.Type.ADMISSION_FEE);
            add(fee, feeCollectionReference);
            Fee fee2 = new Fee("", student.getAdmissionID(), 1000, Timestamp.now(), Fee.Type.FINE);
            add(fee2, feeCollectionReference);
        });

        feeCollectionReference.get().get().forEach(queryDocumentSnapshot -> {
            System.out.println(queryDocumentSnapshot.getData());
        });

    }

    private static void add(Fee fee, CollectionReference collectionReference) {
        DocumentReference document = collectionReference.document();
        fee.setId(document.getId());
        document.set(fee.toJSON());
    }
}
