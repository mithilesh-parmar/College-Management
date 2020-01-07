package test;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import constants.Constants;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class SectionTest {

    List<HashMap<String, Object>> attendance = new ArrayList<>();
    CollectionReference studentRef;


    public void start() throws IOException, InvalidFormatException, ExecutionException, InterruptedException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(Constants.serviceAccount);


        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(Constants.projectID)
                .build();

        List<Map<String, String>> stduentData = new ArrayList<>();

        FirebaseApp.initializeApp(options);

        CollectionReference attendanceRef = FirestoreClient.getFirestore()
                .collection("colleges")
                .document("GKevzOMWBqbFtPezdLzF")
                .collection("attendances");

        studentRef = FirestoreClient.getFirestore().collection("colleges").document("GKevzOMWBqbFtPezdLzF").collection("students");


        getStudentAttendanceForSubject("A028344-16","Maths");
    }


    void getStudentAttendanceForSubject(String studentId, String subjectName) throws ExecutionException, InterruptedException {
        Query admission_id = studentRef.whereEqualTo("admission_id", studentId);
        Map<String, Object> result;
        admission_id.get().get().forEach(queryDocumentSnapshot -> {
            Query query = queryDocumentSnapshot.getReference()
                    .collection("attendance")
                    .whereEqualTo("lecture", subjectName);
            try {
                query.get().get().forEach(queryDocumentSnapshot1 -> {
                    System.out.println(queryDocumentSnapshot1.getData());
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

    }







    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException, InvalidFormatException {
        new SectionTest().start();
    }


}
