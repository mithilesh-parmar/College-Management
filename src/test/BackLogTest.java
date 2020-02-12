package test;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import constants.Constants;
import model.BackLog;
import model.Section;
import model.Student;
import utility.FirestoreConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BackLogTest {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(Constants.serviceAccount);


        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(Constants.projectID)
                .build();


        FirebaseApp.initializeApp(options);

//        FirestoreConstants.sectionsCollectionReference.get().get().forEach(queryDocumentSnapshot -> {
//            System.out.println(queryDocumentSnapshot.getData());
//        });
        List<Student> students = new ArrayList<>();
        FirestoreConstants.studentCollectionReference.get().get().forEach(queryDocumentSnapshot -> {
            if (queryDocumentSnapshot.getData().containsKey("admission_id") && queryDocumentSnapshot.getData().get("admission_id") != null)
                students.add(Student.fromJSON(queryDocumentSnapshot.getData()));
        });

        students.forEach(student -> {
            try {
                Section section = Section.fromJSON(
                        FirestoreConstants.sectionsCollectionReference.document(student.getSectionID()).get().get().getData());
                System.out.println(section.toJSON());
                BackLog backLog = new BackLog(
                        "",
                        student.getAdmissionID(),
                        section.getSubjects().get(0),
                        "Monthly Test",
                        "MQDsWNDFXlT1RrExA628",
                        section.getId(),
                        section.getSectionName(),
                        false
                );
                add(backLog, FirestoreConstants.backLogCollectionReference.document());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        });

//        add(new BackLog("", "A028343-16", "Foreign Trade", "Monthly Test", "MQDsWNDFXlT1RrExA628", false), FirestoreConstants.backLogCollectionReference.document());
//        add(new BackLog("", "A028345-16", "Business Analytics", "Monthly Test", "MQDsWNDFXlT1RrExA628", false), FirestoreConstants.backLogCollectionReference.document());
//        add(new BackLog("", "A028345-16", "Chemistry", "Monthly Test", "MQDsWNDFXlT1RrExA628", false), FirestoreConstants.backLogCollectionReference.document());
//        add(new BackLog("", "A028343-16", "Maths", "Monthly Test", "MQDsWNDFXlT1RrExA628", false), FirestoreConstants.backLogCollectionReference.document());


    }


    private static void read() throws ExecutionException, InterruptedException {
        FirestoreConstants.backLogCollectionReference.get().get().forEach(queryDocumentSnapshot -> {
            System.out.println(queryDocumentSnapshot.getData());
        });
    }

    private static void add(BackLog backLog, DocumentReference documentReference) throws ExecutionException, InterruptedException {
        backLog.setID(documentReference.getId());
        System.out.println("Adding " + backLog.toJSON());
        documentReference.set(backLog.toJSON());
        read();
    }
}
