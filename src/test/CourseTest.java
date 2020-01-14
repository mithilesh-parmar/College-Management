package test;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import constants.Constants;
import model.Course;
import utility.FirestoreConstants;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CourseTest {
    public static void main(String[] args) throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(Constants.serviceAccount);


        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(Constants.projectID)
                .build();


        FirebaseApp.initializeApp(options);


        Map<String, List<String>> bbaSubjects = new HashMap<>();

        bbaSubjects.put(String.valueOf(1), List.of("BBA Hindi", "BBA Maths", "BBA English", "BBA Physics"));
        bbaSubjects.put(String.valueOf(2), List.of("BBA Management", "BBA Business Studies", "BBA Communication", "BBA French"));
        bbaSubjects.put(String.valueOf(3), List.of("BBA Discrete Maths", "BBA Catering", "BBA Art of humanity", "BBA Computer Science"));


        Map<String, List<String>> mbaSubjects = new HashMap<>();

        mbaSubjects.put(String.valueOf(1), List.of("MBA Hindi", "MBA Maths", "MBA English", "MBA Physics"));
        mbaSubjects.put(String.valueOf(2), List.of("MBA Management", "MBA Business Studies", "MBA Communication", "MBA French"));

        Map<String, List<String>> btechSubjects = new HashMap<>();

        btechSubjects.put(String.valueOf(1), List.of("BTech Hindi", "BTech Maths", "BTech English", "BTech Physics"));
        btechSubjects.put(String.valueOf(2), List.of("BTech Management", "BTech Business Studies", "BTech Communication", "BTech French"));
        btechSubjects.put(String.valueOf(3), List.of("BTech Discrete Maths", "BTech Catering", "BTech Art of humanity", "BTech Computer Science"));
        btechSubjects.put(String.valueOf(4), List.of("BTech Advanced Maths", "BTech Advanced Catering", "BTech Advanced Art of humanity", "BTech Advanced Computer Science"));


        Course BBA = new Course("", "BBA", (long) 3, bbaSubjects);

        Course B_Tech = new Course("", "B.Tech", (long) 4, btechSubjects);

        Course MBA = new Course("", "MBA", (long) 2, mbaSubjects);

        DocumentReference bbaDocument = FirestoreConstants.courseCollectionReference.document();
        BBA.setId(bbaDocument.getId());
        bbaDocument.set(BBA.toJSON());

        System.out.println(bbaDocument.listCollections());

        DocumentReference mbaDocument = FirestoreConstants.courseCollectionReference.document();
        MBA.setId(mbaDocument.getId());
        mbaDocument.set(MBA.toJSON());
        System.out.println(mbaDocument.listCollections());

        DocumentReference bTechDocument = FirestoreConstants.courseCollectionReference.document();
        B_Tech.setId(bTechDocument.getId());
        bTechDocument.set(B_Tech.toJSON());

        System.out.println(bTechDocument.listCollections());

    }
}