package test;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import constants.Constants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import model.BackLog;
import model.Section;
import model.Student;
import utility.FirestoreConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BackLogTest {
//    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
//        GoogleCredentials credentials = GoogleCredentials.fromStream(Constants.serviceAccount);
//
//
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(credentials)
//                .setProjectId(Constants.projectID)
//                .build();
//
//
//        FirebaseApp.initializeApp(options);
//
////        FirestoreConstants.sectionsCollectionReference.get().get().forEach(queryDocumentSnapshot -> {
////            System.out.println(queryDocumentSnapshot.getData());
////        });
//        List<Student> students = new ArrayList<>();
//        FirestoreConstants.studentCollectionReference.get().get().forEach(queryDocumentSnapshot -> {
//            if (queryDocumentSnapshot.getData().containsKey("admission_id") && queryDocumentSnapshot.getData().get("admission_id") != null)
//                students.add(Student.fromJSON(queryDocumentSnapshot.getData()));
//        });
//
//        students.forEach(student -> {
//            try {
//                Section section = Section.fromJSON(
//                        FirestoreConstants.sectionsCollectionReference.document(student.getSectionID()).get().get().getData());
//                System.out.println(section.toJSON());
//                BackLog backLog = new BackLog(
//                        "",
//                        student.getAdmissionID(),
//                        section.getSubjects().get(0),
//                        "Monthly Test",
//                        "MQDsWNDFXlT1RrExA628",
//                        section.getId(),
//                        section.getSectionName(),
//                        false
//                );
//                add(backLog, FirestoreConstants.backLogCollectionReference.document());
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//
//        });
//
////        add(new BackLog("", "A028343-16", "Foreign Trade", "Monthly Test", "MQDsWNDFXlT1RrExA628", false), FirestoreConstants.backLogCollectionReference.document());
////        add(new BackLog("", "A028345-16", "Business Analytics", "Monthly Test", "MQDsWNDFXlT1RrExA628", false), FirestoreConstants.backLogCollectionReference.document());
////        add(new BackLog("", "A028345-16", "Chemistry", "Monthly Test", "MQDsWNDFXlT1RrExA628", false), FirestoreConstants.backLogCollectionReference.document());
////        add(new BackLog("", "A028343-16", "Maths", "Monthly Test", "MQDsWNDFXlT1RrExA628", false), FirestoreConstants.backLogCollectionReference.document());
//
//
//    }


    public static void main(String[] args) throws FileNotFoundException {

        ObservableList test = FXCollections.observableArrayList();

        test.add("1");
        test.add("2");
        test.add("3");
        test.add("4");
        test.add("5");
        test.add("6");
        test.add("7");
        test.add("8");
        test.add("9");

        System.out.println(test);

        System.out.println(test.indexOf("5"));
        int i = test.indexOf("5");
        test.remove(test.indexOf("5"));
        test.add(i, "Test");
        System.out.println(test);


//        String url = "/Users/mithileshparmar/Downloads/052094ad9d2ff9399961dad584912758.jpg";
//        File file = new File(url);
//        Image image = new Image(file.toURI().toString());
//        System.out.println("Image url: " + image.getUrl());
//        System.out.println("File URI: " + file.toURI());
//        System.out.println("FIle Path: " + file.toPath().toString());

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
