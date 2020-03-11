package utility;


import com.google.cloud.firestore.Query;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import model.Student;

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
        Platform.runLater(() -> {
            BooleanProperty result = new SimpleBooleanProperty(false);
            ObjectProperty<Student> studentObjectProperty = new SimpleObjectProperty<>();
            Query admissionIDQuery = FirestoreConstants
                    .studentCollectionReference
                    .whereEqualTo("admission_id", admissionID);

            callback.onStart();
            try {
                admissionIDQuery.get().get().getDocuments().forEach(queryDocumentSnapshot -> {
                    result.set(true);
                    studentObjectProperty.set(Student.fromJSON(queryDocumentSnapshot.getData()));
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (!result.get()) callback.onFailure();
            else callback.onSuccess(studentObjectProperty.get());
        });


    }


}
