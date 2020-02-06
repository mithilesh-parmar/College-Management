package test;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import constants.Constants;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import model.ClassItem;
import model.Leave;
import model.Section;
import utility.DateUtility;
import utility.FirestoreConstants;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class test {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        GoogleCredentials credentials = GoogleCredentials.fromStream(Constants.serviceAccount);


        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(Constants.projectID)
                .build();


        FirebaseApp.initializeApp(options);

//        for (int i = 0; i < 10; i++) {
//            Leave leave = new Leave(
//                    "",
//                    Timestamp.now(),
//                    Timestamp.now(),
//                    String.valueOf(i),
//                    String.valueOf("Name: " + i),
//                    "Reason " + i,
//                    i
//            );
//            DocumentReference document = FirestoreConstants.teacherLeavesCollectionReference.document();
//
//            leave.setId(document.getId());
//            document.set(leave.toJSON());
//            System.out.println(document.listCollections());

        CollectionReference sectionsCollectionReference = FirestoreConstants.sectionsCollectionReference;
//
//        Section firstYearMBA = new Section(
//                "",
//                "MBA",
//                "",
//                "1",
//                FXCollections.observableHashMap(),
//                List.of("Accounts", "Communication Skills", "Performa")
//        );
//        Section secondYearMBA = new Section(
//                "",
//                "MBA",
//                "",
//                "2",
//                FXCollections.observableHashMap(),
//                List.of("Foreign Trade", "Pressure Handling Skills", "Management")
//        );
//
//        addSection(firstYearMBA, firstYearMBA.getClassName(), sectionsCollectionReference);
//        addSection(secondYearMBA, secondYearMBA.getClassName(), sectionsCollectionReference);
//
//        Section firstYearBBA = new Section(
//                "",
//                "BBA",
//                "",
//                "1",
//                FXCollections.observableHashMap(),
//                List.of("Accounts", "Media Skills", "Business Analytics")
//        );
//        Section secondYearBBA = new Section(
//                "",
//                "BBA",
//                "",
//                "2",
//                FXCollections.observableHashMap(),
//                List.of("Business Development", "Taxation", "Analytics Data")
//        );
//
//        addSection(firstYearBBA, firstYearBBA.getClassName(), sectionsCollectionReference);
//        addSection(secondYearBBA, secondYearBBA.getClassName(), sectionsCollectionReference);
//
//
//        Section firstYearBCA = new Section(
//                "",
//                "BCA",
//                "",
//                "1",
//                FXCollections.observableHashMap(),
//                List.of("C", "C++", "Multimedia Computing")
//        );
//        Section secondYearBCA = new Section(
//                "",
//                "BCA",
//                "",
//                "2",
//                FXCollections.observableHashMap(),
//                List.of("Python", "Machine Learning", "Advanced C++")
//        );
//
//        addSection(firstYearBCA, firstYearBCA.getClassName(), sectionsCollectionReference);
//        addSection(secondYearBCA, secondYearBCA.getClassName(), sectionsCollectionReference);
//
//
//        Section firstYearMCA = new Section(
//                "",
//                "MCA",
//                "",
//                "1",
//                FXCollections.observableHashMap(),
//                List.of("C", "Java", "Web Development")
//        );
//        Section secondYearMCA = new Section(
//                "",
//                "MCA",
//                "",
//                "2",
//                FXCollections.observableHashMap(),
//                List.of("Adv. Java", "App Development", "Embedded Computing")
//        );
//
//        addSection(firstYearMCA, firstYearMCA.getClassName(), sectionsCollectionReference);
//        addSection(secondYearMCA, secondYearMCA.getClassName(), sectionsCollectionReference);


        Section firstYearBtech = new Section(
                "",
                "BTech",
                "",
                "1",
                FXCollections.observableHashMap(),
                List.of("Law", "English Communications", "Physics", "Chemistry", "Maths")
        );
        Section secondYearBtech = new Section(
                "",
                "BTech",
                "",
                "2",
                FXCollections.observableHashMap(),
                List.of("Value Educations", "Development Skills", "Physics Adv.", "Chemistry II")
        );
        Section thirdYearBtech = new Section(
                "",
                "BTech",
                "",
                "3",
                FXCollections.observableHashMap(),
                List.of("C", "C++", "Multi-Media", "Embedded", "Computer Architecture")
        );
        Section fourthYearBtech = new Section(
                "",
                "BTech",
                "",
                "4",
                FXCollections.observableHashMap(),
                List.of("JAVA", "Web Development", "App Development", "Compiler Construction")
        );

        addSection(firstYearBtech, firstYearBtech.getClassName(), sectionsCollectionReference);
        addSection(secondYearBtech, secondYearBtech.getClassName(), sectionsCollectionReference);
        addSection(thirdYearBtech, thirdYearBtech.getClassName(), sectionsCollectionReference);
        addSection(fourthYearBtech, fourthYearBtech.getClassName(), sectionsCollectionReference);

    }

    private static void addSection(Section section, String className, CollectionReference collectionReference) {

        try {
            FirestoreConstants
                    .classCollectionReference
                    .whereEqualTo("name", className)
                    .get()
                    .get()
                    .forEach(queryDocumentSnapshot -> {
                        ClassItem classItem = ClassItem.fromJSON(queryDocumentSnapshot.getData());
                        section.setClassID(classItem.getId());
                        section.setClassName(classItem.getName());
                        addSection(section, collectionReference);
                    });


        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void addSection(Section item, CollectionReference collectionReference) {
        DocumentReference document = collectionReference.document();
        item.setId(document.getId());
        document.set(item.toJSON());
        try {
            System.out.println(document.get().get().getData());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void addClass(ClassItem item, CollectionReference collectionReference) {
        DocumentReference document = collectionReference.document();
        item.setId(document.getId());
        document.set(item.toJSON());
        try {
            System.out.println(document.get().get().getData());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
