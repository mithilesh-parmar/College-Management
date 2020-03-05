package utility;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Query;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import constants.Constants;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import model.Student;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class StudentVerificationUtility {

    public interface Callback {
        void onSuccess(Student student);

        void onFailure();

        void onStart();
    }

    private StudentVerificationUtility() {

    }


    public static void exist(String admissionID, Callback callback) throws ExecutionException, InterruptedException {
        BooleanProperty result = new SimpleBooleanProperty(false);
        ObjectProperty<Student> studentObjectProperty = new SimpleObjectProperty<>();
        Query admissionIDQuery = FirestoreConstants
                .studentCollectionReference
                .whereEqualTo("admission_id", admissionID);

        callback.onStart();
        admissionIDQuery.get().get().getDocuments().forEach(queryDocumentSnapshot -> {
            result.set(true);
            studentObjectProperty.set(Student.fromJSON(queryDocumentSnapshot.getData()));
        });

        if (!result.get()) callback.onFailure();
        else callback.onSuccess(studentObjectProperty.get());

    }


}
