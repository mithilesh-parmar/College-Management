package utility;

import com.google.cloud.firestore.CollectionReference;
import com.google.firebase.cloud.FirestoreClient;

public class FirestoreConstants {

    final static CollectionReference studentCollectionReference = FirestoreClient
            .getFirestore()
            .collection("colleges")
            .document("GKevzOMWBqbFtPezdLzF")
            .collection("students");

    public final static CollectionReference teacherCollectionReference = FirestoreClient
            .getFirestore()
            .collection("colleges")
            .document("GKevzOMWBqbFtPezdLzF")
            .collection("teachers");

    final static CollectionReference eventCollectionReference = FirestoreClient
            .getFirestore()
            .collection("colleges")
            .document("GKevzOMWBqbFtPezdLzF")
            .collection("events");

    final static CollectionReference sectionsCollectionReference = FirestoreClient
            .getFirestore()
            .collection("colleges")
            .document("GKevzOMWBqbFtPezdLzF")
            .collection("sections");

    final static CollectionReference sectionsClassScheduleCollectionReference = FirestoreClient
            .getFirestore()
            .collection("colleges")
            .document("GKevzOMWBqbFtPezdLzF")
            .collection("sections");





}
