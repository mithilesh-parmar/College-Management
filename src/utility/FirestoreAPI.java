package utility;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FirestoreAPI {

    private Firestore database = FirestoreClient.getFirestore();
    private final String baseDatabaseURL = "colleges/GKevzOMWBqbFtPezdLzF/";

    private static FirestoreAPI instance;

    private FirestoreAPI() {
    }

    public static FirestoreAPI getInstance() {
        if (instance == null) {
            synchronized (FirestoreAPI.class) {
                instance = new FirestoreAPI();
            }
        }
        return instance;
    }

    public List<QueryDocumentSnapshot> getCollection(String endPoint, EventListener<QuerySnapshot> listener) {
        try {

            CollectionReference collectionReference = database.collection(baseDatabaseURL + endPoint);
            if (listener != null) collectionReference.addSnapshotListener(listener);
            ApiFuture<QuerySnapshot> query = database.collection(baseDatabaseURL + endPoint).get();
            QuerySnapshot querySnapshot = query.get();

            return querySnapshot.getDocuments();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();

        }
        return null;
    }


}
