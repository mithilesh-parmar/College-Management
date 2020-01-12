package utility;

import com.google.cloud.firestore.CollectionReference;
import com.google.firebase.cloud.FirestoreClient;

public class FirestoreConstants {

    public final static CollectionReference studentCollectionReference = FirestoreClient
            .getFirestore()
            .collection("colleges")
            .document("GKevzOMWBqbFtPezdLzF")
            .collection("students");

    public final static CollectionReference teacherCollectionReference = FirestoreClient
            .getFirestore()
            .collection("colleges")
            .document("GKevzOMWBqbFtPezdLzF")
            .collection("teachers");

    public final static CollectionReference eventCollectionReference = FirestoreClient
            .getFirestore()
            .collection("colleges")
            .document("GKevzOMWBqbFtPezdLzF")
            .collection("events");

    public final static CollectionReference sectionsCollectionReference = FirestoreClient
            .getFirestore()
            .collection("colleges")
            .document("GKevzOMWBqbFtPezdLzF")
            .collection("sections");

    public final static CollectionReference sectionsClassScheduleCollectionReference = FirestoreClient
            .getFirestore()
            .collection("colleges")
            .document("GKevzOMWBqbFtPezdLzF")
            .collection("sections");


    public final static CollectionReference teacherLeavesCollectionReference = FirestoreClient
            .getFirestore()
            .collection("colleges")
            .document("GKevzOMWBqbFtPezdLzF")
            .collection("teacher_leaves");

    public final static CollectionReference sectionAttendanceCollectionReference = FirestoreClient
            .getFirestore()
            .collection("colleges")
            .document("GKevzOMWBqbFtPezdLzF")
            .collection("attendances");

    public final static CollectionReference classCollectionReference = FirestoreClient
            .getFirestore()
            .collection("colleges")
            .document("GKevzOMWBqbFtPezdLzF")
            .collection("classes");

    public final static CollectionReference batchCollectionReference = FirestoreClient
            .getFirestore()
            .collection("colleges")
            .document("GKevzOMWBqbFtPezdLzF")
            .collection("batches");

    public final static CollectionReference courseCollectionReference = FirestoreClient
            .getFirestore()
            .collection("colleges")
            .document("GKevzOMWBqbFtPezdLzF")
            .collection("courses");

    public static CollectionReference subjectCollectionReference = FirestoreClient
            .getFirestore()
            .collection("colleges")
            .document("GKevzOMWBqbFtPezdLzF")
            .collection("subjects");
    ;
}
